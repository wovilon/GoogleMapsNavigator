package wovilon.googlemapsapp2.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import wovilon.googlemapsapp2.model.mRoute;

public class DbUpdator {
    SQLiteDatabase db;
    Cursor c;
    private Context context;

    public DbUpdator(Context context){
        this.context=context;
        DbHelper dbHelper=new DbHelper(context);
        db=dbHelper.getWritableDatabase();
        c=db.rawQuery("SELECT * FROM RoutesTable", null);
    }

    public void addRouteToDb(mRoute route){
        int id=0;
        try{
            id=c.getCount();
        }catch (NullPointerException ne){}

        ContentValues data=new ContentValues();
        data.put("id",id);
        data.put("name", route.getFormatedAdress(0)+" - "+route.getFormatedAdress(route.getPoints().size()-1));
        data.put("routeJSON", route.getJsonRoute());
        data.put("lat", route.latitudesToJSON());
        data.put("lng", route.longitudesToJSON());
        db.insert("RoutesTable", null, data);
        data.clear();
    }

    public ArrayList<String> getNamesFromDb() {
        c.moveToFirst();
        ArrayList<String> routes = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            routes.add(c.getString(c.getColumnIndex("name")));
            c.moveToNext();
        }
        return routes;
    }

    public String getRouteJSONFromDb(int i){
        c.moveToPosition(i);
        String s=c.getString(c.getColumnIndex("routeJSON"));
        return c.getString(c.getColumnIndex("routeJSON"));
    }

    public int getCount(){
        return c.getCount();
    }

    public ArrayList<LatLng> getLatLng(int id){
        ArrayList<LatLng> points=new ArrayList<>();
        c.moveToPosition(id);
        JSONArray jsonArrayLat=new JSONArray();
        JSONArray jsonArrayLng=new JSONArray();
        try {
            jsonArrayLat = new JSONArray(c.getString(c.getColumnIndex("lat")));
            jsonArrayLng = new JSONArray(c.getString(c.getColumnIndex("lng")));

        LatLng latLng;
            for (int i=0; i<jsonArrayLat.length(); i++){
                latLng=new LatLng(Double.parseDouble(jsonArrayLat.get(i).toString()),
                        Double.parseDouble(jsonArrayLng.get(i).toString()));
                points.add(latLng);
            }
        }catch (JSONException je){}
        return points;
    }

    private String serialize(mRoute object){
        String serializedObject=null;
        // serialize the object
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(object);
            so.flush();
            serializedObject = bo.toString();
        } catch (Exception e) {}
        return serializedObject;
    }

    private mRoute deserialize(String serializedObject){
        mRoute route=null;
        // deserialize the object
        try {
            byte b[] = serializedObject.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
             route = (mRoute) si.readObject();
        } catch (Exception e) {}
        return route;
    }
}
