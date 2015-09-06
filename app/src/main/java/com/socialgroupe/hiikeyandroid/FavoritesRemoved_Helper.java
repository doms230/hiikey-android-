package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/26/2015.
 */
@ParseClassName("FavoritesRemoved")
public class FavoritesRemoved_Helper extends ParseObject {

    public void setUserId(String v){
        put("userId", v);
    }

    public void setFlyerId(String v){
        put("flyerId", v);
    }

    public void setFavUser(ParseUser v) {
        put("user", v);
    }

    public static ParseQuery<FavoritesRemoved_Helper> getData() {
        return ParseQuery.getQuery(FavoritesRemoved_Helper.class);
    }
}
