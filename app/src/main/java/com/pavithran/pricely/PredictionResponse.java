package com.pavithran.pricely;

import com.google.gson.annotations.SerializedName;

public class PredictionResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("predicted_price_lakhs")
    private double predictedPriceLakhs;

    @SerializedName("predicted_price_inr")
    private double predictedPriceInr;

    @SerializedName("formatted_price_inr")
    private String formattedPriceInr;

    @SerializedName("currency")
    private String currency;

    @SerializedName("unit")
    private String unit;

    public PredictionResponse() {
    }

    public PredictionResponse(boolean success, double predictedPriceLakhs, double predictedPriceInr, String formattedPriceInr, String currency, String unit) {
        this.success = success;
        this.predictedPriceLakhs = predictedPriceLakhs;
        this.predictedPriceInr = predictedPriceInr;
        this.formattedPriceInr = formattedPriceInr;
        this.currency = currency;
        this.unit = unit;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getPredictedPriceLakhs() {
        return predictedPriceLakhs;
    }

    public void setPredictedPriceLakhs(double predictedPriceLakhs) {
        this.predictedPriceLakhs = predictedPriceLakhs;
    }

    public double getPredictedPriceInr() {
        return predictedPriceInr;
    }

    public void setPredictedPriceInr(double predictedPriceInr) {
        this.predictedPriceInr = predictedPriceInr;
    }

    public String getFormattedPriceInr() {
        return formattedPriceInr;
    }

    public void setFormattedPriceInr(String formattedPriceInr) {
        this.formattedPriceInr = formattedPriceInr;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
