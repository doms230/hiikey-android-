package com.socialgroupe.hiikeyandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseObject;

/**
 * Parent Activity for UploardFlyer.Java and CreateFLyer_Frag.Java
 */
public class NewDesign extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_event);
        Bundle bundle = getIntent().getExtras();
        ParseObject.registerSubclass(GuestList_Helper.class);
        ParseObject.registerSubclass(PublicPost_Helper.class);

        boolean customOrNa = bundle.getBoolean("custom", false);
        if (savedInstanceState == null) {
            if (customOrNa) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new UploardFlyer().cDesign_Instance())
                        .commit();
            } else{
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new CreateFlyer_Frag().des_inta())
                        .commit();
            }
        }
    }
}
