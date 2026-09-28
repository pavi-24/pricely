import os
import sys
import logging
from typing import Dict, Any, Optional
import joblib
import pandas as pd
import numpy as np
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field, field_validator

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
logger = logging.getLogger("pricely-backend")

# Setup project and phase4 import paths (robust for local & cloud containers)
BACKEND_DIR = os.path.dirname(os.path.abspath(__file__))
BASE_DIR = os.path.dirname(BACKEND_DIR)
PHASE4_DIR = os.path.join(BASE_DIR, "phase4")

# Fallback path checking if running inside a container or flat directory
if not os.path.exists(PHASE4_DIR):
    PHASE4_DIR = os.path.join(os.getcwd(), "phase4")
    BASE_DIR = os.getcwd()

MODEL_PATH = os.path.join(PHASE4_DIR, "house_price_pipeline.joblib")
METADATA_PATH = os.path.join(PHASE4_DIR, "model_metadata.json")

for p in [BASE_DIR, PHASE4_DIR, BACKEND_DIR]:
    if p not in sys.path:
        sys.path.insert(0, p)

# Import and register custom transformer classes so joblib unpickling succeeds
try:
    import train
    sys.modules['phase4.train'] = train
    sys.modules['train'] = train
    from train import RareLocationEncoder, AvailabilityEncoder

    # Inject into __main__ for unpickler compatibility
    import __main__
    setattr(__main__, 'RareLocationEncoder', RareLocationEncoder)
    setattr(__main__, 'AvailabilityEncoder', AvailabilityEncoder)

    logger.info("Custom transformer modules ('RareLocationEncoder', 'AvailabilityEncoder') registered successfully.")
except Exception as e:
    logger.error(f"Failed to import custom transformers from phase4: {e}")

# Global variables for model state
model_pipeline: Optional[Any] = None
model_loaded: bool = False
model_error: Optional[str] = None

def load_model():
    global model_pipeline, model_loaded, model_error
    logger.info(f"Attempting to load model from: {MODEL_PATH}")
    if not os.path.exists(MODEL_PATH):
        model_error = f"Model artifact not found at {MODEL_PATH}"
        model_loaded = False
        logger.error(model_error)
        return

    try:
        model_pipeline = joblib.load(MODEL_PATH)
        model_loaded = True
        model_error = None
        logger.info("Model pipeline loaded successfully into memory.")
    except Exception as e:
        model_loaded = False
        model_error = str(e)
        logger.error(f"Failed to load model pipeline: {e}")

# Load model upon module initialization
load_model()

# Initialize FastAPI App
app = FastAPI(
    title="Pricely - House Price Prediction API",
    description="Production-ready FastAPI Backend for predicting Indian house prices using trained scikit-learn Gradient Boosting model.",
    version="1.0.0"
)

# Enable CORS with configurable origins
allowed_origins_raw = os.environ.get("ALLOWED_ORIGINS", "*")
allowed_origins = [origin.strip() for origin in allowed_origins_raw.split(",") if origin.strip()]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Request Data Model
class HousePredictionRequest(BaseModel):
    location: str = Field(..., description="Locality or neighborhood name in Bengaluru (e.g. 'Whitefield')", min_length=1)
    area_type: str = Field(..., description="Type of area layout e.g., 'Super built-up Area', 'Plot Area', 'Built-up Area', 'Carpet Area'")
    availability: str = Field(..., description="Possession status e.g., 'Ready To Move' or completion timeline")
    total_sqft_num: float = Field(..., gt=0, description="Total built-up property area in sq. ft. (must be > 0)")
    bhk: int = Field(..., ge=1, le=20, description="Number of bedrooms (BHK, must be between 1 and 20)")
    bath: int = Field(..., ge=1, le=20, description="Number of bathrooms (must be between 1 and 20)")
    balcony: int = Field(0, ge=0, le=10, description="Number of balconies (must be between 0 and 10)")

    @field_validator("location", "area_type", "availability")
    @classmethod
    def strip_whitespace(cls, v: str) -> str:
        if isinstance(v, str):
            v_stripped = v.strip()
            if not v_stripped:
                raise ValueError("String fields cannot be empty or blank")
            return v_stripped
        return v

