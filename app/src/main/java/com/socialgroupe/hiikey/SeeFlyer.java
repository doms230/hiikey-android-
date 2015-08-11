package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class SeeFlyer extends ActionBarActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private String objectid;
    private String title, description, location, address, hashtag, website;
    private Bitmap bmp;
    private boolean isitFaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_flyer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bun = getIntent().getExtras();
        objectid = bun.getString("objectId");

            ParseObject.registerSubclass(PublicPost_Helper.class);
            ParseQuery<PublicPost_Helper> query = PublicPost_Helper.getQuery();
            query.whereEqualTo("objectId", objectid)
                    .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                        @Override
                        public void done(PublicPost_Helper publicPostHelper, ParseException e) {
                            if (e == null) {

                                title = publicPostHelper.getTitle();
                                if(title.equals("null")){
                                    title = "";
                                }

                                description = publicPostHelper.getDescription();
                                if(description.equals("null")){
                                    description = "";
                                }
                                location = publicPostHelper.getLocation().getLatitude() + ","
                                        + publicPostHelper.getLocation().getLongitude();

                                address = publicPostHelper.getAddress();
                                hashtag = publicPostHelper.getHashtag();
                                if(hashtag.equals("null")){
                                    hashtag = "";
                                }
                                website = publicPostHelper.getWebsite();
                                if (website.equals("null")){
                                    website = "";
                                } else if(!website.startsWith("http://") & !website.startsWith("https://")){
                                        website = "http://" + website;
                                }

                                ParseFile parseFile = publicPostHelper.getParseFile("Flyer");
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, ParseException e) {
                                        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    }
                                });
                            }
                        }
                    });
        

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_activity_see_flyer),
                                getString(R.string.title_activity_interested),
                        }),
                this);
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       //whichMenu(menu);
        getMenuInflater().inflate(R.menu.menu_see_flyer, menu);
        wasitFaved(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_info:
                openInfoDialog(description, title, hashtag);
                return true;

            case R.id.action_mapit:
                String mapit = "geo:0,0?q=" + location + "(" + address + ")";
                Uri locateEvent = Uri.parse(mapit);
                showMap(locateEvent);
                return true;

            case R.id.action_shareit:
                Uri ui = getImageUri(this, bmp );
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, ui);
                shareIntent.setType("image/png");
                shareIntent.putExtra("sms_body","Check this event out!" + hashtag + " #hiikey");

                if(shareIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(Intent.createChooser(shareIntent, "Share Flyer"));
                }
                return true;

            case R.id.action_loveit:
                if(!isitFaved){
                    item.setIcon(R.drawable.ic_heart_black);
                    isitFaved = true;
                    addLove();
                } else{
                    item.setIcon(R.drawable.ic_action_heart_white);
                    isitFaved = false;
                    removeLove();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected( int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.

        switch (position){
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FlyerFrag.flyerInstance())
                        .commit();
                break;

            /*case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        InterestedFrag.interInsta()).commit();
                break;*/
        }
        return true;
    }

    private void addLove() {
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> checkFav = Favorites.getData();
        checkFav.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo("flyerId", objectid)
                .getFirstInBackground(new GetCallback<Favorites>() {
                    @Override
                    public void done(Favorites favorites, ParseException e) {
                        if (e != null) {
                            Favorites favor = new Favorites();
                            favor.setUserId(ParseUser.getCurrentUser().getObjectId());
                            favor.setFavUser(ParseUser.getCurrentUser());
                            favor.setFlyerId(objectid);
                            favor.saveInBackground();
                        }
                    }
                });
    }

    private void removeLove(){
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> fav = Favorites.getData();
        fav.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo("flyerId", objectid)
                .getFirstInBackground(new GetCallback<Favorites>() {
                    @Override
                    public void done(final Favorites favorites, ParseException e) {
                        if (e == null) {
                            ParseObject.registerSubclass(FavoritesRemoved.class);
                            FavoritesRemoved remove = new FavoritesRemoved();
                            remove.setFlyerId(objectid);
                            remove.setFavUser(ParseUser.getCurrentUser());
                            remove.setUserId(ParseUser.getCurrentUser().getObjectId());
                            remove.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    favorites.deleteInBackground();
                                }
                            });
                    }
                }
                });
    }

    private void wasitFaved( final Menu menu) {
        ParseQuery<Favorites> favo = Favorites.getData();
        favo.whereEqualTo("flyerId", objectid)
                .whereEqualTo("user", ParseUser.getCurrentUser())
                .getFirstInBackground(new GetCallback<Favorites>() {
                    @Override
                    public void done(Favorites favorites, ParseException e) {
                        if (e == null) {
                            menu.removeItem(R.id.action_loveit);
                            MenuItem item = menu.add(0, R.id.action_loveit, 0, R.string.action_love);
                            item.setIcon(R.drawable.ic_heart_black);
                            MenuItemCompat.setShowAsAction(item, MenuItem.SHOW_AS_ACTION_ALWAYS);
                            isitFaved = true;
                        }
                    }
                });
    }

    private Uri getImageUri(Context context, Bitmap image){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image,
                "Flyer", null);
        return Uri.parse(path);
    }

    private void showMap(Uri geoLocation){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void openInfoDialog(String description, String title, String hashtag){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(description + "\n" + hashtag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!website.isEmpty()) {
            builder.setNeutralButton("Website", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(browserIntent);
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}