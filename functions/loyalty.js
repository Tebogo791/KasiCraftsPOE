"use strict";

function calculatePoints(totalCents) {
  if (!Number.isSafeInteger(totalCents) || totalCents < 0) {
    throw new TypeError("totalCents must be a nonnegative safe integer");
  }
  return Math.min(1000, Math.floor(totalCents / 1000));
}

function shouldAward(before, after) {
  return Boolean(before && after &&
    before.status !== "completed" &&
    after.status === "completed" &&
    before.buyerUid === after.buyerUid &&
    before.totalCents === after.totalCents);
}

module.exports = { calculatePoints, shouldAward };
