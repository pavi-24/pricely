# Phase 5: FastAPI Backend Integration Report

**Project Name**: Pricely — Smart House Price Prediction  
**Package**: `com.pavithran.pricely`  
**Location**: `D:\HousePricePrediction`  
**Date**: September 28, 2026  

---

## 1. Executive Summary

Phase 5 of **Pricely** successfully integrated the trained Phase 4 machine learning pipeline (`house_price_pipeline.joblib`) into a production-ready **FastAPI** backend service. The backend exposes RESTful endpoints for API health checks and real-time house price predictions, converting predicted values from Lakhs INR into exact total Rupees (INR).

---

## 2. Existing Model Verification

- **Model File Path**: `D:\HousePricePrediction\phase4\house_price_pipeline.joblib`
- **Metadata Path**: `D:\HousePricePrediction\phase4\model_metadata.json`
- **Model Type**: Gradient Boosting Regressor (Scikit-Learn Pipeline with `RareLocationEncoder` and `AvailabilityEncoder`)
- **Required Input Features (7)**:
  1. `location` (`str`)
  2. `area_type` (`str`)
  3. `availability` (`str`)
  4. `total_sqft_num` (`float`)
  5. `bhk` (`int`)
  6. `bath` (`int`)
  7. `balcony` (`int`)
- **Target Output**: `price` (Lakhs INR).
- **Verification Status**: PASSED (Verified via `phase4/verify_model.py` and live backend inference).

---

## 3. Created Backend Files

All backend artifacts were created under `D:\HousePricePrediction\backend\`:

| File Name | Absolute Path | Description |
| :--- | :--- | :--- |
| **`app.py`** | `D:\HousePricePrediction\backend\app.py` | FastAPI application with CORS middleware, model loader, and Pydantic validation. |
| **`requirements.txt`** | `D:\HousePricePrediction\backend\requirements.txt` | Dependency list (`fastapi`, `uvicorn`, `pydantic`, `scikit-learn`, `joblib`, `pandas`, `numpy`, `httpx`). |
| **`test_api.py`** | `D:\HousePricePrediction\backend\test_api.py` | Automated test suite verifying `GET /`, `GET /health`, `POST /predict` (valid & invalid inputs). |
| **`README.md`** | `D:\HousePricePrediction\backend\README.md` | Setup guide, execution instructions, and API specification. |

---

## 4. API Endpoints Specification

### 1. `GET /`
- **Description**: Returns general API status.
- **Status Code**: `200 OK`
- **Response Payload**:
```json
{
  "status": "online",
  "service": "Pricely - Smart House Price Prediction API",
  "version": "1.0.0",
  "docs_url": "/docs"
}
```

### 2. `GET /health`
- **Description**: Verifies server operational status and confirms whether the Phase 4 ML pipeline is loaded.
- **Status Code**: `200 OK`
- **Response Payload**:
```json
{
  "status": "healthy",
  "model_loaded": true,
  "model_path": "D:\\HousePricePrediction\\phase4\\house_price_pipeline.joblib",
  "target_unit": "Lakhs INR",
  "model_type": "Gradient Boosting Regressor",
  "error": null
}
```

### 3. `POST /predict`
- **Description**: Accepts property input features and outputs predicted house price in Lakhs INR and total INR.
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "location": "Whitefield",
  "area_type": "Super built-up Area",
  "availability": "Ready To Move",
  "total_sqft_num": 1170.0,
  "bhk": 2,
  "bath": 2,
  "balcony": 1
}
```
- **Response Payload (200 OK)**:
```json
{
  "success": true,
  "predicted_price_lakhs": 56.1569,
  "predicted_price_inr": 5615690.0,
  "formatted_price_inr": "₹56.16 Lakhs",
  "currency": "INR",
  "unit": "Lakhs INR",
  "inputs": {
    "location": "Whitefield",
    "area_type": "Super built-up Area",
    "availability": "Ready To Move",
    "total_sqft_num": 1170.0,
    "bhk": 2,
    "bath": 2,
    "balcony": 1
  }
}
```
- **Validation Error (422 Unprocessable Entity)**: Returned automatically for missing fields or invalid constraint values (e.g., negative area sq. ft., zero BHK).

---

## 5. Automated Test Results

The backend test suite (`D:\HousePricePrediction\backend\test_api.py`) was executed against the running Uvicorn server:

```
==================================================
PRICELY — PHASE 5 FASTAPI BACKEND TEST SUITE
==================================================

[TEST 1/5] Testing GET / ...
Status Code: 200 | Status: online -> PASSED!

[TEST 2/5] Testing GET /health ...
Status Code: 200 | Status: healthy | Model Loaded: True -> PASSED!

[TEST 3/5] Testing POST /predict (Valid Sample #1 - Whitefield 2 BHK) ...
Status Code: 200 | Predicted: 56.1569 Lakhs = ₹56,15,690.00 -> PASSED!

[TEST 4/5] Testing POST /predict (Valid Sample #2 - Hebbal 3 BHK) ...
Status Code: 200 | Predicted: 110.3387 Lakhs = ₹1,10,33,870.00 -> PASSED!

[TEST 5/5] Testing POST /predict Validation Errors ...
Missing Field -> 422 Unprocessable Entity -> PASSED!
Invalid sqft <= 0 -> 422 Unprocessable Entity -> PASSED!
Zero BHK -> 422 Unprocessable Entity -> PASSED!

==================================================
ALL API TESTS PASSED SUCCESSFULLY!
==================================================
```

---

## 6. How to Run the Backend Server

```bash
# Navigate to backend directory
cd D:\HousePricePrediction\backend

# Install dependencies
pip install -r requirements.txt

# Start Uvicorn server
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```

---

## 7. Android Integration Specification (Phase 6 Preparation)

- **Base URL for Android Emulator**: `http://10.0.2.2:8000/`
- **Retrofit Client Contract**:
  - Endpoint: `@POST("predict")`
  - Input DTO: `PredictionRequest(location, area_type, availability, total_sqft_num, bhk, bath, balcony)`
  - Output DTO: `PredictionResponse(success, predicted_price_lakhs, predicted_price_inr, formatted_price_inr)`
