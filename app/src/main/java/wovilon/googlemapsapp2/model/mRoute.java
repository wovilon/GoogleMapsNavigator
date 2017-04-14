package wovilon.googlemapsapp2.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class mRoute {
    private ArrayList<LatLng> points=new ArrayList<>();
    private ArrayList<String> atributes=new ArrayList<>();
    private String jsonRoute;
    private int pointsNumber=0;


    public void addPoint(LatLng point, String adress){
        pointsNumber++;
        points.add(point);
        atributes.add(adress);

    }


    public String[] buildPolyline(String resultString){

        jsonRoute=resultString;
        String[] pointsEncoded;
        try {
            //get route from JSON
            JSONObject jsonObj = new JSONObject(resultString);
            JSONArray jsonRoutes = jsonObj.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            pointsEncoded = new String[jsonRoutes.length()];

            for (int i = 0; i < jsonRoutes.length(); i++) {
                String polyline = jsonRoutes.getJSONObject(i)
                        .getJSONObject("polyline").getString("points");
                pointsEncoded[i] = polyline.toString();
            }
            return pointsEncoded;

        }catch(JSONException je){
            Log.d("MyLOG", "JSONException in AsynkTask while routing");
            return null; }
    }

    public String getPointsString(){

        String result="res= ";
        for(int i=0; i<pointsNumber; i++){
            result+="Point "+i+" Lat="+points.get(i).latitude+", Lng="+points.get(i).longitude;
        }
        return result;
    }

    public ArrayList<LatLng> getPoints(){
        return points;
    }

    public LatLng getPoint(int i){
        return points.get(i);
    }

    public String getFormatedAdress(int i){
        return atributes.get(i);
    }

    public void removePoint(){
        int i=points.size()-1;
        points.remove(i);
        atributes.remove(i);
    }


}
