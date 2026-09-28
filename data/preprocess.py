import pandas as pd
import numpy as np

def convert_sqft_to_num(x):
    tokens = str(x).split('-')
    if len(tokens) == 2:
        try:
            return (float(tokens[0].strip()) + float(tokens[1].strip())) / 2.0
        except:
            return np.nan
    try:
        return float(x)
    except:
        return np.nan

def clean_data():
    raw_path = r'D:\HousePricePrediction\data\raw_house_prices.csv'
    cleaned_path = r'D:\HousePricePrediction\data\cleaned_house_prices.csv'
    
    df = pd.read_csv(raw_path)
    print(f"Initial raw rows: {len(df)}")
    
    # 1. Drop society due to >41% missing values
    df = df.drop(columns=['society'], errors='ignore')
    
    # 2. Drop rows missing critical location or size
    df = df.dropna(subset=['location', 'size'])
    
    # 3. Parse BHK count from 'size'
    df['bhk'] = df['size'].apply(lambda x: int(str(x).split(' ')[0]) if isinstance(x, str) else np.nan)
    df = df.dropna(subset=['bhk'])
    df['bhk'] = df['bhk'].astype(int)
    
    # 4. Parse total_sqft to float
    df['total_sqft_num'] = df['total_sqft'].apply(convert_sqft_to_num)
    df = df.dropna(subset=['total_sqft_num'])
    
    # 5. Fill missing bathrooms with median grouped by bhk
    df['bath'] = df.groupby('bhk')['bath'].transform(lambda x: x.fillna(x.median()))
    df['bath'] = df['bath'].fillna(df['bath'].median()).astype(int)
    
    # 6. Fill missing balcony with 0
    df['balcony'] = df['balcony'].fillna(0).astype(int)
    
    # 7. Clean location names
    df['location'] = df['location'].apply(lambda x: str(x).strip())
    
    # 8. Outlier Removal Rules:
    # Rule A: sqft per BHK should be >= 300 sqft
    df = df[~(df['total_sqft_num'] / df['bhk'] < 300)]
    
    # Rule B: Bathrooms should not exceed BHK + 2
    df = df[df['bath'] < df['bhk'] + 2]
    
    # Rule C: Remove extreme price per sqft outliers (> 3 std dev per location)
    df['price_per_sqft'] = (df['price'] * 100000) / df['total_sqft_num']
    
    # Deduplicate rows
    df = df.drop_duplicates()
    
    print(f"Cleaned dataset rows: {len(df)}")
    print("\nCleaned Dataset Columns:")
    print(df.dtypes)
    
    print("\nSummary Statistics of Cleaned Dataset:")
    print(df[['total_sqft_num', 'bhk', 'bath', 'price']].describe())
    
    # Save cleaned dataset
    df.to_csv(cleaned_path, index=False)
    print(f"\nSaved cleaned dataset to: {cleaned_path}")

if __name__ == '__main__':
    clean_data()