# Response Data Models
class HousePredictionResponse(BaseModel):
    success: bool
    predicted_price_lakhs: float
    predicted_price_inr: float
    formatted_price_inr: str
    currency: str = "INR"
    unit: str = "Lakhs INR"
    inputs: Dict[str, Any]

class HealthStatusResponse(BaseModel):
    status: str
    model_loaded: bool
    model_path: str
    target_unit: str = "Lakhs INR"
    model_type: str = "Gradient Boosting Regressor"
    python_version: str = sys.version.split(" ")[0]
    error: Optional[str] = None

@app.get("/", summary="Root Endpoint")
def get_root():
    """
    Returns simple API status message.
    """
    return {
        "status": "online",
        "service": "Pricely - Smart House Price Prediction API",
        "version": "1.0.0",
        "docs_url": "/docs"
    }

@app.get("/health", response_model=HealthStatusResponse, summary="Health Check")
def get_health():
    """
    Returns backend health status and model load state.
    """
    if not model_loaded:
        return HealthStatusResponse(
            status="unhealthy",
            model_loaded=False,
            model_path=MODEL_PATH,
            error=model_error or "Model failed to load"
        )
    return HealthStatusResponse(
        status="healthy",
        model_loaded=True,
        model_path=MODEL_PATH,
        error=None
    )

@app.post("/predict", response_model=HousePredictionResponse, summary="Predict House Price")
def predict_house_price(request: HousePredictionRequest):
    """
    Predicts house price based on 7 input features:
    - location (str)
    - area_type (str)
    - availability (str)
    - total_sqft_num (float)
    - bhk (int)
    - bath (int)
    - balcony (int)

    Returns predicted price in Lakhs INR and converted total INR.
    """
    if not model_loaded or model_pipeline is None:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"Machine learning model is not available. Error: {model_error}"
        )

    try:
        # Construct DataFrame matching exact feature column names expected by pipeline
        input_data = {
            "location": [request.location],
            "area_type": [request.area_type],
            "availability": [request.availability],
            "total_sqft_num": [request.total_sqft_num],
            "bhk": [request.bhk],
            "bath": [request.bath],
            "balcony": [request.balcony]
        }
        input_df = pd.DataFrame(input_data)

        # Run model inference
        predicted_lakhs = float(model_pipeline.predict(input_df)[0])

        # Floor prediction at 0 in rare edge cases of negative outputs
        predicted_lakhs = max(0.0, round(predicted_lakhs, 4))

        # 1 Lakh = 100,000 INR
        predicted_inr = round(predicted_lakhs * 100000.0, 2)

        # Format human readable INR representation
        if predicted_inr >= 10000000:
            formatted = f"₹{predicted_inr / 10000000:.2f} Crores"
        elif predicted_inr >= 100000:
            formatted = f"₹{predicted_inr / 100000:.2f} Lakhs"
        else:
            formatted = f"₹{predicted_inr:,.2f}"

        return HousePredictionResponse(
            success=True,
            predicted_price_lakhs=predicted_lakhs,
            predicted_price_inr=predicted_inr,
            formatted_price_inr=formatted,
            currency="INR",
            unit="Lakhs INR",
            inputs=request.model_dump()
        )

    except Exception as e:
        logger.error(f"Prediction error: {e}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"An error occurred during price prediction: {str(e)}"
        )

if __name__ == "__main__":
    import uvicorn
    port = int(os.environ.get("PORT", 8000))
    logger.info(f"Starting Uvicorn server on 0.0.0.0:{port}")
    uvicorn.run("app:app", host="0.0.0.0", port=port, reload=False)
