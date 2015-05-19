package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/26/2015.
 */
@ParseClassName("FavoritesRemoved")
public class FavoritesRemoved extends ParseObject {

    public void setUserId(String v){
        put("userId", v);
    }

    public void setFlyerId(String v){
        put("flyerId", v);
    }

    public void setFavUser(ParseUser v) {
        put("user", v);
    }

    public static ParseQuery<FavoritesRemoved> getData() {
        return ParseQuery.getQuery(FavoritesRemoved.class);
    }
}
