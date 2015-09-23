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
public class SearchLocation extends Fragment{

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter_location;
    String query_location;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Execute RemoteDataTask AsyncTask

        Search search = (Search) getActivity();
        query_location = search.getMyData();
        if(query_location == null){
            query_location = "";
        }

        new RemoteDataTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_places, container, false);
        return v;
    }

    public static SearchLocation newInstance() {

        SearchLocation sl = new SearchLocation();
        return sl;
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
            String spl = SearchLocation.this.query_location;
            //System.out.println(spl);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Bulletin");
            query.whereEqualTo("bulletinAddress",spl);
            //System.out.println("final " + spl);
            try {
                ob = query.find();
            } catch (com.parse.ParseException e) {
                Toast.makeText(getActivity(), "Error, " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(Void result) {

            listview = (ListView) getView().findViewById(R.id.lvSearchLocation);
            adapter_location = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_iteml);
            for (ParseObject bulletin_name : ob) {
                // Test - load String...
                // adapter.add((String) user.get("userId"));
                adapter_location.add((String) bulletin_name.get("bulletinAddress"));
            }
            listview.setAdapter(adapter_location);
            mProgressDialog.dismiss();
        }

    }
}


