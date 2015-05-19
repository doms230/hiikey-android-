package com.socialgroupe.hiikey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Dominic on 12/23/2014.
 * Fragment to show people's favorite flyers.
 */
public class SavedFlyers_Frag extends ActionBarActivity implements View.OnClickListener {

    private FavoriteAdapter favoriteAdapter;
    private GridView listView;
    private String favFlyZone;
    private LinearLayout noFavs;
    private ProgressBar pb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_favorite);

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
                switch (v.getId()){
                    case R.id.ivFavv2:
                        if(ParseUser.getCurrentUser() != null) {
                            Intent intent = new Intent(SavedFlyers_Frag.this, SeeFlyer.class);
                            intent.putExtra("objectId", v.getTag().toString());
                            startActivity(intent);
                        } else{
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
                            if(publicPostHelpers.isEmpty()){
                                noFavs.setVisibility(View.VISIBLE);
                            } else{
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
}
