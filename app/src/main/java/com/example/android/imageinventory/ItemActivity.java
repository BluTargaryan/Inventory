package com.example.android.imageinventory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.imageinventory.data.ItyContract.ItyEntry;

public class ItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /** Identifier for the pet data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentPetUri;

    /**TextView to hold product name */
    private TextView mNameTextView;

    /**TextView tp hold price and quantity*/
    private TextView mPriceTextView;

    /**TextView to hold product supplier*/
    private TextView mSupplierTextView;

    /**TextView to hold quantity sold*/
    private TextView mQtySoldTextView;

    /**ImageView to hold product image*/
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        /**Adds back button to Catalog activity*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //does not show title
        getSupportActionBar(). setDisplayOptions(0, ActionBar. DISPLAY_SHOW_TITLE);

        //examine intent to get data
        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();


        // Find all relevant views that we will need to read user input from
        mNameTextView = (TextView) findViewById(R.id.item_name);
        mPriceTextView = (TextView) findViewById(R.id.item_price);
        mSupplierTextView = (TextView) findViewById(R.id.item_supplier);
        mQtySoldTextView = (TextView) findViewById(R.id.item_qtysold);
        mImgView = (ImageView) findViewById(R.id.image);

        //for views we need to alter
        LinearLayout alertLayout = findViewById(R.id.alert_layout);
        Button update = findViewById(R.id.update);
        Button delete = findViewById(R.id.delete);
        //make it invisible
        alertLayout.setVisibility(View.INVISIBLE);

        //run loader to populate views
getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER,null,this);

//setup update button
update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ItemActivity.this,EditorActivity.class);
        //set uri on intent
        intent.setData(mCurrentPetUri);
        Log.e("this",mCurrentPetUri.toString());
        //launch intent
        startActivity(intent);
    }
});

//setup delete button
delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        alertLayout.setVisibility(View.VISIBLE);
        alertLayout.getBackground().setAlpha(220);
        //hide back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //get yes button
        Button yes = findViewById(R.id.item_yes);
        Button no = findViewById(R.id.item_no);

        //set them up
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(ItemActivity.this, getString(R.string.editor_delete_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(ItemActivity.this, getString(R.string.editor_delete_pet_successful),
                            Toast.LENGTH_SHORT).show();
                }

                // Close the activity
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLayout.setVisibility(View.INVISIBLE);
                //display back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
    }
});


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    ItyEntry._ID,
                    ItyEntry.COLUMN_PRODUCT_NAME,
                    ItyEntry.COLUMN_PRODUCT_SUPPLIER,
                    ItyEntry.COLUMN_PRODUCT_PRICE,
                    ItyEntry.COLUMN_PRODUCT_QTY,
                    ItyEntry.COLUMN_PRODUCT_QTYSOLD,
                    ItyEntry.COLUMN_PRODUCT_IMG
            };
            return new CursorLoader(this,
                    mCurrentPetUri,
                    projection,null,
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
        // (This should be the only row in the cursor)

        if(cursor.moveToFirst()){
            //find columns of product attrs that we are interested in
            int nameColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_SUPPLIER);
            int priceColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_QTY);
            int qtysoldColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_QTYSOLD);
            int imgColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_IMG);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);
            int qtysold = cursor.getInt(qtysoldColumnIndex);
            String img = cursor.getString(imgColumnIndex);

            Uri imgUri = Uri.parse(img);


            //update views
            mNameTextView.setText(name);
            mSupplierTextView.setText(supplier);
            mPriceTextView.setText("$"+Integer.toString(price)+ " - "+Integer.toString(qty)+" pcs");
            mQtySoldTextView.setText(Integer.toString(qtysold) +" units");
            isValidUri(mImgView,imgUri);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.setText("");
        mSupplierTextView.setText("");
        mPriceTextView.setText("");
        mQtySoldTextView.setText("");
        mImgView.setImageURI(null);
    }

    public void isValidUri(ImageView imgView, Uri myUri) {
        try {
            //doTheThing()
            imgView.setImageURI(myUri);
        } catch(Exception e){
            //followUri is null or empty
            myUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.mipmap.revolt)
                    + '/' + getResources().getResourceTypeName(R.mipmap.revolt)
                    + '/' + getResources().getResourceEntryName(R.mipmap.revolt));
            imgView.setImageURI(myUri);
        }
    }

}