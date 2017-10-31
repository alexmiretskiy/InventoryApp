package com.example.miret.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

  public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

  private static final String DATABASE_NAME = "shelter.db";

  private static final int DATABASE_VERSION = 1;

  public ItemDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Create a String that contains the SQL statement to create the pets table
    String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
        + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ItemEntry.COLUMN_ITEM_PRODUCT_NAME + " TEXT NOT NULL, "
        + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, DEFAULT 0"
        + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0);";

    // Execute the SQL statement
    db.execSQL(SQL_CREATE_INVENTORY_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // The database is still at version 1, so there's nothing to do be done here.
  }
}
