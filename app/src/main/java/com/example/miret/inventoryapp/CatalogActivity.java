package com.example.miret.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;

public class CatalogActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {

  /** Identifier for the pet data loader */
  private static final int PET_LOADER = 0;

  /** Adapter for the ListView */
  ItemCursorAdapter mCursorAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_catalog);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
        startActivity(intent);
      }
    });

    ListView petListView = (ListView) findViewById(R.id.list);

    View emptyView = findViewById(R.id.empty_view);
    petListView.setEmptyView(emptyView);

    mCursorAdapter = new ItemCursorAdapter(this, null);
    petListView.setAdapter(mCursorAdapter);

    petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
        Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
        intent.setData(currentItemUri);
        startActivity(intent);
      }
    });

    getLoaderManager().initLoader(PET_LOADER, null, this);
  }

  /**
   * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
   */
  private void insertExampleItem() {
    ContentValues values = new ContentValues();
    values.put(ItemEntry.COLUMN_ITEM_PRODUCT_NAME, "Pencil with big black balls and boobs");
    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 1);
    values.put(ItemEntry.COLUMN_ITEM_PRICE, 7000000);

    Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
  }

  private void deleteAllItems() {
    int rowsAffected = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
    if (rowsAffected == 0) {
      Toast.makeText(this, getString(R.string.catalog_activity_delete_all_items_failed),
          Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, getString(R.string.catalog_activity_delete_all_items_successful),
          Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_catalog, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_insert_dummy_data:
        insertExampleItem();
        return true;
      case R.id.action_delete_all_entries:
        deleteAllItems();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    String[] projection = {
        ItemEntry._ID,
        ItemEntry.COLUMN_ITEM_PRODUCT_NAME,
        ItemEntry.COLUMN_ITEM_QUANTITY,
        ItemEntry.COLUMN_ITEM_PRICE};

    return new CursorLoader(this,
        ItemEntry.CONTENT_URI,
        projection,
        null,
        null,
        null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mCursorAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
  }
}
