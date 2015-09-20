package com.socialgroupe.hiikeyandroid;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic on 8/8/15.
 * fragment used to load the flyer data into the main UI
 * parent activity: Home.Java
 */
public class Home_fragment extends ListFragment implements View.OnClickListener {
        int mNum;

        private LinearLayout bulletinLayout, interestedLayout;
        private FrameLayout helal;
        private RelativeLayout actionLayout;
        private TextView bulletinName, interestedNumber;
        private ImageButton like, share, interested, bulletinPic, info, trash;
        private ImageView fa;
        private boolean touched = false, liked = false;
        private ProgressBar progressShare;

        private LinearLayout createFun;

        private List<String> flyerFile = new ArrayList<>();
        private List<String> bulletinNameList = new ArrayList<>();
        private List<String> flyerId = new ArrayList<>();
        private List<String> flyerHashtag = new ArrayList<>();

        private String bulletinId;

    static Home_fragment newInstance(int num, ArrayList<String> flyerFile, ArrayList<String> bulletinName,
                                     ArrayList<String> flyerId, ArrayList<String> flyerHashtag){
            Home_fragment f = new Home_fragment();

            // supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            args.putStringArrayList("flyerFile", flyerFile);
            args.putStringArrayList("bulletinName", bulletinName);
            args.putStringArrayList("flyerId", flyerId);
            args.putStringArrayList("flyerHashtag", flyerHashtag);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
           mNum = getArguments() != null ? getArguments().getInt("num") : 1;
            flyerFile =  getArguments().getStringArrayList("flyerFile");
            bulletinNameList = getArguments().getStringArrayList("bulletinName");
            flyerId = getArguments().getStringArrayList("flyerId");
            flyerHashtag = getArguments().getStringArrayList("flyerHashtag");
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home_pager_list, container, false);

            fa = (ImageView) v.findViewById(R.id.homeFlyer);
            fa.setOnClickListener(this);

            progressShare = (ProgressBar) v.findViewById(R.id.pbHomeShareProgress);

            helal = (FrameLayout) v.findViewById(R.id.flHome_pager);

            interestedLayout =(LinearLayout) v.findViewById(R.id.llHomeInterested);
            interestedLayout.setOnClickListener(this);

            bulletinLayout = (LinearLayout) v.findViewById(R.id.llHomeBulletin);
            actionLayout = (RelativeLayout) v.findViewById(R.id.llHomeActions);

            info = (ImageButton) v.findViewById(R.id.ibHomeInfo);
            info.setOnClickListener(this);

            bulletinPic = (ImageButton) v.findViewById(R.id.ibHomeBulletin);
            bulletinPic.setOnClickListener(this);

            like = (ImageButton) v.findViewById(R.id.ibHomeLike);
            like.setOnClickListener(this);

            share = (ImageButton) v.findViewById(R.id.ibHomeShare);
            share.setOnClickListener(this);

            interested = (ImageButton) v.findViewById(R.id.ibHomePerson);
            interested.setOnClickListener(this);

            bulletinName = (TextView) v.findViewById(R.id.tvBulletinName);
            interestedNumber = (TextView) v.findViewById(R.id.tvHomeInterested);

            createFun = (LinearLayout) v.findViewById(R.id.llCreateFun);

            Button button = (Button) v.findViewById(R.id.bHomeCreateFun);
            button.setOnClickListener(this);

            trash = (ImageButton) v.findViewById(R.id.ibHomeTrashIt);
            trash.setOnClickListener(this);

            loadStuff();

            /*ParseQuery<PublicPost_Helper> isOwner = PublicPost_Helper.getQuery();
            isOwner.whereEqualTo("objectId", flyerId.get(mNum))
                    .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                        @Override
                        public void done(PublicPost_Helper publicPost_helper, ParseException e) {
                            if(e == null){
                                if (publicPost_helper.getUserId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())){


                                    trash.setVisibility(View.VISIBLE);
                                } else{
                                    trash.setVisibility(View.GONE);
                                }
                            }
                        }
                    });

                createFun.setVisibility(View.GONE);
                Picasso.with(getActivity())
                        .load(flyerFile.get(mNum))
                        .placeholder(R.drawable.ic_launcher_hiikey)
                        .resize(425, 625)
                        .into(fa);

                //Load Interested People
                ParseQuery<Favorites_Helper> getInterestedNumber = Favorites_Helper.getData();
                getInterestedNumber.whereEqualTo("flyerId", flyerId.get(mNum))
                        .findInBackground(new FindCallback<Favorites_Helper>() {
                            @Override
                            public void done(List<Favorites_Helper> list, ParseException e) {
                                interestedNumber.setText(Integer.toString(list.size()));

                                for (Favorites_Helper getLikes : list) {
                                    if (getLikes.getUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                        like.setBackgroundResource(R.drawable.ic_action_heart_red);
                                        liked = true;
                                    }
                                }
                            }
                        });

                //Load Bulletin Data
                ParseQuery<Bulletin_Helper> loadBulletinData = Bulletin_Helper.getQuery();
                loadBulletinData.whereEqualTo("bulletinName", bulletinNameList.get(mNum));
                loadBulletinData.getFirstInBackground(new GetCallback<Bulletin_Helper>() {
                    @Override
                    public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                        if (e == null) {
                            ParseFile parseFile = bulletin_helper.getParseFile("bulletinPic");

                            Picasso.with(getActivity())
                                    .load(parseFile.getUrl())
                                    .resize(375, 155)
                                    .into(bulletinPic);

                            bulletinName.setText(bulletin_helper.getBulletinName());
                            bulletinName.setVisibility(View.VISIBLE);

                            bulletinId = bulletin_helper.getObjectId();
                        }
                    }
                });*/

