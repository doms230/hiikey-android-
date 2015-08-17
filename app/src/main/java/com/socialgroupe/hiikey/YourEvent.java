package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class YourEvent extends AppCompatActivity implements View.OnClickListener {

    FlyerPager_Adapter mAdapter;

    ViewPager mPager;

    private ArrayList<String> flyerFile = new ArrayList<>();
    private ArrayList<String> bulletinName = new ArrayList<>();
    private ArrayList<String> flyerId = new ArrayList<>();
    private ArrayList<String> flyerHashtag = new ArrayList<>();

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment_pager);
        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");

        /*Button deleteButton = (Button) findViewById(R.id.yourEventDelete);
        deleteButton.setOnClickListener(this);

        if(user.equals(ParseUser.getCurrentUser().getObjectId())){
            deleteButton.setVisibility(View.VISIBLE);
        }*/

        loadStuff();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.yourEventDelete:
                openConfirmDeleteDialog(v);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Action couldn't completed.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openConfirmDeleteDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmDeleteTitle)
                .setMessage(R.string.confirmDeleteMessage)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yaornah = true;

                        ParseObject.registerSubclass(PublicPostRemoved_Helper.class);
                        ParseObject.registerSubclass(PublicPost_Helper.class);
                        ParseQuery<PublicPost_Helper> cladQuery = PublicPost_Helper.getQuery();
                        cladQuery.whereEqualTo("objectId", view.getTag().toString());
                        cladQuery.getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                                                           @Override
                                                           public void done(final PublicPost_Helper publicPostHelper, ParseException e) {
                                                               if (e == null) {
                                                                   PublicPostRemoved_Helper remove = new PublicPostRemoved_Helper();

                                                                   String title = publicPostHelper.getTitle();
                                                                   String address = publicPostHelper.getAddress();
                                                                   String date = publicPostHelper.getDate();
                                                                   String time = publicPostHelper.getTime();
                                                                   String hashtag = publicPostHelper.getHashtag();
                                                                   String category = publicPostHelper.getCategory();
                                                                   String privacy = publicPostHelper.getPrivacy();
                                                                   String des = publicPostHelper.getDescription();
                                                                   String website = publicPostHelper.getWebsite();

                                                                   remove.setTitle(title);
                                                                   remove.setAddress(address);
                                                                   remove.setDate(date);
                                                                   remove.setTime(time);

                                                                   if (!website.equals("null")) {
                                                                       remove.setWebsite(website);
                                                                   }

                                                                   if (!des.equals("null")) {
                                                                       remove.setDescription(des);
                                                                   }

                                                                   if (!hashtag.equals("null")) {
                                                                       remove.setHashtag(hashtag);
                                                                   }
                                                                   remove.setCategory(category);
                                                                   remove.setPrivacy(privacy);

                                                                   remove.setflyer(publicPostHelper.getfyler());
                                                                   remove.setUser(ParseUser.getCurrentUser());
                                                                   remove.setLocation(publicPostHelper.getLocation());
                                                                   remove.saveInBackground(new SaveCallback() {
                                                                       @Override
                                                                       public void done(ParseException e) {
                                                                           publicPostHelper.deleteInBackground(new DeleteCallback() {
                                                                               @Override
                                                                               public void done(ParseException e) {
                                                                                   loadStuff();
                                                                               }
                                                                           });
                                                                       }
                                                                   });
                                                               } else {
                                                                   Toast.makeText(getApplicationContext(),
                                                                           "Deletion failed. Make sure your Internet is connected.",
                                                                           Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       }
                        );
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yaornah = false;
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadStuff(){
        flyerFile.clear();
        bulletinName.clear();
        flyerId.clear();
        flyerHashtag.clear();

        ParseQuery<PublicPost_Helper> loadFlyerData = PublicPost_Helper.getQuery();
        loadFlyerData.whereEqualTo("userId", user);
        loadFlyerData.findInBackground(new FindCallback<PublicPost_Helper>() {
            @Override
            public void done(List<PublicPost_Helper> list, ParseException e) {
                if (e == null) {

                    for(PublicPost_Helper getFlyerData : list){
                        ParseFile parseFile = getFlyerData.getParseFile("Flyer");
                        flyerFile.add(parseFile.getUrl());
                        bulletinName.add(getFlyerData.getCategory());
                        flyerId.add(getFlyerData.getObjectId());
                        flyerHashtag.add(getFlyerData.getHashtag());
                    }

                    //NUM_ITEMS = list.size();
                    mAdapter = new FlyerPager_Adapter(getFragmentManager());

                    mAdapter.setFlyerFile(flyerFile);
                    mAdapter.setBulletinName(bulletinName);
                    mAdapter.setFlyerId(flyerId);
                    mAdapter.setFlyerHashtag(flyerHashtag);
                    mAdapter.setNUM_ITEMS(list.size());

                    mPager = (ViewPager) findViewById(R.id.pager);
                    mPager.setAdapter(mAdapter);
                }
            }
        });
    }
}