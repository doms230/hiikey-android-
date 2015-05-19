package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/26/2015.
 * Helper Class to save removed public posts.
 */
@ParseClassName("PublicPostRemoved")
public class PublicPostRemoved extends ParseObject {

    public PublicPostRemoved() {

    }

    public String getDescription(){
        return getString("Description");
    }

    public void setDescription(String value){
        put("Description", value);
    }

    public void setWebsite(String value){
        put("Website", value);
    }

    //////////////////Clad Post Title/////////////////////
    public String getTitle(){
        return getString("Title");
    }

    public void setTitle(String value){
        put("Title", value);
    }

    //////////////////Clad Post Address//////////////////////////
    public String getAddress(){
        return getString("Address");
    }

    public void setAddress(String address){
        put("Address", address);
    }

    ////////////////////Clad Post Date/////////////////////////////
    public String getDate(){
        return getString("Date");
    }

    public void setDate(String date){
        put("Date", date);
    }

    ////////////////////////// Clad Post Time ////////////////////////////
    public String getTime(){
        return getString("Time");
    }

    public void setTime(String time){
        put("Time", time);
    }

    public void setHashtag(String hashtag){
        put("Hashtag", hashtag);
    }

    /////////////////////////////////Category//////////////////////////////////////////
    public String getCategory(){
        return getString("Category");
    }

    public void setCategory(String category){
        put("Category", category);
    }

    ///////////////////////////////Privacy///////////////////////////////////////////
    public String getPrivacy(){
        return getString("Privacy");
    }

    public void setPrivacy(String privacy){
        put("Privacy", privacy);
    }

    public void setflyer(ParseFile flyer){
        put("Flyer", flyer);
    }

    //////////////////////////////////Clad Post User //////////////////////////////////////
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    ///////////////////////////////////////Clad Post Location coordinates///////////////////////////////
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    /////////////////////////////////////////Clad Post Query///////////////////////////////////////////////
    public static ParseQuery<PublicPostRemoved> getQuery() {
        return ParseQuery.getQuery(PublicPostRemoved.class);
    }
}