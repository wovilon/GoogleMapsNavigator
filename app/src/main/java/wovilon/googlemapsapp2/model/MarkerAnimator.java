package wovilon.googlemapsapp2.model;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.TimeUnit;

import wovilon.googlemapsapp2.R;


public class MarkerAnimator extends AsyncTask {
    private GoogleMap mMap;
    private mRoute route;
    private Marker marker;
    PolylineOptions polyline;
    int i=0;
    double polylineLength=0;
    private  Context context;
    Bitmap car;


    public MarkerAnimator(Context context, GoogleMap mMap, mRoute route){
        this.mMap=mMap;
        this.route=route;
        this.context=context;
        car= BitmapFactory.decodeResource(this.context.getResources(), R.drawable.car_front);
        marker= mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(car))
                .position(route.getPolyline().getPoints().get(0)));
        marker.setAnchor((float)0.5,(float)0.5);
        polyline=route.getPolyline();



        /*polyline=route.getPolyline();

        for(int i=1; i<polyline.getPoints().size(); i++){
            polylineLength+=Math.pow(
                    (Math.pow(polyline.getPoints().get(i).latitude
                    -polyline.getPoints().get(i-1).latitude,2))

                    +(Math.pow(polyline.getPoints().get(i).longitude
                            -polyline.getPoints().get(i-1).longitude, 2))
                    ,0.5);
        }
        Log.d("MyLOG", polylineLength+"");*/
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        /*int k=0; //number of current polyline interval
        int j=10000; //total animation time
        int dt=10; //time interval, milliseconds
        int nmax=j/dt; //number of time intervals
        double length=0; //length of polyline in current point

        for(int n=1; n<=nmax; n++){
            length=polylineLength/nmax*n;

            while(getIntervalNumber(polylineLength*(n/nmax))<length){
            }
        }*/


        for(int i=1; i<route.getPolyline().getPoints().size(); i++) {
            publishProgress();
            try {
                this.i=i;
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException ie) {
            }
        }
        return null;
    }

    /*public void setIteration(int i){
        this.i=i;
    }*/

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        marker.setPosition(route.getPolyline().getPoints().get(i));

    }

    /*double getIntervalNumber(double length){

        return length;
    }*/
}
