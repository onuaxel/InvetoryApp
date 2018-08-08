package com.example.android.invetoryapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.invetoryapp.data.ProductContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) { super(context, c, 0);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        //Find individual views that we want to modify in the list item layout
        Log.d("Position" + cursor.getPosition() + ":", " bindView() has been called.");
        TextView productNameView = view.findViewById(R.id.product_name);
        TextView productQuantityView = view.findViewById(R.id.product_quantity_editText);
        TextView productPriceView = view.findViewById(R.id.price);
        ImageView sellImageView = view.findViewById(R.id.one_out_of_storage);

        //Find the columns of product attributes that we're interested in
        final int columnIdIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);

        //Read the product attributes from the Cursor for the current product
        final String productId = cursor.getString(columnIdIndex);
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);


        sellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CatalogActivity activity = (CatalogActivity) context;
                activity.productSalesCounter(Integer.valueOf(productId), Integer.valueOf(productQuantity));
            }
        });

        //Update the TextViews with the attributes for the current product
        productNameView.setText(productName);
        productQuantityView.setText(productQuantity);
        productPriceView.setText(productPrice);


    }
}
