package com.socialgroupe.hiikey;

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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Created by Dominic on 4/23/2015.
 * Class to Edit Bulletins.
 */
public class EditBulletin extends ActionBarActivity implements View.OnClickListener {

    private EditText title, location;
    private ImageButton uploadBulletin;
    private RadioGroup radio,showhide;
    private String sTitle, sLocation, sSetting = "close", sShowHide = "hide", bulletinId, checkTitle,
          checkLocation;
    private double latitude, longitude;
    private ProgressBar pb;
    private RadioButton close, open, show, hide;
    private boolean addressFound, bTitle, bulletinPicUpload = false, anotherLocation = false;
    private byte[] bytes;
    private int REQUEST_IMAGE_GET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bulletin);

        ParseObject.registerSubclass(Bulletin_Helper.class);

        bulletinId = getIntent().getStringExtra("editBulletin");

        pb = (ProgressBar) findViewById(R.id.pbNewBulletin);

        title = (EditText) findViewById(R.id.etBulletinTitle);
        location = (EditText) findViewById(R.id.etBulletinLocation);

        open = (RadioButton) findViewById(R.id.rbBulletinOpen);
        close = (RadioButton) findViewById(R.id.rbBulletinClosed);
        show = (RadioButton) findViewById(R.id.rbShowTitle);
        hide = (RadioButton) findViewById(R.id.rbHideTitle);

        radio = (RadioGroup) findViewById(R.id.rgBulletinSetting);
        radioStuff();
        showhide = (RadioGroup) findViewById(R.id.rgShowHide);
        radioShowHide();

        uploadBulletin = (ImageButton) findViewById(R.id.ibUploadBulletin);
        uploadBulletin.setOnClickListener(this);
        ImageButton info = (ImageButton) findViewById(R.id.ibBulletinSettingInfo);
        info.setOnClickListener(this);
        ImageButton infoShowHide = (ImageButton) findViewById(R.id.ibShowHide);
        infoShowHide.setOnClickListener(this);

        checkBulletinStuff();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bulletin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_finish_new_bulletin:
                acceptStuff();
                return true;

            case R.id.action_exit_new_bulletin:
                Intent in   = new Intent(this, Bulletin.class);
                startActivity(in);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibUploadBulletin:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
                break;

            case R.id.ibBulletinSettingInfo:
                verifiedMessage();
                break;

            case R.id.ibShowHide:
                showhideMessage();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            Uri fullPhotoUri = data.getData();
            Drawable d = new BitmapDrawable(getResources(), saveFlyer(this, fullPhotoUri, uploadBulletin));
            uploadBulletin.setBackgroundResource(R.color.appColor);
            uploadBulletin.setBackground(d);
            bulletinPicUpload = true;
        }
    }
    /********************************Private Methods*/
    private void checkBulletinStuff(){
        ParseQuery<Bulletin_Helper> getBulletinStuff = Bulletin_Helper.getQuery();
        getBulletinStuff.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo("objectId", bulletinId)
                .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                    @Override
                    public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                        if (e == null) {
                            title.setText(bulletin_helper.getBulletinName());
                            checkTitle = title.getText().toString();

                            location.setText(bulletin_helper.getBulletinAdress());
                            checkLocation = location.getText().toString();

                            if (bulletin_helper.getBulletinSetting().equals("open")) {
                                close.setChecked(false);
                                open.setChecked(true);

                            } else {
                                open.setChecked(false);
                                close.setChecked(true);
                            }

                            if (bulletin_helper.getShowHideSetting().equals("show")) {
                                hide.setChecked(false);
                                show.setChecked(true);
                            } else {
                                show.setChecked(false);
                                hide.setChecked(true);
                            }

                            ParseFile parseFile = bulletin_helper.getBulletinPic();
                            parseFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if(e == null){
                                        Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                        uploadBulletin.setBackground(d);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void showhideMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bulletin Setting");
        builder.setMessage("show: Show bulletin name on Top of Bulletin\n\n" +
                "Hide: Don't Show bulletin name on top of Bulletin");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void verifiedMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bulletin Setting");
        builder.setMessage("Open: Allow anyone to post to your bulletin\n\n" +
                "Closed: Only you can post to your bulletin");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void acceptStuff(){
        sTitle = title.getText().toString().toUpperCase();
        sLocation = location.getText().toString();

        if(!sLocation.equals(checkLocation)) {
            List<Address> geocodeMatches = null;
            try {
                geocodeMatches = new Geocoder(this).getFromLocationName(sLocation, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (geocodeMatches != null && geocodeMatches.size() > 0) {
                latitude = geocodeMatches.get(0).getLatitude();
                longitude = geocodeMatches.get(0).getLongitude();
                addressFound = true;
                anotherLocation = true;
            } else {
                pb.setVisibility(View.GONE);
                addressFound = false;
                Toast.makeText(this.getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        } else{
            addressFound = true;
            anotherLocation = false;
        }
            final ParseGeoPoint geo = new ParseGeoPoint(latitude, longitude);

        if(validateBulletinInfo() && addressFound){

            Toast.makeText(getApplicationContext(),
                    "Processing...", Toast.LENGTH_SHORT).show();

            ParseQuery<Bulletin_Helper> updateBulletin = Bulletin_Helper.getQuery();
            updateBulletin.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                    .whereEqualTo("objectId", bulletinId )
                    .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                        @Override
                        public void done(final Bulletin_Helper bulletin_helper, ParseException e) {
                            if (e == null) {

                                bulletin_helper.setBulletinName(sTitle.toUpperCase());
                                bulletin_helper.setBulletinAddress(sLocation);
                                if(anotherLocation) {
                                    bulletin_helper.setBulletinLocation(geo);
                                }
                                bulletin_helper.setBulletinSetting(sSetting);
                                bulletin_helper.setShowHideSetting(sShowHide);
                                if(bulletinPicUpload) {

                                    final ParseFile parseFile = new ParseFile("bulletin.png", bytes);
                                    parseFile.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                bulletin_helper.setBulletinPic(parseFile);

                                                bulletin_helper.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Bulletin Updated!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                });

                                                Intent intent = new Intent("com.socialgroupe.BULLETIN");
                                                startActivity(intent);
                                                Toast.makeText(getApplicationContext(),
                                                        "Almost done...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else{
                                    bulletin_helper.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Bulletin Updated!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    private void radioShowHide(){
        showhide.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbShowTitle:
                        sShowHide = "show";
                        break;

                    case R.id.rbHideTitle:
                        sShowHide = "hide";
                        break;

                    default:
                        sShowHide = "hide";
                        break;
                }
            }
        });
    }

    private void radioStuff(){
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbBulletinOpen:
                        sSetting = "open";
                        break;

                    case R.id.rbBulletinClosed:
                        sSetting = "close";
                        break;

                    default:
                        sSetting = "close";
                        break;
                }
            }
        });
    }

    private boolean validateBulletinInfo(){
        final boolean  blocation;

        if(sTitle.isEmpty()){
            title.setError(getString(R.string.error_field_required));
            bTitle = false;
        } else if(!sTitle.equals(checkTitle)){
                ParseQuery<Bulletin_Helper> getTitle = Bulletin_Helper.getQuery();
                getTitle.whereEqualTo("bulletinName", sTitle.toUpperCase())
                        .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                            @Override
                            public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                                if (e == null) {
                                    title.setError("Bulletin Name already taken.");
                                    bTitle = false;
                                } else {
                                    bTitle = true;
                                }
                            }
                        });
        } else{
            bTitle = true;
        }

        if(sLocation.isEmpty()){
            location.setError(getString(R.string.error_field_required));
            blocation = false;
        } else{
            blocation = true;
        }
        return bTitle && blocation;
    }

    /**
     * saveFlyer is used to convert and rotate the flyer to a byteArray(byte[])
     */

    private Bitmap saveFlyer(Context context, Uri uri, ImageView imageView) {
        /**
         * see EdProfile.java
         */
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options asdf = new BitmapFactory.Options();
            asdf.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, asdf);
            is.close();

            int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(context, uri);

            if (orientation == 90 || orientation == 270) {
                rotatedWidth = asdf.outHeight;
                rotatedHeight = asdf.outWidth;
            } else {
                rotatedWidth = asdf.outWidth;
                rotatedHeight = asdf.outHeight;
            }

            Bitmap srcBitmap;
            is = context.getContentResolver().openInputStream(uri);
            if (rotatedWidth > imageView.getWidth() || rotatedHeight > imageView.getHeight()) {
                float widthRatio = ((float) rotatedWidth) / ((float) imageView.getWidth());
                float heightRatio = ((float) rotatedHeight) / ((float) imageView.getHeight());
                float maxRatio = Math.max(widthRatio, heightRatio);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(is, null, options);

            } else {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            is.close();

            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }

            String type = context.getContentResolver().getType(uri);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            if (type.equals("image/png")) {
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, boas);
            } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
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
}
