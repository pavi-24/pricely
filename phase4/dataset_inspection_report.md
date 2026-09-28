# Phase 4 Task 1 — Dataset Inspection Report

**Project**: Pricely — Smart House Price Prediction  
**Dataset File**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`  
**Dataset Format**: CSV (Comma Separated Values)  
**Inspection Date**: September 28, 2026  

---

## 1. Executive Summary & Provenance
The dataset used for Phase 4 model training is the cleaned Bengaluru house-price dataset generated during Phase 3 (`cleaned_house_prices.csv`). The dataset originates from Kaggle / Open Data Repository and underwent rigorous cleaning, outlier removal, missing value imputation, and validation in Phase 3.

- **File Path**: `D:\HousePricePrediction\data\cleaned_house_prices.csv`
- **Total Rows**: 11,802 rows
- **Total Columns**: 11 columns
- **Missing Values**: 0 missing values across all active columns
- **Duplicate Rows**: 0 duplicate rows
- **Data Integrity**: Verified clean and ready for machine learning model training.

---

## 2. Target Column & Feature Breakdown

### Primary Target Column (`y`)
- **Target Name**: `price`
- **Data Type**: `float64`
- **Target Unit**: **Lakhs INR** (1 Lakh = 100,000 INR = ₹100,000).
- **Target Nature**: Represents the **total property price** in Lakhs INR (e.g., 109.35 Lakhs = ₹1,09,35,000 = ₹1.0935 Crores). It does **NOT** represent price per square foot.
- **Summary Statistics**:
  - Min: `9.00` Lakhs
  - 25th Percentile: `42.50` Lakhs
  - Median: `70.00` Lakhs
  - Mean: `109.35` Lakhs
  - 75th Percentile: `120.00` Lakhs
  - Max: `2912.00` Lakhs

### Input Features (`X`)
| Feature Name | Data Type | Feature Type | Role / Description |
| :--- | :--- | :--- | :--- |
| `location` | `object` (str) | Categorical | Locality / neighborhood name in Bengaluru (1,197 unique raw localities) |
| `area_type` | `object` (str) | Categorical | Type of area layout (Super built-up Area, Plot Area, Built-up Area, Carpet Area) |
| `availability` | `object` (str) | Categorical | Availability status ('Ready To Move' vs possession dates) |
| `total_sqft_num` | `float64` | Numerical | Total property area in square feet |
| `bhk` | `int64` | Numerical | Standardized integer count of bedrooms / BHK |
| `bath` | `int64` | Numerical | Standardized integer count of bathrooms |
| `balcony` | `int64` | Numerical | Standardized integer count of balconies |

### Excluded & Derived Columns (Data Leakage Prevention)
| Column Name | Reason for Exclusion |
| :--- | :--- |
| `price_per_sqft` | **TARGET LEAKAGE**. Calculated as `(price * 100000) / total_sqft_num`. Directly contains target label `price`. Explicitly excluded from model input features `X`. |
| `size` | Redundant string description (e.g., "2 BHK"). Standardized into integer `bhk`. |
| `total_sqft` | Redundant raw string (ranges/units). Converted to numerical `total_sqft_num`. |

---

## 3. Data Quality & Distribution Audit

### Numerical Features
- **Built-up Area (`total_sqft_num`)**:
  - Min: `300.0` sq ft | Median: `1,300.0` sq ft | Mean: `1,580.91` sq ft | Max: `52,272.0` sq ft
- **Bedrooms (`bhk`)**:
  - Min: `1` | Median: `3` | Mean: `2.64` | Max: `16`
- **Bathrooms (`bath`)**:
  - Min: `1` | Median: `2` | Mean: `2.53` | Max: `16`
- **Balconies (`balcony`)**:
  - Min: `0` | Median: `2` | Mean: `1.52` | Max: `3`

### Categorical Features
- **`area_type`** (4 unique categories):
  - Super built-up Area: ~8,790 occurrences (~74.5%)
  - Built-up Area: ~2,410 occurrences (~20.4%)
  - Plot Area: ~525 occurrences (~4.4%)
  - Carpet Area: ~77 occurrences (~0.7%)
- **`availability`** (80 unique categories):
  - Ready To Move: 9,286 occurrences (~78.7%)
  - Under Construction / Specific Dates: 2,516 occurrences (~21.3%)
- **`location`** (1,197 unique raw categories):
  - High-frequency localities: Whitefield (515), Sarjapur Road (365), Electronic City (280), Kanakpura Road (235), Thanisandra (229).
  - 983 rare localities have 10 or fewer property listings in the dataset.

---

## 4. Verification & Ambiguity Assessment
- **Dataset Ambiguity**: NONE. Dataset format, rows, columns, and target variable are 100% verified.
- **Target Leakage Risk**: ELIMINATED. `price_per_sqft` is excluded from feature matrix `X`.
- **Phase 4 Status**: PASSED. Ready for training scikit-learn regression models.
