# Pricely — FastAPI Backend Service

This directory contains the Python FastAPI backend service for **Pricely — Smart House Price Prediction**. It loads the serialized trained model artifact from Phase 4 (`phase4/house_price_pipeline.joblib`) and exposes RESTful endpoints for real-time house price predictions.

---

## Directory Structure

```
backend/
├── app.py              # Main FastAPI application & endpoint definitions
├── requirements.txt    # Python dependencies for backend service
└── README.md           # Setup, execution, and API documentation
```

---

## Prerequisites & Installation

Ensure Python 3.9+ is installed.

```bash
# 1. Navigate to backend directory
cd D:\HousePricePrediction\backend

# 2. Install dependencies
pip install -r requirements.txt
```

---

## Running the Server

Start the Uvicorn ASGI server:

```bash
# From D:\HousePricePrediction\backend
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```

The service will be accessible at:
- Local URL: `http://127.0.0.1:8000` or `http://localhost:8000`
- Android Emulator URL: `http://10.0.2.2:8000`
- Swagger Interactive Documentation: `http://127.0.0.1:8000/docs`
- ReDoc Documentation: `http://127.0.0.1:8000/redoc`

---

## API Endpoints Summary

### 1. `GET /`
- **Description**: Returns general service status and API version.
- **Response**:
```json
{
  "status": "online",
  "service": "Pricely - Smart House Price Prediction API",
  "version": "1.0.0",
  "docs_url": "/docs"
}
```

### 2. `GET /health`
- **Description**: Verifies server health and confirms whether the Phase 4 trained ML pipeline (`house_price_pipeline.joblib`) is loaded.
- **Response**:
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
- **Description**: Accepts 7 property input features and outputs predicted house price in Lakhs INR and total INR.
- **Request Body**:
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
- **Success Response (200 OK)**:
```json
{
  "success": true,
  "predicted_price_lakhs": 52.4512,
  "predicted_price_inr": 5245120.0,
  "formatted_price_inr": "₹52.45 Lakhs",
  "currency": "INR",
  "unit": "Lakhs INR",
  "inputs": {
    "location": "Whitefield",
    "area_type": "Super built-up Area",
    "availability": "Ready To Move",
    "total_sqft_num": 1200.0,
    "bhk": 2,
    "bath": 2,
    "balcony": 1
  }
}
```
- **Validation Error Response (422 Unprocessable Entity)**:
Returned if any input field is missing or out of valid constraints (e.g. `total_sqft_num <= 0`, negative BHK/bath, empty location string).

---

## Android Emulator Integration Note

When connecting from the Android App (`com.pavithran.pricely`) running inside the official Android Studio Emulator, use `http://10.0.2.2:8000` as the Retrofit `baseUrl`. `10.0.2.2` is a special alias in the Android emulator that routes directly to `127.0.0.1` on the development host computer.
