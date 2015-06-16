package com.socialgroupe.hiikey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class Test extends Activity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    Customlv_Adapter adapter;
     private List<AllFlyers> flyers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(Test.this);
            // Set progressdialog tilte
            mProgressDialog.setTitle("Parse.com Custom ListView");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){
            // Create the array
            flyers = new ArrayList<AllFlyers>();
            try{
                // Locate the class table named  "PublicPost" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("PublicPost");
                // Locate the column named "createdAt" in Parse.com and order list by ascending
                query.orderByAscending("createdAt");
                ob = query.find();
                for(ParseObject PublicPost : ob){
                    AllFlyers map = new AllFlyers();
                    map.setCategory((String) PublicPost.get("Category"));
                    map.setUserId((String) PublicPost.get("userId"));
                    map.setFlyer((ParseFile) PublicPost.get("Flyer"));
                    flyers.add(map);
                }
            }catch (ParseException e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            // Locate the listView in activity_test.xml
            listview = (ListView) findViewById(R.id.itemlistview);
            // Pass the results into Customlv_Adapter.java
            adapter = new Customlv_Adapter(Test.this,flyers);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
