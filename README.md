KasiCrafts — South African Artisan Marketplace

Module: OPSC6312 — Open Source Coding
Submission: Portfolio of Evidence (PoE)
Platform: Android (Kotlin)
Technologies: Android Views / Jetpack Compose, Firebase Authentication, Cloud Firestore, Firebase Cloud Messaging, Retrofit, Material Design

Project Overview

KasiCrafts is an Android marketplace project designed to connect South African artisans and small-business crafters with prospective buyers. The application presents locally made products, including baskets, pottery, jewelry, and textiles, through a mobile browsing experience.

The project explores account authentication, product discovery, seller listings, shopping-cart interactions, localization, and Firebase-backed services. Some features may require a configured Firebase project or additional testing before production deployment.

Repository

Source code: https://github.com/Tebogo791/KasiCraftsPOE
Clone URL: https://github.com/Tebogo791/KasiCraftsPOE.git

Application Features

|Area                |Description                                                                                                                                           |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
|Account access      |Email sign-in interface, Google sign-in option, registration, and guest browsing.                                                                     |
|Product discovery   |Marketplace product cards, search input, category selection, and location/area filtering controls.                                                    |
|Seller listings     |Form for entering a craft title, price, seller name, area, image URL, and category.                                                                   |
|Shopping cart       |Cart interface showing selected products, delivery charges, and a checkout flow.                                                                      |
|Checkout            |Delivery-address entry and payment-method selection interface. Real payment processing and escrow protection have not been independently verified.    |
|Localization        |English, isiZulu, and Afrikaans interface examples, with language selection in Settings.                                                              |
|Profile and settings|Profile editing, password-change interface, preferred payment method, delivery address, and notification settings.                                    |
|Firebase integration|Project configuration and code intended for authentication, Firestore, and messaging; operation depends on the deployed configuration and permissions.|
|Offline behavior    |Firestore caching and fallback behavior should be validated through a documented offline test.                                                        |

Demonstration Video

KasiCrafts application demonstration:
https://youtu.be/UwONyTmksko?si=K4c4_hlt19-iIzKr

The video is provided as the project demonstration. Review it alongside the source code and test evidence when evaluating functionality.

Build and Setup

1. Clone the repository:
  git clone https://github.com/Tebogo791/KasiCraftsPOE.git
2. Open the cloned project in Android Studio.
3. Allow Gradle synchronization to complete and install the required Android SDK components.
4. Configure Firebase for the application’s actual package name and place the appropriate google-services.json in app/ if the build requires it. Do not commit service-account keys, signing keystores, or passwords.
5. Build the debug application:
  Windows (PowerShell):
  .\gradlew.bat assembleDebug
  macOS/Linux:
  ./gradlew assembleDebug
6. Install the resulting debug APK on a compatible device or test service.

Testing and Code Quality

The project includes or may include local unit-test sources under app/src/test/. The tests and their results should be verified against the final uploaded revision.

To run local unit tests:

Windows:

.\gradlew.bat testDebugUnitTest

macOS/Linux:

./gradlew testDebugUnitTest

To run Android Lint:

Windows:

.\gradlew.bat lintDebug

macOS/Linux:

./gradlew lintDebug

Evidence of successful builds, executed tests, lint findings, and application logs should be captured from actual command output or Android Studio. The presence of a command or test file alone does not establish that the test passed.

Test Areas

• Login input validation and authentication behavior
• Cart addition, removal, and price calculations
• Product search and category/area filtering
• Language switching and screen navigation
• Offline and reconnection behavior, where configured

Version Control and Continuous Integration

The project is hosted on GitHub. GitHub Actions workflow files, when included under .github/workflows/, can automate checks such as compilation, unit testing, and linting. Check the repository’s Actions tab for the actual status of each workflow run; a workflow configuration alone is not evidence of a passing build.

Release and Publication Preparation

The app is intended to be prepared for Google Play distribution. Release preparation includes reviewing permissions and privacy disclosures, generating a signed release APK or Android App Bundle, preparing store assets, and completing relevant Play Console requirements.

A debug APK is not a signed release artifact. Google Play publication or upload should only be reported if independently completed and documented.

Release Notes

Final PoE Revision

• Expanded marketplace browsing and product-discovery interfaces.
• Added or refined category and area filtering controls.
• Added seller listing and cart/checkout interfaces.
• Added profile and account-preference screens.
• Demonstrated English, isiZulu, and Afrikaans Home interfaces.
• Prepared project documentation and submission evidence guidance.

These notes summarize the presented revision; backend transactions, notifications, and production release readiness require separate verification.

AI Assistance Disclosure

AI assistance was used during project development and documentation to support explanations, drafting, troubleshooting, and code suggestions. Generated material should be reviewed, tested, and adapted by the project author. Refer to AI_USE_WRITEUP.md if that document is included in the repository for the full disclosure and citations.

Academic Context

This repository supports the OPSC6312 Portfolio of Evidence submission. Feature descriptions reflect the project design and demonstrated interfaces; final assessment marks and production functionality are determined through testing and lecturer evaluation.
