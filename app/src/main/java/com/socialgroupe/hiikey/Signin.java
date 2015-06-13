package com.socialgroupe.hiikey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by lemonie on 6/13/15.
 */
public class Signin extends Activity implements View.OnClickListener{

    private EditText Username, Password;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_login_signup);
        progressbar = (ProgressBar)findViewById(R.id.pbSignProgress);
        Button signina = (Button)findViewById(R.id.bSignIn);
        signina.setOnClickListener(this);
        Button forgotPassword = (Button)findViewById(R.id.bForgotPass);
        forgotPassword.setOnClickListener(this);

        Username = (EditText)findViewById(R.id.etUsername);
        Password = (EditText) findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.bSignIn:
                String username = Username.getText().toString().toLowerCase();
                String password = Password.getText().toString();
                progressbar.setVisibility(View.VISIBLE);

                ParseUser.logInInBackground(username,password, new LogInCallback(){
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            progressbar.setVisibility(View.GONE);
                            Intent timeIntent = new Intent("com.socialgroupe.BULLETIN");
                            startActivity(timeIntent);
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        finish();
                                    }
                                }
                            });

                        } else {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Incorrect username/password combo.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                break;

            case R.id.bForgotPass:
                Intent forGotIntent = new Intent(this, ForgotPassFrag.class);
                startActivity(forGotIntent);
        }
    }
}
