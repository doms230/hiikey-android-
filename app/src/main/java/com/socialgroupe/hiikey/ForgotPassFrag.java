package com.socialgroupe.hiikey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by Dominic on 1/3/2015.
 * Used to reset people's passwords
 */
public class ForgotPassFrag extends Activity implements View.OnClickListener {
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_forgotpassword);

        email = (EditText) findViewById(R.id.etEmailReset);

        Button cancel = (Button) findViewById(R.id.bCancel);
        cancel.setOnClickListener(this);

        Button ok = (Button) findViewById(R.id.bOK);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bCancel:
                showLogin();
                finish();
                break;

            case R.id.bOK:
                String sEmail = email.getText().toString();

                ParseUser.requestPasswordResetInBackground(sEmail,
                        new RequestPasswordResetCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "An email was successfully sent with reset instructions.",
                                            Toast.LENGTH_LONG);
                                    toast.show();
                                    showLogin();
                                    finish();

                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Email wasn't recognized.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
                break;
        }
    }

    /**
     * I decided to put the intent within a method, because the app would crash when it was injected
     * directly into R.id.bOK.
     * -Dominic 1/3/15
     */

    private void showLogin(){
        Intent intent = new Intent(this, Signup_Login.class);
        startActivity(intent);
    }
}
