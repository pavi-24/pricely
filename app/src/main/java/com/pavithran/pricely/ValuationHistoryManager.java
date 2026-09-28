package com.pavithran.pricely;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ValuationHistoryManager {

    private static final String PREF_NAME = "PricelyValuationHistory";
    private static final String KEY_HISTORY = "valuation_history_list";

    public static void saveValuation(Context context, ValuationItem item) {
        List<ValuationItem> list = getValuationHistory(context);
        list.add(0, item); // Add latest at top

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(KEY_HISTORY, json);
        editor.apply();
    }

    public static List<ValuationItem> getValuationHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ValuationItem>>() {}.getType();
        List<ValuationItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static void clearHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}
