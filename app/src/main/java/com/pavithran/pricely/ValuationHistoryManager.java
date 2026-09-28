package com.pavithran.pricely;

import android.content.Context;

import java.util.List;

public class ValuationHistoryManager {

    public static void saveValuation(Context context, ValuationItem item) {
        ValuationDatabaseHelper dbHelper =
                new ValuationDatabaseHelper(context);

        dbHelper.insertValuation(item);
        dbHelper.close();
    }

    public static List<ValuationItem> getValuationHistory(Context context) {
        ValuationDatabaseHelper dbHelper =
                new ValuationDatabaseHelper(context);

        List<ValuationItem> list = dbHelper.getAllValuations();
        dbHelper.close();

        return list;
    }

    public static void clearHistory(Context context) {
        ValuationDatabaseHelper dbHelper =
                new ValuationDatabaseHelper(context);

        dbHelper.clearAllValuations();
        dbHelper.close();
    }
}