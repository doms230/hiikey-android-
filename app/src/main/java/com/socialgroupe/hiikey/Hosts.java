package com.socialgroupe.hiikey;

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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class Hosts extends ActionBarActivity implements View.OnClickListener {
    private ListView gridView;
    private List<String> hostList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosts);
        gridView = (ListView)findViewById(R.id.gvHost);
        ParseObject.registerSubclass(GuestList_Helper.class);
        loadStuff ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStuff ();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadStuff ();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibHostShow:
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
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.pbHostProgress);
        final TextView textView = (TextView)findViewById(R.id.tvNoHost);
        progressBar.setIndeterminate(true);
        Bundle bundle = getIntent().getExtras();
        String string = bundle.getString("host");

        ParseQuery<GuestList_Helper> hostSize = GuestList_Helper.getList();
        hostSize.whereEqualTo("guestId", string)
                .findInBackground(new FindCallback<GuestList_Helper>() {
                    @Override
                    public void done(List<GuestList_Helper> guestLists, ParseException e) {
                        if (e == null) {
                            for (GuestList_Helper listNumber : guestLists) {
                                hostList.add(listNumber.getHostId());
                            }
                            HostAdapter hostAdapter = new HostAdapter(Hosts.this, hostList );
                            gridView.setAdapter(hostAdapter);
                            hostAdapter.setAutoload(true);
                            hostAdapter.loadObjects();
                            hostAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
                                @Override
                                public void onLoading() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onLoaded(List<ParseUser> parseUsers, Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    if(parseUsers.isEmpty()){
                                        textView.setVisibility(View.VISIBLE);

                                    } else{
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    class HostAdapter extends ParseQueryAdapter<ParseUser>{
        public HostAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>(){
                public ParseQuery<ParseUser>create(){
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
                v = View.inflate(getContext(), R.layout.activity_row_host, null);
            }
            super.getItemView(object, v, parent);

            ImageButton imageButton = (ImageButton) v.findViewById(R.id.ibHostShow);
            imageButton.setOnClickListener(Hosts.this);
            imageButton.setTag(object.getObjectId());

            TextView textView = (TextView)v.findViewById(R.id.tvHostName);
            textView.setText(object.getString("DisplayName"));

            ParseImageView imageView = (ParseImageView)v.findViewById(R.id.ivHostPerson);
            ParseFile parseFile = object.getParseFile("Profile");
            Picasso.with(Hosts.this)
                    .load(parseFile.getUrl())
                    .resize(200,250)
                    .centerCrop()
                    .into(imageView);
            return v;
        }
    }
}
