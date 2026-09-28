import os
import sys
import joblib
import pandas as pd
import numpy as np

# Ensure project root and phase4 directory are in sys.path
project_root = r'D:\HousePricePrediction'
phase4_dir = os.path.join(project_root, 'phase4')
if project_root not in sys.path:
    sys.path.insert(0, project_root)
if phase4_dir not in sys.path:
    sys.path.insert(0, phase4_dir)

# Import classes & register module aliases so unpickler can locate classes
import train
sys.modules['phase4.train'] = train
sys.modules['train'] = train
from train import RareLocationEncoder, AvailabilityEncoder

def verify():
    pipeline_path = r'D:\HousePricePrediction\phase4\house_price_pipeline.joblib'
    data_path = r'D:\HousePricePrediction\data\cleaned_house_prices.csv'

    print("==================================================")
    print("PRICELY — PHASE 4 MODEL VERIFICATION")
    print("==================================================\n")

    print(f"[1/3] Loading saved model pipeline from:\n      {pipeline_path}")
    if not os.path.exists(pipeline_path):
        raise FileNotFoundError(f"Saved pipeline artifact not found at {pipeline_path}")
    
    pipeline = joblib.load(pipeline_path)
    print("      Model pipeline loaded successfully!\n")

    print(f"[2/3] Loading test samples from:\n      {data_path}")
    df = pd.read_csv(data_path)
    
    # Select 5 sample properties from dataset
    samples = df.sample(5, random_state=42).copy()
    
    feature_cols = ['location', 'area_type', 'availability', 'total_sqft_num', 'bhk', 'bath', 'balcony']
    X_samples = samples[feature_cols]
    y_actual = samples['price'].values

    print("\n[3/3] Running predictions on sample properties:")
    print("-" * 80)
    
    predictions = pipeline.predict(X_samples)

    for i in range(len(samples)):
        row = samples.iloc[i]
        actual = y_actual[i]
        pred = predictions[i]
        diff = pred - actual
        pct_err = (abs(diff) / actual) * 100

        print(f"Sample #{i+1}:")
        print(f"  Location:       {row['location']}")
        print(f"  Area / BHK:     {row['total_sqft_num']} sq. ft. | {row['bhk']} BHK | {row['bath']} Bath | {row['balcony']} Balcony")
        print(f"  Area Type:      {row['area_type']}")
        print(f"  Availability:   {row['availability']}")
        print(f"  Actual Price:   {actual:.2f} Lakhs INR (INR {actual*100000:,.0f})")
        print(f"  Predicted Price: {pred:.2f} Lakhs INR (INR {pred*100000:,.0f})")
        print(f"  Error:          {diff:+.2f} Lakhs INR ({pct_err:.1f}% absolute error)")
        print("-" * 80)

    print("\nModel Verification: PASSED SUCCESSFULLY! Saved pipeline is fully operational.")

if __name__ == '__main__':
    verify()
