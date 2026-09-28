# Phase 6: Android App & ML Backend Integration Report

**Project Name**: Pricely — Smart House Price Prediction  
**Package**: `com.pavithran.pricely`  
**Location**: `D:\HousePricePrediction`  
**Date**: September 28, 2026  

---

## 1. Executive Summary

Phase 6 of **Pricely** successfully connected the native Android application (`com.pavithran.pricely`) with the live FastAPI backend service (`http://10.0.2.2:8000/`). The prediction form was aligned to send the exact 7 features required by the Phase 4 Gradient Boosting model pipeline. Real-time predictions are fetched asynchronously via Retrofit, displayed cleanly on the valuation result screen, and persisted locally in valuation history.

---

## 2. Files Created & Modified

### Created Java & XML Artifacts
1. **`PredictionRequest.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\PredictionRequest.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/PredictionRequest.java) — DTO matching backend JSON schema (7 input fields).
2. **`PredictionResponse.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\PredictionResponse.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/PredictionResponse.java) — DTO for backend prediction response.
3. **`PredictionApiService.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\PredictionApiService.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/PredictionApiService.java) — Retrofit interface for `POST /predict`.
4. **`RetrofitClient.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\RetrofitClient.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/RetrofitClient.java) — Singleton Retrofit client using `http://10.0.2.2:8000/` base URL.
5. **`ValuationItem.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\ValuationItem.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/ValuationItem.java) — Data model for valuation history entries.
6. **`ValuationHistoryManager.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\ValuationHistoryManager.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/ValuationHistoryManager.java) — Local persistence helper using SharedPreferences & Gson.
7. **`ValuationHistoryAdapter.java`**: [`D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\ValuationHistoryAdapter.java`](file:///D:/HousePricePrediction/app/src/main/java/com/pavithran/pricely/ValuationHistoryAdapter.java) — RecyclerView adapter for history logs.
8. **`item_valuation_history.xml`**: [`D:\HousePricePrediction\app\src\main\res\layout\item_valuation_history.xml`](file:///D:/HousePricePrediction/app/src/main/res/layout/item_valuation_history.xml) — History item layout view.

### Modified Android Components
1. **`app/build.gradle`**: Added `retrofit2:2.9.0` and `converter-gson:2.9.0` dependencies.
2. **`AndroidManifest.xml`**: Added `android:usesCleartextTraffic="true"` to application tag for emulator HTTP communication.
3. **`PropertyDetailsActivity.java` & Layout**: Updated input form to capture `location`, `area_type`, `availability`, `total_sqft_num`, `bhk`, `bath`, and `balcony`. Added input validation and Retrofit async execution.
4. **`PredictionResultActivity.java` & Layout**: Updated to display live model valuation (`₹64.59 Lakhs / ₹64,59,300.00`), parameter summary, and ML disclaimer.
5. **`ValuationHistoryActivity.java` & Layout**: Updated to render saved valuation logs in a RecyclerView.

---

## 3. Retrofit API Integration Contract

- **Base URL**: `http://10.0.2.2:8000/` (Android Emulator loopback to host localhost)
- **Endpoint**: `@POST("predict")`
- **Request Format**:
```json
{
  "location": "Whitefield",
  "area_type": "Super built-up Area",
  "availability": "Ready To Move",
  "total_sqft_num": 1200.0,
  "bhk": 2,
  "bath": 2,
  "balcony": 1
}
```
- **Response Format**:
```json
{
  "success": true,
  "predicted_price_lakhs": 64.593,
  "predicted_price_inr": 6459300.0,
  "formatted_price_inr": "₹64.59 Lakhs",
  "currency": "INR",
  "unit": "Lakhs INR"
}
```

---

## 4. Empirical Build & Emulator Verification Results

- **Gradle Build**: `BUILD SUCCESSFUL in 13s` (`.\gradlew.bat assembleDebug`).
- **APK Installation**: `Success` (`adb install -r app-debug.apk` on `emulator-5554`).
- **End-to-End Live Prediction Test**:
  - **User Inputs**: Whitefield | Super built-up Area | Ready To Move | 1200 sq. ft. | 2 BHK | 2 Bath | 1 Balcony.
  - **HTTP Request**: Asynchronously sent to `http://10.0.2.2:8000/predict`.
  - **Live API Output**: `predicted_price_lakhs = 64.593`, `predicted_price_inr = 6459300.0`.
  - **UI Rendered**: `₹64.59 Lakhs` (`Total Valuation: ₹64,59,300.00 (64.59 Lakhs INR)`).
  - **History Log**: Logged entry under `Valuation History` (`Whitefield | ₹64.59 Lakhs | Sep 28, 2026`).

---

## 5. Remaining Considerations

- **Server Availability**: The FastAPI server must be running (`uvicorn backend.app:app --host 127.0.0.1 --port 8000`) on the host machine while running the app on the emulator.
- **Production Deployment**: To deploy to physical Android devices or cloud servers, change `BASE_URL` in `RetrofitClient.java` to the public HTTPS backend URL (e.g., `https://api.pricely.com/`).
