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

import java.io.IOException;
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
    EditText editViaje;
    String strUsuario;
    String strContrasena;
    String strIDViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        editUsuario = (EditText) findViewById(R.id.editUsuario);
        editContrasena = (EditText) findViewById(R.id.editContrasena);
        editViaje = (EditText) findViewById(R.id.editViaje);
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


    public void OnClickAPI(View view) throws ExecutionException, InterruptedException, JSONException {

        String strResult = "";

        //API PRODUCCION
        String strURL = "http://api.logistikgo.com/api/Usuarios/ValidarUsuario";
        //API DEBUG VISUAL STUDIO
//        String strURL = "http://10.0.2.2:63510/api/Usuarios/ValidarUsuario";
        strUsuario =  editUsuario.getText().toString();
        strContrasena = editContrasena.getText().toString();
        //DATOS DE USUARIO HARDCOREADOS
//        strUsuario = "dbarrientos@logistikgo";
//        strContrasena = "LGK123456";


        JSONObject jdata=new JSONObject();
        JSONObject jParams=new JSONObject();

        try {
            jdata.put("strURL",strURL);

            jParams.put("strUsuario",strUsuario);
            jParams.put("strContrasena",strContrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //VERIFICA SI HAY CONEXIÓN DE INTERNET
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");

            //REALIZA LA PETICION
//            strResult = GetResponse(jdata,jParams);
        }

        //ESTABLECER EL RESULTADO EN EL EDIT
        etResponse.setText(strResult);

        Toast.makeText(this, strResult, Toast.LENGTH_SHORT).show();
    }

    public void OnClickViaje(View view) throws ExecutionException, InterruptedException, JSONException {

        JSONObject resJson = null;

        //API PRODUCCION
        String strURL = "http://api.logistikgo.com/api/Viaje/GetDatosViaje";
        //API DEBUG VISUAL STUDIO
        strIDViaje =  editViaje.getText().toString();

        //VERIFICA SI HAY CONEXIÓN DE INTERNET
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");

            //REALIZA LA PETICION
            try {

                JSONObject jdata=new JSONObject();
                jdata.put("strURL",strURL);

                JSONObject jParams=new JSONObject();
                jParams.put("strIDViaje",editViaje.getText().toString());

                resJson = GetResponse(jdata,jParams);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //ESTABLECER EL RESULTADO EN EL EDIT
        etResponse.setText(resJson.getString("Nombre"));

//        Toast.makeText(this, strResult, Toast.LENGTH_SHORT).show();
    }

    public JSONObject GetResponse(JSONObject jdata,JSONObject jParams) throws ExecutionException, InterruptedException, JSONException {
        JSONObject resJson = null;

        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();

        resJson = getRequest.execute(jdata,jParams).get();

        return resJson;
    }

    public  JSONObject GetHttpResponse(String strURL, JSONObject jData,String strRequest_method, int read_timeout, int connection_timeout)  {
        String strRes = null;
        String inputLine;
        JSONObject jRes = null;
        JSONObject _jMeta = null;
        JSONObject _jData = null;
        JSONObject _jError = null;

        try {
            URL urlCurrent = new URL(strURL);
            HttpURLConnection connection =(HttpURLConnection)urlCurrent.openConnection();

            //Create a URL object holding our url
            //Create a connection
            connection.setRequestMethod(strRequest_method);
            connection.setReadTimeout(read_timeout);
            connection.setConnectTimeout(connection_timeout);

            //POST
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //ENCABEZADOS DE LA PETICIÓN
            //connection.setRequestProperty("User-Agent", "Fiddler");
            //connection.setRequestProperty("Host", "localhost:63510");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            //Connect to our url
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(jData.toString().getBytes("UTF-8"));
            os.close();

            int HttpResult = connection.getResponseCode();

            //VERIFICAR SI LA CONEXION SE REALIZO DE FORMA CORRECTA = 200
            if(HttpResult == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                StringBuilder stringBuilder = new StringBuilder();
                String strResponseMessage = connection.getResponseMessage();
                JsonReader jsonReader = new JsonReader(streamReader);

                //LEER JSON
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String strName = jsonReader.nextName(); // Fetch the next key
                    String strValue = jsonReader.nextString();

                    if (strName.equals("jMeta")) { // VERIFICA EL NOMBRE DEL CAMPO
                        _jMeta = new JSONObject();
                        _jMeta.put(strName, new JSONObject(strValue));
//                        stringBuilder.append(strValue);
                    }
                    else if(strName.equals("jData")){
                        _jData = new JSONObject();
                        _jData.put(strName,strValue);
                    }
                    else if(strName.equals("jDataError")){
                        _jError = new JSONObject();
                        _jError.put(strName,strValue);
                    }
                    else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
                jsonReader.close();

//                JSONObject _metaData = new JSONObject(_jMeta.getString("jMeta"));
//                _metaData.getString("Response");

                String strResponse = _jMeta.getString("Response");

                if(strResponse == "OK"){
                    jRes = _jData;
                }
                else{
                    throw new IOException(_jMeta.getString("Message"));
                }


//                strRes = stringBuilder.toString();

                //Create a new buffered reader and String Builder
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                //Check if the line we are reading is not null
//                while((inputLine = reader.readLine()) != null){
//                    stringBuilder.append(inputLine);
//                }
//                //Close our InputStream and Buffered reader
//                reader.close();
//                streamReader.close();
//                //Set our result equal to our stringBuilder
//                strRes = stringBuilder.toString();
            }
            else{
                String strResponse = connection.getResponseMessage();
                InputStreamReader streamError = new InputStreamReader(connection.getErrorStream());
                JsonReader jsonReader = new JsonReader(streamError);

                //LEER JSON
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("Message")) { // VERIFICA EL NOMBRE DEL CAMPO
                        strRes = jsonReader.nextString();
                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
                jsonReader.close();

                Log.d("ERROR",strResponse);

//                StringBuilder stringBuilder = new StringBuilder();
//               BufferedReader reader = new BufferedReader(streamError);
//                while((inputLine = reader.readLine()) != null){
//                    stringBuilder.append(inputLine);
//                }
//                JsonReader jsonReader = new JsonReader(streamError);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jRes;
    }

    // VERIFICAR SI EXISTE CONEXIÓN A INTERNET
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    //CLASS ASYNC REQUEST
    public class HttpGetRequest extends AsyncTask<JSONObject, Void, JSONObject> {

        //VARIABLES DE CONFIGURACION DE LA CONEXION
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected JSONObject doInBackground(JSONObject... jObject) {
            String stringUrl = null;
            JSONObject resJson = null;

            try {
                stringUrl = jObject[0].getString("strURL");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String result = null;
            String inputLine;

            try {

                resJson = GetHttpResponse(stringUrl,jObject[1],REQUEST_METHOD,READ_TIMEOUT,CONNECTION_TIMEOUT);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resJson;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
        }

    }
}
