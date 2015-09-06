package com.socialgroupe.hiikeyandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProfile extends ActionBarActivity implements View.OnClickListener {

    private EditText editInstagram, editTumblr, editTwitter, editSnapChat, editdisplayName;
   private String stringInstagram;
       private String stringTumblr;
     private String stringTwitter;
     private String stringSnapChat;
     private String displayName;
     private ImageButton changeProPic;
      private static final int REQUEST_IMAGE_GET = 1;
     private byte[] bytes;
    private boolean wasapictureuploaded = false;
    private AlertDialog snapAlert, twitAlert, tumAlert, alertDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profie2);

        //test
        editdisplayName = (EditText)findViewById(R.id.etDisplayName);

        ImageButton instabutton = (ImageButton)findViewById(R.id.ivInstagram);
        instabutton.setOnClickListener(this);

        ImageButton tumButton = (ImageButton)findViewById(R.id.ivTumblr);
        tumButton.setOnClickListener(this);

        ImageButton twitButton = (ImageButton)findViewById(R.id.ivTwitter);
        twitButton.setOnClickListener(this);

        ImageButton snapButton = (ImageButton)findViewById(R.id.ivSnapchat);
        snapButton.setOnClickListener(this);

        changeProPic = (ImageButton)findViewById(R.id.ivUploadProfilePicture);
        changeProPic.setOnClickListener(this);

        ParseQuery<ParseUser> updateProfileParseQuery = ParseUser.getQuery();
        updateProfileParseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        updateProfileParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser updateProfile, ParseException e) {
                ParseFile parseFile = updateProfile.getParseFile("Profile");
                parseFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Drawable d = new BitmapDrawable(getResources(), bmp);
                            changeProPic.setBackground(d);
                        }
                    }
                });

                stringInstagram = updateProfile.getString("InstagramHandle");
                stringTumblr = updateProfile.getString("TumblrHandle");
                stringTwitter = updateProfile.getString("TwitterHandle");
                stringSnapChat = updateProfile.getString("SnapchatHandle");
                displayName = updateProfile.getString("DisplayName");
                editdisplayName.setText(displayName);
                dialogs();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ivUploadProfilePicture:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
                break;

            case R.id.ivInstagram:
                alertDialog.show();
                editInstagram.setText(stringInstagram);
                break;

            case R.id.ivTumblr:
                tumAlert.show();
                editTumblr.setText(stringTumblr);
                break;

            case R.id.ivTwitter:
                twitAlert.show();
                editTwitter.setText(stringTwitter);
                break;

            case R.id.ivSnapchat:
                snapAlert.show();
                editSnapChat.setText(stringSnapChat);
                break;

            default:
                Intent intent1 = new Intent(this,  MyProfile.class);
                startActivity(intent1);
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editprofile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_editProfile_upate:
                updateProfile();
                return true;

            case R.id.action_editProfile_cancel:
                Intent intent = new Intent(this, MyProfile.class);
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            wasapictureuploaded = true;
            Uri fullPhotoUri = data.getData();
            //saveFlyer(this, fullPhotoUri, changeProPic, true);

                Drawable d = new BitmapDrawable(getResources(), saveFlyer(this, fullPhotoUri, changeProPic));

                changeProPic.setBackground(d);
        }
    }

    private void dialogs(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View socialView = inflater.inflate(R.layout.dialog_edit_instagram, null);
        builder.setView(socialView);
        editInstagram = (EditText) socialView.findViewById(R.id.etdInstagram);
        if(!stringInstagram.isEmpty()){
            editInstagram.setText(stringInstagram);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringInstagram = editInstagram.getText().toString();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();

        /***************************************************************/

        AlertDialog.Builder tumBuilder = new AlertDialog.Builder(this);
        LayoutInflater tuminflater = getLayoutInflater();
        View tumView = tuminflater.inflate(R.layout.dialog_edit_tumblr, null);
        tumBuilder.setView(tumView);
        editTumblr = (EditText) tumView.findViewById(R.id.etdEditTumblr);
        if(!stringTumblr.isEmpty()){
            editTumblr.setText(stringTumblr);
        }
        tumBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringTumblr = editTumblr.getText().toString();
            }
        });
        tumBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        tumAlert = tumBuilder.create();
        /********************************************************************/

        AlertDialog.Builder twitBuilder = new AlertDialog.Builder(this);
        LayoutInflater twitInflater = getLayoutInflater();
        View twitView = twitInflater.inflate(R.layout.dialog_edit_twitter, null);
        twitBuilder.setView(twitView);
        editTwitter = (EditText) twitView.findViewById(R.id.etdEditTwitter);
        if(!stringTwitter.isEmpty()){
            editTwitter.setText(stringTwitter);
        }
        twitBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringTwitter = editTwitter.getText().toString();
            }
        });
        twitBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        twitAlert = twitBuilder.create();
        /**********************************************************/
        AlertDialog.Builder snapBuilder = new AlertDialog.Builder(this);
        LayoutInflater snapInflater = getLayoutInflater();
        View snapView = snapInflater.inflate(R.layout.dialog_edit_snapchat, null);
        snapBuilder.setView(snapView);
        editSnapChat = (EditText) snapView.findViewById(R.id.etdEditSnapchat);
        if(!stringSnapChat.isEmpty()){
            editSnapChat.setText(stringSnapChat);
        }
        snapBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stringSnapChat = editSnapChat.getText().toString();
            }
        });
        snapBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        snapAlert = snapBuilder.create();
    }

    private Bitmap saveFlyer(Context context, Uri uri, ImageView imageView){
/******
 * where I got this from:
 * http://stackoverflow.com/questions/13511356/android-image-selected-from-gallery-orientation-is-always-0-exif-tag
 * Dominic
 * 3/8/15
 */
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options asdf = new BitmapFactory.Options();
            asdf.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, asdf);
            is.close();

            int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(context, uri);

            if(orientation == 90 || orientation == 270){
                rotatedWidth = asdf.outHeight;
                rotatedHeight =asdf.outWidth;
            } else{
                rotatedWidth = asdf.outWidth;
                rotatedHeight = asdf.outHeight;
            }

            Bitmap srcBitmap;
            is = context.getContentResolver().openInputStream(uri);
            if(rotatedWidth > imageView.getWidth() || rotatedHeight > imageView.getHeight()){
                float widthRatio = ((float) rotatedWidth) / ((float) imageView.getWidth());
                float heightRatio = ((float) rotatedHeight) / ((float) imageView.getHeight());
                float maxRatio = Math.max(widthRatio, heightRatio);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(is, null, options);

            } else{
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            is.close();

            if(orientation > 0){
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0,0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }

            String type = context.getContentResolver().getType(uri);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            if(type.equals("image/png")){
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, boas);
            } else if( type.equals("image/jpg") || type.equals("image/jpeg")){
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
            }
            bytes = boas.toByteArray();
            boas.close();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getOrientation(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,
                new String[] {MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1){
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private void updateProfile(){
        ParseQuery<ParseUser> updateProfileParseQuery = ParseUser.getQuery();
        updateProfileParseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        updateProfileParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done( ParseUser updateProfile, ParseException e) {
                if (e == null) {
                    String stringEditDisplayName = editdisplayName.getText().toString();
                    progressBar = (ProgressBar)findViewById(R.id.pbEditProfile);
                    progressBar.setVisibility(View.VISIBLE);

                        if (!stringInstagram.isEmpty()) {
                            updateProfile.put("InstagramHandle", stringInstagram);
                        } else {
                            updateProfile.put("InstagramHandle", "");
                        }

                        if (!stringTumblr.isEmpty()) {
                            updateProfile.put("TumblrHandle", stringTumblr);
                        } else {
                            updateProfile.put("TumblrHandle", "");
                        }

                        if (!stringTwitter.isEmpty()) {
                            updateProfile.put("TwitterHandle", stringTwitter);
                        } else {
                            updateProfile.put("TwitterHandle", "");
                        }

                        if (!stringSnapChat.isEmpty()) {
                            updateProfile.put("SnapchatHandle", stringSnapChat);
                        } else {
                            updateProfile.put("SnapchatHandle", "");
                        }

                    updateProfile.put("DisplayName", stringEditDisplayName.toLowerCase());

                    if (wasapictureuploaded) {
                        ParseFile parseFile = new ParseFile("proPic.jpeg", bytes);
                        parseFile.saveInBackground();

                        updateProfile.put("Profile", parseFile);
                        updateProfile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
                                    Intent intent3 = new Intent(EditProfile.this, MyProfile.class);
                                    startActivity(intent3);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),
                                            "Profile update failed, Check internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        updateProfile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(EditProfile.this, MyProfile.class);
                                    startActivity(intent1);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),
                                            "Profile update failed, Check internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Update Failed. Make sure your connected to the internet.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
