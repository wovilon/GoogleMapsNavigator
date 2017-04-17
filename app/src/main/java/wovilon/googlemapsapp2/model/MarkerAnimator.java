package wovilon.googlemapsapp2.model;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.concurrent.TimeUnit;

/**
 * Created by Администратор on 17.04.2017.
 */

public class MarkerAnimator extends AsyncTask {
    private GoogleMap mMap;
    private mRoute route;
    private Marker marker;
    Polyline polyline;
    int i=0;


    public MarkerAnimator(GoogleMap mMap, mRoute route){
        this.mMap=mMap;
        this.route=route;
        marker= mMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(route.getPolyline().getPoints().get(100)));

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        for(int i=0; i<route.getPolyline().getPoints().size(); i++) {
            publishProgress();
            try {
                this.i=i;
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException ie) {
            }
        }
        return null;
    }

    public void setIteration(int i){
        this.i=i;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        marker.setPosition(route.getPolyline().getPoints().get(i));

    }
}
