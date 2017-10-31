package com.example.miret.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

  private ItemContract() {
  }

  public static final String CONTENT_AUTHORITY = "com.example.miret.inventoryapp";

  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_INVENTORY = "inventory";

  public static final class ItemEntry implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

    public static final String CONTENT_LIST_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

    public final static String TABLE_NAME = "inventory";

    public final static String _ID = BaseColumns._ID;

    public final static String COLUMN_ITEM_PRODUCT_NAME = "name";

    public final static String COLUMN_ITEM_QUANTITY = "quantity";

    public final static String COLUMN_ITEM_PRICE = "weight";
  }
}
