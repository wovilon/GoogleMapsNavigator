package wovilon.googlemapsapp2.model;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.google_libraries.PolyUtil;

/**
 * Created by Администратор on 14.04.2017.
 */

public class RouteDrawer {

    public void drawRoute(GoogleMap mMap, String[] pointsEncoded){
        //draw the route


        PolylineOptions line=new PolylineOptions();
        line.color(R.color.colorPolyline);

        for(int k=0; k<pointsEncoded.length; k++) {
            List<LatLng> points= PolyUtil.decode(pointsEncoded[k]);
            for (int i = 0; i < points.size(); i++) {
                line.add(points.get(i));
            }
        }

        mMap.addPolyline(line);
        try{mMap.setMyLocationEnabled(true);}
        catch (SecurityException se) {}
    }

    public void drawPoints(mRoute route, GoogleMap mMap){
        for (int i=0; i<route.getPoints().size(); i++){
            mMap.addMarker(new MarkerOptions().position(route.getPoint(i)).title(route.getFormatedAdress(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(route.getPoint(i)));
        }
    }
}
