package com.pavithran.pricely;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnPredictHousePrice;
    private Button btnValuationHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnPredictHousePrice = findViewById(R.id.btnPredictHousePrice);
        btnValuationHistory = findViewById(R.id.btnValuationHistory);

        btnPredictHousePrice.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PropertyDetailsActivity.class);
            startActivity(intent);
        });

        btnValuationHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ValuationHistoryActivity.class);
            startActivity(intent);
        });
    }
}