# Phase 4: Machine Learning Model Training & Evaluation Report

**Project Name**: Pricely — Smart House Price Prediction  
**Package**: `com.pavithran.pricely`  
**Location**: `D:\HousePricePrediction`  
**Date**: September 28, 2026  

---

## 1. Dataset Details
- **Dataset File**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`
- **Dataset Format**: CSV (Comma-Separated Values)
- **Total Cleaned Records**: 11,802 verified property listings in Bengaluru, India.
- **Total Columns**: 11 columns (7 active features, 1 target label, 3 intermediate/excluded columns).
- **Missing Values**: 0 missing values across all active columns.
- **Duplicates**: 0 duplicate rows.

---

## 2. Target & Selected Features

### Target Label (`y`)
- **Column**: `price`
- **Data Type**: `float64`
- **Unit**: **Lakhs INR** (1 Lakh = 100,000 INR = ₹1,00,000).
- **Nature of Target**: Represents **total property price** in Lakhs INR (e.g. 109.35 Lakhs = ₹1,09,35,000 = ₹1.0935 Crores). It is **NOT** price per square foot.

### Selected Input Features (`X`)
1. `location` (`str`): Locality / neighborhood name in Bengaluru.
2. `area_type` (`str`): Type of area layout ('Super built-up Area', 'Plot Area', 'Built-up Area', 'Carpet Area').
3. `availability` (`str`): Possession status ('Ready To Move' vs specific completion dates).
4. `total_sqft_num` (`float64`): Total built-up property area in square feet.
5. `bhk` (`int64`): Standardized integer count of bedrooms.
6. `bath` (`int64`): Standardized integer count of bathrooms.
7. `balcony` (`int64`): Standardized integer count of balconies.

### Target Leakage Protection & Excluded Columns
- **`price_per_sqft`**: **EXCLUDED FROM TRAINING**. Calculated as `(price * 100000) / total_sqft_num`. Directly derived from the target price label. Excluding it prevents target leakage.
- **`size`**: Excluded (replaced by numeric `bhk`).
- **`total_sqft`**: Excluded (replaced by numeric `total_sqft_num`).

---

## 3. Preprocessing Steps
All preprocessing operations were encapsulated within a scikit-learn `ColumnTransformer` and fit **exclusively on the training dataset** to prevent data leakage:

1. **Rare Location Encoding (`RareLocationEncoder`)**:
   - Localities with frequency $\le 10$ in `X_train` were grouped into `'other'`.
   - Reduces raw locality cardinality from 1,197 to ~215 robust high-frequency locations, preventing overfitting and sparse dimensionality.
2. **Availability Standardizing (`AvailabilityEncoder`)**:
   - Encodes possession status into two primary operational states: `'Ready To Move'` and `'Under Construction'`.
3. **Categorical One-Hot Encoding (`OneHotEncoder`)**:
   - `handle_unknown='ignore'`, `sparse_output=False` applied to `location`, `area_type`, and `availability`.
4. **Numerical Feature Imputation & Scaling**:
   - `SimpleImputer(strategy='median')` for numerical columns.
   - `StandardScaler()` applied to standardise `total_sqft_num`, `bhk`, `bath`, and `balcony`.

---

## 4. Training & Testing Split
- **Split Ratio**: 80% Training set, 20% Held-out Testing set.
- **Random Seed**: `42` (reproducible).
- **Training Set Size**: 9,441 property samples (`X_train`, `y_train`).
- **Testing Set Size**: 2,361 property samples (`X_test`, `y_test`).
- **Validation Strategy**: 5-Fold Cross-Validation on training data (`X_train`) for candidate selection, followed by final evaluation on the isolated test set (`X_test`).

---

## 5. Models Evaluated & Measured Metrics

| Model Name | 5-Fold CV R² (Mean ± Std) | 5-Fold CV MAE (Lakhs) | 5-Fold CV RMSE (Lakhs) | Test MAE (Lakhs INR) | Test RMSE (Lakhs INR) | Test R² Score |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Linear Regression** | 0.6976 ± 0.0163 | 26.50 | 51.52 | 25.80 | 45.45 | 0.7410 |
| **Ridge Regression** | 0.6978 ± 0.0164 | 26.47 | 51.50 | 25.79 | 45.45 | 0.7410 |
| **Decision Tree Regressor** | 0.6699 ± 0.0195 | 23.52 | 53.79 | 23.36 | 47.46 | 0.7177 |
| **Random Forest Regressor** | 0.7381 ± 0.0175 | 20.93 | 47.94 | 20.58 | 43.32 | 0.7647 |
| **Gradient Boosting Regressor** | **0.7720 ± 0.0194** | **20.67** | **44.73** | **20.25** | **39.37** | **0.8056** |

---

## 6. Selected Model & Justification
- **Selected Model**: **Gradient Boosting Regressor** (`n_estimators=150`, `learning_rate=0.1`, `max_depth=5`, `random_state=42`).
- **Selection Rationale**:
  - **Highest Test R² Score**: **0.8056** (explains 80.56% of variance in unseen property prices).
  - **Lowest Test RMSE**: **39.37 Lakhs INR** (significantly penalizes large estimation errors).
  - **Lowest Test MAE**: **20.25 Lakhs INR** (average absolute prediction error on real-world test properties).
  - **Superior Generalizability**: Demonstrated the highest average cross-validation score (**0.7720 R²**) with minimal variance across folds ($\pm 0.0194$).

---

## 7. Saved Artifact Paths

| Artifact Description | Local File Path | Size |
| :--- | :--- | :--- |
| **Serialized Model Pipeline** | `D:\HousePricePrediction\phase4\house_price_pipeline.joblib` | ~13.98 MB |
| **Model Metadata (JSON)** | `D:\HousePricePrediction\phase4\model_metadata.json` | ~2.88 KB |
| **Model Comparison Report** | `D:\HousePricePrediction\phase4\model_comparison_report.md` | ~1.74 KB |
| **Dataset Inspection Report** | `D:\HousePricePrediction\phase4\dataset_inspection_report.md` | ~4.24 KB |
| **Reproducible Training Script** | `D:\HousePricePrediction\phase4\train.py` | ~10.72 KB |
| **Verification Script** | `D:\HousePricePrediction\phase4\verify_model.py` | ~2.45 KB |

---

## 8. Metric Interpretations in Business Context
- **Mean Absolute Error (MAE = 20.25 Lakhs INR)**: On average, the model's price prediction for a Bengaluru property deviates from the actual listed market price by ~₹20.25 Lakhs. For a median property priced at ₹70 Lakhs, this represents a realistic baseline margin for automated valuation.
- **Root Mean Squared Error (RMSE = 39.37 Lakhs INR)**: The higher weight placed on larger errors reflects high-end luxury villas and plots (where prices exceed ₹5–15 Crores).
- **R² Score (0.8056)**: 80.56% of property price variance in Bengaluru is accounted for by the combination of square footage, locality, bedroom/bathroom counts, balcony availability, and area type layout.

---

## 9. Limitations & Next Steps for Android Integration

### Limitations
1. **Locality Generalization**: Properties in ultra-rare localities (<= 10 dataset entries) are mapped to `'other'`. While preventing model overfitting, predictions for obscure localities revert to city-wide area averages.
2. **Inflation / Time Series**: Dataset prices reflect static market listings without temporal inflation indexing.

### Next Steps (Phase 5 Roadmap — FastAPI Backend & ONNX / REST Integration)
1. **FastAPI Service**: Wrap `house_price_pipeline.joblib` in a lightweight Python FastAPI backend service (`d:\HousePricePrediction\backend\app.py`) providing a `/predict` endpoint.
2. **Android Retrofit Client**: Update `com.pavithran.pricely` Android app with Retrofit REST client to send user property inputs (Location, Area sq. ft., BHK, Bathrooms, Balconies) to the local Uvicorn backend (`http://10.0.2.2:8000/predict`).
3. **ONNX Export Option**: Optionally convert the scikit-learn pipeline to ONNX (`.onnx`) format using `skl2onnx` if offline on-device inference via ONNX Runtime for Android is required.
