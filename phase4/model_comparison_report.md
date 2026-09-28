# Phase 4 Model Comparison Report

**Project**: Pricely — Smart House Price Prediction  
**Dataset**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`  
**Train Set**: 9441 rows | **Test Set**: 2361 rows  
**Random Seed**: 42  

---

## 1. Candidate Model Performance Summary

| Model Name | 5-Fold CV R² (Mean ± Std) | 5-Fold CV MAE (Lakhs) | 5-Fold CV RMSE (Lakhs) | Test MAE (Lakhs INR) | Test RMSE (Lakhs INR) | Test R² Score |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Linear Regression** | 0.4327 ± 0.0849 | 41.26 | 101.06 | **42.66** | **121.88** | **0.4320** |
| **Ridge Regression** | 0.4329 ± 0.0850 | 41.14 | 101.04 | **42.57** | **121.87** | **0.4320** |
| **Decision Tree** | 0.4479 ± 0.1493 | 35.11 | 97.82 | **38.66** | **123.78** | **0.4141** |
| **Random Forest** | 0.5981 ± 0.0680 | 32.40 | 84.42 | **35.21** | **111.95** | **0.5207** |
| **Gradient Boosting** | 0.5778 ± 0.0765 | 32.53 | 86.71 | **35.23** | **113.21** | **0.5099** |

---

## 2. Selected Model & Justification
- **Selected Model**: `Random Forest`
- **Reason for Selection**:
  - Highest overall R² score on unseen test data (0.5207).
  - Lowest Root Mean Squared Error on test set (111.95 Lakhs INR).
  - Strong generalizability across 5-fold cross-validation (0.5981 ± 0.0680).
  - Effectively captures non-linear relationships between square footage, location, bathrooms, and property prices.

---

## 3. Saved Pipeline Artifact
- **Path**: `D:\HousePricePrediction\phase4\house_price_pipeline.joblib`
- **Contents**: Full scikit-learn Pipeline incorporating `ColumnTransformer` (rare location encoder, availability encoder, one-hot encoder, standard scaler) and trained `Random Forest` regressor.
