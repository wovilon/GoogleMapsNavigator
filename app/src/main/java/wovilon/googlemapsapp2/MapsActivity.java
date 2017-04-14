package wovilon.googlemapsapp2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.instantapps.internal.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wovilon.googlemapsapp2.adapters.GooglePlacesAdapter;
import wovilon.googlemapsapp2.google_libraries.PolyUtil;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;
import wovilon.googlemapsapp2.io.GeocodeJSONParser;
import wovilon.googlemapsapp2.model.mRoute;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> placesResultList;
    GooglePlacesAdapter adapterStart;
    GooglePlacesAdapter adapterNextPoint;
    String currentPointJSON;
    mRoute route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //start point autocomplete. Here we define an adapter as well
        AutoCompleteTextView startEdit=(AutoCompleteTextView)findViewById(R.id.StartAutocomplete);
        placesResultList=new ArrayList<>();
        adapterStart = new GooglePlacesAdapter(this,
                android.R.layout.simple_dropdown_item_1line, placesResultList, startEdit, mMap, geocodeAsynkHandler);
        startEdit.setAdapter(adapterStart);
        startEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GeocodeJSONParser point = new GeocodeJSONParser(currentPointJSON);
                mMap.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getFormatedAdress()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point.getLatLng()));
                route=new mRoute();
                route.addPoint(point.getLatLng());
                Log.d("MyLOG", route.getPointsString());

                }
        });


        //next points autocomplete
        AutoCompleteTextView toEdit=(AutoCompleteTextView)findViewById(R.id.ToAutocomplete);

        adapterNextPoint = new GooglePlacesAdapter(this,
                android.R.layout.simple_dropdown_item_1line, placesResultList, toEdit, mMap, geocodeAsynkHandler);
        toEdit.setAdapter(adapterNextPoint);
        toEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GeocodeJSONParser point = new GeocodeJSONParser(currentPointJSON);
                mMap.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getFormatedAdress()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point.getLatLng()));
                route.addPoint(point.getLatLng());
                Log.d("MyLOG", route.getPointsString());
            }
        });

    }

    public void onClickTest(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near     Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void onBtGoClick(View view) {
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
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/" +
                    "json?origin="+origin+"&destination="+destination+waypoints+"&key="
                    + getString(R.string.google_maps_key));
            //make http request with above url to get route from Google API
            AsynkRouteRequest asynkRouteRequest = new AsynkRouteRequest(this, url, asynkTaskHandler);
            asynkRouteRequest.execute();
        } catch (MalformedURLException me) {}

    }

    //works when AsynkRouteRequest finished
    AsynkTaskHandler asynkTaskHandler=new AsynkTaskHandler() {
        String[] pointsEncoded;

        @Override
        public void onAsynkTaskFinish(String resultString) {

            drawRoute(mMap, route.buildPolyline(resultString));
        }

    };


    //interface realization for geocoding request case finish. Here we parse JSON
    AsynkTaskHandler geocodeAsynkHandler=new AsynkTaskHandler() {
        @Override
        public void onAsynkTaskFinish(String resultString) {
            try {
                currentPointJSON=resultString;
                GeocodeJSONParser point = new GeocodeJSONParser(resultString);
                placesResultList.clear();
                placesResultList.add(point.getFormatedAdress());
                adapterStart.notifyDataSetChanged();
                adapterNextPoint.notifyDataSetChanged();
            }catch (NullPointerException ne){Log.d("MyLOG", "NullPointer in geocodingAsynkTastHandler");}
        }

    };



    public void drawRoute(GoogleMap googleMap, String[] pointsEncoded){
        //draw the route
        LatLng start = PolyUtil.decode(pointsEncoded[0]).get(0);
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(PolyUtil.decode(pointsEncoded[0]).get(0)));

        int lastPointIndex=PolyUtil.decode(pointsEncoded[pointsEncoded.length-1]).size()-1;
        LatLng place=PolyUtil.decode(pointsEncoded[pointsEncoded.length-1]).get(lastPointIndex);
        mMap.addMarker(new MarkerOptions().position(place).title("Finish"));

        PolylineOptions line=new PolylineOptions();
        line.color(R.color.colorPolyline);

        for(int k=0; k<pointsEncoded.length; k++) {
            List<LatLng> points=PolyUtil.decode(pointsEncoded[k]);
            for (int i = 0; i < points.size(); i++) {
                line.add(points.get(i));
            }
        }

        mMap.addPolyline(line);
        try{googleMap.setMyLocationEnabled(true);}
        catch (SecurityException se) {}



    }
}