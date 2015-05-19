package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 11/17/2014.
 *
 * Helper Class: Methods for creating new flyers.
 */
@ParseClassName("PublicPost")
public class PublicPost_Helper extends ParseObject {

    public PublicPost_Helper() {

    }

    public String getAll(){
        return getString("All");
    }

    public void setAll(String value){
        put("All", value);
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

    public String getWebsite(){
        return getString("Website");
    }

    public void setWebsite(String web){
        put("Website", web );
    }

    public String getDescription(){
        return getString("Description");
    }

    public void setDescription(String des){
        put("Description",des );
    }

    //////////////////////////////////Clad Post HashTag////////////////////////////////
    public String getHashtag(){
        return getString("Hashtag");
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

/////////////////////////////////////FLyer design//////////////////////////////////////
    public ParseFile getfyler(){
        return getParseFile("Flyer");
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

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String userId){
        put("userId", userId);
    }

    ///////////////////////////////////////Clad Post Location coordinates///////////////////////////////
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    /////////////////////////////////////////Clad Post Query///////////////////////////////////////////////
    public static ParseQuery<PublicPost_Helper> getQuery() {
        return ParseQuery.getQuery(PublicPost_Helper.class);
    }
}


