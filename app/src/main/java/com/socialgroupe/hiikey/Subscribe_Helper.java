package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 4/20/2015.
 * Helper class to subscrive people to Bulletins.
 */
@ParseClassName("Subscribe")
public class Subscribe_Helper extends ParseObject {

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String v){
        put("userId", v);

    }
/*****************************************/
    public ParseUser getBulletinSubscriber() {
        return getParseUser("user");
    }

    public void setBulletinSubscriber(ParseUser v) {
        put("user", v);
    }

    /************************************/

    public String getBulletinId(){
        return getString("bulletinId");
    }

    public void setBulletinId(String v){
        put("bulletinId", v);
    }

    /***************************************/

    public String getBulletinTitle(){
        return getString("bulletinTitle");
    }

    public void setBulletinTitle(String bullTitle){
        put("bulletinTitle", bullTitle);
    }
/*********************************************/
    public static ParseQuery<Subscribe_Helper> getData() {
        return ParseQuery.getQuery(Subscribe_Helper.class);
    }

}
