package com.example.android.imageinventory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.imageinventory.data.ItyContract;
import com.example.android.imageinventory.data.ItyContract.ItyEntry;
import com.example.android.imageinventory.data.ItyDBHelper;

public class CreatorActivity extends AppCompatActivity {


    String imageString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creator);

        /**Adds back button to Catalog activity*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //does not show title
        getSupportActionBar(). setDisplayOptions(0, ActionBar. DISPLAY_SHOW_TITLE);

        //create db helper
        ItyDBHelper mDbHelper = new ItyDBHelper(this);

        Button submit = findViewById(R.id.create_submit);
        Button uploadimg = (Button) findViewById(R.id.create_upload);

        //get editTexts
        EditText name = findViewById(R.id.editname);
        EditText supplier = findViewById(R.id.editsupplier);
        EditText price = findViewById(R.id.editprice);
        EditText qty = findViewById(R.id.editqty);
        //get image view and upload button
        ImageView img = findViewById(R.id.create_img);
        //Intent to get image on buttonclick
        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = 0;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get their values
                String nameString = name.getText().toString().trim();
                String supplierString = supplier.getText().toString().trim();
                Integer priceString = Integer.parseInt(price.getText().toString().trim());
                Integer qtyString = Integer.parseInt(qty.getText().toString().trim());



                ContentValues values = new ContentValues();
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_NAME,nameString);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_SUPPLIER,supplierString);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_PRICE,priceString);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_QTY,qtyString);
                values.put(ItyContract.ItyEntry.COLUMN_PRODUCT_QTYSOLD,0);
                values.put(ItyEntry.COLUMN_PRODUCT_IMG,imageString);

                Uri newUri = getContentResolver().insert(ItyEntry.CONTENT_URI,values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(CreatorActivity.this,getString(R.string.editor_insert_pet_failed),Toast.LENGTH_SHORT);
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(CreatorActivity.this, getString(R.string.editor_insert_pet_successful),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreatorActivity.this,CatalogActivity.class);
                    startActivity(intent);
                }
            }
        });
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
            ImageView img = findViewById(R.id.create_img);
            //set img on imageview
            Uri imguri = Uri.parse(imageString);
            img.setImageURI(imguri);
        }
    }
}