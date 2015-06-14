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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class Signup extends Activity implements View.OnClickListener {

    private EditText Username, Password, Email;
    private boolean isUserOk = false;
    private boolean isEmailOk = false;
    private boolean isPassOk = false;
    private ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);

        ParseObject.registerSubclass(Props_Helper.class);

        progressbar = (ProgressBar) findViewById(R.id.pbSignupProgress);

        Button signupa = (Button) findViewById(R.id.bSignup);
        signupa.setOnClickListener(this);

        Username = (EditText) findViewById(R.id.etUsername);
        Password = (EditText) findViewById(R.id.etPassword);
        Email = (EditText) findViewById(R.id.etEmail);

    }

    private void validateSignup(final String pa, final String us, final String em,
                                final EditText password, final EditText username,
                                final EditText email) {

        /*******Password verification*************/

        if (pa.isEmpty()) {
            password.setError(getString(R.string.error_field_required));
            isPassOk = false;

        } else if (pa.length() < 4) {
            password.setError(getString(R.string.error_invalid_password));
            isPassOk = false;

        } else {

            isPassOk = true;
        }

        /*******Username verification*************/

        if (us.isEmpty()) {
            username.setError(getString(R.string.error_field_required));
            isUserOk = false;

        } else if (us.length() < 4) {
            username.setError(getString(R.string.username_too_short));
            isUserOk = false;

        } else {
            ParseQuery<ParseUser> userUsername = ParseUser.getQuery();
            userUsername.whereEqualTo("username", us)
                    .getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                username.setError(getString(R.string.error_username_taken));
                                isUserOk = false;

                            } else {
                                isUserOk = true;
                            }
                        }
                    });
        }

        /*******Email verification*************/

        if (em.isEmpty()) {
            email.setError(getString(R.string.error_field_required));
            isEmailOk = false;

        } else if (!em.contains("@")) {
            email.setError(getString(R.string.email_wrong_format));
            isEmailOk = false;

        } else {
            ParseQuery<ParseUser> userEmail = ParseUser.getQuery();
            userEmail.whereEqualTo("email", em)
                    .getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                email.setError(getString(R.string.error_email_taken));
                                isEmailOk = false;
                            } else {
                                isEmailOk = true;
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {

            String username = Username.getText().toString().toLowerCase();
            String password = Password.getText().toString();
            String email = Email.getText().toString();

            validateSignup(password, username, email, Password, Username, Email);

            if (isUserOk && isPassOk && isEmailOk) {
                // if signup successfully, then go to the main(bulletin) page

                final ParseUser user = new ParseUser();
                user.setUsername(username.toLowerCase());
                user.setPassword(password);
                user.setEmail(email);
                Intent timeIntent = new Intent("com.socialgroupe.BULLETIN");
                startActivity(timeIntent);

            } else {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Profile creation failed," +
                        " check internet connection", Toast.LENGTH_SHORT).show();
            }
        }

}
