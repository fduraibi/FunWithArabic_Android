
/*
 * Copyright (c) 2015. Fahad Alduraibi
 *
 * http://www.fadvisor.net
 */

package net.fadvisor.funwitharabic;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH;
    private static String DB_NAME = "bayanat.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase myDataBase;
    private Context myContext;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

        //DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
        DB_PATH = context.getFilesDir().getAbsolutePath() + '/' + DB_NAME;
    }

    public void createDataBase() throws IOException {
        //By calling this method and empty database will be created into the default system path
        //of the application so we are gonna be able to overwrite that database with our database.
//        this.getReadableDatabase();
//        this.getWritableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            throw new Error(e.getMessage());
        }
    }

    private void copyDataBase() throws IOException{
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        //close();

        //Open DB included in the APK file as the input stream
        InputStream myAPKDB = myContext.getAssets().open(DB_NAME);

        //Open the new DB as the output stream
        OutputStream myNewDB = new FileOutputStream(DB_PATH);

        //transfer bytes from the myAPKDB to the local DB myNewDB
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myAPKDB.read(buffer))>0){
            myNewDB.write(buffer, 0, length);
        }

        //getWritableDatabase().close();

        // Force write of any data that might be buffered
        myNewDB.flush();
        // Close the streams
        myNewDB.close();
        myAPKDB.close();
    }

    public void openDataBase() throws SQLException {
        File myDBfile = new File(DB_PATH);
        if (!myDBfile.exists()) {
            try {
                createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // THIS else IS FOR DEVELOPMENT ONLY TO RENEW THE DB file EVERY TIME, SHOULD BE REMOVED LATER
        else{

            myContext.deleteFile(DB_NAME); // delete the existing DB and create a new one
            try {
                createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
}