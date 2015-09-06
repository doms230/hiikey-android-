package com.socialgroupe.hiikeyandroid;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


/**
 * Created by Dominic on 9/13/2014.
 * Opening scene for the app.
 */

public class Splash extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final static int PLAY_SERVICE_RESOLUSION_REQUEST = 1000;

    private static final String TAG = Home.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private ParseGeoPoint myPoint;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle splash) {
        super.onCreate(splash);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_splash);



//////////////////////Main thread stuff////////////////////////////////////
        Thread timer = new Thread(){
            public void run(){
                try{
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                    }

                    sleep(2500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    if(ParseUser.getCurrentUser() != null) {

                        Intent iBoard = new Intent("com.socialgroupe.HOME");
                        if (mLastLocation!= null) {
                            Bundle bundle = new Bundle();
                            bundle.putDouble("lat", mLastLocation.getLatitude());
                            bundle.putDouble("long", mLastLocation.getLongitude());
                            iBoard.putExtras(bundle);
                        } else{
                           // Bundle bundle = new Bundle();
                            //bundle.putDouble("lat", 0);
                            //bundle.putDouble("long", 0);
                            //iBoard.putExtras(bundle);

                        }

                        startActivity(iBoard);

                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                        installation.put("user", ParseUser.getCurrentUser());
                        installation.saveInBackground();

                    } else{
                        Intent iLogin = new Intent("com.socialgroupe.SIGNIN_SIGNUP");
                        startActivity(iLogin);
                    }
                }
            }
        };
        timer.start();
    }

   /* protected ParseGeoPoint displayLocation(){
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
    }*/

    protected void displayLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


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
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode()="
                + result.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }


///////////////////////////////////////////////////////////

    // setting up the splash termination...
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
