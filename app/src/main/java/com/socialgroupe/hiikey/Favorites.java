package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Helper class: Methods for people's favorite events.
 */

@ParseClassName("Favorites")
public class Favorites extends ParseObject {

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String v){
        put("userId", v);
    }


    public String getFlyerId(){
        return getString("flyerId");
    }

    public void setFlyerId(String v){
        put("flyerId", v);
    }

    public ParseUser getFavUser() {
        return getParseUser("user");
    }

    public void setFavUser(ParseUser v) {
        put("user", v);
    }

    public boolean isitFaved(){
        return  getBoolean("isitFaved");
    }

    public void setFaved(boolean faved){
        put("isitFaved", faved);
    }

    public String getFav(){
        return getString("favorite");
    }

    public void setFav(String v){
        put("favorite", ParseObject.createWithoutData("PublicPost_Helper", v));
    }


    public static ParseQuery<Favorites> getData() {
        return ParseQuery.getQuery(Favorites.class);
    }
}
