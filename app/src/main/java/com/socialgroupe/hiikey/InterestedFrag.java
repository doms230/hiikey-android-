package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class InterestedFrag extends ActionBarActivity implements View.OnClickListener {

    private GridView gridView;
    private InterestedAdapter interestedAdapter;
    private String objectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment

        setContentView(R.layout.fragment_interested);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectid = getIntent().getStringExtra("objectId");

        ParseQueryAdapter<ParseUser> updateProfileParseQueryAdapter =
                new ParseQueryAdapter<>(this, ParseUser.class);
        updateProfileParseQueryAdapter.setTextKey("title");
        gridView = (GridView)findViewById(R.id.gvInterested);

        updateFavorites();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavorites();
    }

    @Override
    public void onClick(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.ivInterestedPeople:
                        Bundle bundle = new Bundle();
                        bundle.putString("user", v.getTag().toString());
                        Intent intent = new Intent("com.socialgroupe.PROFILE");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                    default:
                        Intent intent1 = new Intent(InterestedFrag.this, Home.class);
                        startActivity(intent1);
                        finish();
                }
            }
        });
    }

    private void updateFavorites(){
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> favoritesParseQuery = Favorites.getData();
        favoritesParseQuery.whereEqualTo("flyerId", objectid);
        favoritesParseQuery.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> favoriteses, ParseException e) {
                if (e == null) {
                    List<String> favList = new ArrayList<>();
                    for (Favorites favorites : favoriteses) {
                        favList.add(favorites.getUserId());
                    }
                    interestedAdapter = new InterestedAdapter(InterestedFrag.this, favList);
                    gridView.setAdapter(interestedAdapter);
                    interestedAdapter.loadObjects();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Update failed. Make sure your Internet is connected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class InterestedAdapter extends ParseQueryAdapter<ParseUser> {
        public InterestedAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>(){
                @Override
                public ParseQuery<ParseUser> create() {
                   ParseQuery<ParseUser> updateProfileParseQuery = ParseUser.getQuery();
                   updateProfileParseQuery.whereContainedIn("objectId",list );

                    return updateProfileParseQuery;
                }
            });
        }

        @Override
        public View getItemView(ParseUser object, View v, ViewGroup parent) {
            if(v== null){
                v = View.inflate(getContext(), R.layout.fragment_row_interested, null);
            }
             super.getItemView(object, v, parent);

            final ParseImageView parseImageView =
                    (ParseImageView)v.findViewById(R.id.ivInterestedPeople);
            parseImageView.setOnClickListener(InterestedFrag.this);
            parseImageView.setTag(object.getObjectId());

            ParseFile parseFile = object.getParseFile("Profile");
            Picasso.with(InterestedFrag.this)
                    .load(parseFile.getUrl())
                    .resize(325, 405)
                    .into(parseImageView);
            return v;
        }
    }
}