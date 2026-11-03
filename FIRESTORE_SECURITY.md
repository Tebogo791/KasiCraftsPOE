# Firestore security and loyalty rollout

Firebase project: `kasicrafts-5cd37`. Run commands from the repository root after reviewing the Firebase project and authenticating with the Firebase CLI.

## Current behavior

- Registered users have profiles at `users/{uid}` with `loyaltyPoints: 0`.
- The app reads `artisanVerified` and `loyaltyPoints`, but cannot award points or approve artisans.
- Only signed-in accounts can publish products, which carry their `sellerUid`.
- Checkout is not connected to a payment provider and does not create paid orders.
- The rules in `firestore.rules` have **not** been deployed automatically.

## Validate before deployment

1. Test registration, login, seller upload, product browsing, profile reading and settings with the Firebase emulator suite or Rules Playground.
2. Verify an unauthenticated user cannot create a product.
3. Verify a user cannot create a product with a different `sellerUid`.
4. Verify a user cannot change `loyaltyPoints`, `artisanVerified`, `role`, or `email` on their profile.
5. Verify a user cannot read another user's private profile.
6. Verify a new account can create its own initial profile, but cannot set initial points above zero.
7. Confirm legitimate product creation succeeds with the actual Android client fields.
8. Confirm no passwords or password hashes are stored in Firestore or Realtime Database. If older builds wrote such values, remove them from Firebase and review access.

Once verified, deploy intentionally:

```powershell
firebase deploy --only firestore:rules --project kasicrafts-5cd37
```

This command replaces the project's deployed Firestore rules. Back up and review existing production rules before deployment.

## Backend work still required

A trusted backend (for example, Cloud Functions using the Admin SDK) must validate actual order completion, award loyalty points idempotently using a unique order ID, and administer artisan verification. Do not let the Android client set these fields. The client currently has no genuine payment or completed-order verification.
