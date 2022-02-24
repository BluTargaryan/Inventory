package com.example.android.imageinventory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**To hide action bar*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //setup item click listener to redirect to catalog

        //get arrow view
        ImageView nextView = (ImageView) findViewById(R.id.nextpage);

        //setup item click listener on view
        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //create intent to go to CatalogActivity
                Intent intent = new Intent(MainActivity.this,CatalogActivity.class);

                //launch intent
                startActivity(intent);
                /**
                 * Check if the code above has error later
                 * */
            }
        });
    }
}