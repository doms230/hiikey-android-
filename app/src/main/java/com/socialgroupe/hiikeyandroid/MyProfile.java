package com.socialgroupe.hiikeyandroid;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/*
Class where people view and edit their profile.
 */

public class MyProfile extends AppCompatActivity implements View.OnClickListener{

    //Navigation Drawer Stuff
    private DrawerLayout mDrawerLayout;

    private String instaLink, tumLink, twitLink, snapLink;
    //private ParseUser profileUser;
    private Button editProfile;
    private String profileUserstring;
    private LinearLayout event, host, guest;
    private ArrayList<String> hostList = new ArrayList<>();
    private ArrayList<String> guestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initiate();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        hostList.clear();
        guestList.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibProTwitter:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=".concat(twitLink)));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/".concat(twitLink)));
                    startActivity(i);
                }
                break;

            case R.id.ibProInstagram:
                try {
                    Intent in = getPackageManager().getLaunchIntentForPackage("com.instagram.android");

                    in.setComponent(new ComponentName("com.instagram.android",
                            "com.instagram.android.activity.UrlHandlerActivity"));
                    in.setData(Uri.parse("http://instagram.com/_u/".concat(instaLink)));
                    startActivity(in);
                } catch (Exception e) {
                    Intent you = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com".concat(instaLink)));
                    startActivity(you);
                }
                break;

            case R.id.ibProTumblr:
                Intent ypu = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://" + tumLink + ".tumblr.com"));
                startActivity(ypu);
                break;

            case R.id.bEditProfile:
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                break;

            case R.id.llEventNumber:
                Intent intent1 = new Intent(this, YourEvent.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", profileUserstring);
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;

            case R.id.llHostNumber:
                Bundle bundle1 = new Bundle();
                //bundle1.putStringArrayList("hostList", hostList);
                bundle1.putString("host", ParseUser.getCurrentUser().getObjectId());
                Intent intent2 = new Intent(this, Hosts.class);
                intent2.putExtras(bundle1);
                startActivity(intent2);
                hostList.clear();
                break;

            case R.id.llGuestNumber:
                Bundle bundle2 = new Bundle();
                //bundle2.putStringArrayList("guestList", guestList);
                bundle2.putString("guest", ParseUser.getCurrentUser().getObjectId());
                Intent intent3 = new Intent(this, Guests.class);
                intent3.putExtras(bundle2);
                startActivity(intent3);
                guestList.clear();
                break;

            default:
                Toast.makeText(this.getApplicationContext(), "Action couldn't be completed.", Toast.LENGTH_SHORT)
                        .show();
                Intent intent11 = new Intent(this, Home.class);
                startActivity(intent11);
        }
    }

    private void initiate() {

        final TextView noshare = (TextView)findViewById(R.id.tvNoshareSocials);

        final ParseImageView proPro = (ParseImageView)findViewById(R.id.ivProPic);

        profileUserstring = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> updateProfileParseQuery = ParseUser.getQuery();
        updateProfileParseQuery.whereEqualTo("objectId", profileUserstring);
        updateProfileParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser updateProfile, ParseException e) {
                if(e == null){

                ParseFile parseFile = updateProfile.getParseFile("Profile");
                Picasso.with(getApplicationContext())
                        .load(parseFile.getUrl())
                        .resize(proPro.getWidth(), proPro.getHeight())
                        .into(proPro);

                editProfile = (Button)findViewById(R.id.bEditProfile);
                editProfile.setVisibility(View.VISIBLE);
                editProfile.setOnClickListener(MyProfile.this);

                /**************Determine how many people the profileUser is hosting**************/
                ParseQuery<GuestList_Helper> guestSize = GuestList_Helper.getList();
                guestSize.whereEqualTo("hostId", profileUserstring)
                        .findInBackground(new FindCallback<GuestList_Helper>() {
                            @Override
                            public void done(List<GuestList_Helper> guestLists, ParseException e) {
                                if (e == null) {
                                    for (GuestList_Helper listNumber : guestLists) {
                                        guestList.add(listNumber.getGuestId());
                                    }

                                    TextView guestNumber = (TextView)findViewById(R.id.tvGuestNumber);
                                    guestNumber.setText(Integer.toString(guestList.size()));
                                    guestNumber.setVisibility(View.VISIBLE);

                                    guest = (LinearLayout)findViewById(R.id.llGuestNumber);
                                    guest.setOnClickListener(MyProfile.this);
                                }
                            }
                        });

                /*********Determine how many people are hosting the profileUser****************/
                ParseQuery<GuestList_Helper> hostSize = GuestList_Helper.getList();
                hostSize.whereEqualTo("guestId", profileUserstring)
                        .findInBackground(new FindCallback<GuestList_Helper>() {
                            @Override
                            public void done(List<GuestList_Helper> guestLists, ParseException e) {
                                if (e == null) {
                                    for (GuestList_Helper listNumber : guestLists) {
                                        hostList.add(listNumber.getHostId());
                                    }
                                    TextView hostNumber = (TextView)findViewById(R.id.tvHostNumber);
                                    hostNumber.setText(Integer.toString(hostList.size()));
                                    hostNumber.setVisibility(View.VISIBLE);

                                    host = (LinearLayout)findViewById(R.id.llHostNumber);
                                    host.setOnClickListener(MyProfile.this);
                                }
                            }
                        });

                /*********Determine how many events the profileUser has created****************/
                ParseQuery<PublicPost_Helper> eventSize = PublicPost_Helper.getQuery();
                eventSize.whereEqualTo("userId", profileUserstring)
                        .findInBackground(new FindCallback<PublicPost_Helper>() {
                            @Override
                            public void done(List<PublicPost_Helper> publicPostHelpers, ParseException e) {
                                if (e == null) {
                                    TextView eventNumber = (TextView)findViewById(R.id.tvEventNumber);
                                    eventNumber.setText(Integer.toString(publicPostHelpers.size()));
                                    eventNumber.setVisibility(View.VISIBLE);

                                    event = (LinearLayout)findViewById(R.id.llEventNumber);
                                    event.setOnClickListener(MyProfile.this);
                                }
                            }
                        });

                instaLink = updateProfile.get("InstagramHandle").toString();
                twitLink = updateProfile.get("TwitterHandle").toString();
                tumLink = updateProfile.get("TumblrHandle").toString();
                snapLink = updateProfile.get("SnapchatHandle").toString();

                if (!instaLink.isEmpty()) {
                    ImageButton insta = (ImageButton)findViewById(R.id.ibProInstagram);
                    insta.setOnClickListener(MyProfile.this);
                    insta.setVisibility(View.VISIBLE);
                    noshare.setVisibility(View.GONE);
                }
                if (!twitLink.isEmpty()) {
                    ImageButton twit = (ImageButton)findViewById(R.id.ibProTwitter);
                    twit.setOnClickListener(MyProfile.this);
                    twit.setVisibility(View.VISIBLE);
                }
                if (!tumLink.isEmpty()) {
                    ImageButton tumb = (ImageButton)findViewById(R.id.ibProTumblr);
                    tumb.setOnClickListener(MyProfile.this);
                    tumb.setVisibility(View.VISIBLE);
                    noshare.setVisibility(View.GONE);
                }
                if (!snapLink.isEmpty()) {
                    ImageView imageView = (ImageView)findViewById(R.id.ivProfileSnapchat);
                    imageView.setVisibility(View.VISIBLE);
                    TextView snapText = (TextView)findViewById(R.id.tvSnapUsername);
                    snapText.setText(snapLink);
                    snapText.setVisibility(View.VISIBLE);
                    noshare.setVisibility(View.GONE);
                }
                if (instaLink.isEmpty() && twitLink.isEmpty() && tumLink.isEmpty() && snapLink.isEmpty()) {
                    noshare.setVisibility(View.VISIBLE);
                }
            }
        }
        });
    }
}