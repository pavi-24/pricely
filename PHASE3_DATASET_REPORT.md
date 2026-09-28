# Phase 3: Dataset Inspection & Preparation Report

**Project Name**: Pricely - Smart House Price Prediction  
**Package**: `com.pavithran.pricely`  
**Location**: `D:\HousePricePrediction`  

---

## 1. Dataset Source & Provenance
- **Dataset Source**: Kaggle / Open Data Repository — *Bengaluru House Price Dataset*.
- **Local File Path**: `D:\HousePricePrediction\data\raw_house_prices.csv` (924.7 KB).
- **Cleaned File Path**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`.
- **Script Path**: `D:\HousePricePrediction\data\preprocess.py`.
- **Integrity**: The original raw CSV file is preserved 100% unchanged. No Android app UI or Gradle files were modified.

---

## 2. Dataset Structure & Column Descriptions

| Column Name | Raw Data Type | Cleaned Data Type | Description | Target / Feature |
| :--- | :--- | :--- | :--- | :--- |
| `area_type` | `object` (str) | `object` (str) | Type of area (Super built-up, Plot, Built-up, Carpet Area) | Feature |
| `availability` | `object` (str) | `object` (str) | Possession status (Ready To Move / Specific date) | Feature |
| `location` | `object` (str) | `object` (str) | Property location / neighborhood name | Feature |
| `size` | `object` (str) | `object` (str) | Original size description (e.g., "2 BHK", "4 Bedroom") | Excluded (replaced by `bhk`) |
| `bhk` | N/A | `int64` | Extracted integer number of bedrooms/BHK | Feature |
| `society` | `object` (str) | N/A | Residential society code | Excluded (>41% missing) |
| `total_sqft` | `object` (str) | `str` / `float` | Raw square footage string (ranges/numbers/units) | Intermediate |
| `total_sqft_num`| N/A | `float64` | Cleaned numerical area in square feet | **Primary Feature** |
| `bath` | `float64` | `int64` | Number of bathrooms | Feature |
| `balcony` | `float64` | `int64` | Number of balconies | Feature |
| `price` | `float64` | `float64` | Property Price in **Lakhs INR** | **Target Label (`y`)** |
| `price_per_sqft`| N/A | `float64` | Calculated price per sq. ft. (in INR) | Audit only (Excluded from training to prevent target leakage) |

---

## 3. Data Quality Findings (Raw Audit vs. Cleaned)

### Raw Dataset (13,320 Rows x 9 Columns)
- **Missing Values**:
  - `society`: 5,502 missing (41.3%)
  - `balcony`: 609 missing (4.6%)
  - `bath`: 73 missing (0.5%)
  - `size`: 16 missing (0.1%)
  - `location`: 1 missing (<0.1%)
- **Duplicate Rows**: 529 duplicate rows identified.
- **Inconsistencies & Outliers**:
  - `total_sqft` contained range strings (e.g. `"2100 - 2850"`), unit strings (e.g. `"34.46Sq. Meter"`), and standard floats.
  - Outliers: Unrealistic listings with $< 300$ sq. ft. per BHK or bathroom count exceeding $\text{BHK} + 2$ (e.g., 40 bathrooms).

### Cleaned Dataset (11,802 Rows)
- **Rows Remaining**: 11,802 clean, validated property records.
- **Missing Values Remaining**: 0 missing values across all active feature columns.
- **Numerical Summary**:
  - **Built-up Area (`total_sqft_num`)**: Min = 300 sq ft, Median = 1,300 sq ft, Mean = 1,580.9 sq ft, Max = 52,272 sq ft.
  - **BHK (`bhk`)**: Min = 1, Median = 3, Mean = 2.64, Max = 16.
  - **Bathrooms (`bath`)**: Min = 1, Median = 2, Mean = 2.53, Max = 16.
  - **Price (`price`)**: Min = 9.0 Lakhs, Median = 70.0 Lakhs, Mean = 109.35 Lakhs (1.09 Crores), Max = 2,912.0 Lakhs (29.12 Crores INR).

---

## 4. Preprocessing & Feature Selection Decisions

1. **`society` Excluded**: Over 41% missing values; high cardinality without standardization.
2. **`size` Parsed to `bhk`**: Converted strings like `"2 BHK"` and `"4 Bedroom"` into standardized integer counts.
3. **`total_sqft` Parsed**: Range values averaged to midpoint float (e.g. `2100 - 2850` $\rightarrow$ `2475.0`). Non-standard unit rows dropped cleanly.
4. **Missing Value Imputation**:
   - Missing `bath` imputed using the median count grouped by `bhk`.
   - Missing `balcony` filled with `0`.
5. **Outlier Filtering Rules Applied**:
   - Removed listings where $\frac{\text{total\_sqft}}{\text{bhk}} < 300$ sq ft.
   - Removed listings where $\text{bath} \ge \text{bhk} + 2$.
   - Removed duplicate rows.
6. **Target Leakage Prevention**: `price_per_sqft` was computed solely for outlier auditing and is **explicitly excluded** from model training input features.

---

## 5. Exact Paths of Generated Files

- **Raw Data**: `D:\HousePricePrediction\data\raw_house_prices.csv`
- **Cleaned Data**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`
- **Preprocessing Script**: `D:\HousePricePrediction\data\preprocess.py`
- **Documentation Report**: `D:\HousePricePrediction\PHASE3_DATASET_REPORT.md`

---

## 6. Phase 4 Readiness Assessment

- **Dataset Quality**: **HIGH** (11,802 clean, verified real Indian property records).
- **Target Label**: Verified `price` in Lakhs (INR).
- **Readiness**: **READY FOR PHASE 4 MODEL TRAINING**.