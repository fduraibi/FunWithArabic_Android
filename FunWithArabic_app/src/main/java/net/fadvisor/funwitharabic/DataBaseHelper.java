
/*
 * Copyright (c) 2015. Fahad Alduraibi
 *
 * http://www.fadvisor.net
 */

package net.fadvisor.funwitharabic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final String DB_PATH;
    private static final String DB_NAME = "bayanat.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

        //DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
        DB_PATH = context.getFilesDir().getAbsolutePath() + '/' + DB_NAME;
    }

    private void createDataBase() {
        //By calling this method an empty database will be created into the default system path
        //of the application so we are gonna be able to overwrite that database with our database.
//        this.getReadableDatabase();
//        this.getWritableDatabase();
        copyDataBase();
    }

    private void copyDataBase(){
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        //close();

        //Open DB included in the APK file as the input stream
        InputStream myAPKDB = null;
        try {
            myAPKDB = myContext.getAssets().open(DB_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Open the new DB as the output stream
        OutputStream myNewDB = null;
        try {
            myNewDB = new FileOutputStream(DB_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //transfer bytes from the myAPKDB to the local DB myNewDB
        byte[] buffer = new byte[1024];
        int length;
        if (myAPKDB != null && myNewDB != null) {
            try {
                while ((length = myAPKDB.read(buffer)) > 0){
                    myNewDB.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //getWritableDatabase().close();

            // Force write of any data that might be buffered and close the streams
            try {
                myNewDB.flush();
                myNewDB.close();
                myAPKDB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openDataBase() throws SQLException {
        File myDBfile = new File(DB_PATH);
        if (!myDBfile.exists()) {
            createDataBase();
        }
        // THIS else IS FOR DEVELOPMENT ONLY TO RENEW THE DB file EVERY TIME, SHOULD BE REMOVED LATER
       /*
        else{
            myContext.deleteFile(DB_NAME); // delete the existing DB and create a new one
            createDataBase();
        }*/

        myDataBase = SQLiteDatabase.openDatabase(myDBfile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Do we need to check the DB version to see if it got updated?
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Handle the upgrading of the database
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
    public Cursor getData(String id) {
        return myDataBase.rawQuery("SELECT * FROM qa WHERE _id = ?", new String[]{id});
    }

    public int getTotalRecords() {
        Cursor mCursor = myDataBase.rawQuery("SELECT COUNT(*) FROM qa", null);
        mCursor.moveToFirst();
        int count= mCursor.getInt(0);
        mCursor.close();
        return count;
    }

    /**
     * @param name The player name
     * @param CA The number of correct answers
     * @param WA The number of wrong answers
     * @param finalR The final result
     */
    public void addResult(String name,int CA,int WA,int finalR){
        ContentValues container = new ContentValues();
        container.put("name", name);
        container.put("CA",CA);
        container.put("WA",WA);
        container.put("finalR",finalR);

        myDataBase.insert("results", null, container);

       // myDataBase.execSQL("INSERT INTO results ('name','CA','WA','finalR')" +
       //         " VALUES ('"+ name +"','" +CA +"','"+WA+"','"+finalR+"');");
    }

    public ArrayList getPlayerNames(){
        Cursor cursor = myDataBase.rawQuery("SELECT name FROM results ORDER BY finalR DESC", null);
        ArrayList<String> list = new ArrayList<>();
        int index = cursor.getColumnIndex("name");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(index));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    // SHOULD GET RESULT BY ID NOT NAME, since same played can have multiple results
    public int[] getDataByName(String name){
        Cursor cursor = myDataBase.rawQuery("SELECT CA,WA,finalR FROM results WHERE name=?", new String[]{name});

        int CAindex = cursor.getColumnIndex("CA");
        int WAindex = cursor.getColumnIndex("WA");
        int FRindex = cursor.getColumnIndex("finalR");

        // results array will contains the following data :
        // (0-> correct answers | 1-> wrong answers | 2 -> final result)
        int results[] = new int[3];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            results[0] = cursor.getInt(CAindex);
            results[1] = cursor.getInt(WAindex);
            results[2] = cursor.getInt(FRindex);
            cursor.moveToNext();
        }
        cursor.close();
        return results;
    }
}