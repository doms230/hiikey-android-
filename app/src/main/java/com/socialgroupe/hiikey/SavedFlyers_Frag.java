package com.socialgroupe.hiikey;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 12/23/2014.
 * Fragment to show people's favorite flyers.
 */
public class SavedFlyers_Frag extends ActionBarActivity implements View.OnClickListener {

    private FavoriteAdapter favoriteAdapter;
    private GridView listView;
    private String favFlyZone;
    private LinearLayout noFavs;
    private ProgressBar pb;

    //Navigation Drawer Stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navItems;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_favorite);

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

        listView = (GridView)findViewById(R.id.gridFavView);
        noFavs = (LinearLayout)findViewById(R.id.llnoFavsStuff);
        pb = (ProgressBar)findViewById(R.id.pbFavProgress);
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);

        ParseObject.registerSubclass(Favorites.class);

        ParseObject.registerSubclass(PublicPost_Helper.class);
        ParseQueryAdapter<PublicPost_Helper> favAdapter =
                new ParseQueryAdapter<>(this, PublicPost_Helper.class);
        favAdapter.setTextKey("title");
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTimeline();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimeline();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick( final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.ivFavv2:
                        if (ParseUser.getCurrentUser() != null) {
                            Intent intent = new Intent(SavedFlyers_Frag.this, SeeFlyer.class);
                            intent.putExtra("objectId", v.getTag().toString());
                            startActivity(intent);
                        } else {
                            showLogin();
                        }
                        break;

                    default:
                        Intent intent1 = new Intent(SavedFlyers_Frag.this, Bulletin.class);
                        startActivity(intent1);
                        finish();
                }
            }
        });
    }

    private void showLogin(){
        Intent intent = new Intent(SavedFlyers_Frag.this, Signup_Login.class);
        startActivity(intent);
    }

    private void updateTimeline(){
        ParseQuery<Favorites> includeFavs = Favorites.getData();
        includeFavs.whereEqualTo("user", ParseUser.getCurrentUser());
        includeFavs.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> fava, ParseException e) {
                if (e == null) {
                    final List<String> favList = new ArrayList<>();
                    for (Favorites favFavs : fava) {
                        favFlyZone = favFavs.getFlyerId();
                        favList.add(favFlyZone);
                    }
                    favoriteAdapter = new FavoriteAdapter(SavedFlyers_Frag.this, favList);
                    listView.setAdapter(favoriteAdapter);
                    favoriteAdapter.loadObjects();
                    favoriteAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PublicPost_Helper>() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(List<PublicPost_Helper> publicPostHelpers, Exception e) {
                            pb.setVisibility(View.GONE);
                            if (publicPostHelpers.isEmpty()) {
                                noFavs.setVisibility(View.VISIBLE);
                            } else {
                                noFavs.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    class FavoriteAdapter extends ParseQueryAdapter<PublicPost_Helper> {
        public FavoriteAdapter (Context context, final List list){
            super(context, new QueryFactory<PublicPost_Helper>() {
                @Override
                public ParseQuery<PublicPost_Helper> create() {
                    ParseQuery query = new ParseQuery("PublicPost");
                            query.whereContainedIn("objectId", list);

                    return query;
                }
            });
        }

        @Override
        public View getItemView(PublicPost_Helper object, View v, ViewGroup parent) {
            if( v == null){
                v = View.inflate(getContext(), R.layout.fragment_row_favs, null);
            }
            super.getItemView(object, v, parent);

            final ImageView parseImageView =
                    (ImageView)v.findViewById(R.id.ivFavv2);
            parseImageView.setTag(object.getObjectId());
            parseImageView.setOnClickListener(SavedFlyers_Frag.this);

                ParseFile parseFile2 = object.getParseFile("Flyer");
            Picasso.with(SavedFlyers_Frag.this)
                    .load(parseFile2.getUrl())
                    .resize(325,405)
                    .centerCrop()
                    .into(parseImageView);
            return v;
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

        return super.onOptionsItemSelected(item);
    }
}
