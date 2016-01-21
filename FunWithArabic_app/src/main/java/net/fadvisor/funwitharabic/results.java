package net.fadvisor.funwitharabic;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class results extends Activity {

    ListView listView;
    DataBaseHelper myDB;
    ArrayList<String> items;
    int results[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        listView = (ListView)findViewById(R.id.listView);

        myDB = new DataBaseHelper(this);
        myDB.openDataBase();


        items = myDB.getplayersnames();
        if(items.isEmpty()){
            listView.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "لا توجد نتائج مسجلة", Toast.LENGTH_LONG).show();
        }
        else {
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playername = items.get(position);
                results = myDB.getDataByName(playername);
                showSelectedResult(playername,results[0],results[1],results[2]);
            }
        });
    }
    public void onClick(View v){
        finish();
    }
    private void showSelectedResult(String name,int CA,int WA,int finalR){
        new AlertDialog.Builder(this)
                .setTitle(name)
                .setMessage("عدد الإجابات الصحيحة : " +  CA + "\n" +
                        "عدد الإجابات الخاطئة : "+ WA + "\n" +
                        "النتيجة النهائية : "+ finalR)
                .setNegativeButton("اخفاء",null)
                .show();
    }
    @Override
    protected void onDestroy() {
        myDB.close();
        super.onDestroy();
    }
}