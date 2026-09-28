package com.pavithran.pricely;

import com.google.gson.annotations.SerializedName;

public class PredictionRequest {

    @SerializedName("location")
    private String location;

    @SerializedName("area_type")
    private String areaType;

    @SerializedName("availability")
    private String availability;

    @SerializedName("total_sqft_num")
    private double totalSqftNum;

    @SerializedName("bhk")
    private int bhk;

    @SerializedName("bath")
    private int bath;

    @SerializedName("balcony")
    private int balcony;

    public PredictionRequest() {
    }

    public PredictionRequest(String location, String areaType, String availability,
                             double totalSqftNum, int bhk, int bath, int balcony) {
        this.location = location;
        this.areaType = areaType;
        this.availability = availability;
        this.totalSqftNum = totalSqftNum;
        this.bhk = bhk;
        this.bath = bath;
        this.balcony = balcony;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public double getTotalSqftNum() {
        return totalSqftNum;
    }

    public void setTotalSqftNum(double totalSqftNum) {
        this.totalSqftNum = totalSqftNum;
    }

    public int getBhk() {
        return bhk;
    }

    public void setBhk(int bhk) {
        this.bhk = bhk;
    }

    public int getBath() {
        return bath;
    }

    public void setBath(int bath) {
        this.bath = bath;
    }

    public int getBalcony() {
        return balcony;
    }

    public void setBalcony(int balcony) {
        this.balcony = balcony;
    }
}
