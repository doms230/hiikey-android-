package com.socialgroupe.hiikey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class SubscribedBulletins extends ActionBarActivity{

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_bulletins);

        ParseObject.registerSubclass(Bulletin_Helper.class);
        ParseObject.registerSubclass(Favorites.class);
        ParseObject.registerSubclass(Subscribe_Helper.class);

        listView = (ListView)findViewById(R.id.lvSubBulletin);

        ParseQueryAdapter<Bulletin_Helper> mainA = new ParseQueryAdapter<Bulletin_Helper>(this, Bulletin_Helper.class);
        mainA.setTextKey("title");

        updateBulletin(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscribed_bulletins, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void updateBulletin(final Context context){
        final List<String> getSubs = new ArrayList<>();
        ParseQuery<Subscribe_Helper> mySubs = Subscribe_Helper.getData();
        mySubs.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                .findInBackground(new FindCallback<Subscribe_Helper>() {
                    @Override
                    public void done(List<Subscribe_Helper> list, ParseException e) {
                        if(e == null){
                            for (Subscribe_Helper subscribe : list){
                                getSubs.add(subscribe.getBulletinId());
                            }
                            BulletinAdapter bulletinAdapter = new BulletinAdapter(context, getSubs);
                            listView.setAdapter(bulletinAdapter);
                            bulletinAdapter.loadObjects();
                        }
                    }
                });
    }

    class BulletinAdapter extends ParseQueryAdapter<Bulletin_Helper> {
        public BulletinAdapter(Context context, final List stuff){
            super(context, new ParseQueryAdapter.QueryFactory<Bulletin_Helper>(){

                public ParseQuery<Bulletin_Helper> create(){
                    ParseQuery query = new ParseQuery("Bulletin");
                    query.whereContainedIn("objectId", stuff);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final Bulletin_Helper object, View v, ViewGroup parent){

            if(v==null) {
                v = View.inflate(getContext(), R.layout.activity_row_bulletin, null);
                v.setTag(object.getObjectId());
            }
            super.getItemView(object, v, parent);

            String objectid = object.getObjectId();

            /**
             * Changed "timeTitle" to date, because the titles people make won't always fit in one line.
             * -Dominic
             * 2/8/15
             */

            TextView textView = (TextView) v.findViewById(R.id.tvBulletinName);
            if(object.getShowHideSetting().equals("hide") ){
                textView.setVisibility(View.INVISIBLE);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(object.getBulletinName());
            }

            final ImageView parseImageView =
                    (ImageView) v.findViewById(R.id.ivBulletin);
            parseImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent("com.socialgroupe.PUBLICPOSTV2");
                    bundle.putString("bulletin", v.getTag().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            parseImageView.setTag(objectid);

            ParseFile parseFile = object.getParseFile("bulletinPic");
            Picasso.with(SubscribedBulletins.this)
                    .load(parseFile.getUrl())
                    .resize(800, 450)
                    .centerCrop()
                    .into(parseImageView);
            return v;

            /////.resize(325, 405)
        }
    }

}
