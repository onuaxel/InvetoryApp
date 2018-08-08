package com.example.android.invetoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.invetoryapp.data.ProductContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;

    /**
     * Content URI for the existing product (null is it's a new product)
     */
    private Uri mCurrentProductUri;

    private EditText mProductNameEditText;
    private EditText mProductQuantityEditText;
    private EditText mProductPriceEditText;
    private Spinner mProductSupplierSpinner;
    private EditText mProductSupplierPhoneEditText;

    private int mSupplier = InventoryEntry.SUPPLIER_UNKNOWN;

    private boolean mProductHasChanged = false;

    private int quantity;

    // Set onTouchListener for EditText views
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            ImageView phoneCall = findViewById(R.id.call_supplier);

            // Hide delete and call button from add product mode
            phoneCall.setVisibility(View.GONE);
            ImageView deleteProduct = findViewById(R.id.delete_product);
            deleteProduct.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductNameEditText = (EditText) findViewById(R.id.product_name_editText);
        mProductQuantityEditText = (EditText) findViewById(R.id.product_quantity_editText);
        mProductPriceEditText = (EditText) findViewById(R.id.product_price_editText);
        mProductSupplierSpinner = (Spinner) findViewById(R.id.spinner);
        mProductSupplierPhoneEditText = (EditText) findViewById(R.id.suppliers_phone_editText);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // ha touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mOnTouchListener);
        mProductQuantityEditText.setOnTouchListener(mOnTouchListener);
        mProductPriceEditText.setOnTouchListener(mOnTouchListener);
        mProductSupplierSpinner.setOnTouchListener(mOnTouchListener);
        mProductSupplierPhoneEditText.setOnTouchListener(mOnTouchListener);

        setupSpinner();

        ImageView increment = findViewById(R.id.increment);
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mProductQuantityEditText.getText().toString().equals("")) {
                    quantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
                    quantity ++;
                    mProductQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });

        ImageView decrement = findViewById(R.id.decrement);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mProductQuantityEditText.getText().toString().equals("")) {
                    if (quantity > 0) {
                        quantity --;
                        mProductQuantityEditText.setText(String.valueOf(quantity));
                    }
                }
            }
        });

    }

    /**
     * Setup the dropdown spinner that allows the user to select the supplier.
     */
    private void setupSpinner() {
        ArrayAdapter productSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        productSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mProductSupplierSpinner.setAdapter(productSpinnerAdapter);
        mProductSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_john))) {
                        mSupplier = InventoryEntry.SUPPLIER_JOHN; //John
                    } else if (selection.equals(getString(R.string.supplier_mary))) {
                        mSupplier = InventoryEntry.SUPPLIER_MARY; // Mary
                    } else if (selection.equals(getString(R.string.supplier_bob))) {
                        mSupplier = InventoryEntry.SUPPLIER_BOB; // Robert
                    } else {
                        mSupplier = InventoryEntry.SUPPLIER_UNKNOWN; // Unknown
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = 0; // Unknown
            }
        });
    }

    /**
     * Get user input from editor and save product into db.
     */
    private void saveProduct() {
        String nameString = mProductNameEditText.getText().toString().trim();
        // Product name field check
        if (nameString.equals("")) {
            Toast.makeText(this, R.string.product_name_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        // Price check
        int price = 0;
        if (!mProductPriceEditText.getText().toString().equals("")){
            price = Integer.parseInt(mProductPriceEditText.getText().toString());
        }
        if (mProductPriceEditText.getText().toString().equals("") || price < 0) {
            Toast.makeText(this, R.string.product_price_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        //String quantityString = mProductQuantityEditText.getText().toString().trim();
        // Quantity check
        int quantity = 1;
        if (!mProductQuantityEditText.getText().toString().equals("")) {
            quantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        }
        if (mProductQuantityEditText.getText().toString().equals("") || quantity < 0) {
            Toast.makeText(this, R.string.product_quantity_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        //String phoneString = mProductSupplierPhoneEditText.getText().toString().trim();
        // Phone No check
        String phoneNo = "";
        if (!mProductSupplierPhoneEditText.getText().toString().equals("")) {
                phoneNo = mProductSupplierPhoneEditText.getText().toString().trim();
        }
        if (mProductSupplierPhoneEditText.getText().toString().equals("")) {
            Toast.makeText(this, R.string.suppliers_phone_missing, Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine if this is a new or existing product
        if (mCurrentProductUri == null) {
            // It's new product in inventory!
            // Insert this new product, returning the content URI for the new product

            // Create ContentValues object where column names are the keys,
            // and product attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, price);
            values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE, phoneNo);
            values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME, mSupplier);

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.product_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_insert_successful),
                        Toast.LENGTH_SHORT). show();
                finish();
            }
        } else {
            // Existing product.
            // Create ContentValues object where column names are the keys,
            // and product attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, price);
            values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE, phoneNo);
            values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME, mSupplier);

            int rowsAffected = getContentResolver().update(
                    mCurrentProductUri,
                    values,
                    null,
                    null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.product_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.product_update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu options from res/menu/menu_editor.xml
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar
        switch (item.getItemId()) {
            case R.id.action_save:
                // save
                saveProduct();
                // and exit
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
                case R.id.home:
                    // if the product hasn't changed, continue with navigation up to parent activity
                    if (!mProductHasChanged) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        return true;
                    }

                    // Otherwise if there are unsaved changes, setup a dialog to warn user
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    // Show a dialog that notifies about unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns user there are unsaved changes that will be lost
     * if they leave editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        // Create and show
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // if the product hasn't changed, continue with hadling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // "Discard" clicked -> close
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME,
                InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE};
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {

            // Find the columns
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE);

            // Extract out the value
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);

            // Update the views on screen with the values from the db
            mProductNameEditText.setText(name);
            mProductQuantityEditText.setText(Integer.toString(quantity));
            mProductPriceEditText.setText(Integer.toString(price));
            mProductSupplierPhoneEditText.setText(Integer.toString(phone));

            // Supplier is a dropdown spinner, so map the constant value from db
            switch (supplier) {
                case InventoryEntry.SUPPLIER_JOHN:
                    mProductSupplierSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_MARY:
                    mProductSupplierSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_BOB:
                    mProductSupplierSpinner.setSelection(3);
                    break;
                    default:
                        mProductSupplierSpinner.setSelection(0);
                        break;
            }
        }


        ImageView phoneCall = findViewById(R.id.call_supplier);
        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mProductSupplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                        phone, null));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        ImageView deleteProduct = findViewById(R.id.delete_product);
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the date from input fields.
        mProductNameEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductPriceEditText.setText("");
        mProductSupplierSpinner.setSelection(0);
        mProductSupplierPhoneEditText.setText("");
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // "Cancel" has been clicked -> dismiss dialog and carry on.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // only perform the delete if this is an existing product
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();

            }
        }
        finish();
    }
}
