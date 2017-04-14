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


//here we define an adapter for autocomplete for geocoding
public class GooglePlacesAdapter extends ArrayAdapter implements Filterable {
    URL url;
    private ArrayList<String> resultList=new ArrayList<>();
    AsynkTaskHandler geocodeAsynkHandler;
    AutoCompleteTextView autoCompleteTextView;
    Context context;
    GoogleMap mMap;
    static String[] objects = new String[] {
            "Belgium", "France"
    };


    public GooglePlacesAdapter(Context context, int resource, ArrayList<String> resultList,
                               AutoCompleteTextView autoCompleteTextView, GoogleMap mMap,
                               AsynkTaskHandler geocodeAsynkHandler) {
        super(context, resource, resultList);

        this.context=context;
        this.autoCompleteTextView=autoCompleteTextView;
        this.mMap=mMap;
        this.geocodeAsynkHandler=geocodeAsynkHandler;

    }


    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                String adress=autoCompleteTextView.getText().toString();
                if (constraint!=null){
                    try {
                        url = new URL("https://maps.googleapis.com/maps/api/geocode/" +
                                "json?address="+adress+"&key="
                                + context.getString(R.string.google_maps_key));
                    }catch (MalformedURLException me){
                        Log.d("MyLOG","URL while geocoding problem");}

                    AsynkRouteRequest asynkRouteRequest=new AsynkRouteRequest(context, url, geocodeAsynkHandler);
                    asynkRouteRequest.execute();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };

        return filter;
    }

}
