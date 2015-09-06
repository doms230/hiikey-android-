package com.socialgroupe.hiikeyandroid;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Dominic on 2/18/2015.
 */
public class Prefs extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
