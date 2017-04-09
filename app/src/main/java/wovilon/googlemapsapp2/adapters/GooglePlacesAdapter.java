package wovilon.googlemapsapp2.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;


public class GooglePlacesAdapter extends ArrayAdapter implements Filterable {
    URL url;
    private ArrayList resultList;
    AutoCompleteTextView autoCompleteTextView;
    Context context;

    public GooglePlacesAdapter(Context context, AutoCompleteTextView autoCompleteTextView, int resource) {
        super(context, resource);
        this.context=context;
        this.autoCompleteTextView=autoCompleteTextView;

    }

    //interface realization for geocoding request case
    AsynkTaskHandler asynkTaskHandler=new AsynkTaskHandler() {
        @Override
        public void onAsynkTaskFinish(String pointsEncoded) {
        }
    };

    @Override
    public Filter getFilter(){
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults=new FilterResults();
                if (charSequence!=null){
                    //String adress=context.autoCompleteTextView.getText().toString();
                    try {
                        url = new URL("https://maps.googleapis.com/maps/api/geocode/" +
                                "json?address="+"Toronto"+"&key="
                                + context.getString(R.string.google_maps_key));
                    }catch (MalformedURLException me){Log.d("MyLOG","URL while geocoding problem");}

                    AsynkRouteRequest asynkRouteRequest=new AsynkRouteRequest(context, url, asynkTaskHandler);
                    asynkRouteRequest.execute();
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };

        return filter;
    }

}
