
/*
 * Copyright (c) 2015. Fahad Alduraibi
 *
 * http://www.fadvisor.net
 */

package net.fadvisor.funwitharabic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: maybe check the DB and copy/update at this stage, could be a thread!
        // TODO: create a "splash screen" if loading takes too long

    }

    public void ButtonsOnClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);

                // Activity animation
                overridePendingTransition(R.animator.activity_anime1, R.animator.activity_anime2);
                break;
            case R.id.btnAboutUs:
                //TODO: Show about us
                break;
            case R.id.resultsbtn:
//                startActivity(new Intent(this,results.class));
                ShapedButton btnStart = (ShapedButton) findViewById(R.id.btnStart);
                if (btnStart.isEnabled()) {
                    btnStart.setEnabled(false);
                } else {
                    btnStart.setEnabled(true);
                }
                break;
            case R.id.btnExit:
                finish();
                break;
        }
    }

}
