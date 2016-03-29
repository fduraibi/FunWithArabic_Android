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

public class Results extends Activity {

    private DataBaseHelper myDB;
    private ArrayList<String> items;
    private int results[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ListView listView = (ListView) findViewById(R.id.listView);

        myDB = new DataBaseHelper(this);
        myDB.openDataBase();


        items = myDB.getPlayerNames();
        if(items.isEmpty()){
            listView.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "لا توجد نتائج مسجلة", Toast.LENGTH_LONG).show();
        }
        else {
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playerName = items.get(position);
                results = myDB.getDataByName(playerName);
                showSelectedResult(playerName,results[0],results[1],results[2]);
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