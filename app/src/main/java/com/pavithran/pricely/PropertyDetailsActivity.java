package com.pavithran.pricely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PropertyDetailsActivity extends AppCompatActivity {

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

    private ProgressBar progressBarLoading;
    private Button btnSubmitValuation;

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

        progressBarLoading = findViewById(R.id.progressBarLoading);
        btnSubmitValuation = findViewById(R.id.btnSubmitValuation);

        setupSpinners();

        btnSubmitValuation.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupSpinners() {
        ArrayAdapter<String> areaTypeAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_dropdown_item, AREA_TYPES);
        spinnerAreaType.setAdapter(areaTypeAdapter);

        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_dropdown_item, AVAILABILITY_OPTIONS);
        spinnerAvailability.setAdapter(availabilityAdapter);
    }

    private void validateAndSubmit() {
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

        if (locality.isEmpty()) {
            tilLocality.setError("Locality / area name is required");
            isValid = false;
        }

        if (areaStr.isEmpty()) {
            tilArea.setError("Built-up area is required");
            isValid = false;
        } else {
            try {
                totalSqft = Double.parseDouble(areaStr);
                if (totalSqft <= 0) {
                    tilArea.setError("Area must be greater than 0 sq. ft.");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilArea.setError("Enter a valid area number");
                isValid = false;
            }
        }

        if (bhkStr.isEmpty()) {
            tilBhk.setError("BHK count is required");
            isValid = false;
        } else {
            try {
                bhk = Integer.parseInt(bhkStr);
                if (bhk < 1) {
                    tilBhk.setError("BHK must be at least 1");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBhk.setError("Enter a valid integer");
                isValid = false;
            }
        }

        if (bathStr.isEmpty()) {
            tilBathrooms.setError("Bathroom count is required");
            isValid = false;
        } else {
            try {
                bath = Integer.parseInt(bathStr);
                if (bath < 1) {
                    tilBathrooms.setError("Bathrooms must be at least 1");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBathrooms.setError("Enter a valid integer");
                isValid = false;
            }
        }

        if (balconyStr.isEmpty()) {
            balcony = 0; // Default balcony count if left empty
        } else {
            try {
                balcony = Integer.parseInt(balconyStr);
                if (balcony < 0) {
                    tilBalcony.setError("Balcony count cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBalcony.setError("Enter a valid integer");
                isValid = false;
            }
        }

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

        // Prevent duplicate submissions & show loading
        setLoadingState(true);

        PredictionRequest request = new PredictionRequest(
            finalLocality, finalAreaType, finalAvailability, finalTotalSqft, finalBhk, finalBath, finalBalcony
        );

        PredictionApiService apiService = RetrofitClient.getApiService();
        apiService.predictPrice(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PredictionResponse body = response.body();

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
                    String errorMsg = "Prediction failed (HTTP " + response.code() + ").";
                    if (response.code() == 422) {
                        errorMsg = "Invalid input values sent to backend model.";
                    }
                    showErrorDialog("Model Prediction Error", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                setLoadingState(false);
                showErrorDialog("Connection Failed",
                    getString(R.string.error_backend_unavailable) + "\n\nDetails: " + t.getMessage());
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        btnSubmitValuation.setEnabled(!isLoading);
        progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}