package wovilon.googlemapsapp2.io;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;


public class GeocodeJSONParser {
    private JSONObject jsonObject;
    private String jsonString;


    public GeocodeJSONParser(String resultString){
        try {
            this.jsonObject=new JSONObject(resultString);
        }catch (JSONException je){
            Log.d("MyLOG","JSONException in AsynkTask while geogoding");}
    this.jsonString=resultString;
    }


    public String getFormatedAdress(){
        try {
            return jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        }catch (JSONException je){
            Log.d("MyLOG", "JSONException in AsynkTask while geogoding");
            return  null;}
    }

    public LatLng getLatLng(){

        try {
            String longitude = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            String latitude = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

           return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        }catch (JSONException je){
            Log.d("MyLOG", "JSONException in AsynkTask while geogoding");
            return null;}
    }


    public String getJSONObject(){
        return jsonString;
    }

}
