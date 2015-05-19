package com.socialgroupe.hiikey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class InterestedFrag extends android.support.v4.app.Fragment implements View.OnClickListener {

    private GridView gridView;
    private InterestedAdapter interestedAdapter;
    private String objectid, hostName;
    private TextView favNumber;
    private ImageView memberLevel;
    private ImageButton thumbsup;
    private ImageButton thumbsdown;
    private String hostObjectId;
    private boolean isThumbedup = false, isThumbedDown = false, existence = false;
    private boolean isVerified = false, isGold = false, isSilver = false, isBronze = false;

    public static InterestedFrag interInsta() {
       return new InterestedFrag();
    }

    public InterestedFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View interested = inflater.inflate(R.layout.fragment_interested, container, false);

        ParseObject.registerSubclass(Props_Counter_Helper.class);
        ParseObject.registerSubclass(Props_Helper.class);

        memberLevel = (ImageView) interested.findViewById(R.id.ivMemberLevel);
        memberLevel.setOnClickListener(this);
       // memberLevel.setBackgroundResource(R.drawable.props_verified);
        memberLevel.setVisibility(View.VISIBLE);

        thumbsup = (ImageButton) interested.findViewById(R.id.ibThumbsUp);
        thumbsup.setOnClickListener(this);

        thumbsdown = (ImageButton) interested.findViewById(R.id.ibThumbdown);
        thumbsdown.setOnClickListener(this);

        final TextView textView = (TextView)interested.findViewById(R.id.tvHost);
        final ParseImageView imageView = (ParseImageView)interested.findViewById(R.id.ivShowFLyerHost);
        favNumber = (TextView)interested.findViewById(R.id.tvFavNumber);
        objectid = getActivity().getIntent().getStringExtra("objectId");

        ParseQuery<PublicPost_Helper> pu = PublicPost_Helper.getQuery();
        pu.whereEqualTo("objectId", objectid)
                .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                    @Override
                    public void done(PublicPost_Helper publicPostHelper, ParseException e) {
                        if (e == null) {

                            hostObjectId = publicPostHelper.getUserId();

                            ParseQuery<ParseUser> up = ParseUser.getQuery();
                            up.whereEqualTo("objectId", publicPostHelper.getUser().getObjectId())
                                    .getFirstInBackground(new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(final ParseUser updateProfile, ParseException e) {
                                            if (e == null) {
                                                hostObjectId = updateProfile.getObjectId();
                                                ParseFile parseFile = updateProfile.getParseFile("Profile");
                                                Picasso.with(getActivity()).load(parseFile.getUrl())
                                                        .resize(imageView.getWidth(), imageView.getHeight())
                                                        .into(imageView);
                                                imageView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(getActivity(), Profile.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("user", updateProfile.getObjectId());
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                                hostName = updateProfile.getString("DisplayName");
                                                textView.setText(hostName);

                                                /**To determine if the user has already gave the host props*/

                                                ParseQuery<Props_Counter_Helper> getThumbs = Props_Counter_Helper.getQuery();
                                                getThumbs.whereEqualTo("guestId", ParseUser.getCurrentUser().getObjectId())
                                                        .whereEqualTo("hostId", hostObjectId)
                                                        .whereEqualTo("flyerId", objectid)
                                                        .getFirstInBackground(new GetCallback<Props_Counter_Helper>() {
                                                            @Override
                                                            public void done(Props_Counter_Helper props_counter_helper, ParseException e) {
                                                                /**If the current guest gave the host props up or down*/
                                                                if (e == null) {
                                                                    if (props_counter_helper.getThumb().equals("positive")) {
                                                                        thumbsup.setBackgroundResource(R.drawable.ic_action_thumbup_red);
                                                                        isThumbedup = true;
                                                                        isThumbedDown = false;
                                                                        existence = true;

                                                                    } else {
                                                                        thumbsdown.setBackgroundResource(R.drawable.ic_action_thumbdown_red);
                                                                        isThumbedDown = true;
                                                                        isThumbedup = false;
                                                                        existence = true;
                                                                    }

                                                                    /** Else it doesn't exist*/
                                                                } else {
                                                                    existence = false;

                                                                }
                                                            }
                                                        });

                                                /**To determine how much props the host has */

                                                ParseQuery<Props_Helper> het = Props_Helper.getPropList();
                                                het.whereEqualTo("userId", hostObjectId)
                                                        .getFirstInBackground(new GetCallback<Props_Helper>() {
                                                            @Override
                                                            public void done(Props_Helper props_helper, ParseException e) {
                                                                if(e == null){

                                                                    if (props_helper.getVerification()) {
                                                                        memberLevel.setBackgroundResource(R.drawable.props_verified);
                                                                        memberLevel.setVisibility(View.VISIBLE);
                                                                        isVerified = true;

                                                                    } else if (props_helper.getProps() >= 1000000) {
                                                                        memberLevel.setBackgroundResource(R.drawable.props_gold);
                                                                        memberLevel.setVisibility(View.VISIBLE);
                                                                        isGold = true;

                                                                    } else if (props_helper.getProps() >= 100000) {
                                                                        memberLevel.setBackgroundResource(R.drawable.props_silver);
                                                                        memberLevel.setVisibility(View.VISIBLE);
                                                                        isSilver = true;

                                                                    } else if (props_helper.getProps() >= 1000) {
                                                                        memberLevel.setBackgroundResource(R.drawable.props_bronze);
                                                                        memberLevel.setVisibility(View.VISIBLE);
                                                                        isBronze = true;

                                                                    } else {
                                                                        memberLevel.setVisibility(View.INVISIBLE);
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

        ParseQueryAdapter<ParseUser> updateProfileParseQueryAdapter =
                new ParseQueryAdapter<>(getActivity(), ParseUser.class);
        updateProfileParseQueryAdapter.setTextKey("title");
        gridView = (GridView)interested.findViewById(R.id.gvInterested);

        updateFavorites();

        return interested;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavorites();
    }

    @Override
    public void onClick(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.ivInterestedPeople:
                        Bundle bundle = new Bundle();
                        bundle.putString("user", v.getTag().toString());
                        Intent intent = new Intent("com.socialgroupe.PROFILE");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                    case R.id.ibThumbsUp:
                        /** If it exists, give the host a +1 prop*/
                        if (!isThumbedup && existence) {
                            ParseQuery<Props_Counter_Helper> changeThumb = Props_Counter_Helper.getQuery();
                            changeThumb.whereEqualTo("guestId", ParseUser.getCurrentUser().getObjectId())
                                    .whereEqualTo("hostId", hostObjectId)
                                    .whereEqualTo("flyerId", objectid)
                                    .getFirstInBackground(new GetCallback<Props_Counter_Helper>() {
                                        @Override
                                        public void done(Props_Counter_Helper props_counter_helper, ParseException e) {
                                            if (e == null) {
                                                props_counter_helper.setThumb("positive");
                                                props_counter_helper.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {

                                                            ParseQuery<Props_Helper> propUser = Props_Helper.getPropList();
                                                            propUser.whereEqualTo("userId", hostObjectId)
                                                                    .getFirstInBackground(new GetCallback<Props_Helper>() {
                                                                        @Override
                                                                        public void done(Props_Helper parseUser, ParseException e) {
                                                                            if (e == null) {
                                                                                parseUser.increment("propCount");
                                                                                parseUser.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if (e == null) {
                                                                                            thumbsdown.setBackgroundResource(R.drawable.ic_action_thumbdown);
                                                                                            thumbsup.setBackgroundResource(R.drawable.ic_action_thumbup_red);
                                                                                            existence = true;
                                                                                            isThumbedup = true;
                                                                                            isThumbedDown = false;
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        } else if (!existence) {
                            Props_Counter_Helper propem = new Props_Counter_Helper();
                            propem.setThumb("positive");
                            propem.setFlyerId(objectid);
                            propem.setGuestId(ParseUser.getCurrentUser().getObjectId());
                            propem.setHostId(hostObjectId);
                            propem.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseQuery<Props_Helper> propUser = Props_Helper.getPropList();
                                        propUser.whereEqualTo("userId", hostObjectId)
                                                .getFirstInBackground(new GetCallback<Props_Helper>() {
                                                    @Override
                                                    public void done(Props_Helper parseUser, ParseException e) {
                                                        if (e == null) {
                                                            parseUser.increment("propCount");
                                                            parseUser.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        thumbsdown.setBackgroundResource(R.drawable.ic_action_thumbdown);
                                                                        thumbsup.setBackgroundResource(R.drawable.ic_action_thumbup_red);
                                                                        existence = true;
                                                                        isThumbedup = true;
                                                                        isThumbedDown = false;
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                        break;

                    case R.id.ibThumbdown:
                        /** If it exists, give the host a -1 prop*/
                        if (!isThumbedDown && existence) {
                            ParseQuery<Props_Counter_Helper> changeThumb = Props_Counter_Helper.getQuery();
                            changeThumb.whereEqualTo("guestId", ParseUser.getCurrentUser().getObjectId())
                                    .whereEqualTo("hostId", hostObjectId)
                                    .whereEqualTo("flyerId", objectid)
                                    .getFirstInBackground(new GetCallback<Props_Counter_Helper>() {
                                        @Override
                                        public void done(Props_Counter_Helper props_counter_helper, ParseException e) {
                                            if (e == null) {
                                                props_counter_helper.setThumb("negative");
                                                props_counter_helper.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {

                                                            ParseQuery<Props_Helper> propUser = Props_Helper.getPropList();
                                                            propUser.whereEqualTo("userId", hostObjectId)
                                                                    .getFirstInBackground(new GetCallback<Props_Helper>() {
                                                                        @Override
                                                                        public void done(Props_Helper parseUser, ParseException e) {
                                                                            if (e == null) {
                                                                                parseUser.increment("propCount", -1);
                                                                                parseUser.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if (e == null) {
                                                                                            thumbsup.setBackgroundResource(R.drawable.ic_action_thumbup);
                                                                                            thumbsdown.setBackgroundResource(R.drawable.ic_action_thumbdown_red);
                                                                                            existence = true;
                                                                                            isThumbedDown = true;
                                                                                            isThumbedup = false;
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        } else if (!existence) {
                            Props_Counter_Helper propem = new Props_Counter_Helper();
                            propem.setThumb("negative");
                            propem.setFlyerId(objectid);
                            propem.setGuestId(ParseUser.getCurrentUser().getObjectId());
                            propem.setHostId(hostObjectId);
                            propem.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseQuery<Props_Helper> propUser = Props_Helper.getPropList();
                                        propUser.whereEqualTo("objectId", hostObjectId)
                                                .getFirstInBackground(new GetCallback<Props_Helper>() {
                                                    @Override
                                                    public void done(Props_Helper parseUser, ParseException e) {
                                                        if (e == null) {
                                                            parseUser.increment("propCount", -1);
                                                            parseUser.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        thumbsup.setBackgroundResource(R.drawable.ic_action_thumbup);
                                                                        thumbsdown.setBackgroundResource(R.drawable.ic_action_thumbdown_red);
                                                                        existence = true;
                                                                        isThumbedDown = true;
                                                                        isThumbedup = false;
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                        break;

                    case R.id.ivMemberLevel:
                        if(isVerified){
                            verifiedMessage();

                        } else if(isGold){
                            goldMessage();

                        } else if(isSilver){
                            silverMessage();

                        } else if(isBronze){
                            bronzeMessage();
                        }
                        break;

                    default:
                        Intent intent1 = new Intent(getActivity(), Bulletin.class);
                        startActivity(intent1);
                        getActivity().finish();
                }
            }
        });
    }

    private void verifiedMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Verified Member");
        builder.setMessage(hostName + " was personally vetted by the Hiikey team.");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goldMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Gold Member");
        builder.setMessage(hostName +
                " attained a gold membership by receiving 1 million thumbs up props from event guests.");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        AlertDialog alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void silverMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Silver Member");
        builder.setMessage(hostName +
                " attained a silver membership by receiving 100,000 thumbs up props from event guests.");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // dialog.dismiss();
            }
        });
        AlertDialog alertDialog2 = builder.create();
        alertDialog2.show();
    }

    private void bronzeMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bronze Member");
        builder.setMessage(hostName +
                " attained a bronze membership by receiving 1,000 thumbs up props from event guests.");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // dialog.dismiss();
            }
        });
        AlertDialog alertDialog3 = builder.create();
        alertDialog3.show();
    }

    private void updateFavorites(){
        ParseObject.registerSubclass(Favorites.class);
        ParseQuery<Favorites> favoritesParseQuery = Favorites.getData();
        favoritesParseQuery.whereEqualTo("flyerId", objectid);
        favoritesParseQuery.findInBackground(new FindCallback<Favorites>() {
            @Override
            public void done(List<Favorites> favoriteses, ParseException e) {
                if (e == null) {
                    List<String> favList = new ArrayList<>();
                    favNumber.setText(String.valueOf(favoriteses.size()));
                    for (Favorites favorites : favoriteses) {
                        favList.add(favorites.getUserId());
                 }
                    interestedAdapter = new InterestedAdapter(getActivity(), favList);
                    gridView.setAdapter(interestedAdapter);
                    interestedAdapter.loadObjects();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Update failed. Make sure your Internet is connected.", Toast.LENGTH_SHORT).show();
                }
            }
            });
    }

    class InterestedAdapter extends ParseQueryAdapter<ParseUser> {
        public InterestedAdapter(Context context, final List list){
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>(){
                @Override
                public ParseQuery<ParseUser> create() {
                   ParseQuery<ParseUser> updateProfileParseQuery = ParseUser.getQuery();
                   updateProfileParseQuery.whereContainedIn("objectId",list );

                    return updateProfileParseQuery;
                }
            });
        }

        @Override
        public View getItemView(ParseUser object, View v, ViewGroup parent) {
            if(v== null){
                v = View.inflate(getContext(), R.layout.fragment_row_interested, null);
            }
             super.getItemView(object, v, parent);

            final ParseImageView parseImageView =
                    (ParseImageView)v.findViewById(R.id.ivInterestedPeople);
            parseImageView.setOnClickListener(InterestedFrag.this);
            parseImageView.setTag(object.getObjectId());

            ParseFile parseFile = object.getParseFile("Profile");
            Picasso.with(getActivity())
                    .load(parseFile.getUrl())
                    .resize(325, 405)
                    .into(parseImageView);
            return v;
        }
    }
}