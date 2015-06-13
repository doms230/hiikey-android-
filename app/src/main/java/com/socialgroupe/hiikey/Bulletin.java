package com.socialgroupe.hiikey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.melnykov.fab.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;


public class Bulletin extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{

    private ListView listView;

    //Navigation Drawer Stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navItems;
    private ActionBarDrawerToggle mDrawerToggle;

    /**********************Location stuff*****************/
    private static final long ONE_MIN = 1000 * 60;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private GoogleApiClient mGoogleApiClient;
    private double mLat, mLong;
    private ParseGeoPoint myPoint;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);

        // Navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu_white);

        ParseObject.registerSubclass(Bulletin_Helper.class);
        ParseObject.registerSubclass(Props_Helper.class);
        ParseObject.registerSubclass(Subscribe_Helper.class);



        ParseQuery<Subscribe_Helper> getStuff = Subscribe_Helper.getData();
        getStuff.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo("bulletinTitle", "PARTY")
                .getFirstInBackground(new GetCallback<Subscribe_Helper>() {
                    @Override
                    public void done( Subscribe_Helper parseUser, ParseException e) {
                        if (e != null) {

                            Subscribe_Helper helper = new Subscribe_Helper();
                            subStuff(helper, "PARTY", "j39Pp4rtzu");
                            helper.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Subscribe_Helper helper = new Subscribe_Helper();
                                        subStuff(helper, "OTHER", "UL6qP7JGJb");
                                        helper.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Subscribe_Helper helper = new Subscribe_Helper();
                                                    subStuff(helper, "MEETUP", "ih5bqtmBmI");
                                                    helper.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {

                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });

        listView = (ListView) findViewById(R.id.lvBulletin);

      FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabBulletin);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVerified();
            }
        });

        /*************************location setup****************************/
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ParseQueryAdapter<Bulletin_Helper> mainA = new ParseQueryAdapter<Bulletin_Helper>(this, Bulletin_Helper.class);
        mainA.setTextKey("title");

        updateBulletin(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateBulletin(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        updateBulletin(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    //Navigation Drawer
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bulletin, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_searchBulletin).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        navItems = getResources().getStringArray(R.array.navItems_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, navItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()){

            case R.id.action_searchBulletin:
                //Intent intent = new Intent(this, SearchBulletins.class);
                //startActivity(intent);
                return true;

            case R.id.action_verifyMembers:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hiikey.com/verified-members"));
                startActivity(browserIntent);
                return true;

            case R.id.action_shareFriendsv2:
                try
                { Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Hiikey");
                    String sAux = "\n Yo, check this out! \n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.socialgroupe.hiikey \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share Hiikey"));
                }
                catch(Exception e)
                {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*********************************Location methods****************************/

    @Override
    public void onConnected(Bundle bundle) {
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            /*******************************************************/
            // Get best last location measurement meeting criteria
           Location currentLocation = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
            if(currentLocation != null) {
                mLat = currentLocation.getLatitude();
                mLong = currentLocation.getLongitude();

                //myPoint = new ParseGeoPoint(mLat,mLong);

            } else{
                Toast.makeText(getApplicationContext(), "Couldn't receive your location",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if ( !mIntentInProgress && result.hasResolution()){
            mIntentInProgress = true;
            try {
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN){
            mIntentInProgress = false;

            if(!mGoogleApiClient.isConnecting()){
                mGoogleApiClient.connect();
            }
        }
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

    /****************Private methods***********************/

    private void subStuff(Subscribe_Helper helper,String bName, String bObject){
        helper.setBulletinSubscriber(ParseUser.getCurrentUser());
        helper.setUserId(ParseUser.getCurrentUser().getObjectId());
        helper.setBulletinId(bObject);
        helper.setBulletinTitle(bName);
    }

    private void isVerified(){
        ParseQuery<Props_Helper> parsa = Props_Helper.getPropList();
        parsa.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .getFirstInBackground(new GetCallback<Props_Helper>() {
                    @Override
                    public void done(Props_Helper props_helper, ParseException e) {
                        if (e == null) {
                            if (props_helper.getVerification()) {
                                createNew();

                            } else {
                                Intent intent = new Intent("com.socialgroupe.PROMOTION");
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }

    private void createNew(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create")
                .setPositiveButton(getString(R.string.newEvent), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("com.socialgroupe.PROMOTION");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.newBulletin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("com.socialgroupe.NEWBULLETIN");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateBulletin(final Context context){
        BulletinAdapter bulletinAdapter = new BulletinAdapter(context);
        listView.setAdapter(bulletinAdapter);
        bulletinAdapter.loadObjects();
    }

    class BulletinAdapter extends ParseQueryAdapter<Bulletin_Helper> {
        public BulletinAdapter(Context context){
            super(context, new ParseQueryAdapter.QueryFactory<Bulletin_Helper>(){

                public ParseQuery<Bulletin_Helper> create(){
                    ParseQuery query = new ParseQuery("Bulletin");
                   // query.whereWithinMiles("bulletinLocation", myPoint, 50);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final Bulletin_Helper object, View v, ViewGroup parent){

            if(v==null) {
                v = View.inflate(getContext(), R.layout.activity_row_bulletin, null);
                v.setTag(object.getObjectId());
            }
            super.getItemView(object, v, parent);

            String objectid = object.getObjectId();

            /**
             * Changed "timeTitle" to date, because the titles people make won't always fit in one line.
             * -Dominic
             * 2/8/15
             */

            TextView textView = (TextView) v.findViewById(R.id.tvBulletinName);
            if(object.getShowHideSetting().equals("hide") ){
                textView.setVisibility(View.INVISIBLE);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(object.getBulletinName());
            }
            textView.setTag(object.getObjectId());

            final ImageView parseImageView =
                    (ImageView) v.findViewById(R.id.ivBulletin);
            parseImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent("com.socialgroupe.PUBLICPOSTV2");
                    bundle.putString("bulletin", v.getTag().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            parseImageView.setTag(objectid);

            ParseFile parseFile = object.getParseFile("bulletinPic");
            Picasso.with(Bulletin.this)
                    .load(parseFile.getUrl())
                    .resize(800, 450)
                    .centerCrop()
                    .into(parseImageView);
            return v;

            /////.resize(325, 405)
        }
    }
}
