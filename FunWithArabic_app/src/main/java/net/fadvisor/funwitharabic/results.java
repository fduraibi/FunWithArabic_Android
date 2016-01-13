package net.fadvisor.funwitharabic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class results extends Activity {

    ListView listView;
    DataBaseHelper myDB;
    ArrayList<String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        listView = (ListView)findViewById(R.id.listView);

        myDB = new DataBaseHelper(this);
        myDB.openDataBase();


        items = myDB.getplayersnames();
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

    }

    @Override
    protected void onDestroy() {
        myDB.close();
        super.onDestroy();
    }
}
