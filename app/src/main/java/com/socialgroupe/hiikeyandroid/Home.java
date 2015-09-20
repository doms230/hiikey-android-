package com.socialgroupe.hiikeyandroid;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.DataOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 8/5/15.
 * Home is a parent activity.
 * Fragment: Home_fragment
 *
 * Home is where the users see the flyers.
 *
 */

public class Home extends AppCompatActivity implements android.support.v7.app.ActionBar.OnNavigationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    static int NUM_ITEMS = 0;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    ParseGeoPoint adsf;

    ParseQuery<PublicPost_Helper> loadFlyerData;

    FlyerPager_Adapter mAdapter;
    ViewPager mPager;
   // ActionBar actionBar;

    private Double lat;
    private Double longa;

    private ArrayList<String> flyerFile = new ArrayList<>();
    private ArrayList<String> bulletinName = new ArrayList<>();
    private ArrayList<String> flyerId = new ArrayList<>();
    private ArrayList<String> flyerHashtag = new ArrayList<>();

    /******Location Stuff*******/
    private static final String TAG = Home.class.getSimpleName();
    private final static int PLAY_SERVICE_RESOLUSION_REQUEST = 1000;
    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private ParseGeoPoint myPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_fragment_pager);

        if(checkPlayServices()){
            buildGoogleApiClient();
        }
        /***Get Subscriptions***/

        // Set up the action bar to show a dropdown list.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                "Subscriptions",
                                "Local",
                                "Private",
                                "Likes"
                        }),
                this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    /**
     *
     * @param position used to distinguish which Flyer type was chosen..
     * @param id not used.
     * @return
     * functions that decides which data to load.
     *
     */
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        switch (position){
            /**Subscriptions was selected
             * Loads all of the flyers under the bulletins that the user
             * subscribed to
             * **/

            case 0:
                flyerFile.clear();
                bulletinName.clear();
                flyerId.clear();
                flyerHashtag.clear();
                loadFlyerData = PublicPost_Helper.getQuery();

                if(adsf != null)
                {

                    loadFlyerData.whereWithinMiles("location", adsf, 50);
                } else {

                    loadFlyerData.whereWithinMiles("location", myPoint, 50);
                }

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
                break;
            /**
             * Local was selected
             *
             * Loads all of the flyers within a 50 mile radius of the user.
             *
             * */
            case 1:
                flyerFile.clear();
                bulletinName.clear();
                flyerId.clear();
                flyerHashtag.clear();

                ParseQuery<Subscribe_Helper> loadSubs = Subscribe_Helper.getData();
                loadSubs.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                        .findInBackground(new FindCallback<Subscribe_Helper>() {
                            @Override
                            public void done(List<Subscribe_Helper> list, ParseException e) {
                                if (e == null) {

                                    List<String> subList = new ArrayList<>();
                                    for (Subscribe_Helper asd : list){
                                        subList.add(asd.getBulletinTitle());
                                    }

                                    ParseQuery<PublicPost_Helper> loadFlyerData1 = PublicPost_Helper.getQuery();
                                     loadFlyerData1.whereContainedIn("Category", subList);
                                    loadFlyerData1.findInBackground(new FindCallback<PublicPost_Helper>() {
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
                        });
                break;

            /**
             * *Private was selected
             * Loads all of the Flyers listed as private by the users' hosts
             * Users can only see exclusive events if their host added them upon flyer creation.
             *
             * ****/
            case 2:
                flyerFile.clear();
                bulletinName.clear();
                flyerId.clear();
                flyerHashtag.clear();

                ParseQuery<PublicPost_Helper> loadPrivateFlyerData = PublicPost_Helper.getQuery();
                loadPrivateFlyerData.whereEqualTo("Privacy", "Private");
                loadPrivateFlyerData.whereEqualTo("Privacy", "Exclusive");
                loadPrivateFlyerData.findInBackground(new FindCallback<PublicPost_Helper>() {
                    @Override
                    public void done(List<PublicPost_Helper> list, ParseException e) {
                        if (e == null) {

                            for (PublicPost_Helper getFlyerData : list) {
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
                break;

            /**
             * liked flyers was selected
             * Shows all of the flyers that the user liked.
             */
            case 3:
                /**
                 * cleared the array lists, because in case new content is added/deleted
                 * May be a better way to do this.
                 */
                flyerFile.clear();
                bulletinName.clear();
                flyerId.clear();
                flyerHashtag.clear();

                ParseQuery<Favorites_Helper> loadFavs = Favorites_Helper.getData();
                loadFavs.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                        .findInBackground(new FindCallback<Favorites_Helper>() {
                            @Override
                            public void done(List<Favorites_Helper> list, ParseException e) {
                                if (e == null) {
                                    List<String> favList = new ArrayList<>();

                                    for (Favorites_Helper fava : list) {
                                        favList.add(fava.getFlyerId());
                                    }

                                    ParseQuery<PublicPost_Helper> loadInterestedData = PublicPost_Helper.getQuery();
                                    loadInterestedData.whereContainedIn("objectId", favList);
                                    loadInterestedData.findInBackground(new FindCallback<PublicPost_Helper>() {
                                        @Override
                                        public void done(List<PublicPost_Helper> list, ParseException e) {
                                            if (e == null) {

                                                for (PublicPost_Helper getFlyerData : list) {
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
                        });
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_newFlyer:
                Intent intent3 = new Intent(this, Promotion.class);
                startActivity(intent3);
                return true;

            case R.id.action_myProfile:
                Intent intent4 = new Intent(this, Profile.class);
                intent4.putExtra("user", ParseUser.getCurrentUser().getObjectId());
                startActivity(intent4);
                return true;

            case R.id.action_search2:
                Intent intent = new Intent(this, Search.class);
                startActivity(intent);
                return true;

           /* case R.id.action_verifyMembers:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hiikey.com/verified-members"));
                startActivity(browserIntent);
                return true;*/

            case R.id.action_shareFriends:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Hiikey");
                    String sAux = "\n Yo, check this out! \n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.socialgroupe.hiikeyandroid \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share Hiikey"));
                } catch (Exception e) {
                }
                return true;

            case R.id.action_signout:
                ParseUser.logOut();
                if(ParseUser.getCurrentUser() == null) {
                    Intent a = new Intent(this, Signup_Login.class);
                    startActivity(a);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**Location updates***/
    protected ParseGeoPoint displayLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            myPoint = new ParseGeoPoint(latitude,longitude);

        }else{
            Toast.makeText(getApplicationContext(), "Couldn't receive your location",
                    Toast.LENGTH_SHORT).show();
        }
        return myPoint;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RESOLUSION_REQUEST)
                        .show();
            } else{
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
    }

    @Override

    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG,"Connection failed: ConnectionResult.getErrorCode()="
                +result.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();

       /* lat =  bundle.getDouble("lat");
        longa =  bundle.getDouble("long");
        adsf = new ParseGeoPoint(lat, longa);*/

        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
