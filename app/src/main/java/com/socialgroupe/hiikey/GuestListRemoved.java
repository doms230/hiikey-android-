package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/27/2015.
 */
@ParseClassName("ListRemoved")
public class GuestListRemoved extends ParseObject {

    public GuestListRemoved(){}

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

    public static ParseQuery<GuestListRemoved> getList(){
        return ParseQuery.getQuery(GuestListRemoved.class);
    }
}
