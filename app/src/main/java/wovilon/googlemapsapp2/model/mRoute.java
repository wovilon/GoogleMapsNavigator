package wovilon.googlemapsapp2.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class mRoute {
    private ArrayList<LatLng> points=new ArrayList<>();
    private ArrayList<String> attributes=new ArrayList<>();
    private String[] polyline;
    private String jsonRoute;
    private int pointsNumber=0;


    public void addPoint(LatLng point, String address){
        pointsNumber++;
        points.add(point);
        attributes.add(address);
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
            polyline=pointsEncoded;
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

    public String[] getPolyline(){
        return polyline;
    }

    public String getFormatedAdress(int i){
        return attributes.get(i)+"";
    }

    public ArrayList<String> getAllFormatedAdresses(){
        return attributes;
    }

    public void removePoint(){
        int i=points.size()-1;
        points.remove(i);
        attributes.remove(i);
    }

    public String getJsonRoute(){
        return jsonRoute;
    }

    public String latitudesToJSON(){
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<points.size(); i++) {
            try{
                jsonArray.put(points.get(i).latitude);
            }catch (JSONException je){}

        }
        String StringJSON = jsonArray.toString();
        return StringJSON;
    }

    public ArrayList<String> latitudesFromJSON(String StringJSON){
        ArrayList<String> array=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(StringJSON);
            for (int i=0; i<points.size(); i++) {
                array.add(jsonArray.get(i).toString());
            }

        }catch (JSONException je){}
        return array;
    }

    public String longitudesToJSON(){
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<points.size(); i++) {
            try{
                jsonArray.put(points.get(i).longitude);
            }catch (JSONException je){}

        }
        String StringJSON = jsonArray.toString();
        return StringJSON;
    }

    public ArrayList<String> longitudesFromJSON(String StringJSON){
        ArrayList<String> array=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(StringJSON);
            for (int i=0; i<points.size(); i++) {
                array.add(jsonArray.get(i).toString());
            }

        }catch (JSONException je){}
        return array;
    }

    public void getPointsFromJSON(){

    }



}
