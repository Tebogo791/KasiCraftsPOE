"use strict";
const { before, after, test } = require("node:test");
const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { doc, getDoc, setDoc, updateDoc } = require("firebase/firestore");
const { readFileSync } = require("node:fs");
const { resolve } = require("node:path");

const projectId = "kasicrafts-rules-test";
const TIMEOUT_MS = 15000;
async function bounded(label, operation) {
  console.log("[rules] " + label + "...");
  let timer;
  try {
    return await Promise.race([
      operation(),
      new Promise((_, reject) => { timer = setTimeout(() => reject(new Error(label + " timed out after 15s; check emulator logs")), TIMEOUT_MS); })
    ]);
  } finally { clearTimeout(timer); }
}
let env;
before(async () => {
  if (!process.env.FIRESTORE_EMULATOR_HOST) {
    throw new Error("Start the Firestore emulator first; FIRESTORE_EMULATOR_HOST is missing");
  }
  const [host, port] = process.env.FIRESTORE_EMULATOR_HOST.split(":");
  env = await bounded("initializeTestEnvironment", () => initializeTestEnvironment({
    projectId,
    firestore: { host, port: Number(port), rules: readFileSync(resolve(__dirname, "../firestore.rules"), "utf8") }
  }));
  console.log("[rules] test environment initialized");
});
after(async () => { if (env) await bounded("cleanup", () => env.cleanup()); });

const buyer = () => env.authenticatedContext("buyer1", { email: "buyer@example.com" }).firestore();
const stranger = () => env.authenticatedContext("stranger", { email: "other@example.com" }).firestore();
const seller = () => env.authenticatedContext("seller1", { email: "seller@example.com" }).firestore();
const guest = () => env.unauthenticatedContext().firestore();
const profile = { name: "Buyer", email: "buyer@example.com", role: "buyer", loyaltyPoints: 0 };
const listing = { title: "Handmade basket", price: "R100", sellerName: "Artisan", sellerArea: "Limpopo", sellerUid: "seller1", sellerEmailVerified: false, timestamp: Date.now() };

test("user can create and read own profile, but strangers cannot read it", async () => {
  await bounded("clearFirestore", () => env.clearFirestore());
  console.log("[rules] clearFirestore complete");
  console.log("[rules] creating buyer profile...");
  await bounded("create buyer profile", () => assertSucceeds(setDoc(doc(buyer(), "users/buyer1"), profile)));
  console.log("[rules] buyer profile created");
  await bounded("read buyer profile", () => assertSucceeds(getDoc(doc(buyer(), "users/buyer1"))));
  await bounded("deny stranger profile read", () => assertFails(getDoc(doc(stranger(), "users/buyer1"))));
  await bounded("deny guest profile read", () => assertFails(getDoc(doc(guest(), "users/buyer1"))));
});
test("client cannot award loyalty points or claim verification", async () => {
  await bounded("clearFirestore", () => env.clearFirestore());
  await bounded("seed fixture", () => env.withSecurityRulesDisabled(async context => {
    await setDoc(doc(context.firestore(), "users/buyer1"), profile);
  }));
  await assertFails(updateDoc(doc(buyer(), "users/buyer1"), { loyaltyPoints: 500 }));
  await assertFails(updateDoc(doc(buyer(), "users/buyer1"), { artisanVerified: true }));
  await assertSucceeds(updateDoc(doc(buyer(), "users/buyer1"), { name: "Updated Buyer" }));
  await assertFails(setDoc(doc(buyer(), "users/buyer1"), { ...profile, loyaltyPoints: 500 }));
});
test("seller cannot impersonate another seller or edit a published listing", async () => {
  await bounded("clearFirestore", () => env.clearFirestore());
  await assertFails(setDoc(doc(stranger(), "products/fake"), listing));
  await assertSucceeds(setDoc(doc(seller(), "products/valid"), listing));
  await assertSucceeds(getDoc(doc(guest(), "products/valid")));
  await assertFails(updateDoc(doc(seller(), "products/valid"), { price: "R1" }));
});
test("clients cannot write orders or loyalty awards; only buyer can read order", async () => {
  await bounded("clearFirestore", () => env.clearFirestore());
  await bounded("seed fixture", () => env.withSecurityRulesDisabled(async context => {
    await setDoc(doc(context.firestore(), "orders/order1"), { buyerUid: "buyer1", status: "pending", totalCents: 10000 });
  }));
  await assertSucceeds(getDoc(doc(buyer(), "orders/order1")));
  await assertFails(getDoc(doc(stranger(), "orders/order1")));
  await assertFails(setDoc(doc(buyer(), "orders/order2"), { buyerUid: "buyer1", status: "completed", totalCents: 10000 }));
  await assertFails(updateDoc(doc(buyer(), "orders/order1"), { status: "completed" }));
  await assertFails(setDoc(doc(buyer(), "loyaltyAwards/order1"), { points: 1000 }));
});
