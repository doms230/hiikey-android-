package com.socialgroupe.hiikey;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by Dominic on 9/13/2014.
 * Edited by Yaning
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
    }

    public void signIn(View view){
        // click sign_in, enter sign_in page
        Intent intent = new Intent(this,Signin.class);
        startActivity(intent);
    }

    public void signUp(View view){
        // click sign_up, enter sign_up page
        //Intent intent = new Intent(this,)
    }

    public void explore(View view){
        // click explore, enter the main page with limited authority

    }
}

