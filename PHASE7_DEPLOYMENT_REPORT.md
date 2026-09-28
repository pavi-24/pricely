# Phase 7: Cloud Deployment Preparation Report

**Project Name**: Pricely — Smart House Price Prediction  
**Package**: `com.pavithran.pricely`  
**Location**: `D:\HousePricePrediction`  
**Date**: September 28, 2026  

---

## 1. Executive Summary

Phase 7 of **Pricely** prepared the FastAPI backend service and native Android application for production deployment on **Render** (as a Render Web Service). The backend was enhanced with dynamic port binding, container path resolution, CORS origin controls, and unpickler module registration for custom transformers (`RareLocationEncoder`, `AvailabilityEncoder`). The Android app was updated with production URL toggling and custom base URL configuration in `RetrofitClient.java`.

---

## 2. Files Created & Modified

| File Name | Absolute Path | Description |
| :--- | :--- | :--- |
| **`requirements.txt`** | `D:\HousePricePrediction\backend\requirements.txt` | Deployment dependencies with version specifications (`fastapi`, `uvicorn`, `pydantic`, `scikit-learn`, `joblib`, `pandas`, `numpy`, `gunicorn`, `httpx`). |
| **`app.py`** | `D:\HousePricePrediction\backend\app.py` | Production-ready FastAPI app with dynamic `$PORT` reading, CORS controls, and robust model loading. |
| **`render.yaml`** | `D:\HousePricePrediction\render.yaml` | Render Blueprint configuration specifying service type, build commands, start commands, and environment variables. |
| **`RetrofitClient.java`** | `D:\HousePricePrediction\app\src\main\java\com\pavithran\pricely\RetrofitClient.java` | Updated Android API client supporting `DEV_BASE_URL` (`http://10.0.2.2:8000/`), `PROD_BASE_URL`, `setUseProduction()`, and `setCustomBaseUrl()`. |
| **`README.md`** | `D:\HousePricePrediction\backend\README.md` | Updated setup, execution, and Render cloud deployment documentation. |

---

## 3. Render Blueprint Configuration

The `render.yaml` specification defines the cloud web service environment:

```yaml
services:
  - type: web
    name: pricely-house-price-api
    env: python
    region: oregon
    plan: free
    buildCommand: pip install -r backend/requirements.txt
    startCommand: uvicorn backend.app:app --host 0.0.0.0 --port $PORT
    healthCheckPath: /health
    autoDeploy: true
    envVars:
      - key: PORT
        value: 8000
      - key: ALLOWED_ORIGINS
        value: "*"
```

---

## 4. Environment & Dependency Specifications

- **Python Runtime Version**: Python `3.10` / `3.11` / `3.12` / `3.13` (Tested on local `3.13.13`).
- **Core ML Dependencies**:
  - `scikit-learn`: `1.4.0+` (Model trained on `1.9.0`)
  - `joblib`: `1.3.0+` (Model trained on `1.5.3`)
  - `pandas`: `2.0.0+` (Model trained on `3.0.5`)
  - `numpy`: `1.26.0+` (Model trained on `2.5.1`)
- **Web Framework**:
  - `fastapi`: `0.110.0+`
  - `uvicorn`: `0.28.0+`
  - `pydantic`: `2.0.0+`
  - `gunicorn`: `21.2.0+`

---

## 5. Model Loading Verification

The Phase 4 serialized pipeline (`house_price_pipeline.joblib`) was verified to load cleanly in a production environment:
1. `backend/app.py` registers `RareLocationEncoder` and `AvailabilityEncoder` into `sys.modules['phase4.train']`, `sys.modules['train']`, and `sys.modules['__main__']`.
2. Model deserializes during app initialization without missing attribute errors.
3. `/health` endpoint exposes model load status, Python runtime version (`3.13.13`), target unit (`Lakhs INR`), and model type (`Gradient Boosting Regressor`).

---

## 6. Local Verification Results

- **Backend API Tests**: Executed `python backend/test_api.py` against live server on port 8000:
  - `GET /`: `200 OK` (`"status": "online"`)
  - `GET /health`: `200 OK` (`"status": "healthy"`, `"model_loaded": true`)
  - `POST /predict` (Whitefield 2 BHK): `200 OK` (`56.16 Lakhs / ₹56,15,690.00`)
  - `POST /predict` (Hebbal 3 BHK): `200 OK` (`110.34 Lakhs / ₹1,10,33,870.00`)
  - `POST /predict` (Validation Errors): `422 Unprocessable Entity` for missing/invalid inputs
- **Android App Build**: Executed `.\gradlew.bat assembleDebug` -> **`BUILD SUCCESSFUL in 13s`**.

---

## 7. Step-by-Step Render Deployment Guide

1. **Push Repository to GitHub / GitLab**:
   Commit all project files, including `phase4/house_price_pipeline.joblib`, `backend/`, and `render.yaml`.
2. **Create New Web Service on Render**:
   - Log into [Render Dashboard](https://dashboard.render.com/).
   - Click **New +** -> **Web Service** or **New +** -> **Blueprint**.
   - Connect your GitHub repository (`HousePricePrediction`).
3. **Configure Service Settings (Manual Setup Option)**:
   - **Name**: `pricely-house-price-api`
   - **Environment**: `Python 3`
   - **Region**: Oregon (or closest region)
   - **Branch**: `main`
   - **Build Command**: `pip install -r backend/requirements.txt`
   - **Start Command**: `uvicorn backend.app:app --host 0.0.0.0 --port $PORT`
4. **Deploy & Verify**:
   - Click **Create Web Service**.
   - Render will build the environment, install dependencies, load `house_price_pipeline.joblib`, and launch Uvicorn.
   - Once live, verify health at `https://<your-service-name>.onrender.com/health`.
5. **Update Android Application**:
   - Copy your public Render URL (e.g., `https://pricely-house-price-api.onrender.com/`).
   - In `RetrofitClient.java`, update `PROD_BASE_URL` and call `RetrofitClient.setUseProduction(true);`.
