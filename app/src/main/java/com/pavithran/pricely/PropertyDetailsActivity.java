package com.pavithran.pricely;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PropertyDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PricelyAPI";

    private Spinner spinnerAreaType;
    private Spinner spinnerAvailability;

    private TextInputLayout tilLocality;
    private TextInputLayout tilArea;
    private TextInputLayout tilBhk;
    private TextInputLayout tilBathrooms;
    private TextInputLayout tilBalcony;

    private EditText etLocality;
    private EditText etArea;
    private EditText etBhk;
    private EditText etBathrooms;
    private EditText etBalcony;

    private View layoutLoadingContainer;
    private ProgressBar progressBarLoading;
    private TextView tvLoadingMessage;
    private Button btnSubmitValuation;

    private boolean isRequestInProgress = false;

    private static final String[] AREA_TYPES = {
        "Super built-up Area", "Plot Area", "Built-up Area", "Carpet Area"
    };

    private static final String[] AVAILABILITY_OPTIONS = {
        "Ready To Move", "Under Construction"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        spinnerAreaType = findViewById(R.id.spinnerAreaType);
        spinnerAvailability = findViewById(R.id.spinnerAvailability);

        tilLocality = findViewById(R.id.tilLocality);
        tilArea = findViewById(R.id.tilArea);
        tilBhk = findViewById(R.id.tilBhk);
        tilBathrooms = findViewById(R.id.tilBathrooms);
        tilBalcony = findViewById(R.id.tilBalcony);

        etLocality = findViewById(R.id.etLocality);
        etArea = findViewById(R.id.etArea);
        etBhk = findViewById(R.id.etBhk);
        etBathrooms = findViewById(R.id.etBathrooms);
        etBalcony = findViewById(R.id.etBalcony);

        layoutLoadingContainer = findViewById(R.id.layoutLoadingContainer);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        btnSubmitValuation = findViewById(R.id.btnSubmitValuation);

        setupSpinners();
        setupTextWatchers();

        btnSubmitValuation.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupSpinners() {
        ArrayAdapter<String> areaTypeAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, AREA_TYPES);
        areaTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerAreaType.setAdapter(areaTypeAdapter);

        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, AVAILABILITY_OPTIONS);
        availabilityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerAvailability.setAdapter(availabilityAdapter);
    }

    private void setupTextWatchers() {
        etLocality.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilLocality.setError(null);
            }
        });

        etArea.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilArea.setError(null);
            }
        });

        etBhk.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilBhk.setError(null);
            }
        });

        etBathrooms.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilBathrooms.setError(null);
            }
        });

        etBalcony.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilBalcony.setError(null);
            }
        });
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void validateAndSubmit() {
        // Prevent duplicate submissions if request is already in progress
        if (isRequestInProgress) {
            return;
        }

        // Clear all previous errors
        tilLocality.setError(null);
        tilArea.setError(null);
        tilBhk.setError(null);
        tilBathrooms.setError(null);
        tilBalcony.setError(null);

        String locality = etLocality.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String bhkStr = etBhk.getText().toString().trim();
        String bathStr = etBathrooms.getText().toString().trim();
        String balconyStr = etBalcony.getText().toString().trim();

        boolean isValid = true;
        double totalSqft = 0;
        int bhk = 0;
        int bath = 0;
        int balcony = 0;

        // 1. Locality validation: must not be empty
        if (locality.isEmpty()) {
            tilLocality.setError("Locality / area name is required");
            isValid = false;
        }

        // 2. Area validation: must be a valid positive number
        if (areaStr.isEmpty()) {
            tilArea.setError("Built-up area is required");
            isValid = false;
        } else {
            try {
                totalSqft = Double.parseDouble(areaStr);
                if (totalSqft <= 0) {
                    tilArea.setError("Area must be a valid positive number (greater than 0)");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilArea.setError("Please enter a valid numerical area");
                isValid = false;
            }
        }

        // 3. BHK validation: must be between 1 and 20
        if (bhkStr.isEmpty()) {
            tilBhk.setError("BHK count is required");
            isValid = false;
        } else {
            try {
                bhk = Integer.parseInt(bhkStr);
                if (bhk < 1 || bhk > 20) {
                    tilBhk.setError("BHK must be a positive whole number between 1 and 20");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBhk.setError("Please enter a valid whole number for BHK");
                isValid = false;
            }
        }

        // 3. Bathrooms validation: must be between 1 and 20
        if (bathStr.isEmpty()) {
            tilBathrooms.setError("Bathroom count is required");
            isValid = false;
        } else {
            try {
                bath = Integer.parseInt(bathStr);
                if (bath < 1 || bath > 20) {
                    tilBathrooms.setError("Bathrooms must be a positive whole number between 1 and 20");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBathrooms.setError("Please enter a valid whole number for bathrooms");
                isValid = false;
            }
        }

        // 4. Balcony validation: must be between 0 and 10
        if (balconyStr.isEmpty()) {
            balcony = 0; // Default balcony count if left empty
        } else {
            try {
                balcony = Integer.parseInt(balconyStr);
                if (balcony < 0 || balcony > 10) {
                    tilBalcony.setError("Balconies must be a non-negative whole number between 0 and 10");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBalcony.setError("Please enter a valid whole number for balconies");
                isValid = false;
            }
        }

        // 5. Dropdown selections validation
        if (spinnerAreaType.getSelectedItem() == null || spinnerAreaType.getSelectedItem().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select an area layout type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (spinnerAvailability.getSelectedItem() == null || spinnerAvailability.getSelectedItem().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select possession availability status", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Do not send API request if validation fails
        if (!isValid) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        final String finalLocality = locality;
        final String finalAreaType = spinnerAreaType.getSelectedItem().toString();
        final String finalAvailability = spinnerAvailability.getSelectedItem().toString();
        final double finalTotalSqft = totalSqft;
        final int finalBhk = bhk;
        final int finalBath = bath;
        final int finalBalcony = balcony;

        // Diagnostic logging of outgoing request summary
        Log.d(TAG, "Submitting prediction request -> location=" + finalLocality
                + ", area_type=" + finalAreaType
                + ", availability=" + finalAvailability
                + ", total_sqft_num=" + finalTotalSqft
                + ", bhk=" + finalBhk
                + ", bath=" + finalBath
                + ", balcony=" + finalBalcony);

        // Prevent duplicate submissions & show professional loading indicator
        setLoadingState(true);

        PredictionRequest request = new PredictionRequest(
            finalLocality, finalAreaType, finalAvailability, finalTotalSqft, finalBhk, finalBath, finalBalcony
        );

        PredictionApiService apiService = RetrofitClient.getApiService();
        apiService.predictPrice(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                // Dismiss loading indicator on all response paths
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PredictionResponse body = response.body();
                    Log.d(TAG, "Prediction successful: " + body.getFormattedPriceInr());

                    // Save to local valuation history
                    ValuationItem historyItem = new ValuationItem(
                        System.currentTimeMillis(),
                        finalLocality,
                        finalAreaType,
                        finalAvailability,
                        finalTotalSqft,
                        finalBhk,
                        finalBath,
                        finalBalcony,
                        body.getPredictedPriceLakhs(),
                        body.getPredictedPriceInr(),
                        body.getFormattedPriceInr()
                    );
                    ValuationHistoryManager.saveValuation(PropertyDetailsActivity.this, historyItem);

                    // Open PredictionResultActivity
                    Intent intent = new Intent(PropertyDetailsActivity.this, PredictionResultActivity.class);
                    intent.putExtra("LOCATION", finalLocality);
                    intent.putExtra("AREA_TYPE", finalAreaType);
                    intent.putExtra("AVAILABILITY", finalAvailability);
                    intent.putExtra("TOTAL_SQFT", finalTotalSqft);
                    intent.putExtra("BHK", finalBhk);
                    intent.putExtra("BATH", finalBath);
                    intent.putExtra("BALCONY", finalBalcony);
                    intent.putExtra("PREDICTED_PRICE_LAKHS", body.getPredictedPriceLakhs());
                    intent.putExtra("PREDICTED_PRICE_INR", body.getPredictedPriceInr());
                    intent.putExtra("FORMATTED_PRICE_INR", body.getFormattedPriceInr());

                    startActivity(intent);

                } else {
                    int statusCode = response.code();
                    String errorBodyString = "";
                    if (response.errorBody() != null) {
                        try {
                            errorBodyString = response.errorBody().string();
                        } catch (IOException e) {
                            errorBodyString = "";
                        }
                    }
                    Log.e(TAG, "API HTTP " + statusCode + " Error response: " + errorBodyString);

                    String title = "Valuation Service Error";
                    String errorMsg;
                    if (statusCode == 400) {
                        errorMsg = "Bad Request (HTTP 400): Invalid request parameters submitted.";
                    } else if (statusCode == 422) {
                        title = "Validation Error";
                        errorMsg = "Unprocessable Entity (HTTP 422): Input values rejected by ML backend model validation rules.";
                        if (!errorBodyString.isEmpty()) {
                            errorMsg += "\n\nServer Response: " + errorBodyString;
                        }
                    } else if (statusCode == 500) {
                        title = "Backend Server Error";
                        errorMsg = "Server Error (HTTP 500): ML prediction model backend encountered an internal error.";
                    } else if (statusCode == 503) {
                        title = "Service Temporarily Unavailable";
                        errorMsg = "Service Unavailable (HTTP 503): Backend service is temporarily unavailable or restarting. Please retry in 30 seconds.";
                    } else {
                        errorMsg = "Backend returned error code HTTP " + statusCode + ". Please try again.";
                    }
                    showErrorDialog(title, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                // Dismiss loading indicator on failure path
                setLoadingState(false);
                Log.e(TAG, "Network failure: " + t.getMessage(), t);

                String title = "Connection Error";
                String details;

                if (t instanceof SocketTimeoutException) {
                    title = "Server Cold-Start Timeout";
                    details = "The request timed out waiting for the production ML server.\n\nRender free instances take 30–60 seconds to wake up from idle mode. Please click 'Predict Property Price' again to retry.";
                } else if (t instanceof UnknownHostException) {
                    title = "Internet Connection Required";
                    details = "Unable to reach the production server (https://pricely-house-price-api.onrender.com/). Please verify your internet or Wi-Fi connection.";
                } else if (t instanceof IOException) {
                    title = "Network Error";
                    details = "Network communication failed while contacting production server.\n\nDetails: " + t.getLocalizedMessage();
                } else {
                    details = "An unexpected error occurred: " + t.getMessage();
                }

                showErrorDialog(title, details);
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        isRequestInProgress = isLoading;
        btnSubmitValuation.setEnabled(!isLoading);
        btnSubmitValuation.setAlpha(isLoading ? 0.65f : 1.0f);
        btnSubmitValuation.setText(isLoading ? "Calculating Valuation..." : getString(R.string.btn_submit_valuation));
        if (layoutLoadingContainer != null) {
            layoutLoadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}