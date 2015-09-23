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
 * Created by Yaning on 6/30/15.
 */

// Search for Bulletins

public class SearchBulletinsF extends Fragment {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter_bulletin;
    String query_bulletin;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Execute RemoteDataTask AsyncTask

        Search search = (Search) getActivity();
        query_bulletin = search.getMyData();

        new RemoteDataTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_bulletins, container, false);
        return v;
    }

    public static SearchBulletinsF newInstance() {

        SearchBulletinsF sb = new SearchBulletinsF();
        return sb;
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
            String spl = SearchBulletinsF.this.query_bulletin;
            //System.out.println(spl);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Bulletin");
            query.whereEqualTo("bulletinName",spl);
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

            listview = (ListView) getView().findViewById(R.id.lvSearchBulletins);
            adapter_bulletin = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_item);
            for (ParseObject bulletin_name : ob) {
                // Test - load String...
                // adapter.add((String) user.get("userId"));
                adapter_bulletin.add((String) bulletin_name.get("bulletinName"));
            }
            listview.setAdapter(adapter_bulletin);
            mProgressDialog.dismiss();
        }

    }

}


