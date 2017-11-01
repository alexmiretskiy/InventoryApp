package com.example.miret.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    // Inflate a list item view using the layout specified in list_item.xml
    return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // Find individual views that we want to modify in the list item layout
    TextView nameTextView = (TextView) view.findViewById(R.id.name);
    TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
    TextView priceTextView = (TextView) view.findViewById(R.id.price);

    // Find the columns of pet attributes that we're interested in
    int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
    int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
    int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

    // Read the pet attributes from the Cursor for the current pet
    String itemName = cursor.getString(nameColumnIndex);
    String itemQuantity = cursor.getString(quantityColumnIndex);
    String itemPrice = cursor.getString(priceColumnIndex);

    String formattedPrice = new DecimalFormat("##,##0$").format(Integer.parseInt(itemPrice));

    // Update the TextViews with the attributes for the current pet
    nameTextView.setText(itemName);
    quantityTextView.setText(itemQuantity);
    priceTextView.setText(formattedPrice);
  }
}
