package com.pavithran.pricely;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class PredictionResultActivity extends AppCompatActivity {

    private TextView tvFormattedPrice;
    private TextView tvSubPrice;
    private TextView tvSummaryDetails;
    private Button btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);

        tvFormattedPrice = findViewById(R.id.tvFormattedPrice);
        tvSubPrice = findViewById(R.id.tvSubPrice);
        tvSummaryDetails = findViewById(R.id.tvSummaryDetails);
        btnBackHome = findViewById(R.id.btnBackHome);

        Intent intent = getIntent();
        if (intent != null) {
            String location = intent.getStringExtra("LOCATION");
            String areaType = intent.getStringExtra("AREA_TYPE");
            String availability = intent.getStringExtra("AVAILABILITY");
            double totalSqft = intent.getDoubleExtra("TOTAL_SQFT", 0);
            int bhk = intent.getIntExtra("BHK", 0);
            int bath = intent.getIntExtra("BATH", 0);
            int balcony = intent.getIntExtra("BALCONY", 0);

            double priceLakhs = intent.getDoubleExtra("PREDICTED_PRICE_LAKHS", 0);
            double priceInr = intent.getDoubleExtra("PREDICTED_PRICE_INR", 0);
            // Save prediction to SQLite only once when this screen is first created.
if (savedInstanceState == null && priceInr > 0) {

    String formattedPrice = intent.getStringExtra("FORMATTED_PRICE_INR");

    ValuationItem item = new ValuationItem(
            System.currentTimeMillis(),
            location,
            areaType,
            availability,
            totalSqft,
            bhk,
            bath,
            balcony,
            priceLakhs,
            priceInr,
            formattedPrice != null ? formattedPrice : ""
    );

    ValuationHistoryManager.saveValuation(
            PredictionResultActivity.this,
            item
    );
}
            String formattedInr = intent.getStringExtra("FORMATTED_PRICE_INR");

            // Format main price display
            if (formattedInr != null && !formattedInr.isEmpty()) {
                tvFormattedPrice.setText(formattedInr);
            } else {
                tvFormattedPrice.setText(String.format(Locale.getDefault(), "₹%.2f Lakhs", priceLakhs));
            }

            // Format sub-price display
            NumberFormat inrFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String inrStr = inrFormatter.format(priceInr);
            tvSubPrice.setText(String.format(Locale.getDefault(), "Total Valuation: %s (%.2f Lakhs INR)", inrStr, priceLakhs));

            // Format property details parameter summary
            StringBuilder summary = new StringBuilder();
            summary.append("• Locality Name: ").append(location != null ? location : "N/A").append("\n");
            summary.append("• Area Layout Type: ").append(areaType != null ? areaType : "N/A").append("\n");
            summary.append("• Possession Status: ").append(availability != null ? availability : "N/A").append("\n");
            summary.append("• Built-up Area: ").append(String.format(Locale.getDefault(), "%.1f", totalSqft)).append(" sq. ft.\n");
            summary.append("• Bedrooms (BHK): ").append(bhk).append(" BHK\n");
            summary.append("• Bathrooms: ").append(bath).append("\n");
            summary.append("• Balconies: ").append(balcony);

            tvSummaryDetails.setText(summary.toString());
        }

        btnBackHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PredictionResultActivity.this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });
    }
}