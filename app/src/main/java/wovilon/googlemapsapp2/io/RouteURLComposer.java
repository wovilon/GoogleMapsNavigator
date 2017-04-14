package wovilon.googlemapsapp2.io;

import android.content.Context;
import android.content.res.Resources;

import java.net.MalformedURLException;
import java.net.URL;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.model.mRoute;

/**
 * Created by Администратор on 14.04.2017.
 */

public class RouteURLComposer {
mRoute route;
    Context context;

    public RouteURLComposer(Context context, mRoute route){
        this.route=route;
        this.context=context;
    }

    public URL formURL(){
        //url for GoogleMaps API
        //add origin and destination place
        String origin=""+route.getPoint(0).latitude+","+route.getPoint(0).longitude;
        String destination=""+route.getPoint(route.getPoints().size()-1).latitude
                +","+route.getPoint(route.getPoints().size()-1).longitude;
        //add waypoints if exist
        String waypoints="";
        if (route.getPoints().size()>2) {
            waypoints="&waypoints=";
            for (int i = 1; i < route.getPoints().size()-1; i++) {
                waypoints+="via:"+route.getPoint(i).latitude+","+route.getPoint(i).longitude;
                if (i<route.getPoints().size()-2){waypoints+="|";}
            }
        }
        //form URL for directions API
        URL url=null;
        try {

             url = new URL("https://maps.googleapis.com/maps/api/directions/" +
                    "json?origin="+origin+"&destination="+destination+waypoints+"&key="
                    + context.getString(R.string.google_maps_key));

        } catch (MalformedURLException me) {}
        return url;
    }
}
