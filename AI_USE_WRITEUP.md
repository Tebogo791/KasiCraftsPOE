# AI-Use Write-Up & Citation Statement

**Course:** OPSC6312 — Open Source Coding  
**Application:** KasiCrafts Android Marketplace Application  
**Author:** Tebogo & KasiCrafts Development Team  

---

## 1. Overview of AI Assistance
During the development of the **KasiCrafts** Android application, Artificial Intelligence tools (specifically the Android Studio AI Assistant powered by Google Gemini and LLM developer tooling) were used to assist with code optimization, internationalization translations, layout refinement, unit test generation, and documentation drafting.

---

## 2. Specific Uses of AI in Development

### 2.1 Internationalization (Multi-language Support)
- **Prompting:** AI was utilized to translate standard application strings into isiZulu (`res/values-zu/strings.xml`) and Afrikaans (`res/values-af/strings.xml`), ensuring natural cultural terminology for marketplace categories (e.g., *Izimbiza Nezakhiwo*, *Pottebakkery*, *Tekstiele*).
- **Verification:** All generated string keys were manually audited against `values/strings.xml` to prevent missing resource references and XML compilation errors.

### 2.2 UI Refinement & Material 3 Layouts
- **Prompting:** AI assisted in designing Material Design 3 cards for `item_product.xml`, `item_discover_product.xml`, and `item_cart_product.xml` with high-contrast color palettes (dark background `#0A0A0C` and vibrant South African orange `#FF6D00`).

### 2.3 Automated Testing & Code Verification
- **Prompting:** AI was used to draft unit tests in JUnit 4 (`CartManagerTest` and `ProductFilterTest`) to verify state edge cases, search string matching, and cart subtotal formatting.

---

## 3. Academic Integrity & Transparency Statement

> **Declaration:**  
> All AI-generated suggestions were thoroughly reviewed, edited, and tested by the student developer. Core architectural decisions, Firebase integration, application logic, navigation routing, and project structure were directed and validated manually.

---

## 4. Citation of AI Tools Used

1. **Google Gemini / Android Studio AI Assistant** (2026). *Code refactoring, layout generation, and Kotlin Jetpack component assistance*. Google.
2. **OpenAI ChatGPT** (2026). *Language translation assistance for isiZulu and Afrikaans localization*. OpenAI.
