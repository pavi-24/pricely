import urllib.request
import urllib.error
import json
import sys

BASE_URL = "http://127.0.0.1:8000"

def make_request(url, method="GET", data=None):
    req = urllib.request.Request(url, method=method)
    req.add_header("Content-Type", "application/json")
    body = json.dumps(data).encode("utf-8") if data is not None else None
    
    try:
        with urllib.request.urlopen(req, data=body) as response:
            status_code = response.status
            resp_body = json.loads(response.read().decode("utf-8"))
            return status_code, resp_body
    except urllib.error.HTTPError as e:
        status_code = e.code
        resp_body = json.loads(e.read().decode("utf-8"))
        return status_code, resp_body
    except Exception as e:
        print(f"Connection error to {url}: {e}")
        return None, str(e)

def run_tests():
    print("==================================================")
    print("PRICELY — PHASE 5 FASTAPI BACKEND TEST SUITE")
    print("==================================================\n")

    # 1. Test GET /
    print("[TEST 1/5] Testing GET / ...")
    status_code, response = make_request(f"{BASE_URL}/")
    print(f"Status Code: {status_code}")
    print(f"Response: {json.dumps(response, indent=2)}")
    assert status_code == 200, f"Expected 200 OK, got {status_code}"
    assert response.get("status") == "online", "Expected status 'online'"
    print("PASSED!\n")

    # 2. Test GET /health
    print("[TEST 2/5] Testing GET /health ...")
    status_code, response = make_request(f"{BASE_URL}/health")
    print(f"Status Code: {status_code}")
    print(f"Response: {json.dumps(response, indent=2)}")
    assert status_code == 200, f"Expected 200 OK, got {status_code}"
    assert response.get("status") == "healthy", "Expected status 'healthy'"
    assert response.get("model_loaded") is True, "Expected model_loaded == True"
    print("PASSED!\n")

    # 3. Test POST /predict - Valid Sample 1 (Whitefield 2 BHK)
    print("[TEST 3/5] Testing POST /predict (Valid Sample #1 - Whitefield 2 BHK) ...")
    payload1 = {
        "location": "Whitefield",
        "area_type": "Super built-up Area",
        "availability": "Ready To Move",
        "total_sqft_num": 1170.0,
        "bhk": 2,
        "bath": 2,
        "balcony": 1
    }
    status_code, response = make_request(f"{BASE_URL}/predict", method="POST", data=payload1)
    print(f"Status Code: {status_code}")
    print(f"Response: {json.dumps(response, indent=2)}")
    assert status_code == 200, f"Expected 200 OK, got {status_code}"
    assert response.get("success") is True
    lakhs = response.get("predicted_price_lakhs")
    inr = response.get("predicted_price_inr")
    assert lakhs is not None and lakhs > 0, "Predicted lakhs must be > 0"
    assert abs(inr - lakhs * 100000) < 1.0, "INR conversion mismatch!"
    print(f"Verified Conversion: {lakhs} Lakhs = INR {inr:,.2f} (Formatted: {response.get('formatted_price_inr').encode('utf-8')})")
    print("PASSED!\n")

    # 4. Test POST /predict - Valid Sample 2 (Hebbal 3 BHK)
    print("[TEST 4/5] Testing POST /predict (Valid Sample #2 - Hebbal 3 BHK) ...")
    payload2 = {
        "location": "Hebbal",
        "area_type": "Plot Area",
        "availability": "Ready To Move",
        "total_sqft_num": 1715.0,
        "bhk": 3,
        "bath": 3,
        "balcony": 2
    }
    status_code, response = make_request(f"{BASE_URL}/predict", method="POST", data=payload2)
    print(f"Status Code: {status_code}")
    print(f"Response: {json.dumps(response, indent=2)}")
    assert status_code == 200, f"Expected 200 OK, got {status_code}"
    assert response.get("success") is True
    print("PASSED!\n")

    # 5. Test POST /predict - Invalid & Missing Inputs
    print("[TEST 5/5] Testing POST /predict Validation Errors (Missing / Invalid Fields) ...")
    # 5a. Missing 'total_sqft_num'
    invalid_payload_missing = {
        "location": "Whitefield",
        "area_type": "Super built-up Area",
        "availability": "Ready To Move",
        "bhk": 2,
        "bath": 2
    }
    status_code, response = make_request(f"{BASE_URL}/predict", method="POST", data=invalid_payload_missing)
    print(f"Missing Field Status Code: {status_code} (Expected 422 Unprocessable Entity)")
    assert status_code == 422, f"Expected 422 validation error, got {status_code}"

    # 5b. Invalid total_sqft_num <= 0
    invalid_payload_negative_sqft = {
        "location": "Whitefield",
        "area_type": "Super built-up Area",
        "availability": "Ready To Move",
        "total_sqft_num": -500.0,
        "bhk": 2,
        "bath": 2,
        "balcony": 1
    }
    status_code, response = make_request(f"{BASE_URL}/predict", method="POST", data=invalid_payload_negative_sqft)
    print(f"Invalid sqft <= 0 Status Code: {status_code} (Expected 422 Unprocessable Entity)")
    assert status_code == 422, f"Expected 422 validation error, got {status_code}"

    # 5c. Invalid bhk <= 0
    invalid_payload_zero_bhk = {
        "location": "Whitefield",
        "area_type": "Super built-up Area",
        "availability": "Ready To Move",
        "total_sqft_num": 1000.0,
        "bhk": 0,
        "bath": 2,
        "balcony": 1
    }
    status_code, response = make_request(f"{BASE_URL}/predict", method="POST", data=invalid_payload_zero_bhk)
    print(f"Zero BHK Status Code: {status_code} (Expected 422 Unprocessable Entity)")
    assert status_code == 422, f"Expected 422 validation error, got {status_code}"
    print("PASSED!\n")

    print("==================================================")
    print("ALL API TESTS PASSED SUCCESSFULLY!")
    print("==================================================")

if __name__ == "__main__":
    run_tests()
