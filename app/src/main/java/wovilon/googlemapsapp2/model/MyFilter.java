package wovilon.googlemapsapp2.model;

import android.content.Context;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;

import java.net.MalformedURLException;
import java.net.URL;

import wovilon.googlemapsapp2.R;
import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;
import wovilon.googlemapsapp2.io.AsynkRouteRequest;


//filter fot geocoding request
public class MyFilter extends Filter {
    Context context;
    URL url;
    AutoCompleteTextView autoCompleteTextView;
    AsynkTaskHandler asynkTaskHandler;
    public MyFilter(Context context, URL url, AutoCompleteTextView autoCompleteTextView,
                    AsynkTaskHandler asynkTaskHandler){
        super();
        this.context=context;
        this.url=url;
        this.autoCompleteTextView=autoCompleteTextView;
        this.asynkTaskHandler=asynkTaskHandler;
    }

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

            AsynkRouteRequest asynkRouteRequest=new AsynkRouteRequest(context, url, asynkTaskHandler);
            asynkRouteRequest.execute();
        }
        return null;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        if (results != null && results.count > 0) {

        }
    }
}
