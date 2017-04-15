package wovilon.googlemapsapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wovilon.googlemapsapp2.adapters.GooglePlacesAdapter;
import wovilon.googlemapsapp2.db.DbUpdator;
import wovilon.googlemapsapp2.model.mRoute;


public class MyRoutesActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myroutes);

        DbUpdator db=new DbUpdator(this);

        ListView listView=(ListView)findViewById(R.id.routesListView);
        ArrayList<String> adressesList=db.getNamesFromDb();


        ArrayAdapter adapterStart = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,adressesList);
        listView.setAdapter(adapterStart);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                returnResult(i);
            }
        });

    }

    void returnResult(int i){
        Intent resultIntent=new Intent();
        resultIntent.putExtra("id",i+"");
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}
