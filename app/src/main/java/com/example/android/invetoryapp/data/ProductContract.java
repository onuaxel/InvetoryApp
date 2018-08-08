package com.example.android.invetoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class ProductContract {

    //Prevent someone accidentally instantiating the contract class
    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.invetoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static abstract class InventoryEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INVENTORY_NAME = "productName";
        public static final String COLUMN_INVENTORY_PRICE = "price";
        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";
        public static final String COLUMN_INVENTORY_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_INVENTORY_SUPPLIER_PHONE = "suppliers_phone";

        public final static int SUPPLIER_UNKNOWN = 0;
        public final static int SUPPLIER_JOHN = 1;
        public final static int SUPPLIER_MARY = 2;
        public final static int SUPPLIER_BOB = 3;

        public static boolean isValidSupplier (int supplier) {
            if (supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_JOHN ||
                    supplier == SUPPLIER_MARY || supplier == SUPPLIER_BOB ) {
                return true;
            }
            return false;
        }
    }
}
