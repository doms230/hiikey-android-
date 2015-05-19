package com.socialgroupe.hiikey;

/*/
Class where the flyer design is handled.
 */

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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateFlyer_Frag extends Fragment implements View.OnClickListener {

    private ImageView filter;
    private RelativeLayout background;
    private String stitle, saddress, sdate, stime, shashtag, website, description, privacy;
    private static final int REQUEST_IMAGE_GET = 1;
    private HorizontalScrollView filterColors, designLayouts;
    private String category;
    private Bundle createFlyInfo;
    private double latitude, longitude;
    private byte[] bytes;
    private ProgressBar pr;
    private ImageButton upload, uploadDismiss;

    public static CreateFlyer_Frag des_inta(){
        return new CreateFlyer_Frag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dCreate = inflater.inflate(R.layout.fragment_create_design, container, false);
        createFlyer(dCreate);

        ImageButton back = (ImageButton)dCreate.findViewById(R.id.ibCreateBack);
        back.setOnClickListener(this);

        upload = (ImageButton)dCreate.findViewById(R.id.ibCreateUploadBack);
        upload.setOnClickListener(this);

        uploadDismiss = (ImageButton)dCreate.findViewById(R.id.ibCreateDismissBack);
        uploadDismiss.setOnClickListener(this);

        ImageButton next = (ImageButton)dCreate.findViewById(R.id.ibCreateNext);
        next.setOnClickListener(this);

        pr = (ProgressBar)dCreate.findViewById(R.id.pbCreatePost);
        pr.setIndeterminate(true);
        return dCreate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibCreateUploadBack:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    }
                break;

            case R.id.ibCreateNext:
                pr.setVisibility(View.VISIBLE);
                switch (privacy){
                    case "Public":
                        publicPublish();
                        break;

                    case "Private":
                        privatePublish();
                        break;

                    case "Exclusive":
                        exclusivePublish();
                        break;

                    default:
                        privatePublish();
                        break;
                }
                break;

            case R.id.ibCreateBack:
                Intent intent2 = new Intent(getActivity(), Promotion.class);
                startActivity(intent2);

            case R.id.ibRedBack:
                filter.setBackgroundResource(R.color.redFilter);
                break;

            case R.id.ibBlackBack:
                filter.setBackgroundResource(R.color.black_overlay);
                break;

            case R.id.ibBlueBack:
                filter.setBackgroundResource(R.color.blueFilter);
                break;

            case R.id.ibGreenBack:
                filter.setBackgroundResource(R.color.greeFilter);
                break;

            case R.id.ibPurpleBack:
                filter.setBackgroundResource(R.color.purpleFilter);
                break;

            case R.id.ibBlueGBack:
                filter.setBackgroundResource(R.color.blueGFilter);
                break;

            case R.id.ibCreateDismissBack:
                filter.setVisibility(View.INVISIBLE);
                filterColors.setVisibility(View.GONE);
                uploadDismiss.setVisibility(View.GONE);
                upload.setVisibility(View.VISIBLE);
                designLayouts.setVisibility(View.VISIBLE);
                background.setBackgroundResource(R.color.black_overlay);
                break;

            case R.id.ibDesign1:
                background.setBackgroundResource(R.color.black_overlay);
                break;

            case R.id.ibDesign2:
                background.setBackgroundResource(R.color.redText);
                break;

            case R.id.ibDesign3:
                background.setBackgroundResource(R.color.purpleText);
                break;

            case R.id.ibDesign4:
                background.setBackgroundResource(R.color.deepPurText);
                break;

            case R.id.ibDesign5:
                background.setBackgroundResource(R.color.blueText);
                break;

            case R.id.ibDesign6:
                background.setBackgroundResource(R.color.tealText);
                break;

            case R.id.ibDesign7:
                background.setBackgroundResource(R.color.greenText);
                break;

            case R.id.ibDesign8:
                background.setBackgroundResource(R.color.amberText);
                break;

            case R.id.ibDesign9:
                background.setBackgroundResource(R.color.orangeText);
                break;

            case R.id.ibDesign10:
                background.setBackgroundResource(R.color.deepOrangeText);
                break;

            case R.id.ibDesign11:
                background.setBackgroundResource(R.color.blueGreyText);
                break;

            default:
                Toast.makeText(getActivity().getApplicationContext(), "Action couldn't be completed.",
                        Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getActivity(), Bulletin.class);
                startActivity(intent1);
                getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_GET && resultCode == getActivity().RESULT_OK){
            Uri fullPhotoUri = data.getData();
          //  saveFlyer(getActivity(), fullPhotoUri, background);
            Drawable d = new BitmapDrawable(getResources(), saveFlyer(getActivity(), fullPhotoUri, background));
            background.setBackground(d);
            filter.setVisibility(View.VISIBLE);
            filterColors.setVisibility(View.VISIBLE);
            filter.setBackgroundResource(R.color.black_overlay);
            designLayouts.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            uploadDismiss.setVisibility(View.VISIBLE);
        }
    }

    private void createFlyer(View view){

        filter = (ImageView)view.findViewById(R.id.ivFilter);

        ImageButton design1 = (ImageButton)view.findViewById(R.id.ibDesign1);
        design1.setOnClickListener(this);

        ImageButton design2 = (ImageButton)view.findViewById(R.id.ibDesign2);
        design2.setOnClickListener(this);

        ImageButton design3 = (ImageButton)view.findViewById(R.id.ibDesign3);
        design3.setOnClickListener(this);

        ImageButton design4 = (ImageButton)view.findViewById(R.id.ibDesign4);
        design4.setOnClickListener(this);

        ImageButton design5 = (ImageButton)view.findViewById(R.id.ibDesign5);
        design5.setOnClickListener(this);

        ImageButton design6 = (ImageButton)view.findViewById(R.id.ibDesign6);
        design6.setOnClickListener(this);

        ImageButton design7 = (ImageButton)view.findViewById(R.id.ibDesign7);
        design7.setOnClickListener(this);

        ImageButton design8 = (ImageButton)view.findViewById(R.id.ibDesign8);
        design8.setOnClickListener(this);

        ImageButton design9 = (ImageButton)view.findViewById(R.id.ibDesign9);
        design9.setOnClickListener(this);

        ImageButton design10 = (ImageButton)view.findViewById(R.id.ibDesign10);
        design10.setOnClickListener(this);

        ImageButton design11 = (ImageButton)view.findViewById(R.id.ibDesign11);
        design11.setOnClickListener(this);

        ImageButton filterColor1 = (ImageButton)view.findViewById(R.id.ibRedBack);
        filterColor1.setOnClickListener(this);

        ImageButton filterColor2 = (ImageButton)view.findViewById(R.id.ibPurpleBack);
        filterColor2.setOnClickListener(this);

        ImageButton filterColor3 = (ImageButton)view.findViewById(R.id.ibGreenBack);
        filterColor3.setOnClickListener(this);

        ImageButton filterColor4 = (ImageButton)view.findViewById(R.id.ibBlueGBack);
        filterColor4.setOnClickListener(this);

        ImageButton filterColor5 = (ImageButton)view.findViewById(R.id.ibBlueBack);
        filterColor5.setOnClickListener(this);

        ImageButton filterColor6 = (ImageButton)view.findViewById(R.id.ibBlackBack);
        filterColor6.setOnClickListener(this);

        createFlyInfo = getActivity().getIntent().getExtras();

        TextView title = (TextView) view.findViewById(R.id.tvTimeTitleV2);
        stitle = createFlyInfo.getString("title");
        title.setText(stitle);

        TextView address = (TextView) view.findViewById(R.id.tvAddress);
        saddress = createFlyInfo.getString("address");
        address.setText(saddress);

        TextView date = (TextView) view.findViewById(R.id.tvDate);
        sdate = createFlyInfo.getString("date");
        date.setText(sdate);

        TextView time = (TextView) view.findViewById(R.id.tvTime);
        stime = createFlyInfo.getString("time");
        time.setText(stime);

        TextView hashtag = (TextView) view.findViewById(R.id.tvHashtag);
        shashtag = createFlyInfo.getString("hashtag");
        if(shashtag.equals("null")){
            hashtag.setVisibility(View.GONE);
        }else {
            hashtag.setText(shashtag);
        }
        background = (RelativeLayout)view.findViewById(R.id.rlBackground);

        filterColors = (HorizontalScrollView)view.findViewById(R.id.svFilterColor);

        category = createFlyInfo.getString("category");
        website = createFlyInfo.getString("website");
        description = createFlyInfo.getString("description");
        privacy = createFlyInfo.getString("privacy");
        latitude = createFlyInfo.getDouble("latitude");
        longitude = createFlyInfo.getDouble("longitude");

        designLayouts = (HorizontalScrollView)view.findViewById(R.id.svDesignLayouts);
    }

    private void publicPublish(){
        Toast.makeText(getActivity().getApplicationContext(),
                "Processing...", Toast.LENGTH_SHORT).show();

        final PublicPost_Helper pubPost = new PublicPost_Helper();
        pubPost.setTitle(stitle);
        pubPost.setCategory(category);
        pubPost.setDate(sdate);
        pubPost.setTime(stime);
        if(shashtag.equals("null")){
            pubPost.setHashtag("null");
        } else {
            pubPost.setHashtag(shashtag);
        }
        pubPost.setWebsite(website);
        pubPost.setDescription(description);
        pubPost.setAddress(saddress);

        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,
                longitude);
        pubPost.setLocation(geoPoint);
        pubPost.setUser(ParseUser.getCurrentUser());
        pubPost.setUserId(ParseUser.getCurrentUser().getObjectId());
        pubPost.setPrivacy(privacy);
        savy();
        final ParseFile parseFile = new ParseFile("flyer.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    pubPost.setflyer(parseFile);

                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                    pubPost.setACL(acl);

                    pubPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Event Published!", Toast.LENGTH_SHORT).show();
                              /*  ParsePush push = new ParsePush();
                                push.setChannel("Party");
                                push.setMessage("A new event has been published in " + category + "!" );
                                push.sendInBackground() ;*/
                                getActivity().finish();
                            } else{
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Publication failed. Make sure your Internet is connected.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Intent intent = new Intent(getActivity(), Bulletin.class);
                    startActivity(intent);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Almost done...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void privatePublish(){
        Toast.makeText(getActivity().getApplicationContext(),
                "Processing...", Toast.LENGTH_SHORT).show();

        final PublicPost_Helper privPost = new PublicPost_Helper();

        privPost.setTitle(stitle);
        privPost.setCategory(category);
        privPost.setDate(sdate);
        privPost.setTime(stime);
        if(shashtag.equals("null")){
            privPost.setHashtag("null");
        } else {
            privPost.setHashtag(shashtag);
        }
        privPost.setWebsite(website);
        privPost.setDescription(description);
        privPost.setAddress(saddress);
        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,longitude);
        privPost.setLocation(geoPoint);
        privPost.setUser(ParseUser.getCurrentUser());
        privPost.setUserId(ParseUser.getCurrentUser().getObjectId());
        privPost.setPrivacy(privacy);

        savy();
        final ParseFile parseFile = new ParseFile("flyer.png", bytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    privPost.setflyer(parseFile);

                    ParseObject.registerSubclass(GuestList.class);
                    final ParseQuery<GuestList> priv = GuestList.getList();
                    priv.whereEqualTo("hostId", ParseUser.getCurrentUser().getObjectId())
                            .findInBackground(new FindCallback<GuestList>() {
                                @Override
                                public void done(List<GuestList> guestLists, ParseException e) {
                                    List<String> privateList = new ArrayList<>();
                                    for(GuestList list : guestLists){
                                        privateList.add(list.getGuestId());
                                    }
                                    ParseACL asdf = new ParseACL();
                                    for (String user : privateList){
                                        asdf.setReadAccess(user, true);
                                    }
                                    asdf.setReadAccess(ParseUser.getCurrentUser(), true);
                                    asdf.setWriteAccess(ParseUser.getCurrentUser(), true);
                                    privPost.setACL(asdf);


                                    privPost.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "Event Published!", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                            } else{
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "Publication failed. Make sure your Internet is connected.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                    Intent intent1 = new Intent("com.socialgroupe.BULLETIN");
                    startActivity(intent1);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Almost done...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   private void exclusivePublish(){
       Toast.makeText(getActivity().getApplicationContext(),
               "Processing...", Toast.LENGTH_SHORT).show();

       final PublicPost_Helper exPost = new PublicPost_Helper();

       exPost.setTitle(stitle);
       exPost.setCategory(category);
       exPost.setDate(sdate);
       exPost.setTime(stime);
       if(shashtag.equals("null")){
           exPost.setHashtag("null");
       } else {
           exPost.setHashtag(shashtag);
       }
       exPost.setWebsite(website);
       exPost.setDescription(description);
       exPost.setAddress(saddress);
       ParseGeoPoint geoPoint = new ParseGeoPoint(latitude,longitude);
       exPost.setLocation(geoPoint);
       exPost.setUser(ParseUser.getCurrentUser());
       exPost.setUserId(ParseUser.getCurrentUser().getObjectId());
       exPost.setPrivacy(privacy);

       savy();
       final ParseFile parseFile = new ParseFile("flyer.png", bytes);
       parseFile.saveInBackground(new SaveCallback() {
           @Override
           public void done(ParseException e) {
               if(e == null){
                   exPost.setflyer(parseFile);

                   ParseQuery<GuestList> exc = GuestList.getList();
                   exc.whereEqualTo("hostId", ParseUser.getCurrentUser().getObjectId())
                           .whereContainedIn("guestId",createFlyInfo.getStringArrayList("guestlist"))
                           .findInBackground(new FindCallback<GuestList>() {
                               @Override
                               public void done(List<GuestList> guestLists, ParseException e) {
                                   if (e == null) {
                                       List<String> exclusiveList = new ArrayList<>();
                                       for (GuestList list : guestLists) {
                                           exclusiveList.add(list.getGuestId());
                                       }
                                       ParseACL acl = new ParseACL();
                                       for (String user : exclusiveList) {
                                           acl.setReadAccess(user, true);

                                       }
                                       acl.setReadAccess(ParseUser.getCurrentUser(), true);
                                       acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                                       exPost.setACL(acl);
                                       exPost.saveInBackground(new SaveCallback() {
                                           @Override
                                           public void done(ParseException e) {
                                               if (e == null) {
                                                   Toast.makeText(getActivity().getApplicationContext(),
                                                           "Event Published!", Toast.LENGTH_SHORT).show();
                                                   getActivity().finish();
                                               }
                                           }
                                       });
                                   }else{
                                       Toast.makeText(getActivity().getApplicationContext(),
                                               "Publication failed. Make sure your Internet is connected.", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                   Intent intent1 = new Intent("com.socialgroupe.BULLETIN");
                   startActivity(intent1);
                   Toast.makeText(getActivity().getApplicationContext(),
                           "Almost done...", Toast.LENGTH_SHORT).show();
               }
           }
       });
   }

    private void savy(){
        background.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(background.getDrawingCache());
        background.setDrawingCacheEnabled(false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bytes = bos.toByteArray();
    }

    private Bitmap saveFlyer(Context context, Uri uri, RelativeLayout imageView) {
        /**
         * see saveFlyer() in EditProfile.java for info
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