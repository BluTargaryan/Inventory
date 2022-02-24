package com.example.android.imageinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.imageinventory.data.ItyDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.android.imageinventory.data.ItyContract.ItyEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    //loader constant
    private  static final int PRODUCT_LOADER=0;
    //adapter for listview
    ItyCursorAdapter mCursorAdapter;

    /** Database helper that will provide us access to the database */
    private ItyDBHelper mDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        // Setup FAB to open CreatorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this,CreatorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDBHelper = new ItyDBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo(){
        //to access db, we instantiate subclass of SQLiteOpenHelper
        //and pass context, which is the current activity

        String[] projection = {
                ItyEntry._ID,
                ItyEntry.COLUMN_PRODUCT_NAME,
                ItyEntry.COLUMN_PRODUCT_SUPPLIER,
                ItyEntry.COLUMN_PRODUCT_PRICE,
                ItyEntry.COLUMN_PRODUCT_QTY,
                ItyEntry.COLUMN_PRODUCT_QTYSOLD,
                ItyEntry.COLUMN_PRODUCT_IMG
        };

        //perform query on Provider using ContentResolver


        Cursor cursor = getContentResolver().query(
                ItyEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        //find listview to be populated w product data
        ListView productListView = (ListView) findViewById(R.id.list);
        //find and set empty view on ListView so it only shows when listview has 0 items
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
        //setup adapter to create list item for each row of data
        ItyCursorAdapter adapter = new ItyCursorAdapter(this,cursor);
        //attach adapter to listview
        productListView.setAdapter(adapter);

        //set item onclick listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create intent to go to EditorActivity
                Intent intent = new Intent(CatalogActivity.this,ItemActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ItyEntry.CONTENT_URI,id);

                //set uri on intent
                intent.setData(currentProductUri);
                Log.e("this",currentProductUri.toString());
                //launch intent
                startActivity(intent);
            }
        });
    }



    private void insertProduct(){
        //create contentvalues object where column names are keys
        //and Revolt's porduct attrs are values
        ContentValues values = new ContentValues();
        values.put(ItyEntry.COLUMN_PRODUCT_NAME,"Revolt");
        values.put(ItyEntry.COLUMN_PRODUCT_SUPPLIER,"Nike");
        values.put(ItyEntry.COLUMN_PRODUCT_PRICE,400);
        values.put(ItyEntry.COLUMN_PRODUCT_QTY,10);
        values.put(ItyEntry.COLUMN_PRODUCT_QTYSOLD,0);

        //get uri for image
                Uri myUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.mipmap.revolt)
                        + '/' + getResources().getResourceTypeName(R.mipmap.revolt)
                        + '/' + getResources().getResourceEntryName(R.mipmap.revolt));

         //This will get the uri in a string format
                String img = myUri.toString();

                Log.e("CatalogActivity",img);

        values.put(ItyEntry.COLUMN_PRODUCT_IMG,img);

        Uri newUri = getContentResolver().insert(ItyEntry.CONTENT_URI,values);
    }


    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ItyEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from product database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items to app bar
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //user clicked on menu option
        switch (item.getItemId()){
            //response to click on "Insert dummy data"
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
                //response to click on "Delete all"
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO:Finish this then go to CreatorActivity
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItyEntry._ID,
                ItyEntry.COLUMN_PRODUCT_NAME,
                ItyEntry.COLUMN_PRODUCT_SUPPLIER,
                ItyEntry.COLUMN_PRODUCT_PRICE,
                ItyEntry.COLUMN_PRODUCT_QTY,
                ItyEntry.COLUMN_PRODUCT_QTYSOLD
        };
        return new CursorLoader(this,
                ItyEntry.CONTENT_URI,
                projection,null,
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
