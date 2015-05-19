package com.socialgroupe.hiikey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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


public class Promotion extends ActionBarActivity implements
        AdapterView.OnItemSelectedListener {

    private String stringFlyPrivacy;
    private TextView exc, priv, pub, promoFees;
    private LinearLayout exclusive;
    private ArrayList<String> onTheList = new ArrayList<>();
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_promotion);

        listview = (ListView)findViewById(R.id.lvWhoList);

       /* ImageButton back = (ImageButton)findViewById(R.id.ibPromoBack);
        back.setOnClickListener(this);

        ImageButton foward = (ImageButton)findViewById(R.id.ibPromoPost);
        foward.setOnClickListener(this);*/

        Spinner privatey = (Spinner) findViewById(R.id.sPrivate);
        privatey.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> privAdapter = ArrayAdapter.createFromResource(this,
                R.array.privacy_array, android.R.layout.simple_spinner_item);
        privAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privatey.setAdapter(privAdapter);

        exc = (TextView)findViewById(R.id.tvExclusiveState);
        priv = (TextView)findViewById(R.id.tvPrivateState);
        pub = (TextView)findViewById(R.id.tvPublicState);
        promoFees = (TextView)findViewById(R.id.tvPromoFees);
        exclusive = (LinearLayout)findViewById(R.id.llExclusive);

        updateProfileList(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateProfileList(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileList(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_promotion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_promotion_next:
                switch(stringFlyPrivacy) {
                    case "Public":
                        next();
                        finish();
                        break;

                    case "Private":
                        next();
                        finish();
                        break;

                    case "Exclusive":
                        exclusiveNext();
                        finish();
                }
                return true;

            case R.id.action_promotion_exit:
                Intent inte = new Intent(this, Bulletin.class);
                startActivity(inte);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sPrivate:
                stringFlyPrivacy = parent.getItemAtPosition(position).toString();
                switch (stringFlyPrivacy) {
                    case "Public":
                        pub.setVisibility(View.VISIBLE);
                        priv.setVisibility(View.GONE);
                        exc.setVisibility(View.GONE);
                        exclusive.setVisibility(View.GONE);
                        promoFees.setVisibility(View.VISIBLE);
                        break;

                    case "Private":
                        priv.setVisibility(View.VISIBLE);
                        pub.setVisibility(View.GONE);
                        exc.setVisibility(View.GONE);
                        promoFees.setVisibility(View.GONE);
                        exclusive.setVisibility(View.GONE);
                        break;

                    case "Exclusive":
                        exc.setVisibility(View.VISIBLE);
                        pub.setVisibility(View.GONE);
                        priv.setVisibility(View.GONE);
                        promoFees.setVisibility(View.GONE);
                        exclusive.setVisibility(View.VISIBLE);
                        break;
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updateProfileList(final Context context){
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.pbPromo);
        final TextView textView = (TextView)findViewById(R.id.tvNoOnetoinvite);
        ParseObject.registerSubclass(GuestList.class);
        ParseQuery<GuestList> userList = GuestList.getList();
        userList.whereEqualTo("hostId", ParseUser.getCurrentUser().getObjectId())
                .findInBackground(new FindCallback<GuestList>() {
                    @Override
                    public void done(List<GuestList> guestLists, ParseException e) {
                        if (e == null) {
                            List<String> userGuestList = new ArrayList<>();
                            for (GuestList guestList : guestLists) {
                                userGuestList.add(guestList.getGuestId());
                            }

                            guestListAdapter guestListAdapter = new guestListAdapter(context, userGuestList);
                            listview.setAdapter(guestListAdapter);
                            guestListAdapter.loadObjects();
                            guestListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseUser>() {
                                @Override
                                public void onLoading() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onLoaded(List<ParseUser> parseUsers, Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    if(parseUsers.isEmpty()){
                                        listview.setVisibility(View.GONE);
                                        textView.setVisibility(View.VISIBLE);
                                    } else{
                                        textView.setVisibility(View.GONE);
                                        listview.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void next(){
        Bundle bundle = new Bundle();
        bundle.putString("privacy", stringFlyPrivacy);
        Intent intw = new Intent(this, Event_Info.class);
        intw.putExtras(bundle);
        startActivity(intw);
    }

    private void exclusiveNext(){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("guestlist", onTheList);
        bundle.putString("privacy", stringFlyPrivacy);
        Intent intw = new Intent(this, Event_Info.class);
        intw.putExtras(bundle);
        startActivity(intw);
    }

    class guestListAdapter extends ParseQueryAdapter<ParseUser> {
        public guestListAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>(){
                public ParseQuery<ParseUser> create(){
                    ParseQuery query = new ParseQuery("_User");
                    query.orderByDescending("createdAt");
                    query.whereContainedIn("objectId", list);
                 return query;
                }
            });
        }

        @Override
        public View getItemView(final ParseUser object, View v, ViewGroup parent) {

            if(v == null){
                v = View.inflate(getContext(), R.layout.activity_row_exclusive, null);
            }
            super.getItemView(object, v, parent);


            TextView tv = (TextView)v.findViewById(R.id.tvPersonUsername);
            tv.setText(object.getString("DisplayName"));

            CheckBox check = (CheckBox)v.findViewById(R.id.cbPerson);
            check.setTag(object.getObjectId());
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        onTheList.add(buttonView.getTag().toString());
                    } else{
                        onTheList.remove(buttonView.getTag().toString());
                    }
                }
            });

            final ParseImageView par = (ParseImageView)v.findViewById(R.id.inPromoPerson);

            ParseFile parse = object.getParseFile("Profile");
            Picasso.with(getApplicationContext())
                    .load(parse.getUrl())
                    .resize(50,50)
                    .into(par);
            return v;
        }
    }
}