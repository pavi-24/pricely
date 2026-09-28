package com.pavithran.pricely;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ValuationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pricely_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "valuation_history";

    private static final String COL_ID = "id";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_LOCATION = "location";
    private static final String COL_AREA_TYPE = "area_type";
    private static final String COL_AVAILABILITY = "availability";
    private static final String COL_TOTAL_SQFT = "total_sqft";
    private static final String COL_BHK = "bhk";
    private static final String COL_BATH = "bath";
    private static final String COL_BALCONY = "balcony";
    private static final String COL_PRICE_LAKHS = "price_lakhs";
    private static final String COL_PRICE_INR = "price_inr";
    private static final String COL_FORMATTED_PRICE = "formatted_price";

    public ValuationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIMESTAMP + " INTEGER, " +
                COL_LOCATION + " TEXT, " +
                COL_AREA_TYPE + " TEXT, " +
                COL_AVAILABILITY + " TEXT, " +
                COL_TOTAL_SQFT + " REAL, " +
                COL_BHK + " INTEGER, " +
                COL_BATH + " INTEGER, " +
                COL_BALCONY + " INTEGER, " +
                COL_PRICE_LAKHS + " REAL, " +
                COL_PRICE_INR + " REAL, " +
                COL_FORMATTED_PRICE + " TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add database migration logic here if the schema changes.
    }

    public long insertValuation(ValuationItem item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TIMESTAMP, item.getTimestamp());
        values.put(COL_LOCATION, item.getLocation());
        values.put(COL_AREA_TYPE, item.getAreaType());
        values.put(COL_AVAILABILITY, item.getAvailability());
        values.put(COL_TOTAL_SQFT, item.getTotalSqft());
        values.put(COL_BHK, item.getBhk());
        values.put(COL_BATH, item.getBath());
        values.put(COL_BALCONY, item.getBalcony());
        values.put(COL_PRICE_LAKHS, item.getPriceLakhs());
        values.put(COL_PRICE_INR, item.getPriceInr());
        values.put(COL_FORMATTED_PRICE, item.getFormattedPrice());

        return db.insert(TABLE_HISTORY, null, values);
    }

    public List<ValuationItem> getAllValuations() {
        List<ValuationItem> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                COL_TIMESTAMP + " DESC")) {

            while (cursor.moveToNext()) {
                ValuationItem item = new ValuationItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AVAILABILITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_SQFT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BHK)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BATH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BALCONY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE_LAKHS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE_INR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FORMATTED_PRICE))
                );

                list.add(item);
            }
        }

        return list;
    }

    public void clearAllValuations() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null);
    }
}