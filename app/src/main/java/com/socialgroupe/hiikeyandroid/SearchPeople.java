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
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_people,container,false);
        /**
        TextView tv = (TextView) v.findViewById(R.id.tvSP);
        tv.setText(getArguments().getString("msg"));
         */
        return v;
    }

    public static SearchPeople newInstance(){

        SearchPeople sp = new SearchPeople();
        /**
        Bundle bundle = new Bundle();
        bundle.putString("msg",text);
        sp.setArguments(bundle);
        */

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
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Favorites_Helper");

            try {
                ob = query.find();
            } catch (com.parse.ParseException e) {
                Toast.makeText(getActivity(), "Error, " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            listview = (ListView) getView().findViewById(R.id.lvSearchPeople);
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_itemp);
            for (ParseObject user : ob) {
                // Test - load String...
                adapter.add((String) user.get("userId"));
            }
            listview.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

}
