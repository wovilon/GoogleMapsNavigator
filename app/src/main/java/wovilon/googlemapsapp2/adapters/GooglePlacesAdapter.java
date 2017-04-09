package wovilon.googlemapsapp2.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.maps.GoogleMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;
import wovilon.googlemapsapp2.model.MyFilter;

//here we define an adapter for autocomplete for geocoding
public class GooglePlacesAdapter extends ArrayAdapter implements Filterable {
    URL url;
    private ArrayList resultList;
    AutoCompleteTextView autoCompleteTextView;
    Context context;
    GoogleMap mMap;
    AsynkTaskHandler startAsynkHandler;

    public GooglePlacesAdapter(Context context, AutoCompleteTextView autoCompleteTextView,
                               int resource,GoogleMap mMap, AsynkTaskHandler startAsynkHandler) {
        super(context, resource);
        this.context=context;
        this.autoCompleteTextView=autoCompleteTextView;
        this.mMap=mMap;
        this.startAsynkHandler=startAsynkHandler;
    }



    @Override
    public Filter getFilter(){
        MyFilter myFilter=new MyFilter(context,url,autoCompleteTextView,startAsynkHandler);

        return myFilter;
    }

}
