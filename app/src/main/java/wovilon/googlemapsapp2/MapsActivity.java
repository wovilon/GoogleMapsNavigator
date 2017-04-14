package wovilon.googlemapsapp2;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import wovilon.googlemapsapp2.io.RouteURLComposer;
import wovilon.googlemapsapp2.model.RouteDrawer;
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
                route.addPoint(point.getLatLng(),point.getFormatedAdress());

                }
        });


        //next points autocomplete
        final AutoCompleteTextView toEdit=(AutoCompleteTextView)findViewById(R.id.ToAutocomplete);

        adapterNextPoint = new GooglePlacesAdapter(this,
                android.R.layout.simple_dropdown_item_1line, placesResultList, toEdit, mMap, geocodeAsynkHandler);
        toEdit.setAdapter(adapterNextPoint);
        toEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GeocodeJSONParser point = new GeocodeJSONParser(currentPointJSON);
                mMap.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getFormatedAdress()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point.getLatLng()));
                route.addPoint(point.getLatLng(),point.getFormatedAdress());
                toEdit.setText("");

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
        URL url=new RouteURLComposer(this,route).formURL();
        //make http request with above url to get route from Google API
        AsynkRouteRequest asynkRouteRequest = new AsynkRouteRequest(this, url, asynkTaskHandler);
        asynkRouteRequest.execute();
        //hide keyboard
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //works when AsynkRouteRequest finished
    AsynkTaskHandler asynkTaskHandler=new AsynkTaskHandler() {


        @Override
        public void onAsynkTaskFinish(String resultString) {
            new RouteDrawer().drawRoute(mMap, route.buildPolyline(resultString));
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

    public void onBtRemovePointClick(View view) {
        route.removePoint();
        mMap.clear();
        new RouteDrawer().drawPoints(route,mMap);
    }
}