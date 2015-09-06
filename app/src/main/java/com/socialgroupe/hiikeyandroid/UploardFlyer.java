package com.socialgroupe.hiikeyandroid;

import android.content.Context;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 1/12/2015.
 * Fragment used to upload and post custom flyers.
 * Parent activity: NewDesign.class
 */
public class UploardFlyer extends Fragment implements View.OnClickListener{

    private ImageView imageView;
    private boolean photoUploaded = false;
    private int REQUEST_IMAGE_GET = 1;
    private byte[] bytes;
    private Bundle bundle;
    private String title, date, category, time, hashStuff, websiteStuff, descriptionStuff, address, privateStuff;
    private double latitude, longitude;
    private ProgressBar pr;

    public static UploardFlyer cDesign_Instance(){
        return new UploardFlyer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View customDesign = inflater.inflate(R.layout.fragment_custom_design, container, false);

        ImageButton back = (ImageButton)customDesign.findViewById(R.id.ibCustomBack);
        back.setOnClickListener(this);

        ImageButton uploadfly = (ImageButton)customDesign.findViewById(R.id.ibCustomUpload);
        uploadfly.setOnClickListener(this);

        ImageButton next = (ImageButton)customDesign.findViewById(R.id.ibCustomNext);
        next.setOnClickListener(this);

        imageView = (ImageView)customDesign.findViewById(R.id.ivCustomFlyer);

        pr = (ProgressBar)customDesign.findViewById(R.id.pbCustomPost);
        pr.setIndeterminate(true);
        return customDesign;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibCustomUpload:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    }
                break;

            case R.id.ibCustomNext:
                if(photoUploaded) {
                    pr.setVisibility(View.VISIBLE);
                    bundle = getActivity().getIntent().getExtras();
                    privateStuff = bundle.getString("privacy");
                    hashStuff = bundle.getString("hashtag");
                    websiteStuff = bundle.getString("website");
                    descriptionStuff = bundle.getString("description");
                    title = bundle.getString("title");
                    category = bundle.getString("category");
                    date = bundle.getString("date");
                    time = bundle.getString("time");
                    address = bundle.getString("address");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");

                    PublicPost_Helper publicpost = new PublicPost_Helper();
                    switch (privateStuff){
                        case "Public":
                            publicPublish(publicpost);
                            break;

                        case "Private":
                            privatePublish(publicpost);
                            break;

                        case "Exclusive":
                            exclusivePublish(publicpost);
                            break;

                        default:
                            privatePublish(publicpost);
                            break;
                    }
                    }
                break;

