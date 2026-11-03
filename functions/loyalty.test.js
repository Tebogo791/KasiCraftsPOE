"use strict";

const test = require("node:test");
const assert = require("node:assert/strict");
const { calculatePoints, shouldAward } = require("./loyalty");

test("points accrue for full R10 increments", () => {
  assert.equal(calculatePoints(0), 0);
  assert.equal(calculatePoints(999), 0);
  assert.equal(calculatePoints(1000), 1);
  assert.equal(calculatePoints(10500), 10);
});
test("points are capped", () => {
  assert.equal(calculatePoints(2000000), 1000);
});
test("invalid totals are rejected", () => {
  for (const n of [-1, NaN, Infinity, 1.2, "1000"]) {
    assert.throws(() => calculatePoints(n));
  }
});
test("only an immutable completed-order transition qualifies", () => {
  const pending = { status: "pending", buyerUid: "buyer1", totalCents: 1000 };
  const completed = { ...pending, status: "completed" };
  assert.equal(shouldAward(pending, completed), true);
  assert.equal(shouldAward(completed, completed), false);
  assert.equal(shouldAward(pending, pending), false);
  assert.equal(shouldAward(pending, { ...completed, buyerUid: "other" }), false);
  assert.equal(shouldAward(pending, { ...completed, totalCents: 2000 }), false);
});
