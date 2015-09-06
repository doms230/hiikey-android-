package com.socialgroupe.hiikeyandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Created By: Dominic
 *
 * Fragment class used to signin/ signup people for imboard.
 *
 * Parent Activity: SignupAct.class
 *
 */

public class Signup_Login extends Activity implements View.OnClickListener{

    private EditText Username, Password, Email;
    private int signupPressed = 0;
    private boolean isUserOk = false;
    private boolean isPassOk = false;
    private boolean isEmailOk = false;
    private Bitmap bitmap;
    private byte[] bytes;
    private ProgressBar progressbar;
    private ImageView blur;
    private RelativeLayout yolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_login_signup);

        progressbar = (ProgressBar)findViewById(R.id.pbSignProgress);

        Button signina = (Button)findViewById(R.id.bSignIn);
        signina.setOnClickListener(this);

        Button signupa = (Button)findViewById(R.id.bSignup);
        signupa.setOnClickListener(this);

        Button forgotPassword = (Button)findViewById(R.id.bForgotPass);
        forgotPassword.setOnClickListener(this);

        ImageButton logTwitter= (ImageButton) findViewById(R.id.ibLogTwitter);
        logTwitter.setOnClickListener(this);

        Username = (EditText)findViewById(R.id.etUsername);
        Password = (EditText)findViewById(R.id.etPassword);
        Email = (EditText)findViewById(R.id.etEmail);
        blur =(ImageView) findViewById(R.id.ivBlur);
//        yolo = (RelativeLayout) findViewById(R.id.rlLogLog);
        //Blurry.with(this).capture(yolo).into(blur);

        String picDrawable = "android.resource://com.socialgroupe.hiikeyandroid/drawable/hiikey_splash";
        Uri defaultPicture = Uri.parse(picDrawable);
        saveFlyer(defaultPicture);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bSignIn:
                String username = Username.getText().toString().toLowerCase();
                String password = Password.getText().toString();
                progressbar.setVisibility(View.VISIBLE);

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            progressbar.setVisibility(View.GONE);
                            Intent timeIntent = new Intent("com.socialgroupe.HOME");
                            startActivity(timeIntent);
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
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

            case R.id.bSignup:
                signupPressed++;
                Email.setVisibility(View.VISIBLE);

                if(signupPressed > 1) {

                    username = Username.getText().toString().toLowerCase();
                    password = Password.getText().toString();
                    String email = Email.getText().toString();

                    validateSignup( password, username, email, Password, Username, Email );

                    if (isUserOk && isPassOk && isEmailOk) {

                        progressbar.setVisibility(View.VISIBLE);

                        final ParseUser user = new ParseUser();
                        user.setUsername(username.toLowerCase());
                        user.setPassword(password);
                        user.put("email", email);
                        user.put("DisplayName", "");
                        user.put("InstagramHandle", "");
                        user.put("SnapchatHandle", "");
                        user.put("TwitterHandle", "");
                        user.put("TumblrHandle", "");
                        user.put("sawExplanation", false);
                        String picDrawable = "android.resource://com.socialgroupe.hiikeyandroid/drawable/hiikey_splash";
                        Uri defaultPicture = Uri.parse(picDrawable);
                        saveFlyer(defaultPicture);

                    final ParseFile parseFile = new ParseFile("proPic.jpeg", bytes);
                        parseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    user.put("Profile", parseFile);
                                    user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            //ParseInstallation.getCurrentInstallation().saveInBackground();
                                            Intent signedIntent = new Intent("com.socialgroupe.HOME");
                                            //signedIntent.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
                                            startActivity(signedIntent);
                                            Toast.makeText(getApplicationContext(), "Profile Created.", Toast.LENGTH_SHORT).show();
                                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                            installation.put("user", ParseUser.getCurrentUser());
                                            installation.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e == null){
                                                        finish();
                                                    }
                                                }
                                            });

                                        } else {
                                            progressbar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Profile creation failed," +
                                                    " check internet connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            }
                        });
                    }
                }
                break;

            case R.id.bForgotPass:
                Intent forGotIntent = new Intent(this, ForgotPassFrag.class);
                startActivity(forGotIntent);
                break;

            case R.id.ibLogTwitter:
                progressbar.setVisibility(View.VISIBLE);
                ParseTwitterUtils.logIn(this, new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            user.put("DisplayName", "");
                            user.put("InstagramHandle", "");
                            user.put("SnapchatHandle", "");
                            user.put("TwitterHandle", "");
                            user.put("TumblrHandle", "");
                            user.put("sawExplanation", false);


                            final ParseFile parseFile = new ParseFile("proPic.jpeg", bytes);
                            parseFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        user.put("Profile", parseFile);
                                        user.signUpInBackground(new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    //ParseInstallation.getCurrentInstallation().saveInBackground();
                                                    Intent signedIntent = new Intent("com.socialgroupe.HOME");
                                                    //signedIntent.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
                                                    startActivity(signedIntent);
                                                    Toast.makeText(getApplicationContext(), "Profile Created.", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getApplicationContext(), "Profile creation failed," +
                                                            " check internet connection", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            /*Intent signedIntent = new Intent("com.socialgroupe.HOME");
                            //signedIntent.putExtra("userId", ParseUser.getCurrentUser().getObjectId());
                            startActivity(signedIntent);
                            finish();*/

                        } else {
                            Intent timeIntent = new Intent("com.socialgroupe.HOME");
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
                        }
                    }
                });
                break;
        }
    }

    private void saveFlyer( Uri uri){

        getContentResolver().notifyChange(uri, null);
        ContentResolver contentResolver = getContentResolver();

        try{
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap1Scaled = Bitmap.createScaledBitmap(
                bitmap,800 , 800* bitmap.getHeight() / bitmap.getWidth(), false);

        try {
            ExifInterface exifInterface = new ExifInterface(bitmap1Scaled.toString());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_270);
                    break;

                case ExifInterface.ORIENTATION_UNDEFINED:
                    if (bitmap1Scaled.getWidth() > bitmap1Scaled.getHeight()) {
                        matrix.postRotate(90);
                    }
                    break;
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1Scaled, 0, 0,
                    bitmap1Scaled.getWidth(), bitmap1Scaled.getHeight(), matrix, true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateSignup( final String pa, final String us, final String em,
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
}