package com.socialgroupe.hiikeyandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaning on 6/30/15.
 */

// Search for Bulletins

public class SearchBulletinsF extends Fragment {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    Search_adapter adapter_bulletin;
    String query_bulletin;
    List<Bulletin_Helper> bulletin_search;

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
            bulletin_search = new ArrayList<Bulletin_Helper>();
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
                for(ParseObject bulletins_search : ob){
                    Bulletin_Helper item = new Bulletin_Helper();
                    item.setBulletinPic((ParseFile) bulletins_search.get("bulletinPic"));
                    item.setBulletinName((String) bulletins_search.get("bulletinName"));
                    bulletin_search.add(item);

                }
            } catch (com.parse.ParseException e) {
                Toast.makeText(getActivity(), "Error, " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(Void result) {

            listview = (ListView) getView().findViewById(R.id.lvSearchBulletins);
            //adapter_bulletin = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_item, R.id.tvtry);
            adapter_bulletin = new Search_adapter(getActivity(), bulletin_search);
            listview.setAdapter(adapter_bulletin);
            mProgressDialog.dismiss();
        }

    }

}


