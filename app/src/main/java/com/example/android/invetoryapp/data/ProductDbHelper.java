package com.example.android.invetoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.invetoryapp.data.ProductContract.InventoryEntry;


public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
        + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT, "
        + InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER, "
        + InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL, "
        + InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME + " INTEGER NOT NULL DEFAULT 0, "
        + InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE + " INTEGER );";
        //Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
