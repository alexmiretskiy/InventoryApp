package com.example.miret.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;

public class ItemProvider extends ContentProvider {

  public static final String LOG_TAG = ItemProvider.class.getSimpleName();
  private static final int ITEMS = 100;
  private static final int ITEM_ID = 101;
  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY, ITEMS);
    sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_INVENTORY + "/#", ITEM_ID);
  }

  private ItemDbHelper mDbHelper;

  @Override
  public boolean onCreate() {
    mDbHelper = new ItemDbHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    SQLiteDatabase database = mDbHelper.getReadableDatabase();
    Cursor cursor;
    int match = sUriMatcher.match(uri);
    switch (match) {
      case ITEMS:
        cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
            null, null, sortOrder);
        break;
      case ITEM_ID:
        selection = ItemEntry._ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
            null, null, sortOrder);
        break;
      default:
        throw new IllegalArgumentException("Cannot query unknown URI " + uri);
    }
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ITEMS:
        return insertItem(uri, contentValues);
      default:
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }
  }

  private Uri insertItem(Uri uri, ContentValues values) {
    String name = values.getAsString(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
    if (name == null) {
      throw new IllegalArgumentException("Item requires a product name");
    }

    Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
    if (quantity != null && quantity < 0) {
      throw new IllegalArgumentException("Item requires valid quantity");
    }

    Integer price = values.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
    if (price != null && price < 0) {
      throw new IllegalArgumentException("Item requires valid price");
    }

    SQLiteDatabase database = mDbHelper.getWritableDatabase();

    long id = database.insert(ItemEntry.TABLE_NAME, null, values);
    if (id == -1) {
      Log.e(LOG_TAG, "Failed to insert row for " + uri);
      return null;
    }

    getContext().getContentResolver().notifyChange(uri, null);

    return ContentUris.withAppendedId(uri, id);
  }

  @Override
  public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
      String[] selectionArgs) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ITEMS:
        return updateItem(uri, contentValues, selection, selectionArgs);
      case ITEM_ID:
        selection = ItemEntry._ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        return updateItem(uri, contentValues, selection, selectionArgs);
      default:
        throw new IllegalArgumentException("Update is not supported for " + uri);
    }
  }

  private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    if (values.containsKey(ItemEntry.COLUMN_ITEM_PRODUCT_NAME)) {
      String name = values.getAsString(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
      if (name == null) {
        throw new IllegalArgumentException("Item requires a product name");
      }
    }

    if (values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)) {
      Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
      if (quantity != null && quantity < 0) {
        throw new IllegalArgumentException("Item requires valid quantity");
      }
    }

    if (values.containsKey(ItemEntry.COLUMN_ITEM_PRICE)) {
      Integer price = values.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
      if (price != null && price < 0) {
        throw new IllegalArgumentException("Item requires valid price");
      }
    }

    if (values.size() == 0) {
      return 0;
    }

    SQLiteDatabase database = mDbHelper.getWritableDatabase();

    int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);

    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsUpdated;
  }

  @Override
  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase database = mDbHelper.getWritableDatabase();

    int rowsDeleted;

    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ITEMS:
        rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case ITEM_ID:
        selection = ItemEntry._ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Deletion is not supported for " + uri);
    }

    if (rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsDeleted;
  }

  @Override
  public String getType(@NonNull Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ITEMS:
        return ItemEntry.CONTENT_LIST_TYPE;
      case ITEM_ID:
        return ItemEntry.CONTENT_ITEM_TYPE;
      default:
        throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
    }
  }
}
