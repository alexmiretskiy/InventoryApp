package com.example.miret.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.miret.inventoryapp.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {

  final String LOG_TAG = "myLogs";

  private static final int EXISTING_ITEM_LOADER = 0;

  private Uri mCurrentItemUri;

  private EditText mProductNameEditText;

  private EditText mQuantityEditText;

  private EditText mPriceEditText;

  private boolean mItemHasChanged = false;

  private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      mItemHasChanged = true;
      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_editor);

    Intent intent = getIntent();
    mCurrentItemUri = intent.getData();

    if (mCurrentItemUri == null) {
      setTitle(getString(R.string.editor_activity_title_new_item));
      invalidateOptionsMenu();
    } else {
      setTitle(getString(R.string.editor_activity_title_edit_item));

      getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
    }

    mProductNameEditText = (EditText) findViewById(R.id.edit_item_name);
    mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
    mPriceEditText = (EditText) findViewById(R.id.edit_item_price);

    mProductNameEditText.setOnTouchListener(mTouchListener);
    mQuantityEditText.setOnTouchListener(mTouchListener);
    mPriceEditText.setOnTouchListener(mTouchListener);

    Log.e("LOG", "onCreate Editor");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("itemHasChanged", mItemHasChanged);
    Log.e(LOG_TAG, "onSaveInstanceState");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mItemHasChanged = savedInstanceState.getBoolean("itemHasChanged");
    Log.e(LOG_TAG, "onRestoreInstanceState");
  }

  private void saveItem() {
    String productNameString = mProductNameEditText.getText().toString().trim();
    String quantityString = mQuantityEditText.getText().toString().trim();
    String priceString = mPriceEditText.getText().toString().trim();

    if (mCurrentItemUri == null &&
        TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(quantityString) &&
        TextUtils.isEmpty(priceString)) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(ItemEntry.COLUMN_ITEM_PRODUCT_NAME, productNameString);

    int quantity = 0;
    if (!TextUtils.isEmpty(priceString)) {
      quantity = Integer.parseInt(quantityString);
    }
    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);

    int price = 0;
    if (!TextUtils.isEmpty(priceString)) {
      price = Integer.parseInt(priceString);
    }
    values.put(ItemEntry.COLUMN_ITEM_PRICE, price);

    if (mCurrentItemUri == null) {
      Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

      if (newUri == null) {
        Toast.makeText(this, getString(R.string.editor_insert_item_failed),
            Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, getString(R.string.editor_insert_item_successful),
            Toast.LENGTH_SHORT).show();
      }
    } else {
      int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

      if (rowsAffected == 0) {
        Toast.makeText(this, getString(R.string.editor_update_item_failed),
            Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, getString(R.string.editor_update_item_successful),
            Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_editor, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    if (mCurrentItemUri == null) {
      MenuItem menuItem = menu.findItem(R.id.action_delete);
      menuItem.setVisible(false);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        saveItem();
        finish();
        return true;
      case R.id.action_delete:
        showDeleteConfirmationDialog();
        return true;
      case android.R.id.home:
        if (!mItemHasChanged) {
          NavUtils.navigateUpFromSameTask(EditorActivity.this);
          return true;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
              }
            };

        showUnsavedChangesDialog(discardButtonClickListener);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (!mItemHasChanged) {
      super.onBackPressed();
      return;
    }

    DialogInterface.OnClickListener discardButtonClickListener =
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            finish();
          }
        };

    showUnsavedChangesDialog(discardButtonClickListener);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    String[] projection = {
        ItemEntry._ID,
        ItemEntry.COLUMN_ITEM_PRODUCT_NAME,
        ItemEntry.COLUMN_ITEM_QUANTITY,
        ItemEntry.COLUMN_ITEM_PRICE,
    };

    return new CursorLoader(this,
        mCurrentItemUri,
        projection,
        null,
        null,
        null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (cursor == null || cursor.getCount() < 1) {
      return;
    }

    if (cursor.moveToFirst()) {
      int productNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRODUCT_NAME);
      int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
      int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

      String productName = cursor.getString(productNameColumnIndex);

      int quantity = cursor.getInt(quantityColumnIndex);

      int price = cursor.getInt(priceColumnIndex);

      mProductNameEditText.setText(productName);
      mQuantityEditText.setText(Integer.toString(quantity));
      mPriceEditText.setText(Integer.toString(price));
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mProductNameEditText.setText("");
    mQuantityEditText.setText("");
    mPriceEditText.setText("");
  }

  private void showUnsavedChangesDialog(
      DialogInterface.OnClickListener discardButtonClickListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.unsaved_changes_dialog_msg);
    builder.setPositiveButton(R.string.discard, discardButtonClickListener);
    builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  private void showDeleteConfirmationDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.delete_dialog_msg);
    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        deletePet();
        finish();
      }
    });
    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  private void deletePet() {
    int rowsAffected = getContentResolver().delete(mCurrentItemUri, null, null);

    if (rowsAffected == 0) {
      Toast.makeText(this, getString(R.string.editor_delete_item_failed),
          Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, getString(R.string.editor_delete_item_successful),
          Toast.LENGTH_SHORT).show();
    }
  }
}
