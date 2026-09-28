package com.pavithran.pricely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ValuationHistoryActivity extends AppCompatActivity {

    private LinearLayout layoutEmptyState;
    private RecyclerView rvHistoryList;
    private Button btnHistoryBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuation_history);

        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvHistoryList = findViewById(R.id.rvHistoryList);
        btnHistoryBackHome = findViewById(R.id.btnHistoryBackHome);

        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));

        btnHistoryBackHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ValuationHistoryActivity.this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryData();
    }

    private void loadHistoryData() {
        List<ValuationItem> historyItems = ValuationHistoryManager.getValuationHistory(this);

        if (historyItems == null || historyItems.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvHistoryList.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvHistoryList.setVisibility(View.VISIBLE);

            ValuationHistoryAdapter adapter = new ValuationHistoryAdapter(historyItems);
            rvHistoryList.setAdapter(adapter);
        }
    }
}