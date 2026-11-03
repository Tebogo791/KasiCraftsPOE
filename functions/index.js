"use strict";

const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore, FieldValue } = require("firebase-admin/firestore");
const { calculatePoints, shouldAward } = require("./loyalty");

initializeApp();
const db = getFirestore();

// Orders are server-written only. Payment confirmation and order fulfillment
// must be independently verified before the backend sets status=completed.
exports.awardCompletedOrderLoyalty = onDocumentUpdated(
  "orders/{orderId}",
  async (event) => {
    const before = event.data?.before?.data();
    const after = event.data?.after?.data();
    if (!shouldAward(before, after)) return;

    const orderId = event.params.orderId;
    const buyerUid = after.buyerUid;
    const totalCents = after.totalCents;
    if (typeof buyerUid !== "string" || !buyerUid ||
        !Number.isSafeInteger(totalCents) || totalCents < 0) {
      throw new Error("Completed order has invalid buyer or total");
    }

    // 1 point per full R10 spent; cap a single order at 1,000 points.
    const points = calculatePoints(totalCents);
    const awardRef = db.collection("loyaltyAwards").doc(orderId);
    const userRef = db.collection("users").doc(buyerUid);
    await db.runTransaction(async (tx) => {
      const [existingAward, user] = await Promise.all([
        tx.get(awardRef), tx.get(userRef)
      ]);
      if (existingAward.exists) return;
      if (!user.exists) throw new Error("Buyer profile does not exist");
      tx.create(awardRef, {
        orderId, buyerUid, points,
        awardedAt: FieldValue.serverTimestamp()
      });
      tx.update(userRef, { loyaltyPoints: FieldValue.increment(points) });
    });
  }
);
