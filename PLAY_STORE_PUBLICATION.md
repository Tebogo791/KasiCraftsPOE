# Google Play Store Publication & Deployment Preparation

**Application Name:** KasiCrafts — Authentic South African Marketplace  
**Package Name:** `com.example.myapplication` (Production Target: `co.za.kasicrafts.app`)  
**Target SDK:** 34 (Android 14)  
**Minimum SDK:** 26 (Android 8.0 Oreo)  

---

## 1. Store Listing Metadata

### App Title
`KasiCrafts — South African Artisan Marketplace`

### Short Description (80 characters)
`Discover and buy authentic handcrafted South African crafts from local artisans.`

### Full Description
> KasiCrafts is a digital marketplace celebrating authentic South African artistry. Connect directly with local crafters, explore hand-woven Zulu baskets, carved wooden sculptures, vibrant beaded jewelry, and traditional textiles.  
>  
> Key Features:  
> • Browse authentic African crafts with location filtering  
> • Secure escrow buyer protection  
> • Multi-language support (English, isiZulu, Afrikaans)  
> • Real-time marketplace updates and seller listing uploads  
> • Google Single Sign-On and email authentication  

---

## 2. Required Graphic Assets Checklist

| Asset Type | Specifications | Status |
| :--- | :--- | :--- |
| **App Icon** | 512 x 512 px PNG (High-res, 32-bit PNG) | Ready (`ic_launcher.webp`) |
| **Feature Graphic** | 1024 x 500 px JPG/PNG | Ready (`kasi_crafts_banner.webp`) |
| **Phone Screenshots** | Minimum 2 screenshots (1080 x 1920 px or 16:9 aspect ratio) | Home, Discover, Cart, & Settings screens captured |
| **Category** | Shopping / Lifestyle | Selected |
| **Content Rating** | General (Everyone) | Questionnaire completed |

---

## 3. Release Build & APK / AAB Preparation Steps

1. **Generate Signed Android App Bundle (AAB):**
   ```bash
   ./gradlew app:bundleRelease
   ```
2. **ProGuard / R8 Optimization & Keep Rules:**
   - Obfuscation and code shrinking enabled in `build.gradle.kts`.
   - Custom keep rules configured in `app/src/main/keepRules/rules.keep` for Firebase models and Retrofit data classes.

3. **Privacy Policy Link:**
   - Hosted at: `https://kasicrafts.web.app/privacy-policy.html`
