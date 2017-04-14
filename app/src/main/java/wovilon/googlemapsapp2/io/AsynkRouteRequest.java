package wovilon.googlemapsapp2.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.google_libraries.PolyUtil;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;


public class AsynkRouteRequest extends AsyncTask {
    private URL url;
    private String resultString;
    String mResultString;
    private String apiKey;
    private List<LatLng> points;
    String[] pointsEncoded;
    public AsynkTaskHandler asynkTaskHandler;
    Context context;

    public AsynkRouteRequest(Context context, URL url, AsynkTaskHandler asynkTaskHandler){
        this.url=url;
        this.context=context;
        this.asynkTaskHandler=asynkTaskHandler;
        this.apiKey=context.getString(R.string.google_maps_key);
    }

    @Override
    protected void onPreExecute() {

            super.onPreExecute();
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            byte[] data;
            InputStream is;

            try {
                //set internet connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                //OutputStream os = conn.getOutputStream();

                conn.connect();
                int responseCode= conn.getResponseCode();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //get response and JSON
                if (responseCode == 200) {
                    is = conn.getInputStream();

                    byte[] buffer = new byte[8192]; // Такого вот размера буфер
                    // Далее, напр имер, вот так читаем ответ
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    //get JSON data
                    data = baos.toByteArray();
                    //whole JSON
                    resultString = new String(data, "UTF-8");
                    //Log.d("MyLOG",resultString);

                } else {resultString="Server connection error";
                }
            conn.disconnect();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

                //push result to main thread
        asynkTaskHandler.onAsynkTaskFinish(resultString);
    }
}
