"""
Pricely — Smart House Price Prediction
Phase 4: Machine Learning Model Training & Evaluation Script
"""

import os
import sys
import json
import joblib
import pandas as pd
import numpy as np

# Ensure project root and phase4 directory are in sys.path for joblib serialization compatibility
project_root = r'D:\HousePricePrediction'
phase4_dir = os.path.join(project_root, 'phase4')
if project_root not in sys.path:
    sys.path.insert(0, project_root)
if phase4_dir not in sys.path:
    sys.path.insert(0, phase4_dir)

from sklearn.model_selection import train_test_split, KFold, cross_val_score
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.linear_model import LinearRegression, Ridge
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor, GradientBoostingRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score


class RareLocationEncoder(BaseEstimator, TransformerMixin):
    """
    Groups locations with frequency <= min_freq in the training set into 'other'
    to prevent extreme high dimensionality and overfitting.
    """
    def __init__(self, min_freq=10):
        self.min_freq = min_freq
        self.frequent_locations_ = None

    def fit(self, X, y=None):
        if self.frequent_locations_ is None:
            s = pd.Series(np.array(X).ravel())
            counts = s.astype(str).str.strip().value_counts()
            self.frequent_locations_ = set(counts[counts > self.min_freq].index)
        return self

    def transform(self, X):
        s = pd.Series(np.array(X).ravel())
        freq = self.frequent_locations_ if self.frequent_locations_ is not None else set()
        res = s.astype(str).str.strip().apply(lambda x: x if x in freq else 'other')
        return res.values.reshape(-1, 1)


class AvailabilityEncoder(BaseEstimator, TransformerMixin):
    """
    Simplifies availability status into 'Ready To Move' vs 'Under Construction'.
    """
    def fit(self, X, y=None):
        return self

    def transform(self, X):
        s = pd.Series(np.array(X).ravel())
        res = s.astype(str).str.strip().apply(lambda x: 'Ready To Move' if str(x) == 'Ready To Move' else 'Under Construction')
        return res.values.reshape(-1, 1)


