package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/27/2015.
 */
@ParseClassName("ListRemoved")
public class GuestListRemoved_Helper extends ParseObject {

    public GuestListRemoved_Helper(){}

    public void setFromUser(ParseUser user){
        put("from", user);
    }

    public void setToUser(ParseUser user){
        put("to", user);
    }


    public String getDate(){
        return getString("Date");
    }

    public void setDate(String date){
        put("Date", date);
    }

    public static ParseQuery<GuestListRemoved_Helper> getList(){
        return ParseQuery.getQuery(GuestListRemoved_Helper.class);
    }
}
