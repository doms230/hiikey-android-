package com.socialgroupe.hiikey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Yaning on 6/27/15.
 */
public class SearchPeople extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_people,container,false);
        TextView tv = (TextView) v.findViewById(R.id.tvSP);
        tv.setText(getArguments().getString("msg"));
        return v;
    }

    public static SearchPeople newInstance(String text){

        SearchPeople sp = new SearchPeople();
        Bundle bundle = new Bundle();
        bundle.putString("msg",text);
        sp.setArguments(bundle);

        return sp;
    }

}