            return v;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.ibHomeTrashIt:
                    openConfirmDeleteDialog();

                /*case R.id.bHomeCreateFun:
                    Intent intent1 = new Intent(getActivity(), Promotion.class);
                    startActivity(intent1);
                    break;*/

                case R.id.ibHomeInfo:

                    /**
                     * queries for the flyer info: PublicPost database class.
                     */
                    ParseQuery<PublicPost_Helper> getInfo = PublicPost_Helper.getQuery();
                    getInfo.whereEqualTo("objectId", flyerId.get(mNum))
                    .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                                @Override
                                public void done(PublicPost_Helper publicPost_helper, ParseException e) {
                                    if(e == null){
                                        String locationMerger = publicPost_helper.getLocation().getLatitude() + ","
                                                + publicPost_helper.getLocation().getLongitude();

                                        String checkDes = publicPost_helper.getDescription();
                                        if (checkDes.equals("null")) {
                                            checkDes = " ";
                                        }

                                        String checkHashtag = publicPost_helper.getHashtag();
                                        if (checkHashtag.equals("null")) {
                                            checkHashtag = " ";
                                        }

                                        String checkWebsite = publicPost_helper.getWebsite();
                                        if (checkWebsite.equals("null")) {
                                            checkWebsite = " ";
                                        } else if (!checkWebsite.startsWith("https://")) {
                                            checkWebsite = "https://" + publicPost_helper.getWebsite();

                                        } else if (!checkWebsite.startsWith("http://")) {
                                            checkWebsite = "http://" + publicPost_helper.getWebsite();

                                        }

                                        openInfoDialog(checkDes,
                                                publicPost_helper.getTitle(),
                                                checkHashtag,
                                                checkWebsite,
                                                publicPost_helper.getAddress(),
                                                locationMerger);
                                    }
                                }
                            });
                    break;

                case R.id.homeFlyer:
                    if(!touched) {
                        bulletinLayout.setVisibility(View.VISIBLE);
                        actionLayout.setVisibility(View.VISIBLE);
                        touched = true;
                    } else{
                        bulletinLayout.setVisibility(View.GONE);
                        actionLayout.setVisibility(View.GONE);
                        touched = false;
                    }
                    break;

                /**
                 * let's the user like the flyer.
                 * adds a new column to the Favorites database class.
                 */
                case R.id.ibHomeLike:
                    if (!liked) {
                        Favorites_Helper favThat = new Favorites_Helper();
                        favThat.setUserId(ParseUser.getCurrentUser().getObjectId());
                        favThat.setFlyerId(flyerId.get(mNum));
                        favThat.setFavUser(ParseUser.getCurrentUser());
                        favThat.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    like.setBackgroundResource(R.drawable.ic_action_heart_red);
                                    liked = true;
                                }
                            }
                        });
                    } else{
                        ParseQuery<Favorites_Helper> getLiked = Favorites_Helper.getData();
                        getLiked.whereEqualTo("flyerId", flyerId.get(mNum))
                                .getFirstInBackground(new GetCallback<Favorites_Helper>() {
                                    @Override
                                    public void done(Favorites_Helper favorites, ParseException e) {
                                        favorites.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null){
                                                    like.setBackgroundResource(R.drawable.ic_action_heart_white);
                                                    liked = false;
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                    break;

                /**
                 * let's the user share the flyer elsewhere.
                 * does a query in the PublicPost database class for the hashtag and flyer
                 */
                case R.id.ibHomeShare:
                    progressShare.setVisibility(View.VISIBLE);
                    final String flyerHasher;
                    if(flyerHashtag.get(mNum).equals("null"))
                    {
                        flyerHasher = " ";
                    } else {
                        flyerHasher = flyerHashtag.get(mNum);
                    }

                    ParseQuery<PublicPost_Helper> shareFly = PublicPost_Helper.getQuery();
                    shareFly.whereEqualTo("objectId", flyerId.get(mNum))
                            .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                                @Override
                                public void done(PublicPost_Helper publicPost_helper, ParseException e) {
                                    if (e == null) {

                                        ParseFile parseFile = publicPost_helper.getParseFile("Flyer");
                                        parseFile.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] bytes, ParseException e) {
                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                Uri ui = getImageUri(getActivity(), bmp);
                                                Intent shareIntent = new Intent();
                                                shareIntent.setAction(Intent.ACTION_SEND);
                                                shareIntent.putExtra(Intent.EXTRA_STREAM, ui);
                                                shareIntent.setType("image/png");
                                                shareIntent.putExtra("sms_body", "Check this event out! " + flyerHasher + " #hiikeyandroid");

                                                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                    startActivity(Intent.createChooser(shareIntent, "Share Flyer"));
                                                }
                                                progressShare.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }
                            });
                    break;
                /**
                 * queries the database for the bulletin board info and the whether or not the user
                 * subscribed to the bulletin board.
                 *
                 * if the bulletin board name is local, which is the basic bulletin, the hiikey stops
                 * there and simply displays local. No use in going any further since the user can't
                 * subscribe or un-subscribe from local.
                 */
                case R.id.ibHomeBulletin:
                    if (!bulletinNameList.get(mNum).equals("LOCAL")) {
                        ParseQuery<Subscribe_Helper> seeSubscription = Subscribe_Helper.getData();
                        seeSubscription.whereEqualTo("bulletinTitle", bulletinNameList.get(mNum))
                                .whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId())
                                .getFirstInBackground(new GetCallback<Subscribe_Helper>() {
                                    @Override
                                    public void done(final Subscribe_Helper subscribe_helper, ParseException e) {
                                        if (e == null) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle(bulletinNameList.get(mNum))
                                                    .setPositiveButton("Un-Subscribe", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(final DialogInterface dialog, int which) {
                                                            subscribe_helper.deleteInBackground(new DeleteCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })

                                                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle(bulletinNameList.get(mNum))
                                                    .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(final DialogInterface dialog, int which) {
                                                            Subscribe_Helper newSubscribe = new Subscribe_Helper();
                                                            newSubscribe.setUserId(ParseUser.getCurrentUser().getObjectId());
                                                            newSubscribe.setBulletinTitle(bulletinNameList.get(mNum));
                                                            newSubscribe.setBulletinSubscriber(ParseUser.getCurrentUser());
                                                            newSubscribe.setBulletinId(bulletinId);
                                                            newSubscribe.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    })

                                                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    }
                                });
                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(bulletinNameList.get(mNum))
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    break;

                case R.id.llHomeInterested:
                    Intent intent = new Intent(getActivity(), InterestedFrag.class);
                    intent.putExtra("objectId", flyerId.get(mNum));
                    startActivity(intent);
                    break;
            }
        }

        private Uri getImageUri(Context context, Bitmap image){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image,
                    "Flyer", null);
            return Uri.parse(path);
        }

    private void showMap(Uri geoLocation){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
        }
    }

    /**
     * open a dialog that shows the flyer info. Also gives the user the ability to map the address,
     * and go to the website.
     * @param description The Flyer description
     * @param title Flyer title
     * @param hashtag flyer hashtag
     * @param website website, the user will be able to go straight to the website form alert dialog.
     * @param address
     * @param location flyer's location, user will be able to get directions from the app.
     */
    private void openInfoDialog(String description, String title, String hashtag,
                                 final String website, final String address, final String location){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(description + "\n" + hashtag)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (!website.equals(" ")) {

            builder.setNeutralButton("Website", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(browserIntent);
                    dialog.dismiss();
                }
            });
        }

        builder.setNegativeButton("Directions", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mapit = "geo:0,0?q=" + location + "(" + address + ")";
                Uri locateEvent = Uri.parse(mapit);
                showMap(locateEvent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * opens dialog that confirms the user wants to delete the flyer.
     * if the user clicks yes, hiikey queries the flyer and deletes it from the PublicPost database class
     * and add it to the PublicPost_removed database class.
     */
    private void openConfirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirmDeleteTitle)
                .setMessage(R.string.confirmDeleteMessage)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yaornah = true;

                        ParseObject.registerSubclass(PublicPostRemoved_Helper.class);
                        ParseObject.registerSubclass(PublicPost_Helper.class);
                        ParseQuery<PublicPost_Helper> cladQuery = PublicPost_Helper.getQuery();
                        cladQuery.whereEqualTo("objectId", flyerId.get(mNum));
                        cladQuery.getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                                                           @Override
                                                           public void done(final PublicPost_Helper publicPostHelper, ParseException e) {
                                                               if (e == null) {
                                                                   PublicPostRemoved_Helper remove = new PublicPostRemoved_Helper();

                                                                   String title = publicPostHelper.getTitle();
                                                                   String address = publicPostHelper.getAddress();
                                                                   String date = publicPostHelper.getDate();
                                                                   String time = publicPostHelper.getTime();
                                                                   String hashtag = publicPostHelper.getHashtag();
                                                                   String category = publicPostHelper.getCategory();
                                                                   String privacy = publicPostHelper.getPrivacy();
                                                                   String des = publicPostHelper.getDescription();
                                                                   String website = publicPostHelper.getWebsite();

                                                                   remove.setTitle(title);
                                                                   remove.setAddress(address);
                                                                   remove.setDate(date);
                                                                   remove.setTime(time);

                                                                   if (!website.equals("null")) {
                                                                       remove.setWebsite(website);
                                                                   }

                                                                   if (!des.equals("null")) {
                                                                       remove.setDescription(des);
                                                                   }

                                                                   if (!hashtag.equals("null")) {
                                                                       remove.setHashtag(hashtag);
                                                                   }
                                                                   remove.setCategory(category);
                                                                   remove.setPrivacy(privacy);

                                                                   remove.setflyer(publicPostHelper.getfyler());
                                                                   remove.setUser(ParseUser.getCurrentUser());
                                                                   remove.setLocation(publicPostHelper.getLocation());
                                                                   remove.saveInBackground(new SaveCallback() {
                                                                       @Override
                                                                       public void done(ParseException e) {
                                                                           publicPostHelper.deleteInBackground(new DeleteCallback() {
                                                                               @Override
                                                                               public void done(ParseException e) {
                                                                                   flyerFile.remove(mNum);
                                                                                    bulletinNameList.remove(mNum);
                                                                                    flyerId.remove(mNum);
                                                                                   flyerHashtag.remove(mNum);
                                                                                   loadStuff();
                                                                               }
                                                                           });
                                                                       }
                                                                   });
                                                               } else {
                                                                   Toast.makeText(getActivity().getApplicationContext(),
                                                                           "Deletion failed. Make sure your Internet is connected.",
                                                                           Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       }
                        );
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yaornah = false;
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * function to load the flyer info.
     * loads the amount of people who have liked the flyer.
     * also decides whether or not the current user has liked the flyer.
     * if yes, then the heart will be red.
     * if no, then the heart will be white.
     */
    private void loadStuff(){
        ParseQuery<PublicPost_Helper> isOwner = PublicPost_Helper.getQuery();
        isOwner.whereEqualTo("objectId", flyerId.get(mNum))
                .getFirstInBackground(new GetCallback<PublicPost_Helper>() {
                    @Override
                    public void done(PublicPost_Helper publicPost_helper, ParseException e) {
                        if(e == null){
                            if (publicPost_helper.getUserId()
                                    .equals(ParseUser.getCurrentUser().getObjectId())){


                                trash.setVisibility(View.VISIBLE);
                            } else{
                                trash.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        createFun.setVisibility(View.GONE);
        Picasso.with(getActivity())
                .load(flyerFile.get(mNum))
                .placeholder(R.drawable.ic_launcher_hiikey)
                .resize(425, 625)
                .into(fa);

        //Load Interested People
        ParseQuery<Favorites_Helper> getInterestedNumber = Favorites_Helper.getData();
        getInterestedNumber.whereEqualTo("flyerId", flyerId.get(mNum))
                .findInBackground(new FindCallback<Favorites_Helper>() {
                    @Override
                    public void done(List<Favorites_Helper> list, ParseException e) {
                        interestedNumber.setText(Integer.toString(list.size()));

                        for (Favorites_Helper getLikes : list) {
                            if (getLikes.getUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                like.setBackgroundResource(R.drawable.ic_action_heart_red);
                                liked = true;
                            }
                        }
                    }
                });

        //Load Bulletin Data
        ParseQuery<Bulletin_Helper> loadBulletinData = Bulletin_Helper.getQuery();
        loadBulletinData.whereEqualTo("bulletinName", bulletinNameList.get(mNum));
        loadBulletinData.getFirstInBackground(new GetCallback<Bulletin_Helper>() {
            @Override
            public void done(Bulletin_Helper bulletin_helper, ParseException e) {
                if (e == null) {
                    ParseFile parseFile = bulletin_helper.getParseFile("bulletinPic");

                    Picasso.with(getActivity())
                            .load(parseFile.getUrl())
                            .resize(375, 155)
                            .into(bulletinPic);

                    bulletinName.setText(bulletin_helper.getBulletinName());
                    bulletinName.setVisibility(View.VISIBLE);

                    bulletinId = bulletin_helper.getObjectId();
                }
            }
        });
    }
}
