package com.socialgroupe.hiikey;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_searchbulletins, container, false);
        //final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.pbSearchBulletins);
        TextView textView = (TextView) v.findViewById(R.id.tvNoBulletins);
        textView.setText(getArguments().getString("msg"));
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

        return v;
    }

    public static SearchBulletinsF newInstance(String text) {

        SearchBulletinsF sb = new SearchBulletinsF();
        Bundle bundle = new Bundle();
        bundle.putString("msg", text);
        sb.setArguments(bundle);
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
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "Bulletins");
            query.orderByDescending("numId");

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
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.search_listview_item);
            for (ParseObject bulletin : ob) {
                // Test - load String...
                adapter.add((String) bulletin.get("bulletinName"));
            }
            listview.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }
}
