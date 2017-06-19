package com.logistikgo.imorales.testapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//hi
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnClickAPI(View view){
        Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
        final TextView textView = (TextView) findViewById(R.id.textView);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String strURL = "https://api.github.com/";
                String strFinalResponse = ".";
                String key;
                String value;
                String newText;

                URL githubEndpoint = null;

                try {
                    githubEndpoint = new URL(strURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpsURLConnection myConnection;

                try {
                    myConnection = (HttpsURLConnection)githubEndpoint.openConnection();
                    myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");

                    myConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                    myConnection.setRequestProperty("Contact-Me", "hathibelagal@example.com");

                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            key = jsonReader.nextName(); // Fetch the next key
                            value = jsonReader.nextString();
                            newText = key + " : "+ value;

                            strFinalResponse = strFinalResponse + newText;

                            jsonReader.skipValue(); // Skip values of other keys
                        }
                        textView.setText( strFinalResponse );

                        jsonReader.close();
                        myConnection.disconnect();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
