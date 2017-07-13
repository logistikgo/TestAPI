package com.logistikgo.imorales.testapi;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText etResponse;
    TextView tvIsConnected;
    EditText editUsuario;
    EditText editContrasena;
    String strUsuario;
    String strContrasena;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String test = "";

        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        editUsuario = (EditText) findViewById(R.id.editUsuario);
        editContrasena = (EditText) findViewById(R.id.editContrasena);

//        if(isConnected()){
//            tvIsConnected.setBackgroundColor(0xFF00CC00);
//            tvIsConnected.setText("You are conncted");
//        }
//        else{
//            //tvIsConnected.setText("You are NOT conncted");
//        }

        // show response on the EditText etResponse
//        try {
//                String strURL = "http://api.logistikgo.com/api/Usuarios/ValidarUsuario";
//                test = GetResponse(strURL);
//                etResponse.setText(test);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//        }
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

    public class HttpGetRequest extends AsyncTask<String, Void, String> {

        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            String result = null;
            String inputLine;

            try {

                result = GetHttpResponse(stringUrl,REQUEST_METHOD,READ_TIMEOUT,CONNECTION_TIMEOUT);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public  String GetHttpResponse(String strURL, String strRequest_method, int read_timeout, int connection_timeout)  {
        String strRes = null;
        String inputLine;

        try {
            URL urlCurrent = new URL(strURL);
            HttpURLConnection connection =(HttpURLConnection)urlCurrent.openConnection();

            JSONObject jdata=new JSONObject();
            jdata.put("strUsuario",strUsuario);
            jdata.put("strContrasena", strContrasena);

            //Create a URL object holding our url
            //Create a connection
            connection.setRequestMethod(strRequest_method);
            connection.setReadTimeout(read_timeout);
            connection.setConnectTimeout(connection_timeout);

            connection.setDoOutput(true);
            connection.setDoInput(true);
            //connection.setRequestProperty("User-Agent", "Fiddler");
            //connection.setRequestProperty("Host", "localhost:63510");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            //Connect to our url
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(jdata.toString().getBytes("UTF-8"));
            os.close();

            int HttpResult = connection.getResponseCode();

            if(HttpResult == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                strRes = stringBuilder.toString();
            }
            else{
                String strResponse = connection.getResponseMessage();

                InputStreamReader streamError = new InputStreamReader(connection.getErrorStream());

                JsonReader jsonReader = new JsonReader(streamError);

//                BufferedReader reader = new BufferedReader(streamError);

                StringBuilder stringBuilder = new StringBuilder();

//                while((inputLine = reader.readLine()) != null){
//                    stringBuilder.append(inputLine);
//                }

//                JsonReader jsonReader = new JsonReader(streamError);

                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("Message")) { // Check if desired key
                        // Fetch the value as a String
                        strRes = jsonReader.nextString();

                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }

                jsonReader.close();

//                strRes = stringBuilder.toString();

                Log.d("ERROR",strResponse);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strRes;
    }

    public String GetResponse(final String strUrl) throws ExecutionException, InterruptedException {
        String strRes = "";

        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        strRes = getRequest.execute(strUrl).get();

        return strRes;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    // check network connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void OnClickAPI(View view) throws ExecutionException, InterruptedException {

        String strResult = "";
        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        String strURL = "http://api.logistikgo.com/api/Usuarios/ValidarUsuario";
//        String strURL = "http://10.0.2.2:63510/api/Usuarios/ValidarUsuario";
////
        strUsuario =  editUsuario.getText().toString();
        strContrasena = editContrasena.getText().toString();
//
//        strUsuario = "dbarrientos@logistikgo";
//        strContrasena = "LGK123456";

        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
            strResult = GetResponse(strURL);
        }
        etResponse.setText(strResult);
        Toast.makeText(this, strResult, Toast.LENGTH_SHORT).show();
    }
}
