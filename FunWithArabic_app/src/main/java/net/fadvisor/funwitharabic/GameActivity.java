
/*
 * Copyright (c) 2015. Fahad Alduraibi
 *
 * http://www.fadvisor.net
 */

package net.fadvisor.funwitharabic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity {

    private static final int MAX_STREAMS = 1;
    private TextView txtQ;
    private TextView txtQT;
    private Button btnA[] = new Button[4];
    private Button btnNext;
    private int correctA;
    private List<Integer> rndList;   // Random list for the answers
    private Random rndValue;    // Random value for the DB record query
    private DataBaseHelper myDB;
    private int DB_TOTAL;
    private SoundPool mySoundPool;
    private int sound_correct;
    private boolean soundLoaded_correct = false;
    private int sound_wrong;
    private boolean soundLoaded_wrong = false;
    private int sound_select;
    private boolean soundLoaded_select = false;


    private TextView R_Answers; // The right answers counter in the top of the layout
    private TextView W_Answers;// The wrong answers counter in the top of the layout
    private TextView score;

    private int R_Answers_num;
    private int W_Answers_num;
    private int score_num;


    {
        if (Build.VERSION.SDK_INT < 21) {
            //noinspection deprecation
            mySoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mySoundPool = new SoundPool.Builder().setAudioAttributes(attr).setMaxStreams(MAX_STREAMS).build();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mySoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == sound_correct)
                    soundLoaded_correct = true;
                else if (sampleId == sound_wrong)
                    soundLoaded_wrong = true;
                else if (sampleId == sound_select)
                    soundLoaded_select = true;
            }
        });

        sound_correct = mySoundPool.load(this, R.raw.sound_correct, 1);
        sound_wrong = mySoundPool.load(this, R.raw.sound_wrong, 1);
        sound_select = mySoundPool.load(this, R.raw.sound_select, 1);

        txtQ = (TextView) findViewById(R.id.txtQ);
        txtQT = (TextView) findViewById(R.id.txtQT);

        R_Answers = (TextView)findViewById(R.id.right_ans_count);
        W_Answers = (TextView)findViewById(R.id.wrong_ans_count);
        score = (TextView)findViewById(R.id.result_count);

        btnA[0] = (Button) findViewById(R.id.btn0);
        btnA[1] = (Button) findViewById(R.id.btn1);
        btnA[2] = (Button) findViewById(R.id.btn2);
        btnA[3] = (Button) findViewById(R.id.btn3);
        btnNext = (Button) findViewById(R.id.btnNew);



        // Used for randomizing the answers (these are the button numbers)
        rndList = new ArrayList<>();
        rndList.add(0);
        rndList.add(1);
        rndList.add(2);
        rndList.add(3);

        rndValue = new Random();

        myDB = new DataBaseHelper(this);
        myDB.openDataBase();
        DB_TOTAL = myDB.getTotalRecords();
        fetchNewQ();
    }

    @Override
    protected void onDestroy() {
        myDB.close();
        mySoundPool.release();

        super.onDestroy();
    }

    public void ButtonsOnClick(View v) {
        int Answer = -1;
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnNew:
                if (soundLoaded_select) {
                    mySoundPool.play(sound_select, 1, 1, 1, 0, 1);
                }
                fetchNewQ();
                break;

            case R.id.btn0:
                Answer = 0;
                break;

            case R.id.btn1:
                Answer = 1;
                break;

            case R.id.btn2:
                Answer = 2;
                break;

            case R.id.btn3:
                Answer = 3;
                break;
        }

        if (Answer >= 0) {

            R_Answers_num = Integer.parseInt(R_Answers.getText().toString());
            W_Answers_num = Integer.parseInt(W_Answers.getText().toString());
            score_num = Integer.parseInt(score.getText().toString());

            checkAnswer(Answer);
        }
    }

    private void checkAnswer(int Answer) {
        if (Answer == correctA) {
            btnA[Answer].setBackgroundResource(R.drawable.btn1_green_pressed);

            R_Answers.setText(String.valueOf(R_Answers_num + 1));
            score.setText(String.valueOf(score_num + 1));

            if (soundLoaded_correct) {
                mySoundPool.play(sound_correct, 1, 1, 1, 0, 1);
            }
        } else {
            btnA[Answer].setBackgroundResource(R.drawable.btn1_red_pressed);
            btnA[correctA].setBackgroundResource(R.drawable.btn1_green_normal);

            W_Answers.setText(String.valueOf(W_Answers_num + 1));

            if (soundLoaded_wrong) {
                mySoundPool.play(sound_wrong, 1, 1, 1, 0, 1);
            }


                score.setText(String.valueOf(score_num - 1));


        }

        // TODO: set score
        // TODO: add score to the database to show the best score

        /*
        * Add DB table named (results) with 4 columns(
        * player name , number of correct answers ,
        * number of wrong answers , final result
        * )
        */


        for (int i = 0; i < 4 ; i++){
            btnA[i].setClickable(false);
        }
        btnNext.setVisibility(View.VISIBLE);
        //TODO: maybe load next Q? after some wait?
    }
    private void fetchNewQ() {
        for (int i = 0; i < 4; i++) {
            StateListDrawable state_up = new StateListDrawable();
            state_up.addState(new int[] {-android.R.attr.state_enabled},getResources().getDrawable(R.drawable.btn1_blue_normal));
            state_up.addState(new int[] {android.R.attr.state_pressed},getResources().getDrawable(R.drawable.btn1_blue_pressed));

            if(Build.VERSION.SDK_INT < 16) {
                //noinspection deprecation
                btnA[i].setBackgroundDrawable(state_up);
            } else {
                btnA[i].setBackground(state_up);
            }

            btnA[i].setClickable(true);
            btnNext.setVisibility(View.INVISIBLE);
        }

        Cursor mCursor = myDB.getData(String.valueOf(rndValue.nextInt(DB_TOTAL) + 1));
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            String tmpQ = QParser(mCursor.getString(1));
            txtQ.setText(tmpQ);
            txtQT.setText(QTParser(tmpQ));
            Collections.shuffle(rndList);

            correctA = (rndList.get(0)); // This one has the correct answer
            btnA[correctA].setText(mCursor.getString(2));
            btnA[(rndList.get(1))].setText(mCursor.getString(3));
            btnA[(rndList.get(2))].setText(mCursor.getString(4));
            btnA[(rndList.get(3))].setText(mCursor.getString(5));
        }
        mCursor.close();
    }

    private String QParser(String strQ) {
        return strQ.replace("*", "......");
    }

    private String QTParser(String strQ) {
        // Remove all Tashkeel ( U+064B to U+0653 )
        return strQ.replaceAll("[\\u064B-\\u0653]", "");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        // Warn the player that exiting will cancel the score!! or maybe save the score and allow resuming

        // Add all strings to language strings to allow translations .
        new AlertDialog.Builder(this)
                .setMessage("هل أنت متأكد؟")
                .setCancelable(false)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        overridePendingTransition(R.animator.activity_anime2reverse, R.animator.activity_anime1reverse);
                    }
                })
                .setNegativeButton("لا", null)
                .show();
    }

}