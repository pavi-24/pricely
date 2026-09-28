package com.pavithran.pricely;

public class ValuationItem {
    private long timestamp;
    private String location;
    private String areaType;
    private String availability;
    private double totalSqft;
    private int bhk;
    private int bath;
    private int balcony;
    private double priceLakhs;
    private double priceInr;
    private String formattedPrice;

    public ValuationItem() {
    }

    public ValuationItem(long timestamp, String location, String areaType, String availability,
                         double totalSqft, int bhk, int bath, int balcony,
                         double priceLakhs, double priceInr, String formattedPrice) {
        this.timestamp = timestamp;
        this.location = location;
        this.areaType = areaType;
        this.availability = availability;
        this.totalSqft = totalSqft;
        this.bhk = bhk;
        this.bath = bath;
        this.balcony = balcony;
        this.priceLakhs = priceLakhs;
        this.priceInr = priceInr;
        this.formattedPrice = formattedPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    public String getAreaType() {
        return areaType;
    }

    public String getAvailability() {
        return availability;
    }

    public double getTotalSqft() {
        return totalSqft;
    }

    public int getBhk() {
        return bhk;
    }

    public int getBath() {
        return bath;
    }

    public int getBalcony() {
        return balcony;
    }

    public double getPriceLakhs() {
        return priceLakhs;
    }

    public double getPriceInr() {
        return priceInr;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }
}
