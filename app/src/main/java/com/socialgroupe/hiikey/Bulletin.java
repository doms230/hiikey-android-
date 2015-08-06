package com.socialgroupe.hiikey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class Bulletin extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{

    //test test

    // Custom Listview Stuff
    StickyListHeadersListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    Customlv_Adapter adapter;
    private List<FlyerObject> flyers = null;

    //Navigation Drawer Stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navItems;
    private ActionBarDrawerToggle mDrawerToggle;


    /**
     * *******************Location stuff****************
     */

    private static final String TAG = Bulletin.class.getSimpleName();
    private final static int PLAY_SERVICE_RESOLUSION_REQUEST = 1000;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates =false;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000; //10 sec
    private static int DISPLACEMENT =10; //10 meters
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

        //Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

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

        listview = (StickyListHeadersListView) findViewById(R.id.lvBulletin);

        /*
      FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabBulletin);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVerified();
            }
        });

        */
        // check google play services (Location stuff)
        if(checkPlayServices()){
            buildGoogleApiClient();
        }
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(Bulletin.this);
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            flyers = new ArrayList<FlyerObject>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PublicPost");
                query.orderByAscending("bulletinId");
                //query.whereWithinMiles("location",location,50);
                ob = query.find();
                for (ParseObject obj : ob) {
                    FlyerObject temp = new FlyerObject();
                    temp.setCategory((String) obj.get("Category"));
                    temp.setFlyer((ParseFile) obj.get("Flyer"));

                    ParseQuery bquery = new ParseQuery<ParseObject>("Bulletin");
                    bquery.whereEqualTo("bulletinName", temp.getCategory());
                    //bquery.whereWithinMiles("bulletinLocation",myPoint,50);// showing the bulletins within 50 miles
                    List<ParseObject> bull;
                    bull = bquery.find();
                    for (ParseObject b : bull) {
                        BulletinObject bo = new BulletinObject();
                        bo.setName((String) b.get("bulletinName"));
                        //bo.setCreator((String) b.get("userId"));
                        bo.setPic((ParseFile) b.get("bulletinPic"));
                        bo.setId(b.getLong("numId"));

                        temp.setBullId(bo.getId());
                        temp.setBulletin(bo);
                    }
                    /*
                    bquery = new ParseQuery<ParseObject>("User");
                    bquery.whereEqualTo("objectId", temp.getBulletin().getCreator());
                    bull = bquery.find();
                    for (ParseObject b : bull) {
                        temp.getBulletin().setCreator((String) b.get("username"));
                    }
                    */

                    flyers.add(temp);
                }

            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listView in activity_test.xml
            listview = (StickyListHeadersListView) findViewById(R.id.lvBulletin);
            // Pass the results into Customlv_Adapter.java
            adapter = new Customlv_Adapter(Bulletin.this, flyers);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
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
        /*
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_searchBulletin).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
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
        /*if (mDrawerToggle.onOpionsItemSelected(item)) {
            return true;
        }*/

        switch (item.getItemId()) {

            case R.id.action_searchBulletin:
                Intent intent = new Intent(this, Search.class);
                startActivity(intent);
                return true;

            case R.id.action_verifyMembers:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hiikey.com/verified-members"));
                startActivity(browserIntent);
                return true;

            case R.id.action_shareFriendsv2:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Hiikey");
                    String sAux = "\n Yo, check this out! \n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.socialgroupe.hiikey \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share Hiikey"));
                } catch (Exception e) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ******************************Location methods***************************
     */

    // get location (latitude,longitude)

    protected ParseGeoPoint displayLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            myPoint = new ParseGeoPoint(latitude,longitude);
            Toast.makeText(getApplicationContext(),""+myPoint,
                    Toast.LENGTH_SHORT).show();

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
        if(resultCode!=ConnectionResult.SUCCESS){
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
