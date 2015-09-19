package com.socialgroupe.hiikeyandroid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Yaning on 6/27/15.
 */
public class SearchLocation extends Fragment{

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Execute RemoteDataTask AsyncTask

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
}


