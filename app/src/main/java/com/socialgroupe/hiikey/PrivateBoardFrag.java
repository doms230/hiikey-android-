package com.socialgroupe.hiikey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by Dominic on 1/27/2015.
 *
 * Class where people view all of their lists' events and repost
 */
public class PrivateBoardFrag extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    /****************Grid Stuff***************/
    private PrivatBoardAdapter privateboardAdapter;
    private GridView gridView;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_privateboard);

        swo = (SwipeRefreshLayout)findViewById(R.id.srlPrBReload);
        swo.setColorSchemeResources(R.color.appColor);

        updateBoard(this);

        gridView = (GridView)findViewById(R.id.gvPrivateBoard);
        ParseObject.registerSubclass(PublicPost_Helper.class);

        linearLayout = (LinearLayout)findViewById(R.id.llPrivateNoEvents);
        Button button = (Button)findViewById(R.id.bPrivateCreateFun);
        button.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.pbPrivateProgress);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        ParseQueryAdapter<PublicPost_Helper> mainAdapter = new ParseQueryAdapter<>(this, PublicPost_Helper.class);
        mainAdapter.setTextKey("title");


        swo.setOnRefreshListener(this);
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
    public void onRefresh() {
        updateBoard(this);
    }

    @Override
    public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.ivPrivBv2:
                        if(ParseUser.getCurrentUser() != null) {
                            Bundle bund = new Bundle();
                            bund.putString("objectId", v.getTag().toString());
                            Intent intent1 = new Intent("com.socialgroupe.SEEFLYER");
                            intent1.putExtras(bund);
                            startActivity(intent1);
                        } else{
                            showLogin();
                        }
                        break;

                    case R.id.bPrivateCreateFun:
                        if(ParseUser.getCurrentUser() != null) {
                            Intent intent = new Intent(this, Promotion.class);
                            startActivity(intent);
                        } else{
                            showLogin();
                        }
                        break;

                    default:
                        Intent intent11 = new Intent(this, Bulletin.class);
                        startActivity(intent11);
                        finish();
                        break;
                }
    }

    private void showLogin(){
        Intent intent = new Intent(this, Signup_Login.class);
        startActivity(intent);
    }

    /**
     * *****************Private helper methods*********************************************
     */

    private void updateBoard(final Context context){

        final ParseUser user = ParseUser.getCurrentUser();
        final List<String> fvList = new ArrayList<>();
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> noincludeFavs = Favorites.getData();
        noincludeFavs.whereEqualTo("user", user);

        noincludeFavs.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> favoriteses, ParseException e) {
                if (e == null) {
                    for (Favorites favorites : favoriteses) {
                        fvList.add(favorites.getFlyerId());
                    }
                    privateboardAdapter = new PrivatBoardAdapter(context, fvList);
                    gridView.setAdapter(privateboardAdapter);
                    privateboardAdapter.setAutoload(true);
                    privateboardAdapter.loadObjects();
                    privateboardAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PublicPost_Helper>() {
                        @Override
                        public void onLoading() {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoaded(List<PublicPost_Helper> publicPostHelpers, Exception e) {
                            progressBar.setVisibility(View.GONE);
                            if(publicPostHelpers.isEmpty()){
                                linearLayout.setVisibility(View.VISIBLE);
                            } else{
                                linearLayout.setVisibility(View.GONE);
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
    class PrivatBoardAdapter extends ParseQueryAdapter<PublicPost_Helper> {
        public PrivatBoardAdapter(Context context, final List favList){
            super(context, new ParseQueryAdapter.QueryFactory<PublicPost_Helper>(){
                public ParseQuery<PublicPost_Helper> create(){
                    ParseQuery query = new ParseQuery("PublicPost_Helper");
                    query.orderByDescending("createdAt")
                        .whereNotEqualTo("Privacy", "Public")
                        .whereNotContainedIn("objectId", favList);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final PublicPost_Helper object, View v, final ViewGroup parent){
            if(v == null) {
                v = View.inflate(getContext(), R.layout.fragment_row_privateboard_v2, null);
            }
            super.getItemView(object, v, parent);

            String objectid = object.getObjectId();

             ImageView parseImageView =
                    (ImageView)v.findViewById(R.id.ivPrivBv2);
            parseImageView.setTag(object.getObjectId());
            parseImageView.setOnClickListener(PrivateBoardFrag.this);

            ParseFile parseFile = object.getParseFile("Flyer");
            Picasso.with(PrivateBoardFrag.this)
                    .load(parseFile.getUrl())
                    .resize(325,405)
                    .centerCrop()
                    .into(parseImageView);
            return v;
        }
    }
}