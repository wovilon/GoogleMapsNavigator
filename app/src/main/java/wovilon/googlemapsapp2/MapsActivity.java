package wovilon.googlemapsapp2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import wovilon.googlemapsapp2.adapters.GooglePlacesAdapter;
import wovilon.googlemapsapp2.google_libraries.PolyUtil;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //start point autocomplete. Here we define an adapter
        AutoCompleteTextView startEdit=(AutoCompleteTextView)findViewById(R.id.StartAutocomplete);
        GooglePlacesAdapter adapterStart = new GooglePlacesAdapter(this,startEdit,
                android.R.layout.simple_dropdown_item_1line, mMap,startAsynkHandler);
        startEdit.setAdapter(adapterStart);

        AutoCompleteTextView ToEdit=(AutoCompleteTextView)findViewById(R.id.ToAutocomplete);
        GooglePlacesAdapter adapterTo = new GooglePlacesAdapter(this,ToEdit,
                android.R.layout.simple_dropdown_item_1line, mMap,startAsynkHandler);
        ToEdit.setAdapter(adapterTo);

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
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng place=new LatLng(-34,150);
        mMap.addMarker(new MarkerOptions().position(place).title("MyMarker"));

        PolylineOptions line=new PolylineOptions();
        line.add(sydney);
        line.add(place);
        mMap.addPolyline(line);
        try{googleMap.setMyLocationEnabled(true);}
        catch (SecurityException se) {}

        */
    }

    public void onBtGoClick(View view) {
        //url for GoogleMaps API
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/" +
                    "json?origin=Toronto&destination=Montreal&key=" + getString(R.string.google_maps_key));
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
            }catch(JSONException je){Log.d("MyLOG", "JSONException in AsynkTask");}

            drawRoute(mMap, pointsEncoded);
        }
    };

    //interface realization for geocoding request case.Here we parse JSON
    AsynkTaskHandler startAsynkHandler=new AsynkTaskHandler() {
        @Override
        public void onAsynkTaskFinish(String resultString) {
        System.exit(0);
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