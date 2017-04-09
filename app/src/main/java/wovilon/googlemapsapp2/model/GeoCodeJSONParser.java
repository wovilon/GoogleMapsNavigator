package wovilon.googlemapsapp2.model;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GeoCodeJSONParser {
    String resultString;

    GeoCodeJSONParser(String resultString){
        this.resultString=resultString;
    }

    String getLatLng() {
        try {
            JSONObject jsonObj = new JSONObject(resultString);


        } catch (JSONException je) {Log.d("MyLOG", "JSONException occured");}



        String LatLngEncoded=null;
        return LatLngEncoded;
    }

}
