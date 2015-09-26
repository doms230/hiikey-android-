package com.socialgroupe.hiikeyandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Yaning on 6/27/15.
 */
public class SearchPeople extends Fragment{

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter_people;
    String query_people;
    //List<Bulletin_Helper> people_search;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        // Execute RemoteDataTask AsyncTask

        Search search = (Search) getActivity();
        query_people = search.getMyData();
        if(query_people == null){
            query_people = "";
        }

        new RemoteDataTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_people,container,false);
        return v;
    }
    public static SearchPeople newInstance(){

        SearchPeople sp = new SearchPeople();

        return sp;
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Loading...)");
            // Set progressdialog message
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                  //  "Bulletin");
            //SearchPeople spp = new SearchPeople();
            String spl = SearchPeople.this.query_people;
            //System.out.println(spl);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("username",spl);
            //System.out.println("final " + spl);
            try {
                ob = query.find();
                /*
                for(ParseObject user_search : ob){
                    Bulletin_Helper item = new Bulletin_Helper();
                    item.setBulletinPic((ParseFile) user_search.get("Profile"));
                    item.setBulletinName((String) user_search.get("username"));
                    people_search.add(item);

                }
                */
            } catch (com.parse.ParseException e) {
                Toast.makeText(getActivity(), "Error, " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            listview = (ListView) getView().findViewById(R.id.lvSearchPeople);
            adapter_people = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_itemp, R.id.username);
            for (ParseObject bulletin_name : ob) {
                // Test - load String...
                // adapter.add((String) user.get("userId"));
                adapter_people.add((String) bulletin_name.get("username"));
            }
            listview.setAdapter(adapter_people);
            mProgressDialog.dismiss();
        }

    }

}
