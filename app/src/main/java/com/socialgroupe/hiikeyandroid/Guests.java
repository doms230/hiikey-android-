package com.socialgroupe.hiikeyandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to show a list of the chosen user's Guest list.
 */

public class Guests extends ActionBarActivity implements View.OnClickListener {

    private List<String> guestList = new ArrayList<>();
    private ListView listView;
    private ProgressBar progressBar;
    private TextView noguest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guests);

        listView = (ListView)findViewById(R.id.lvGuest);
        progressBar = (ProgressBar)findViewById(R.id.pbGuestProgressBar);
        noguest = (TextView)findViewById(R.id.tvNoguest);

        progressBar.setIndeterminate(true);

        loadStuff();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStuff();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadStuff();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibGuestShow:
                Intent intent = new Intent(this, Profile.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", v.getTag().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Profile couldn't be opened.", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, Home.class);
                startActivity(intent1);
                finish();
        }
    }

    private void loadStuff(){
        /**************Determine how many people the profileUser is hosting**************/
        Bundle bundle = getIntent().getExtras();
        ParseQuery<GuestList_Helper> guestSize = GuestList_Helper.getList();
        guestSize.whereEqualTo("hostId", bundle.getString("guest"))
                .findInBackground(new FindCallback<GuestList_Helper>() {
                    @Override
                    public void done(List<GuestList_Helper> guestLists, ParseException e) {
                        if (e == null) {
                            for (GuestList_Helper listNumber : guestLists) {
                                guestList.add(listNumber.getGuestId());
                            }

                            GuestAdapter guestAdapter = new GuestAdapter(Guests.this,guestList );
                            listView.setAdapter(guestAdapter);
                            guestAdapter.setAutoload(true);
                            guestAdapter.loadObjects();
                            guestAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
                                @Override
                                public void onLoading() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onLoaded(List<ParseUser> parseUsers, Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    if(parseUsers.isEmpty()){
                                        noguest.setVisibility(View.VISIBLE);
                                    } else{
                                        noguest.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    /**
     * ParseQueryAdapter<ParseObject> is a custom adapter class created Parse
     * to make it easier for people to load their parse data into a listView/GridView
     */

    class GuestAdapter extends ParseQueryAdapter<ParseUser> {
        public GuestAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>(){
                public ParseQuery<ParseUser> create(){
                    ParseQuery query = new ParseQuery("_User");
                    query.orderByDescending("createdAt")
                            .whereContainedIn("objectId", list);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final ParseUser object, View v, ViewGroup parent) {
            if(v == null){
                v = View.inflate(getContext(), R.layout.activity_row_guest, null);
            }
            super.getItemView(object, v, parent);

            ImageButton imageButton = (ImageButton) v.findViewById(R.id.ibGuestShow);
            imageButton.setOnClickListener(Guests.this);
            imageButton.setTag(object.getObjectId());

            TextView textView = (TextView)v.findViewById(R.id.tvGuestName);
            textView.setText(object.getString("DisplayName"));

            ParseImageView imageView = (ParseImageView)v.findViewById(R.id.ivHostPerson);
            ParseFile parseFile = object.getParseFile("Profile");
            Picasso.with(Guests.this)
                    .load(parseFile.getUrl())
                    .resize(200,250)
                    .centerCrop()
                    .into(imageView);
            return v;
        }
    }
}