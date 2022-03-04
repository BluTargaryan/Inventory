package com.example.android.imageinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.imageinventory.data.ItyContract;
import com.example.android.imageinventory.data.ItyContract.ItyEntry;
import com.example.android.imageinventory.data.ItyDBHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //get attached uri
    Uri currentUri ;
    private static final int EXISTING_PRODUCT_LOADER = 0;

    //get views
    EditText mname;
    EditText msupplier;
    EditText mprice ;
    EditText munits_added ;
    EditText munits_removed;
    ImageButton maddsold;
    ImageButton mremovesold;
    ImageButton maddadded;
    ImageButton mremoveadded;
    Button mupload;
    ImageView mimage;
    Button submit;

    String imageString;

    //for storage
    int oprice;
    int oqty;
    int oqtysold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        /**Adds back button to Catalog activity*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //does not show title
        getSupportActionBar(). setDisplayOptions(0, ActionBar. DISPLAY_SHOW_TITLE);

        //examine intent to get data
        Intent intent = getIntent();
        currentUri= intent.getData();

         mname = findViewById(R.id.changename);
         msupplier = findViewById(R.id.changesupplier);
         mprice = findViewById(R.id.adjustprice);
         munits_added = findViewById(R.id.units_added);
         munits_removed = findViewById(R.id.units_sold);
         maddsold = findViewById(R.id.units_sold_add);
         mremovesold = findViewById(R.id.units_sold_subtract);
         maddadded = findViewById(R.id.units_added_add);
         mremoveadded = findViewById(R.id.units_added_subtract);
         mupload = findViewById(R.id.edit_upload);
         mimage = findViewById(R.id.edit_image);
         submit = findViewById(R.id.editor_submit);

        //populate views
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER,null,this);

        //setup buttons
        maddsold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String text = munits_removed.getText().toString();
             int num = Integer.parseInt(text);
             num = num+1;

             munits_removed.setText(String.valueOf(num));
            }
        });
        mremovesold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = munits_removed.getText().toString();
                int num = Integer.parseInt(text);
                if(num>0){
                    num = num-1;
                }
                munits_removed.setText(String.valueOf(num));
            }
        });
        maddadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = munits_added.getText().toString();
                int num = Integer.parseInt(text);
                num = num+1;

                munits_added.setText(String.valueOf(num));
            }
        });
        mremoveadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = munits_added.getText().toString();
                int num = Integer.parseInt(text);
                if(num>0){
                    num = num-1;
                }

                munits_added.setText(String.valueOf(num));
            }
        });

        //setup image re-upload
        mupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = 0;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }
        });

        //create db helper
        ItyDBHelper mDbHelper = new ItyDBHelper(getApplicationContext());
        //setup submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mname.getText().toString();
                String supplier = msupplier.getText().toString();
                int price =Integer.parseInt(mprice.getText().toString());
                int qty =Integer.parseInt(munits_added.getText().toString());
                int qtysold =Integer.parseInt(munits_removed.getText().toString());

                ContentValues values = new ContentValues();
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_NAME,name);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_SUPPLIER,supplier);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_PRICE,price);
                int newqty = (oqty+qty)-qtysold;
                int sold = (oqtysold+qtysold);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_QTY,newqty);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_QTYSOLD,sold);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_IMG,imageString);

                int newUri = getContentResolver().update(currentUri, values, null, null);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == 0) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(EditorActivity.this,"Error with update",Toast.LENGTH_SHORT);
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_pet_successful),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditorActivity.this,ItemActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(),ItemActivity.class);
        startActivity(myIntent);
        return true;
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
                currentUri,
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

            Uri imageUri = null;

            //condition for if uri is null
            if(!(img ==null)) {
                imageUri = Uri.parse(img);
                imageString = imageUri.toString();
            }else{
                imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.mipmap.revolt)
                        + '/' + getResources().getResourceTypeName(R.mipmap.revolt)
                        + '/' + getResources().getResourceEntryName(R.mipmap.revolt));
                imageString = imageUri.toString();
            }
            //store for storage later
            oprice = price;
            oqty = qty;
            oqtysold = qtysold;

            //update views
          mname.setText(name);
          msupplier.setText(supplier);
          mprice.setText(String.valueOf(price));
          munits_removed.setText(String.valueOf(0));
          munits_added.setText(String.valueOf(0));
          isValidUri(mimage,imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//update views
        mname.setText("");
        msupplier.setText("");
        mprice.setText("");
        munits_removed.setText("");
        munits_added.setText("");
        mimage.setImageURI(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK
                && null != data)
        {
            Uri selectedImage = data.getData();

            //to make sure uri persists
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                this.getContentResolver().takePersistableUriPermission(selectedImage, takeFlags);
            }
            String s_img = selectedImage.toString();
            imageString=s_img;


            //get image view and upload button
            ImageView img = findViewById(R.id.edit_image);
            //set img on imageview
            Uri imguri = Uri.parse(imageString);
            img.setImageURI(imguri);
        }
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