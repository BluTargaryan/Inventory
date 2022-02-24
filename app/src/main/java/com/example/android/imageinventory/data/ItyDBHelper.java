package com.example.android.imageinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.imageinventory.data.ItyContract.ItyEntry;

public class ItyDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ItyDBHelper.class.getSimpleName();

    public ItyDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static  final String DB_NAME = "store.db";
    public static final int DB_VERSION = 1;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ItyEntry.TABLE_NAME + " ("
                + ItyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItyEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItyEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + ItyEntry.COLUMN_PRODUCT_PRICE + " INTEGER, "
                + ItyEntry.COLUMN_PRODUCT_QTY + " INTEGER, "
                + ItyEntry.COLUMN_PRODUCT_QTYSOLD + " INTEGER, "
                + ItyEntry.COLUMN_PRODUCT_IMG + " BLOB);";



        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
