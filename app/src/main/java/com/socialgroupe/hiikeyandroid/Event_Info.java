package com.socialgroupe.hiikeyandroid;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Event_Info extends ActionBarActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private EditText flyTitle, flyHashtag, flyAddress, flyWebsite, flyDesc;
    private Button bDate, bTime;
    private String stringFlyCategory,stringampm, stringFlyTime, stringFlyDate, privacy, stringFlyAddress;
    private boolean titleValidated = false, dateValidated = false, timeValidated =false,
            locationValidated = false, custom  = false, addressFound = false, websiteValidated =false;
    private double latitude, longitude;
    private ProgressBar pb;
    private TextView tvPrivateBulletin;

    /**
     * for some reason when I localize "addressFound = false;" it reads it us unitiliazed, so I
     * keep it global.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        pb = (ProgressBar)findViewById(R.id.pbEventInfo);
        pb.setIndeterminate(true);
        initialize_post();

        flyAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    List<Address> geocodeMatches = null;
                    try {
                        geocodeMatches = new Geocoder(Event_Info.this).getFromLocationName(flyAddress.getText().toString(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (geocodeMatches != null && geocodeMatches.size() > 0) {
                        latitude = geocodeMatches.get(0).getLatitude();
                        longitude = geocodeMatches.get(0).getLongitude();
                        addressFound = true;
                        flyAddress.setTextColor(getResources().getColor(R.color.greenText));
                    } else{
                        pb.setVisibility(View.GONE);
                        addressFound = false;
                        flyAddress.setTextColor(getResources().getColor(R.color.redText));
                        flyAddress.setError("Address not found");
                        //Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_eventifo_next:
                pb.setVisibility(View.VISIBLE);
                customNext();
                return true;

            case R.id.action_eventifo_back:
                exit();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){

            case R.id.sCategory:
                stringFlyCategory = parent.getItemAtPosition(position).toString();
                break;

            default:
                stringampm ="5";
                stringFlyCategory = "LOCAL";
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.bDate:
                date_picker(bDate);
                break;

            case R.id.bTime:
                time_picker(bTime);
                break;

            default:
                exit();
                finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /***
     * function validate all of the info stated below.
     *
     */
    private void validateInfo(String location, EditText lo, String date,
                              String time, String title, EditText tit, String website, EditText web){

        if(location.isEmpty()){
            lo.setError(getString(R.string.error_field_required));
            locationValidated = false;
        }  else{
            locationValidated = true;
        }

        if(date == null){
            Toast.makeText(getApplicationContext(), "Tap the Date button to set the date.", Toast.LENGTH_SHORT).show();
            dateValidated = false;
        } else{
            dateValidated = true;
        }

        if(time == null){
            Toast.makeText(getApplicationContext(), "Tap the Time button to set the time.", Toast.LENGTH_SHORT).show();
            timeValidated = false;
        } else{
            timeValidated = true;
        }

        if(title.isEmpty()){
            tit.setError(getString(R.string.error_field_required));
            titleValidated = false;
        } else{
            titleValidated = true;
        }

        if(!website.isEmpty())
        {
            if(website.startsWith("https://")){
                websiteValidated = true;
            } else if (website.startsWith("http://")) {
                websiteValidated = true;

            } else{
                websiteValidated = false;
                web.setError("Website in incorrect format");
            }
        }
    }

    private void exit(){
        Intent exIntent = new Intent(this, Promotion.class);
        startActivity(exIntent);
        finish();
    }

    /**
     * function to put the event info in a bundle and send it to the activity.
     */
    private void customNext() {

        String stringFlyHashtag = flyHashtag.getText().toString();
        stringFlyAddress = flyAddress.getText().toString();
        String stringtitle = flyTitle.getText().toString();
        String stringWebsite = flyWebsite.getText().toString();
        String stringDesc = flyDesc.getText().toString();

        validateInfo(stringFlyAddress, flyAddress, stringFlyDate, stringFlyTime,
                stringtitle, flyTitle, stringWebsite, flyWebsite);

       if(locationValidated && dateValidated && timeValidated && titleValidated && addressFound) {
            Bundle bundleCustomPost = new Bundle();
            bundleCustomPost.putDouble("latitude", latitude);
            bundleCustomPost.putDouble("longitude", longitude);
            bundleCustomPost.putString("title", stringtitle);

           if(!privacy.equals("Public"))
           {
               bundleCustomPost.putString("category", "PRIVATE");
           } else {
               bundleCustomPost.putString("category", stringFlyCategory);
           }
           if(stringFlyHashtag.isEmpty()){
               bundleCustomPost.putString("hashtag", "null");
           } else {
               bundleCustomPost.putString("hashtag","#" + stringFlyHashtag);
           }
            bundleCustomPost.putString("address", stringFlyAddress);
            bundleCustomPost.putString("date", stringFlyDate);
            bundleCustomPost.putString("time", stringFlyTime);
            if(stringDesc.isEmpty()){
                bundleCustomPost.putString("description", "null");
            } else {
                bundleCustomPost.putString("description", stringDesc);
            }
            if(stringWebsite.isEmpty()){
                bundleCustomPost.putString("website", "null");
            }else {
                bundleCustomPost.putString("website", stringWebsite);
            }
            bundleCustomPost.putBoolean("custom", custom);
            bundleCustomPost.putString("privacy",privacy );
            if(privacy.equals("Exclusive")) {
                bundleCustomPost.putStringArrayList("guestlist", getIntent().getStringArrayListExtra("guestlist"));
            }
            Intent intentCustom = new Intent(this, NewDesign.class);
            intentCustom.putExtras(bundleCustomPost);
            startActivity(intentCustom);
            finish();
                }
             }

    /**
     * function to choose the event date.
     * returns date in 1/1/2015 orientation.
     * @param date
     */
    private void date_picker(final Button date){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year );
                        stringFlyDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    /**
     * function to choose the event time.
     * the time is is returned on a 24 hr. clock by default,
     * so the switch statement below converts the time to 12 hr. clock.
     * @param time
     */
    private void time_picker(final Button time){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minpluszero = "0" + minute;
                        switch (hourOfDay){
                            case 12:
                                if(minute < 10) {
                                    time.setText(12 + ":" + minpluszero + " pm");
                                    stringFlyTime = 12 + ":" + minpluszero + " pm";
                                } else{
                                    time.setText(12 + ":" + minute + " pm");
                                    stringFlyTime = (12 + ":" + minute + " pm");
                                }
                            case 13:
                                if(minute < 10) {
                                    time.setText(1 + ":" + minpluszero + " pm");
                                    stringFlyTime = 1 + ":" + minpluszero + " pm";
                                } else{
                                    time.setText(1 + ":" + minute + " pm");
                                    stringFlyTime = (1 + ":" + minute + " pm");
                                }
                                break;

                            case 14:
                                if(minute < 10) {
                                    time.setText(2 + ":" + minpluszero + " pm");
                                    stringFlyTime = (2 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(2 + ":" + minute + " pm");
                                    stringFlyTime = (2 + ":" + minute + " pm");
                                }
                                break;

                            case 15:
                                if(minute < 10) {
                                    time.setText(3 + ":" + minpluszero + " pm");
                                    stringFlyTime = (3 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(3 + ":" + minute + " pm");
                                    stringFlyTime = (3 + ":" + minute + " pm");
                                }
                                break;

                            case 16:
                                if(minute < 10) {
                                    time.setText(4 + ":" + minpluszero + " pm");
                                    stringFlyTime = (4 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(4 + ":" + minute + " pm");
                                    stringFlyTime = (4 + ":" + minute + " pm");
                                }
                                break;

                            case 17:
                                if(minute < 10) {
                                    time.setText(5 + ":" + minpluszero + " pm");
                                    stringFlyTime = (5 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(5 + ":" + minute + " pm");
                                    stringFlyTime = (5 + ":" + minute + " pm");
                                }
                                break;

                            case 18:
                                if(minute < 10) {
                                    time.setText(6 + ":" + minpluszero + " pm");
                                    stringFlyTime = (6 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(6 + ":" + minute + " pm");
                                    stringFlyTime = (6 + ":" + minute + " pm");
                                }
                                break;

                            case 19:
                                if(minute < 10) {
                                    time.setText(7 + ":" + minpluszero + " pm");
                                    stringFlyTime = (7 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(7 + ":" + minute + " pm");
                                    stringFlyTime = (7 + ":" + minute + " pm");
                                }
                                break;

                            case 20:
                                if(minute < 10) {
                                    time.setText(8 + ":" + minpluszero + " pm");
                                    stringFlyTime = (8 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(8 + ":" + minute + " pm");
                                    stringFlyTime = (8 + ":" + minute + " pm");
                                }
                                break;

                            case 21:
                                if(minute < 10) {
                                    time.setText(9 + ":" + minpluszero + " pm");
                                    stringFlyTime = (9 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(9 + ":" + minute + " pm");
                                    stringFlyTime = (9 + ":" + minute + " pm");
                                }
                                break;

                            case 22:
                                if(minute < 10) {
                                    time.setText(10 + ":" + minpluszero + " pm");
                                    stringFlyTime = (10 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(10 + ":" + minute + " pm");
                                    stringFlyTime = (10 + ":" + minute + " pm");
                                }
                                break;

                            case 23:
                                if(minute < 10) {
                                    time.setText(11 + ":" + minpluszero + " pm");
                                    stringFlyTime = (11 + ":" + minpluszero + " pm");
                                } else{
                                    time.setText(11 + ":" + minute + " pm");
                                    stringFlyTime = (11 + ":" + minute + " pm");
                                }
                                break;

                            case 0:
                                if(minute < 10) {
                                    time.setText(12 + ":" + minpluszero + " am");
                                    stringFlyTime = (12 + ":" + minpluszero + " am");
                                } else{
                                    time.setText(12 + ":" + minute + " am");
                                    stringFlyTime = (12 + ":" + minpluszero + " am");
                                }
                                break;

                            default:
                                if(minute < 10) {
                                    time.setText(hourOfDay + ":" + minpluszero + " am");
                                    stringFlyTime = (hourOfDay + ":" + minpluszero + " am");
                                } else{
                                    time.setText(hourOfDay + ":" + minute + " am");
                                    stringFlyTime = (hourOfDay + ":" + minute + " am");
                                }
                                /*Toast.makeText(getApplicationContext(), "Time picker crashed, choose again."
                                        , Toast.LENGTH_SHORT).show();*/
                                break;
                        }
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }

    /**
     * initializes all of the main UI components.
     */
    private void initialize_post() {

        privacy = getIntent().getExtras().getString("privacy");

        bDate = (Button) findViewById(R.id.bDate);
        bDate.setOnClickListener(this);

        bTime = (Button) findViewById(R.id.bTime);
        bTime.setOnClickListener(this);

        flyTitle = (EditText) findViewById(R.id.etTitle);
        flyAddress = (EditText) findViewById(R.id.etAddress);
        flyHashtag = (EditText) findViewById(R.id.etOfficialHashtag);

        flyWebsite = (EditText) findViewById(R.id.etWebsite);
        flyDesc = (EditText) findViewById(R.id.etDescription);

        tvPrivateBulletin = (TextView) findViewById(R.id.tvPrivateBulletin);

        if (!privacy.equals("Public")) {
            tvPrivateBulletin.setVisibility(View.VISIBLE);
        }

        else {

        final Spinner category = (Spinner) findViewById(R.id.sCategory);
        category.setOnItemSelectedListener(Event_Info.this);

            final List<String> bulletinList = new ArrayList<>();
            bulletinList.add("LOCAL");
            ParseQuery<Bulletin_Helper> getBull = Bulletin_Helper.getQuery()
            .whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                    getBull.findInBackground(new FindCallback<Bulletin_Helper>() {
                        @Override
                        public void done(List<Bulletin_Helper> list, ParseException e) {
                            if (e == null) {
                                for (Bulletin_Helper ble : list) {
                                    bulletinList.add(ble.getBulletinName());

                                }

                            }

                            ArrayAdapter<String> catAdapter = new ArrayAdapter<>(Event_Info.this,
                                    android.R.layout.simple_spinner_dropdown_item, bulletinList);
                            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            category.setAdapter(catAdapter);
                            category.setVisibility(View.VISIBLE);
                        }
                    });
    }
        CheckBox checkBox= (CheckBox)findViewById(R.id.cbIsitCustom);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    custom = true;
                } else {
                    custom = false;
                }
            }
        });
    }
}