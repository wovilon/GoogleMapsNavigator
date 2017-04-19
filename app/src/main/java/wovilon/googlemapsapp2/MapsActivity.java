package wovilon.googlemapsapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;
import java.util.ArrayList;

import wovilon.googlemapsapp2.adapters.GooglePlacesAdapter;
import wovilon.googlemapsapp2.db.DbUpdator;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;
import wovilon.googlemapsapp2.io.GeocodeJSONParser;
import wovilon.googlemapsapp2.io.RouteURLComposer;
import wovilon.googlemapsapp2.model.MarkerAnimator;
import wovilon.googlemapsapp2.model.RouteDrawer;
import wovilon.googlemapsapp2.model.mRoute;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> placesResultList;
    GooglePlacesAdapter adapterStart;
    GooglePlacesAdapter adapterNextPoint;
    String currentPointJSON;
    mRoute route;
    Context context=this;

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

        /*Animation animation= AnimationUtils.loadAnimation(this, R.anim.linear);
        animation.reset();
        Button btRemove=(Button)findViewById(R.id.ButtonDelete);
        btRemove.clearAnimation();
        btRemove.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        btRemove.clearAnimation();
        btRemove.startAnimation(animation);*/

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
        try{
            URL url=new RouteURLComposer(this,route).formURL();
            //make http request with above url to get route from Google API
            AsynkRouteRequest asynkRouteRequest = new AsynkRouteRequest(this, url, asynkTaskHandler);
            asynkRouteRequest.execute();
            //hide keyboard
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }catch (NullPointerException ne){
            //do nothing, so that not enough points are added
        }


    }

    //works when AsynkRouteRequest finished
    AsynkTaskHandler asynkTaskHandler=new AsynkTaskHandler() {
        @Override
        public void onAsynkTaskFinish(String resultString) {
            new RouteDrawer().drawRoute(mMap, route.buildPolyline(resultString));
            //add route to database
            new DbUpdator(context).addRouteToDb(route);

            route.setPolyjine(route.buildPolyline(route.getJsonRoute()));
            MarkerAnimator markerAnimator=new MarkerAnimator(getBaseContext(), mMap, route);
            markerAnimator.execute();
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
        mMap.clear();
        if (route!=null) {
            route.removePoint();
            new RouteDrawer().drawPoints(route, mMap);
        }

    }


    public void onBtMyRoutesClick(View view) {
        Intent routesActivityIntent=new Intent(MapsActivity.this, MyRoutesActivity.class);
        startActivityForResult(routesActivityIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int routeId = Integer.parseInt(data.getStringExtra("id"));
            if (route == null) {
                route = new mRoute();
            }

            mMap.clear();
            DbUpdator db = new DbUpdator(this);
            String stringRouteJSON = db.getRouteJSONFromDb(routeId);
            route.setPolyjine(route.buildPolyline(stringRouteJSON));
            route.setPoints(db.getLatLng(routeId));
            new RouteDrawer().drawRoute(mMap, route.getPolyline());
            for (int i = 0; i < db.getLatLng(routeId).size(); i++) {

                //mMap.addMarker(new MarkerOptions().position(route.getPoint(i)));
            }
            LatLngBounds bounds;
            try {
                bounds = new LatLngBounds(
                        route.getPolyline().getPoints().get(route.getPolyline().getPoints().size() - 1),
                        route.getPolyline().getPoints().get(0));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(route.getPolyline().getPoints().get(0)));
            } catch (IllegalArgumentException e) {
                bounds = new LatLngBounds(
                        route.getPolyline().getPoints().get(0),
                        route.getPolyline().getPoints().get(route.getPolyline().getPoints().size() - 1));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(route.getPolyline().getPoints().get(0)));
            }


            MarkerAnimator markerAnimator = new MarkerAnimator(this, mMap, route);
            markerAnimator.execute();

        /*for(int i=0; i<route.getPolyline().getPoints().size(); i++){
            markerAnimator.setIteration(i);
            markerAnimator.execute();
        }*/
        }catch (NullPointerException ne){//do nothing, so that no route choosen}
    }


}}