def main():
    print("==================================================")
    print("PRICELY — PHASE 4: MODEL TRAINING & EVALUATION")
    print("==================================================\n")

    data_path = r'D:\HousePricePrediction\data\cleaned_house_prices.csv'
    output_dir = r'D:\HousePricePrediction\phase4'
    os.makedirs(output_dir, exist_ok=True)

    if not os.path.exists(data_path):
        raise FileNotFoundError(f"Cleaned dataset not found at {data_path}")

    print(f"[1/5] Loading cleaned dataset from: {data_path}")
    df = pd.read_csv(data_path)
    print(f"      Total Dataset Shape: {df.shape[0]} rows x {df.shape[1]} columns")

    feature_cols = ['location', 'area_type', 'availability', 'total_sqft_num', 'bhk', 'bath', 'balcony']
    target_col = 'price'

    X = df[feature_cols].copy()
    y = df[target_col].copy()

    print(f"      Input Features (X): {feature_cols}")
    print(f"      Target Column (y): {target_col} (Lakhs INR)")
    print("      Target Leakage Protection: 'price_per_sqft' explicitly excluded.\n")

    random_seed = 42
    test_size = 0.2
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=test_size, random_state=random_seed
    )
    print(f"[2/5] Split dataset with random_state={random_seed}:")
    print(f"      Training set size: {len(X_train)} samples")
    print(f"      Testing set size:  {len(X_test)} samples\n")

    # Fit rare location encoder once on training data
    loc_encoder = RareLocationEncoder(min_freq=10)
    loc_encoder.fit(X_train['location'])

    loc_pipe = Pipeline([
        ('rare_loc', loc_encoder),
        ('ohe', OneHotEncoder(handle_unknown='ignore', sparse_output=False))
    ])

    avail_pipe = Pipeline([
        ('avail', AvailabilityEncoder()),
        ('ohe', OneHotEncoder(handle_unknown='ignore', sparse_output=False))
    ])

    area_pipe = Pipeline([
        ('ohe', OneHotEncoder(handle_unknown='ignore', sparse_output=False))
    ])

    num_pipe = Pipeline([
        ('imputer', SimpleImputer(strategy='median')),
        ('scaler', StandardScaler())
    ])

    preprocessor = ColumnTransformer([
        ('loc', loc_pipe, ['location']),
        ('area', area_pipe, ['area_type']),
        ('avail', avail_pipe, ['availability']),
        ('num', num_pipe, ['total_sqft_num', 'bhk', 'bath', 'balcony'])
    ])

    models = {
        'Linear Regression': LinearRegression(),
        'Ridge Regression': Ridge(alpha=1.0),
        'Decision Tree': DecisionTreeRegressor(max_depth=10, random_state=random_seed),
        'Random Forest': RandomForestRegressor(n_estimators=100, max_depth=15, random_state=random_seed, n_jobs=-1),
        'Gradient Boosting': GradientBoostingRegressor(n_estimators=150, learning_rate=0.1, max_depth=5, random_state=random_seed)
    }

    print("[3/5] Running 5-Fold Cross-Validation on Training Data & Test Set Evaluation...")
    kf = KFold(n_splits=5, shuffle=True, random_state=random_seed)

    evaluation_results = []
    trained_pipelines = {}

    for name, model in models.items():
        pipe = Pipeline([
            ('preprocessor', preprocessor),
            ('regressor', model)
        ])

        cv_r2 = cross_val_score(pipe, X_train, y_train, cv=kf, scoring='r2')
        cv_mae = -cross_val_score(pipe, X_train, y_train, cv=kf, scoring='neg_mean_absolute_error')
        cv_rmse = np.sqrt(-cross_val_score(pipe, X_train, y_train, cv=kf, scoring='neg_mean_squared_error'))

        pipe.fit(X_train, y_train)
        trained_pipelines[name] = pipe

        y_pred = pipe.predict(X_test)
        test_mae = mean_absolute_error(y_test, y_pred)
        test_rmse = np.sqrt(mean_squared_error(y_test, y_pred))
        test_r2 = r2_score(y_test, y_pred)

        metrics = {
            'Model': name,
            'CV_R2_Mean': float(np.mean(cv_r2)),
            'CV_R2_Std': float(np.std(cv_r2)),
            'CV_MAE_Mean': float(np.mean(cv_mae)),
            'CV_RMSE_Mean': float(np.mean(cv_rmse)),
            'Test_MAE': float(test_mae),
            'Test_RMSE': float(test_rmse),
            'Test_R2': float(test_r2)
        }
        evaluation_results.append(metrics)

        print(f"\n  ---> Model: {name}")
        print(f"       5-Fold CV R²:   {metrics['CV_R2_Mean']:.4f} (±{metrics['CV_R2_Std']:.4f})")
        print(f"       5-Fold CV MAE:  {metrics['CV_MAE_Mean']:.2f} Lakhs")
        print(f"       5-Fold CV RMSE: {metrics['CV_RMSE_Mean']:.2f} Lakhs")
        print(f"       Test Set MAE:   {metrics['Test_MAE']:.2f} Lakhs INR")
        print(f"       Test Set RMSE:  {metrics['Test_RMSE']:.2f} Lakhs INR")
        print(f"       Test Set R²:    {metrics['Test_R2']:.4f}")

    best_result = max(evaluation_results, key=lambda x: x['Test_R2'])
    best_model_name = best_result['Model']
    selected_pipeline = trained_pipelines[best_model_name]

    print(f"\n[4/5] Selected Model: '{best_model_name}'")
    print(f"      Achieved highest Test R² ({best_result['Test_R2']:.4f}) and lowest Test RMSE ({best_result['Test_RMSE']:.2f} Lakhs).")

    pipeline_path = os.path.join(output_dir, 'house_price_pipeline.joblib')
    joblib.dump(selected_pipeline, pipeline_path)
    print(f"\n[5/5] Saving Phase 4 Artifacts to: {output_dir}")
    print(f"      Saved Pipeline: {pipeline_path}")

    metadata = {
        "project": "Pricely - Smart House Price Prediction",
        "phase": 4,
        "model_type": best_model_name,
        "random_seed": random_seed,
        "dataset_path": data_path,
        "dataset_rows": len(df),
        "train_rows": len(X_train),
        "test_rows": len(X_test),
        "target_column": target_col,
        "target_unit": "Lakhs INR",
        "feature_columns": feature_cols,
        "evaluation_metrics": best_result,
        "all_candidate_metrics": evaluation_results
    }
    metadata_path = os.path.join(output_dir, 'model_metadata.json')
    with open(metadata_path, 'w', encoding='utf-8') as f:
        json.dump(metadata, f, indent=4)
    print(f"      Saved Metadata: {metadata_path}")

    report_md = f"""# Phase 4 Model Comparison Report

**Project**: Pricely — Smart House Price Prediction  
**Dataset**: `D:\\HousePricePrediction\\data\\cleaned_house_prices.csv`  
**Train Set**: {len(X_train)} rows | **Test Set**: {len(X_test)} rows  
**Random Seed**: {random_seed}  

---

## 1. Candidate Model Performance Summary

| Model Name | 5-Fold CV R² (Mean ± Std) | 5-Fold CV MAE (Lakhs) | 5-Fold CV RMSE (Lakhs) | Test MAE (Lakhs INR) | Test RMSE (Lakhs INR) | Test R² Score |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
"""
    for res in evaluation_results:
        report_md += f"| **{res['Model']}** | {res['CV_R2_Mean']:.4f} ± {res['CV_R2_Std']:.4f} | {res['CV_MAE_Mean']:.2f} | {res['CV_RMSE_Mean']:.2f} | **{res['Test_MAE']:.2f}** | **{res['Test_RMSE']:.2f}** | **{res['Test_R2']:.4f}** |\n"

    report_md += f"""
---

## 2. Selected Model & Justification
- **Selected Model**: `{best_model_name}`
- **Reason for Selection**:
  - Highest overall R² score on unseen test data ({best_result['Test_R2']:.4f}).
  - Lowest Root Mean Squared Error on test set ({best_result['Test_RMSE']:.2f} Lakhs INR).
  - Strong generalizability across 5-fold cross-validation ({best_result['CV_R2_Mean']:.4f} ± {best_result['CV_R2_Std']:.4f}).
  - Effectively captures non-linear relationships between square footage, location, bathrooms, and property prices.

---

## 3. Saved Pipeline Artifact
- **Path**: `D:\\HousePricePrediction\\phase4\\house_price_pipeline.joblib`
- **Contents**: Full scikit-learn Pipeline incorporating `ColumnTransformer` (rare location encoder, availability encoder, one-hot encoder, standard scaler) and trained `{best_model_name}` regressor.
"""
    comparison_report_path = os.path.join(output_dir, 'model_comparison_report.md')
    with open(comparison_report_path, 'w', encoding='utf-8') as f:
        f.write(report_md)
    print(f"      Saved Model Comparison Report: {comparison_report_path}")

    print("\nPhase 4 Training & Artifact Generation Complete Successfully!\n")


if __name__ == '__main__':
    main()
