package com.example.miret.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "shelter.db";

  private static final int DATABASE_VERSION = 1;

  ItemDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
        + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ItemEntry.COLUMN_ITEM_PRODUCT_NAME + " TEXT NOT NULL, "
        + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
        + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0);";

    db.execSQL(SQL_CREATE_INVENTORY_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
