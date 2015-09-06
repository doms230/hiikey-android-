package com.socialgroupe.hiikeyandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_main);

//////////////////////Main thread stuff////////////////////////////////////
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    if(ParseUser.getCurrentUser() != null) {
                        Intent iBoard = new Intent("com.socialgroupe.HOME");
                        startActivity(iBoard);

                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("user", ParseUser.getCurrentUser());
                        installation.saveInBackground();

                    } else{
                        Intent iLogin = new Intent("com.socialgroupe.SIGNIN_SIGNUP");
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
