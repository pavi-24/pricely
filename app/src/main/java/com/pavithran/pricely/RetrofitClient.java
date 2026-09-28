package com.pavithran.pricely;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Default development URL for Android Emulator pointing to local host machine
    public static final String DEV_BASE_URL = "http://10.0.2.2:8000/";

    // Production Cloud URL placeholder (Replace with your actual deployed Render URL)
    public static final String PROD_BASE_URL = "https://pricely-house-price-api.onrender.com/";

    // Toggle flag: Set to true when switching to production cloud API endpoint
    private static boolean USE_PRODUCTION = true;

    private static String customBaseUrl = null;
    private static Retrofit retrofit = null;

    public static synchronized String getBaseUrl() {
        if (customBaseUrl != null && !customBaseUrl.isEmpty()) {
            return customBaseUrl;
        }
        return USE_PRODUCTION ? PROD_BASE_URL : DEV_BASE_URL;
    }

    public static synchronized boolean isProductionMode() {
        return USE_PRODUCTION;
    }

    public static synchronized void setUseProduction(boolean useProduction) {
        if (USE_PRODUCTION != useProduction) {
            USE_PRODUCTION = useProduction;
            retrofit = null; // Reset singleton to rebuild with new base URL
        }
    }

    public static synchronized void setCustomBaseUrl(String newUrl) {
        if (newUrl != null && !newUrl.endsWith("/")) {
            newUrl = newUrl + "/";
        }
        if (customBaseUrl == null || !customBaseUrl.equals(newUrl)) {
            customBaseUrl = newUrl;
            retrofit = null; // Reset singleton to rebuild with new base URL
        }
    }

    public static synchronized PredictionApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(PredictionApiService.class);
    }
}
