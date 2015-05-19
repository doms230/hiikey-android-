package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 4/16/2015.
 * Class to show events under Bulletins.
 */
public class Bulletin_Posts extends ActionBarActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private TimeLineAdapter timeLineAdapter;
    private GridView listView;
    private List<String>favList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private String bulletinTitle, bulletinId;
    private FloatingActionButton fab;
    private boolean isItSubscribed = false, isUser = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bulletin_posts);

        ParseObject.registerSubclass(Subscribe_Helper_delete.class);
        ParseObject.registerSubclass(PublicPost_Helper.class);
        ParseObject.registerSubclass(Bulletin_Helper.class);
        ParseObject.registerSubclass(Subscribe_Helper.class);

        isCurrentUser();
        bulletinTitleQuery();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srlPBReload);
        swipeRefreshLayout.setColorSchemeColors(R.color.appColor);

        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (GridView)findViewById(R.id.fvPubBoardFAB);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.pbPublicProgress);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        Button button = (Button)findViewById(R.id.bPublicCreateFun);
        button.setOnClickListener(this);
        linearLayout = (LinearLayout)findViewById(R.id.llPublicNoEvents);

        ParseQueryAdapter<PublicPost_Helper> mainAdapter = new ParseQueryAdapter<>(this, PublicPost_Helper.class);
        mainAdapter.setTextKey("title");

        updateBoard(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateBoard(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBoard(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRefresh() {
        updateBoard(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //whichMenu(menu);
        if(isUser) {
            getMenuInflater().inflate(R.menu.menu_post, menu);
        }
       /* menu.removeGroup(R.id.action_bulletin_usergroupe);
        isCurrentUser(menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_delete_bulletin:
                confirmBulletinDeletion();
            return true;

            case R.id.action_edit_bulletin:
                Intent intent = new Intent(this, EditBulletin.class);
                intent.putExtra("editBulletin", bulletinId);
                startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.ivFlyerv2:
                        Bundle bun = new Bundle();
                        bun.putString("objectId", v.getTag().toString());
                        Intent intent1 = new Intent("com.socialgroupe.SEEFLYER");
                        intent1.putExtras(bun);
                        startActivity(intent1);
                        break;

                    case R.id.fab:
                        if(!bulletinTitle.equals("PARTY")) {
                            if(!bulletinTitle.equals("MEETUP")) {
                                if(!bulletinTitle.equals("OTHER")) {
                                    if (!isItSubscribed) {
                                        Subscribe_Helper subit = new Subscribe_Helper();
                                        subit.setUserId(ParseUser.getCurrentUser().getObjectId());
                                        subit.setBulletinSubscriber(ParseUser.getCurrentUser());
                                        subit.setBulletinId(bulletinId);
                                        subit.setBulletinTitle(bulletinTitle);
                                        subit.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // fab.setBackgroundResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                                    Toast.makeText(getApplicationContext(), "Subscribed to " + bulletinTitle,
                                                            Toast.LENGTH_SHORT).show();
                                                    isItSubscribed = true;

                                                    ParsePush.subscribeInBackground(bulletinTitle);
                                                }
                                            }
                                        });

                                    } else {
                                        ParseQuery<Subscribe_Helper> subscribeParseQuery = Subscribe_Helper.getData();
                                        subscribeParseQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                                                .whereEqualTo("bulletinId", bulletinId)
                                                .getFirstInBackground(new GetCallback<Subscribe_Helper>() {
                                                    @Override
                                                    public void done(final Subscribe_Helper subscribe, ParseException e) {
                                                        if (e == null) {
                                                            Subscribe_Helper_delete subscribeHelperDelete = new Subscribe_Helper_delete();
                                                            subscribeHelperDelete.setBulletinTitle(bulletinTitle);
                                                            subscribeHelperDelete.setBulletinId(bulletinId);
                                                            subscribeHelperDelete.setBulletinSubscriber(ParseUser.getCurrentUser());
                                                            subscribeHelperDelete.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        subscribe.deleteInBackground(new DeleteCallback() {
                                                                            @Override
                                                                            public void done(ParseException e) {
                                                                                if (e == null) {
                                                                                    //fab.setBackgroundResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                                                                    Toast.makeText(getApplicationContext(), "un-subscribed to " + bulletinTitle,
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                    isItSubscribed = false;
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                } else{
                                    Toast.makeText(getApplicationContext(), "Can't un-subscribe/subscribe to default bulletins ",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                Toast.makeText(getApplicationContext(), "Can't un-subscribe/subscribe to default bulletins ",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(getApplicationContext(), "Can't un-subscribe/subscribe to default bulletins ",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.bPublicCreateFun:
                        Intent intent2 = new Intent(Bulletin_Posts.this, Promotion.class);
                        startActivity(intent2);
                        break;

                    default:
                        Intent intent5 = new Intent(Bulletin_Posts.this, Bulletin.class);
                        startActivity(intent5);
                }
            }
        });
    }

    /**
     * *****************Private helper methods*********************************************
     */

    private void confirmBulletinDeletion(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.confirmDeleteTitle) + " of " + bulletinTitle)
                    .setMessage(R.string.title_activity_delete_bulletin)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //yaornah = true;
                            ParseQuery<Bulletin_Helper> deleteBulletin = Bulletin_Helper.getQuery();
                            deleteBulletin.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                                    .whereEqualTo("objectId", bulletinId)
                                    .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                                        @Override
                                        public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                                            if (e == null) {
                                                bulletin_helper.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Intent intent = new Intent(Bulletin_Posts.this, Bulletin.class);
                                                            startActivity(intent);
                                                            Toast.makeText(getApplicationContext(), "Bulletin Deleted." + bulletinTitle,
                                                                    Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //yaornah = false;
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    private void isCurrentUser(){
        //ParseObject.registerSubclass(Bulletin_Helper.class);
        ParseQuery<Bulletin_Helper>  getBulletinUser = Bulletin_Helper.getQuery();
        getBulletinUser.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .whereEqualTo("objectId", bulletinId)
                .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                    @Override
                    public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                        if(e == null){
                            isUser = true;
                        }
                    }
                });
    }

    private void bulletinTitleQuery(){

        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("bulletin");

        ParseQuery<Bulletin_Helper> bull = Bulletin_Helper.getQuery();
        bull.whereEqualTo("objectId", s)
                .getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                    @Override
                    public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                        if(e == null){
                            bulletinTitle = bulletin_helper.getBulletinName();
                            bulletinId = bulletin_helper.getObjectId();

                            /*****************See if the person has already subsribed to the bulletin************************/
                            ParseQuery<Subscribe_Helper> getSub = Subscribe_Helper.getData();
                            getSub.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                                    .whereEqualTo("bulletinId" , bulletinId )
                                    .getFirstInBackground(new GetCallback<Subscribe_Helper>() {
                                        @Override
                                        public void done(Subscribe_Helper subscribe, ParseException e) {
                                            if(e == null){
                                               // fab.setBackgroundResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                                                isItSubscribed = true;
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void updateBoard(final Context context){
        final ParseUser user = ParseUser.getCurrentUser();
        final List<String> cpList = new ArrayList<>();
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> noincludeFavs = Favorites.getData();
        noincludeFavs.whereEqualTo("user", user);

        noincludeFavs.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> favoriteses, ParseException e) {
                if (e == null) {
                    for (Favorites favorites : favoriteses) {
                        cpList.add(favorites.getFlyerId());
                    }

                    timeLineAdapter = new TimeLineAdapter(context, cpList);
                    listView.setAdapter(timeLineAdapter);
                    timeLineAdapter.setAutoload(true);
                    timeLineAdapter.loadObjects();
                    timeLineAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PublicPost_Helper>() {
                        @Override
                        public void onLoading() {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoaded(List<PublicPost_Helper> publicPostHelpers, Exception e) {
                            if(e == null) {
                                progressBar.setVisibility(View.GONE);
                                if (publicPostHelpers.isEmpty()){
                                    linearLayout.setVisibility(View.VISIBLE);
                                } else{
                                    linearLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     ********************Class to Query the Data from Parse********************************************
     */
    class TimeLineAdapter extends ParseQueryAdapter<PublicPost_Helper> {
        public TimeLineAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<PublicPost_Helper>(){
                public ParseQuery<PublicPost_Helper> create(){
                    ParseQuery query = new ParseQuery("PublicPost");
                    query.orderByDescending("createdAt")
                            .whereNotContainedIn("objectId", list)
                            .whereEqualTo("Category", bulletinTitle);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final PublicPost_Helper object, View v, ViewGroup parent){

            if(v==null) {
                v = View.inflate(getContext(), R.layout.fragment_row_pb_v2, null);
                v.setTag(object.getObjectId());
            }
            super.getItemView(object, v, parent);

            String objectid = object.getObjectId();

            /**
             * Changed "timeTitle" to date, because the titles people make won't always fit in one line.
             * -Dominic
             * 2/8/15
             */

            final ImageView parseImageView =
                    (ImageView) v.findViewById(R.id.ivFlyerv2);
            parseImageView.setOnClickListener(Bulletin_Posts.this);
            parseImageView.setTag(objectid);

            ParseFile parseFile = object.getParseFile("Flyer");
            Picasso.with(Bulletin_Posts.this)
                    .load(parseFile.getUrl())
                    .resize(325, 405)
                    .centerCrop()
                    .into(parseImageView);
            return v;
        }
    }
}
