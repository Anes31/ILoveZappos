package com.example.hp.ilovezappos;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductPreview extends AppCompatActivity {

    public String term;
    private String TAG = MainActivity.class.getSimpleName();
    Map<String, String> product = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Log.e("SearchActivity Toolbar", "You have got a NULL POINTER EXCEPTION");
        }

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
            term = extras.getString("term");
            //Toast.makeText(this, term, Toast.LENGTH_LONG).show();
            new getJson().execute();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, product.get("productName")+ " has been added to your cart !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private class getJson extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(term);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("results");

                    // getting the first product data
                    JSONObject c = results.getJSONObject(0);

                    String brandName = c.getString("brandName");
                    String imgURL = c.getString("thumbnailImageUrl");
                    String productName = c.getString("productName");
                    String price = c.getString("price");
                    String originalPrice = c.getString("originalPrice");
                    String productURL = c.getString("productUrl");


                    // adding each child node to HashMap key => value
                    product.put("brandName", brandName);
                    product.put("imgURL", imgURL);
                    product.put("productName", productName);
                    product.put("Price", price);
                    product.put("originalPrice", originalPrice);
                    product.put("productURL", productURL);


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            TextView bn = (TextView) findViewById(R.id.brandName);
            TextView pn = (TextView) findViewById(R.id.productName);
            TextView pr = (TextView) findViewById(R.id.price);

            /**/

            bn.setText(product.get("brandName"));
            pn.setText(product.get("productName"));
            pr.setText(product.get("Price"));

            String img_url = product.get("imgURL");
            new DownloadImageTask((ImageView) findViewById(R.id.imageView)) .execute(img_url);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}