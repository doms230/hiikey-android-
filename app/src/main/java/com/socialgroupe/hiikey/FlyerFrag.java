package com.socialgroupe.hiikey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

/**
 * Created by Dominic on 1/8/2015.
 *
 * Fragment used to view the Event Flyer.
 * Parent Activity: SeeFlyer.java
 */
public class FlyerFrag extends android.support.v4.app.Fragment{

    public static FlyerFrag flyerInstance(){
        return new FlyerFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View seeFlyer = inflater.inflate(R.layout.fragment_see_flyer, container, false);

        final ParseImageView flyer = (ParseImageView)seeFlyer.findViewById(R.id.ivExpandFlyer);

        Bundle bundle = getActivity().getIntent().getExtras();

        String objectid = bundle.getString("objectId");

            ParseQuery<PublicPost_Helper> expandedFlyer = PublicPost_Helper.getQuery();
            expandedFlyer.whereEqualTo("objectId", objectid);
            expandedFlyer.getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                @Override
                public void done(PublicPost_Helper publicPostHelper, ParseException e) {
                    if(e == null){
                        ParseFile parseFile = publicPostHelper.getParseFile("Flyer");
                        Picasso.with(getActivity()).load(parseFile.getUrl())
                                .resize(flyer.getWidth(), flyer.getHeight())
                                .into(flyer);
                    } else{
                        Toast.makeText(getActivity(), "Couldn't retrieve flyer, check internet connection",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        return seeFlyer;
    }
}
