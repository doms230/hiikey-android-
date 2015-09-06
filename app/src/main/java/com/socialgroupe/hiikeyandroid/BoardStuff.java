package com.socialgroupe.hiikeyandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Dominic on 2/18/2015.
 * Preference activity to for system preferences
 */
public class BoardStuff extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new Prefs()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
