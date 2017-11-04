package com.example.miret.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;
import java.text.DecimalFormat;

public class ItemCursorAdapter extends CursorAdapter {

  public ItemCursorAdapter(Context context, Cursor c) {
    super(context, c, 0 /* flags */);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
  }

  @Override
  public void bindView(View view, final Context context, Cursor cursor) {
    TextView nameTextView = (TextView) view.findViewById(R.id.name);
    TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
    TextView priceTextView = (TextView) view.findViewById(R.id.price);

    int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
    int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
    int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

    String itemName = cursor.getString(nameColumnIndex);
    String itemQuantity = cursor.getString(quantityColumnIndex);
    String itemPrice = cursor.getString(priceColumnIndex);

    String formattedPrice = new DecimalFormat("##,##0$").format(Integer.parseInt(itemPrice));

    nameTextView.setText(itemName);
    quantityTextView.setText(itemQuantity);
    priceTextView.setText(formattedPrice);

    final int integerItemQuantity = cursor.getInt(quantityColumnIndex);
    int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
    long id = cursor.getLong(idColumnIndex);
    final Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);


    Button button = (Button) view.findViewById(R.id.button_sell);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int currentQuantity = integerItemQuantity;
        if (currentQuantity > 0) {
          currentQuantity--;
        } else {
          currentQuantity = 0;
        }
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, currentQuantity);
        int rowsAffected = context.getContentResolver().update(currentItemUri, values, null, null);

      }
    });
  }
}
