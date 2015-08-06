package com.socialgroupe.hiikey;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 8/5/15.
 */


public class Home extends ActionBarActivity{

    static int NUM_ITEMS = 0;


    MyAdapter mAdapter;
    ViewPager mPager;
   // ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseObject.registerSubclass(PublicPost_Helper.class);
        setContentView(R.layout.home_fragment_pager);

        ParseQuery<PublicPost_Helper> loadFlyerData = PublicPost_Helper.getQuery();
        loadFlyerData.findInBackground(new FindCallback<PublicPost_Helper>() {
            @Override
            public void done(List<PublicPost_Helper> list, ParseException e) {
                if (e == null) {
                    List<String> flyerNumber = new ArrayList<>();
                    /*for (PublicPost_Helper liasdf : list) {
                        flyerNumber.add(liasdf.getObjectId());

                    }*/
                    NUM_ITEMS = list.size();
                    mAdapter = new MyAdapter(getFragmentManager());

                    mPager = (ViewPager) findViewById(R.id.pager);
                    mPager.setAdapter(mAdapter);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bulletin, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

    public static class MyAdapter extends FragmentStatePagerAdapter{
        public MyAdapter(android.app.FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position) ;
        }

        @Override
        public int getCount() {

            return NUM_ITEMS;

        }
    }

    public static class ArrayListFragment extends ListFragment{
        int mNum;

        private List<String> flyerFile = new ArrayList<>();

        static ArrayListFragment newInstance(int num){
            ArrayListFragment f = new ArrayListFragment();

            // supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;

            //ParseQuery Data


        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home_pager_list, container, false);
            //View tv = v.findViewById(R.id.text);
            //((TextView)tv).setText("Fragment #" + mNum);
            //final ImageView flyerImage = (ImageView) v.findViewById(R.id.homeFlyer);

            final View fa = v.findViewById(R.id.homeFlyer);


            ParseQuery<PublicPost_Helper> loadFlyerData = PublicPost_Helper.getQuery();
            loadFlyerData.findInBackground(new FindCallback<PublicPost_Helper>() {
                @Override
                public void done(List<PublicPost_Helper> list, ParseException e) {
                    if (e == null) {
                        for (PublicPost_Helper setFlyerData : list) {
                            ParseFile parseFile = setFlyerData.getParseFile("Flyer");
                            flyerFile.add(parseFile.getUrl());

                        }

                        Picasso.with(getActivity())
                                .load(flyerFile.get(mNum))
                                .placeholder(R.drawable.ic_launcher_hiikey)
                                .resize(fa.getWidth(), fa.getHeight())

                                .into((ImageView) fa);
                    }
                }
            });

            return v;
        }

       /* @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            /*setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, R.array.myLo_array));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            Log.i("FragmentList", "Item clicked: " + id);
        }*/
    }
}
