package com.socialgroupe.hiikey;

/**
 * Created by Dominic on 4/23/2015.

public class SearchBulletins extends ActionBarActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject.registerSubclass(Bulletin_Helper.class);
        setContentView(R.layout.activity_searchbulletins);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbSearchBulletins);
        final TextView textView = (TextView) findViewById(R.id.tvNoBulletins);

        ParseQueryAdapter<PublicPost_Helper> mainAdapter = new ParseQueryAdapter<>(this, PublicPost_Helper.class);
        mainAdapter.setTextKey("title");

        editText = (EditText) findViewById(R.id.etSearchBulletins);
        final ListView listView = (ListView) findViewById(R.id.lvSearchBulletins);
        ImageButton imageButton = (ImageButton) findViewById(R.id.ibSearchBulletins);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = editText.getText().toString();
                final List<String> userList = new ArrayList<>();

                ParseQuery<Bulletin_Helper> query = Bulletin_Helper.getQuery();
                query.whereMatches("bulletinName", string.toUpperCase())
                        .findInBackground(new FindCallback<Bulletin_Helper>() {
                            @Override
                            public void done(List<Bulletin_Helper> parseUsers, ParseException e) {
                                if (e == null) {
                                    for (Bulletin_Helper user : parseUsers) {
                                        userList.add(user.getObjectId());
                                    }

                                    PeopleSearchAdapter peopleSearchAdapter =
                                            new PeopleSearchAdapter(SearchBulletins.this, userList);
                                    listView.setAdapter(peopleSearchAdapter);
                                    peopleSearchAdapter.setAutoload(true);
                                    peopleSearchAdapter.loadObjects();
                                    peopleSearchAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Bulletin_Helper>() {
                                        @Override
                                        public void onLoading() {
                                            progressBar.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onLoaded(List<Bulletin_Helper> parseUsers, Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            userList.clear();
                                            if (parseUsers.isEmpty()) {
                                                textView.setVisibility(View.VISIBLE);
                                            } else {
                                                textView.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    InputMethodManager imm = (InputMethodManager) getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    class PeopleSearchAdapter extends ParseQueryAdapter<Bulletin_Helper> {
        public PeopleSearchAdapter(Context context, final List string) {
            super(context, new QueryFactory<Bulletin_Helper>() {

                public ParseQuery<Bulletin_Helper> create() {
                    ParseQuery query = new ParseQuery("Bulletin");
                    query.whereContainedIn("objectId", string);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(final Bulletin_Helper object, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.activity_row_bulletin, null);
                v.setTag(object.getObjectId());
            }
            super.getItemView(object, v, parent);

            final ImageView imageView = (ImageView) v.findViewById(R.id.ivBulletin);
            imageView.setTag(object.getObjectId());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bulletin", v.getTag().toString());
                    Intent intent = new Intent(SearchBulletins.this, Bulletin_Posts.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            ParseFile parse = object.getParseFile("bulletinPic");
            Picasso.with(SearchBulletins.this)
                    .load(parse.getUrl())
                    .resize(800, 450)
                    .into(imageView);

            TextView textView = (TextView) v.findViewById(R.id.tvBulletinName);
            if (object.getShowHideSetting().equals("hide")) {
                textView.setVisibility(View.INVISIBLE);
            } else {
                textView.setText(object.getBulletinName());
                textView.setVisibility(View.VISIBLE);
            }

            return v;
        }
    }
}

*/
