package com.socialgroupe.hiikeyandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

/**
 * Created by Dominic on 11/18/2014.
 * //
 */
public class Application extends android.app.Application {
    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Bulletin_Helper.class);
        ParseObject.registerSubclass(GuestList_Helper.class);
        ParseObject.registerSubclass(GuestListRemoved_Helper.class);
        ParseObject.registerSubclass(PublicPost_Helper.class);
        ParseObject.registerSubclass(PublicPostRemoved_Helper.class);
        ParseObject.registerSubclass(Subscribe_Helper.class);
        ParseObject.registerSubclass(Subscribe_Helper_delete.class);
        ParseObject.registerSubclass(Favorites_Helper.class);
        ParseObject.registerSubclass(FavoritesRemoved_Helper.class);

        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        String PARSE_CLIENT_ID = "LdoCUneRAIK9sJLFFhBFwz2YkW02wChG5yi2wkk2";
        String PARSE_APP_ID = "O9M9IE9aXxHHaKmA21FpQ1SR26EdP2rf4obYxzBF";
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_ID);

        ParseTwitterUtils.initialize("c47fm3Ta5yY5MoTTr5m529XxQ",
                "0do2WWQ8UbNLfw2GciLUo5sR1wVEScxXWM8JoWoq3QjTZ2aM45");

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
