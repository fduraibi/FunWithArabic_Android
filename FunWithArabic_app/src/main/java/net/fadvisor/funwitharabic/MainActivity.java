
/*
 * Copyright (c) 2015. Fahad Alduraibi
 *
 * http://www.fadvisor.net
 */

package net.fadvisor.funwitharabic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends Activity {

    SurfaceView surfaceView;
    AnimGuy animGuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceViewAnimGuy);
        animGuy = new AnimGuy(this);

        // TODO: maybe check the DB and copy/update at this stage, could be a thread!
        // TODO: create a "splash screen" if loading takes too long

    }

    @Override
    protected void onPause() {
        super.onPause();
        animGuy.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animGuy.resume();
    }

    public void buttonsOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);

                // Activity animation
                overridePendingTransition(R.animator.activity_anime1, R.animator.activity_anime2);
                break;
            case R.id.btnAboutUs:
                //TODO: Show about us

                // START: This code is just for testing
                ShapedButton btnStart = (ShapedButton) findViewById(R.id.btnStart);
                if (btnStart.isEnabled()) {
                    btnStart.setEnabled(false);
                } else {
                    btnStart.setEnabled(true);
                }
                // END: of testing code

                break;
            case R.id.resultsbtn:
                startActivity(new Intent(this,Results.class));
                break;
            case R.id.btnExit:
                finish();
                break;
            default: //do nothing
                break;
        }
    }

    // This is a surface view class that runs in a thread to animate the character
    public class AnimGuy extends SurfaceView implements Runnable{

        Thread thread = null;
        SurfaceHolder surfaceHolder;
        boolean animate = false;

        public AnimGuy(Context context) {
            super(context);

            surfaceView.setZOrderOnTop(true);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        }

        @Override
        public void run() {
            while (animate) {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }

                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawARGB(5, 0,0,255 );
                surfaceHolder.unlockCanvasAndPost(canvas);


            }
        }

        public void pause() {
            animate = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //thread = null; // do i need this?
        }

        public void resume() {
            animate = true;
            thread = new Thread(this);
            thread.start();

        }
    }

}
