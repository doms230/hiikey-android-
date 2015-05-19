package com.socialgroupe.hiikey;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseInstallation;
import com.parse.ParseUser;


/**
 * Created by Dominic on 9/13/2014.
 * Opening scene for the app.
 */

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle splash) {
        super.onCreate(splash);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_splash);

//////////////////////Main thread stuff////////////////////////////////////
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    if(ParseUser.getCurrentUser() != null) {
                        Intent iBoard = new Intent("com.socialgroupe.BULLETIN");
                        startActivity(iBoard);

                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("user", ParseUser.getCurrentUser());
                        installation.saveInBackground();

                    } else{
                        Intent iLogin = new Intent("com.socialgroupe.LOGIN");
                        startActivity(iLogin);
                    }
                }
            }
        };
        timer.start();
    }
///////////////////////////////////////////////////////////

    // setting up the splash termination...
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
