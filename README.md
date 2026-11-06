# KasiCrafts — South African Artisan Marketplace

**Course:** OPSC6312 — Open Source Coding (Part 3 POE Submission)

**Development Language:** Kotlin

**UI Technology:** XML Layouts (Android Views)

**Technology Stack:** Kotlin, XML, Material Design, Firebase Authentication, Cloud Firestore, Firebase Cloud Messaging (FCM), Retrofit REST API, Gradle and GitHub Actions.

## Project Links

- **GitHub Repository:** https://github.com/Tebogo791/KasiCraftsPOE
- **YouTube Demonstration:** https://youtu.be/UwONyTmksko
- **Branch:** main

---

## 1. Project Overview

**KasiCrafts** is a native Android marketplace application designed to connect South African artisans and small business owners with local and international buyers.

The application allows users to discover handmade products such as baskets, pottery, jewelry and textiles, search for products, manage shopping carts, and place orders.

KasiCrafts aims to support local entrepreneurship by giving artisans a digital platform to showcase their products.

The application is developed using **Kotlin for functionality and XML layouts for the user interface**, following the OPSC6312 Part 3 assessment requirements.

## 2. Application Features

| Feature | Implementation |
|---|---|
| User Authentication | Email/password authentication and Google Sign-In using Firebase Authentication |
| Offline Mode | Firestore local persistence and synchronization when internet connectivity returns |
| Push Notifications | Firebase Cloud Messaging with Android notification permission handling |
| Multilingual Support | English, isiZulu and Afrikaans |
| Marketplace Search | Product keyword, category and location filtering |
| Shopping Cart | Add products, remove products and calculate totals |
| Checkout | Delivery address entry, delivery fee calculation and order confirmation |
| Database | Cloud Firestore NoSQL database |
| REST API | Retrofit-based API integration; remote hosting must be verified |
| User Interface | Kotlin and XML layouts using Android Views |
| Testing | Local JUnit tests and GitHub Actions workflow |

### Feature 4: Marketplace Search and Filtering

Users can search for products by keyword, filter products by category, and discover products based on seller location.

Categories include baskets, pottery, jewelry and textiles.

### Feature 5: Shopping Cart and Checkout

Users can add products to their shopping carts, remove items, calculate subtotals, enter delivery information and confirm orders.

A delivery fee of R65.00 is included in the checkout calculations.

**Assessment note:** Features 4 and 5 must be additional features not previously submitted as the three features for Part 2. Their eligibility must be checked against the Part 2 submission.

## 3. Supported Languages

KasiCrafts supports three South African languages:

1. English (`en`)
2. isiZulu (`zu`)
3. Afrikaans (`af`)

Users can select their preferred language through the application's Settings screen.

The multilingual implementation must be demonstrated across the assessed screens, including navigation, authentication, shopping and settings.

## 4. Database and REST API

### Firebase Cloud Firestore

Cloud Firestore provides NoSQL storage for application data, including product listings, users and orders.

Firestore's offline persistence supports locally cached data and synchronization when connectivity becomes available.

### REST API

The application uses Retrofit for REST API integration.

Any student-created REST API must be deployed to a remotely accessible hosting environment. A localhost-only API is not acceptable for the final assessment.

**Verification required:** Confirm the hosted API endpoint and demonstrate successful requests from the Android application without a locally running server.

## 5. Automated Testing

Automated tests are located in:

`app/src/test/java/com/example/myapplication/`

Existing test classes include:

- `LoginValidationTest`
- `CartManagerTest`
- `ProductFilterTest`

The final submission must demonstrate at least five documented test cases, including valid and invalid input scenarios.

Tests should contain comments explaining the purpose of each test.

### Run Unit Tests

```bash
./gradlew app:testDebugUnitTest
```

On Windows:

```powershell
.\gradlew.bat app:testDebugUnitTest
```

### Continuous Integration

The GitHub Actions workflow must execute successfully and demonstrate automated project validation or testing.

Workflow files are located in:

`.github/workflows/`

## 6. GitHub Version Control

**Repository:** https://github.com/Tebogo791/KasiCraftsPOE

**Main Branch:** `main`

The project uses Git and GitHub for version control.

The assessment guidance recommends at least 10 meaningful commits distributed across at least three days, demonstrating progressive development rather than one large upload.

Commit history and workflow execution can be inspected through the GitHub repository.

## 7. Build and Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/Tebogo791/KasiCraftsPOE.git
```

2. Open the project in Android Studio.
3. Allow Gradle to synchronize.
4. Ensure the Firebase configuration file `app/google-services.json` is configured for the correct Firebase project.
5. Select an Android device or emulator.
6. Build and run the application.

For final assessment, the application must also be demonstrated on a physical Android device.

## 8. User Interface Design

KasiCrafts uses Android XML layouts with Kotlin-based application logic.

The interface includes:

- Dark theme with orange accent colors
- Product cards and category navigation
- Authentication and registration screens
- Product search and filtering
- Shopping cart and checkout screens
- Settings and language selection

**Primary background:** `#0A0A0C`

**Accent color:** `#FF6D00`

## 9. Google Play Store Preparation

Publication preparation documentation is available in:

`PLAY_STORE_PUBLICATION.md`

This includes store listing information, graphic asset guidance and release preparation.

The document describes publication preparation and does not by itself establish that the application has been published.

## 10. AI Usage Declaration

AI-assisted development and documentation are described in:

`AI_USE_WRITEUP.md`

This document records AI assistance, development activities and relevant academic citations.

## 11. Demonstration Video

**Watch the KasiCrafts Android Application Demonstration:**

https://youtu.be/UwONyTmksko

The demonstration video is intended to show the actual application running, including its core features and functionality.

The final demonstration should provide evidence of authentication, offline behavior, notifications, multilingual support, additional features, and successful operation on an Android device.

The video must demonstrate the application itself, rather than presentation slides or AI-generated demonstrations.

---

**Project:** KasiCrafts — South African Artisan Marketplace

**Module:** OPSC6312 — Open Source Coding

**Submission:** Part 3 — Final Portfolio of Evidence

**GitHub:** https://github.com/Tebogo791/KasiCraftsPOE

**YouTube:** https://youtu.be/UwONyTmksko
