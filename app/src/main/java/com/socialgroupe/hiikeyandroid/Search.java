package com.socialgroupe.hiikeyandroid;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


/**
 * Created by Yaning on 6/30/2015.
 */
public class Search extends ActionBarActivity {

    // Tab Stuff
    static final int NUM_ITEMS = 3; // people, bulletin, location
    MyAdapter mAdapter;
    ViewPager mPager;

    private SearchManager searchManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchviewpager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.searchPager);
        mPager.setAdapter(mAdapter);

        // Watch for button clicks
        Button button = (Button) findViewById(R.id.bulletin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);

            }
        });
        button = (Button) findViewById(R.id.people);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(NUM_ITEMS - 1);

            }
        });
        button = (Button) findViewById(R.id.location);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPager.setCurrentItem(NUM_ITEMS - 2);
            }
        });
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return SearchBulletinsF.newInstance();
                // test -- pass string from activity to fragment
                case 1: return SearchPeople.newInstance();
                //return SearchBulletinsF.newInstance("SearchBulletins, Default");
                case 2: return SearchLocation.newInstance();
            }
            return SearchBulletinsF.newInstance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        //searchView.OnQueryTextListener asdf = new SearchView.OnQueryTextListener();

        //public boolean OnQueryTextCha
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
