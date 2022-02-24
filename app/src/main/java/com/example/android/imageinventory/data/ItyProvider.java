package com.example.android.imageinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.imageinventory.data.ItyContract.ItyEntry;

public class ItyProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ItyProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int INVENTORY = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int ITY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(ItyContract.CONTENT_AUTHORITY, ItyContract.PATH_ITY, INVENTORY);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ItyContract.CONTENT_AUTHORITY, ItyContract.PATH_ITY + "/#", ITY_ID);
    }

    /**Database helper object*/
    private ItyDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItyDBHelper(getContext());
        return true;
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //get readable db
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(ItyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITY_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ItyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ItyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
                // Set notification URI on the Cursor,
                // so we know what content URI the Cursor was created for.
                // If the data at this URI changes, then we know we need to update the Cursor.
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                //return the cursor
                return cursor;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case INVENTORY:
                return insertProduct(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+ uri);
        }
    }

    /**
     * Inser pet into db with given content values
     * Return the new content uri for that specific db
     * */
    private Uri insertProduct(Uri uri, ContentValues values){
        //check that name is not null
        String name = values.getAsString(ItyEntry.COLUMN_PRODUCT_NAME);
        if(name==null){
            throw  new IllegalArgumentException("Product requires a name");
        }

        //check that supplier is not null
        String supplier = values.getAsString(ItyEntry.COLUMN_PRODUCT_SUPPLIER);
        if(supplier==null){
            throw  new IllegalArgumentException("Product requires a supplier");
        }

        //if price is provided, check that it's greater or equal to 0
        Integer price = values.getAsInteger(ItyEntry.COLUMN_PRODUCT_PRICE);
        if(price !=null && price<0){
            throw new IllegalArgumentException("Valid price required");
        }

        //if qty is provided, check that it's greater or equal to 0
        Integer qty = values.getAsInteger(ItyEntry.COLUMN_PRODUCT_QTY);
        if(qty !=null && qty<0){
            throw new IllegalArgumentException("Valid quantity required");
        }



        //Get writeable db
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Insert product with the given values
        long id = database.insert(ItyEntry.TABLE_NAME,null,values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final  int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateProduct(uri, values, selection, selectionArgs);
            case ITY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ItyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        // If the {@link ItyEntry#COLUMN_PRODUCT_NAME} key is present,
        //        // check that the name value is not null.
        if (values.containsKey(ItyEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ItyEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        //check that supplier is not null
        if (values.containsKey(ItyEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplier = values.getAsString(ItyEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }


        // check that the [price] value is valid.
        if (values.containsKey(ItyEntry.COLUMN_PRODUCT_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer price = values.getAsInteger(ItyEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        // check that the qty value is valid.
        if (values.containsKey(ItyEntry.COLUMN_PRODUCT_QTY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer qty = values.getAsInteger(ItyEntry.COLUMN_PRODUCT_QTY);
            if (qty != null && qty < 0) {
                throw new IllegalArgumentException("Product requires valid qty");
            }
        }

        // check that the qty value is valid.
        if (values.containsKey(ItyEntry.COLUMN_PRODUCT_QTYSOLD)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer qtysold = values.getAsInteger(ItyEntry.COLUMN_PRODUCT_QTYSOLD);
            if (qtysold != null && qtysold < 0) {
                throw new IllegalArgumentException("Product requires valid qtysold");
            }
        }

        //no needto check image, any value (incl null) is ok

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ItyEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITY_ID:
                // Delete a single row given by the ID in the URI
                selection = ItyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ItyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final  int match=sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return ItyEntry.CONTENT_LIST_TYPE;
            case ITY_ID:
                return ItyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