            case R.id.ibCustomBack:
                Intent intent1 = new Intent(getActivity(), Promotion.class);
                startActivity(intent1);
        }
    }

    private void privatePublish(final PublicPost_Helper privatePost){
        Toast.makeText(getActivity().getApplicationContext(),
                "Processing...", Toast.LENGTH_SHORT).show();

        privatePost.setTitle(title);
        privatePost.setCategory(category);
        privatePost.setDate(date);
        privatePost.setTime(time);
        if(hashStuff.equals("null")){
            privatePost.setHashtag("null");
        } else {
            privatePost.setHashtag(hashStuff);
        }
        privatePost.setWebsite(websiteStuff);
        privatePost.setDescription(descriptionStuff);
        privatePost.setAddress(address);
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,
                longitude);
        privatePost.setLocation(geoPoint);
        privatePost.setUser(ParseUser.getCurrentUser());
        privatePost.setUserId(ParseUser.getCurrentUser().getObjectId());
        privatePost.setPrivacy(privateStuff);

        final ParseFile parseFile = new ParseFile("flyer.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    privatePost.setflyer(parseFile);

                    ParseObject.registerSubclass(GuestList_Helper.class);
                    ParseQuery<GuestList_Helper> priv = GuestList_Helper.getList();
                    priv.whereEqualTo("hostId", ParseUser.getCurrentUser().getObjectId())
                            .findInBackground(new FindCallback<GuestList_Helper>() {
                                @Override
                                public void done(List<GuestList_Helper> guestLists, ParseException e) {
                                    if (e == null) {
                                        List<String> privateList = new ArrayList<>();
                                        ParseACL asdf = new ParseACL();
                                        for(GuestList_Helper list : guestLists){
                                            privateList.add(list.getGuestId());
                                        }

                                        for(String user : privateList){
                                            asdf.setReadAccess(user, true);
                                        }
                                        asdf.setReadAccess(ParseUser.getCurrentUser(), true);
                                        asdf.setWriteAccess(ParseUser.getCurrentUser(), true);
                                        privatePost.setACL(asdf);
                                        privatePost.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null){
                                                    Toast.makeText(getActivity().getApplicationContext(),
                                                            "Event Published!", Toast.LENGTH_SHORT).show();
                                                    getActivity().finish();
                                                } else {
                                                    Toast.makeText(getActivity().getApplicationContext(),
                                                            "Publication failed. Make sure your connected to the internet.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }

                Intent intent1 = new Intent("com.socialgroupe.HOME");
                startActivity(intent1);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Almost done...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publicPublish(final PublicPost_Helper publicPostHelper){
        Toast.makeText(getActivity().getApplicationContext(),
                "Processing...", Toast.LENGTH_SHORT).show();

        publicPostHelper.setTitle(title);
        publicPostHelper.setCategory(category);
        publicPostHelper.setDate(date);
        publicPostHelper.setTime(time);
        if(hashStuff.equals("null")){
            publicPostHelper.setHashtag("null");
        } else {
            publicPostHelper.setHashtag(hashStuff);
        }
        publicPostHelper.setWebsite(websiteStuff);
        publicPostHelper.setDescription(descriptionStuff);
        publicPostHelper.setAddress(address);
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,longitude);
        publicPostHelper.setLocation(geoPoint);
        publicPostHelper.setPrivacy(privateStuff);
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
        publicPostHelper.setACL(acl);
        publicPostHelper.setUser(ParseUser.getCurrentUser());
        publicPostHelper.setUserId(ParseUser.getCurrentUser().getObjectId());

        final ParseFile parseFile = new ParseFile("flyer.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    publicPostHelper.setflyer(parseFile);
                    publicPostHelper.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent1 = new Intent("com.socialgroupe.HOME");
                                startActivity(intent1);
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Event Published!", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Publication failed. Make sure your connected to the internet.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                /*Intent intent1 = new Intent("com.socialgroupe.BULLETIN");
                startActivity(intent1);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Almost done...", Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    private void exclusivePublish(final PublicPost_Helper exclusivePost){
        Toast.makeText(getActivity().getApplicationContext(),
                "Processing...", Toast.LENGTH_SHORT).show();

        exclusivePost.setTitle(title);
        exclusivePost.setCategory(category);
        exclusivePost.setDate(date);
        exclusivePost.setTime(time);
        if(hashStuff.equals("null")){
            exclusivePost.setHashtag("null");
        } else {
            exclusivePost.setHashtag( hashStuff);
        }
        exclusivePost.setWebsite(websiteStuff);
        exclusivePost.setDescription(descriptionStuff);
        exclusivePost.setAddress(address);
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,longitude);
        exclusivePost.setLocation(geoPoint);
        exclusivePost.setUser(ParseUser.getCurrentUser());
        exclusivePost.setUserId(ParseUser.getCurrentUser().getObjectId());
        exclusivePost.setPrivacy(privateStuff);

        final ParseFile parseFile = new ParseFile("flyer.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    exclusivePost.setflyer(parseFile);
                    final ParseACL acl = new ParseACL();
                    ParseQuery<GuestList_Helper> exc = GuestList_Helper.getList();
                    exc.whereEqualTo("hostId", ParseUser.getCurrentUser().getObjectId())
                            .whereContainedIn("guestId",bundle.getStringArrayList("guestlist"))
                            .findInBackground(new FindCallback<GuestList_Helper>() {
                                @Override
                                public void done(List<GuestList_Helper> guestLists, ParseException e) {
                                    if (e == null) {
                                        List<String> exclusiveList = new ArrayList<>();
                                        for (GuestList_Helper list : guestLists) {
                                            exclusiveList.add(list.getGuestId());
                                        }

                                        for (String user : exclusiveList) {
                                            acl.setReadAccess(user, true);
                                        }
                                        acl.setReadAccess(ParseUser.getCurrentUser(), true);
                                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                                        exclusivePost.setACL(acl);
                                        exclusivePost.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "Event Published!", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Publication failed. Make sure your connected to the internet.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });

                    Intent intent1 = new Intent("com.socialgroupe.HOME");
                    startActivity(intent1);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Almost done...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_GET && resultCode == getActivity().RESULT_OK){
            Uri fullPhotoUri = data.getData();
            Drawable d = new BitmapDrawable(getResources(), saveFlyer(getActivity(), fullPhotoUri, imageView));
            imageView.setBackground(d);
            photoUploaded = true;
        }
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
