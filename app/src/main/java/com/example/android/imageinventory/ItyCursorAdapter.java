package com.example.android.imageinventory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.imageinventory.data.ItyContract.ItyEntry;

import java.io.File;
import java.net.URI;

/**
 * {@link ItyCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ItyCursorAdapter extends CursorAdapter{



    /**
     * Constructs a new {@link ItyCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItyCursorAdapter(Context context, Cursor c){
        super(context,c,0 /*flags*/);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //find individual views we want modified in list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.listitemName);
        TextView inStockTextView = (TextView) view.findViewById(R.id.inStock);
        TextView inSoldTextView = (TextView) view.findViewById(R.id.inSold);
        TextView priceTextView = (TextView) view.findViewById(R.id.listitemPrice);
        ImageView imgView = (ImageView) view.findViewById(R.id.listitemImg);

        //find columns of inventory attr we are interested in
        int nameColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_SUPPLIER);
        int inStockColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_QTY);
        int inSoldColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_QTYSOLD);
        int priceColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_PRICE);
        int imgColumnIndex = cursor.getColumnIndex(ItyEntry.COLUMN_PRODUCT_IMG);

        //read product attr from cursor for the current pet
        String productName = cursor.getString(nameColumnIndex);
        String productSupplier = cursor.getString(supplierColumnIndex);
        String productStock = cursor.getString(inStockColumnIndex);
        String productSold = cursor.getString(inSoldColumnIndex);
        String productPrice= cursor.getString(priceColumnIndex);
        String productImg= cursor.getString(imgColumnIndex);


        //if any is empty
        if(TextUtils.isEmpty(productName)){
            productName = "Unknown";
        }
        if(TextUtils.isEmpty(productSupplier)){
            productSupplier = "Unknown";
        }
        if(TextUtils.isEmpty(productStock)){
            productStock = "0";
        }
        if(TextUtils.isEmpty(productSold)){
            productSold = "0";
        }
        if(TextUtils.isEmpty(productPrice)){
            productPrice = "0";
        }
        if(TextUtils.isEmpty(productImg)){
            Uri myUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.getResources().getResourcePackageName(R.mipmap.revolt)
                    + '/' + context.getResources().getResourceTypeName(R.mipmap.revolt)
                    + '/' + context.getResources().getResourceEntryName(R.mipmap.revolt));

            //This will get the uri in a string format
            productImg = myUri.toString();
        }


        //update textviews
        nameTextView.setText(productName+" by "+productSupplier);
        inStockTextView.setText(productStock+" in stock");
        inSoldTextView.setText(" - "+productSold+" in sold");
        priceTextView.setText("$"+productPrice+".00");

        Log.e("this",productImg);
        //turn uri to string
        Uri myUri = Uri.parse(productImg);
         imgView.setImageURI(myUri);
    }
}
