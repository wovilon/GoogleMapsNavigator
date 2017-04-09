package wovilon.googlemapsapp2;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import wovilon.googlemapsapp2.interfaces.AsynkTaskHandler;


public class GooglePlacesAdapter extends ArrayAdapter implements Filterable {
    private ArrayList resultList;
    Context context;

    public GooglePlacesAdapter(Context context, int resource) {
        super(context, resource);
        this.context=context;
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
                    AsynkRouteRequest asynkRouteRequest=new AsynkRouteRequest(context, asynkTaskHandler);
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
