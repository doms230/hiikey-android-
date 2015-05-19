package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
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

import java.util.List;

public class YourEvent extends ActionBarActivity implements View.OnClickListener {

    private String string;
    private ListView listView;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_event);
        Bundle bundle = getIntent().getExtras();
        string = bundle.getString("user");

        linearLayout = (LinearLayout)findViewById(R.id.llNoYourEvent);
        linearLayout.setVisibility(View.VISIBLE);

        progressBar = (ProgressBar)findViewById(R.id.pbYourEventProgress);
        progressBar.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.lvYourevents);

        ParseObject.registerSubclass(PublicPost_Helper.class);

        ParseQueryAdapter<PublicPost_Helper> mainAdapter = new ParseQueryAdapter<>(this, PublicPost_Helper.class);
        mainAdapter.setTextKey("title");

        reloadStuff(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibYourEventFlyer:
                Intent intent = new Intent(this, SeeFlyer.class);
                Bundle bundle = new Bundle();
                bundle.putString("objectId", v.getTag().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.ibYourEventTrash:
                openConfirmDeleteDialog(v);
                break;

            case R.id.bNoYourEvent:
                Intent intent1 = new Intent(this, Promotion.class);
                startActivity(intent1);
                break;

            default:
                Toast.makeText(getApplicationContext(), "Action couldn't completed.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openConfirmDeleteDialog(final View view ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmDeleteTitle)
                .setMessage(R.string.confirmDeleteMessage)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yaornah = true;

                        ParseObject.registerSubclass(PublicPostRemoved.class);
                        ParseObject.registerSubclass(PublicPost_Helper.class);
                        ParseQuery<PublicPost_Helper> cladQuery = PublicPost_Helper.getQuery();
                        cladQuery.whereEqualTo("objectId", view.getTag().toString());
                        cladQuery.getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                                                           @Override
                                                           public void done(final PublicPost_Helper publicPostHelper, ParseException e) {
                                                               if (e == null) {
                                                                   PublicPostRemoved remove = new PublicPostRemoved();

                                                                   String title = publicPostHelper.getTitle();
                                                                   String address = publicPostHelper.getAddress();
                                                                   String date = publicPostHelper.getDate();
                                                                   String time = publicPostHelper.getTime();
                                                                   String hashtag = publicPostHelper.getHashtag();
                                                                   String category = publicPostHelper.getCategory();
                                                                   String privacy = publicPostHelper.getPrivacy();
                                                                   String des = publicPostHelper.getDescription();
                                                                   String website = publicPostHelper.getWebsite();

                                                                   remove.setTitle(title);
                                                                   remove.setAddress(address);
                                                                   remove.setDate(date);
                                                                   remove.setTime(time);

                                                                   if(!website.equals("null")){
                                                                       remove.setWebsite(website);
                                                                   }

                                                                   if(!des.equals("null")){
                                                                       remove.setDescription(des);
                                                                   }

                                                                   if (!hashtag.equals("null")){
                                                                       remove.setHashtag(hashtag);
                                                                   }
                                                                   remove.setCategory(category);
                                                                   remove.setPrivacy(privacy);

                                                                   remove.setflyer(publicPostHelper.getfyler());
                                                                   remove.setUser(ParseUser.getCurrentUser());
                                                                   remove.setLocation(publicPostHelper.getLocation());
                                                                   remove.saveInBackground(new SaveCallback() {
                                                                       @Override
                                                                       public void done(ParseException e) {
                                                                           publicPostHelper.deleteInBackground(new DeleteCallback() {
                                                                               @Override
                                                                               public void done(ParseException e) {
                                                                                   reloadStuff(YourEvent.this);
                                                                               }
                                                                           });
                                                                       }
                                                                   });
                                                               }

                                                               else{
                                                                   Toast.makeText(getApplicationContext(),
                                                                           "Deletion failed. Make sure your Internet is connected.",
                                                                           Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       }
                        );
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

    private void reloadStuff(Context context){
        YourBoardAdapter yourBoardAdapter = new YourBoardAdapter(context);
        listView.setAdapter(yourBoardAdapter);
        yourBoardAdapter.setAutoload(true);
        yourBoardAdapter.loadObjects();
        yourBoardAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<PublicPost_Helper>() {
            @Override
            public void onLoading() {
                progressBar = (ProgressBar)findViewById(R.id.pbYourEventProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
            }

            @Override
            public void onLoaded(List<PublicPost_Helper> publicPostHelpers, Exception e) {
                progressBar.setVisibility(View.GONE);
                if(publicPostHelpers.isEmpty()){

                    Button button = (Button)findViewById(R.id.bNoYourEvent);
                    button.setOnClickListener(YourEvent.this);
                } else{
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    class YourBoardAdapter extends ParseQueryAdapter<PublicPost_Helper>{
        public YourBoardAdapter(Context context){
            super(context, new ParseQueryAdapter.QueryFactory<PublicPost_Helper>() {
               public ParseQuery<PublicPost_Helper>create(){
                   ParseQuery query = new ParseQuery("PublicPost");
                   query.orderByDescending("createdAt")
                        .whereEqualTo("userId", string );
                   return query;
               }
            });
        }

        @Override
        public View getItemView(PublicPost_Helper object, View v, ViewGroup parent) {
            if(v == null){
                v = View.inflate(getContext(), R.layout.activity_row_yourevent, null);
            }
            super.getItemView(object, v, parent);

            ImageButton deleteFlyer = (ImageButton)v.findViewById(R.id.ibYourEventTrash);
            deleteFlyer.setOnClickListener(YourEvent.this);
            deleteFlyer.setTag(object.getObjectId());

            if(object.getUser().equals(ParseUser.getCurrentUser())){
                deleteFlyer.setVisibility(View.VISIBLE);
            } else{
                deleteFlyer.setVisibility(View.GONE);
            }

            ImageButton showFlyer = (ImageButton)v.findViewById(R.id.ibYourEventFlyer);
            showFlyer.setTag(object.getObjectId());
            showFlyer.setOnClickListener(YourEvent.this);

            final ParseImageView parseImageView =
                    (ParseImageView)v.findViewById(R.id.ivYourEventFlyer);

            ParseFile parseFile = object.getParseFile("Flyer");
            Picasso.with(YourEvent.this)
                    .load(parseFile.getUrl())
                    .resize(425,505)
                    .centerCrop()
                    .into(parseImageView);
            return v;
        }
    }
}