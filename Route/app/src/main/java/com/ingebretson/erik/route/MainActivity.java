package com.ingebretson.erik.route;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchShortestRoute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchShortestRoute() {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String shortestRoute = "?";
                try {

                    StringBuilder stringBuilder = new StringBuilder();
                    HttpPost httppost = new HttpPost(getUrl());

                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }

                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                    JSONArray array = jsonObject.getJSONArray("routes");

                    JSONObject routes = array.getJSONObject(0);

                    JSONArray legs = routes.getJSONArray("legs");

                    JSONObject steps = legs.getJSONObject(0);

                    JSONObject distance = steps.getJSONObject("distance");

                    Log.i("Distance", distance.toString());
//            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }

                return shortestRoute;
            }

            @Override
            protected void onPostExecute(String shortestRoute) {
                super.onPostExecute(shortestRoute);
                ((TextView)findViewById(R.id.route_label)).setText(shortestRoute);

            }
        }.execute();
    }

    private String getOrigin() {
        return "";
    }

    private String getDestination() {
        return "";
    }

    private String getUrl() {
        // todo - find out how to use key from R.something
        String url = "";
        try {
            String apiKey = "";
            String origin = URLEncoder.encode(getOrigin(), "utf-8");
            String destination = URLEncoder.encode(getDestination(), "utf-8");
            url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + origin +
                    "&destination=" + destination +
                    "&key=" + apiKey;
        } catch (UnsupportedEncodingException ignore) {
        }
        return url;
    }
}
