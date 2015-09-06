package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 1/27/2015.
 *
 * Helper Class: Methods for adding users to guest lists.
 * see https://parse.com/docs/relations_guide "Joining tables" for logic.
 */
@ParseClassName("List")
public class GuestList_Helper extends ParseObject {

    public GuestList_Helper(){}

//////////////////The Current User is adding..///////////////////

    public void setHost(ParseUser user){
        put("host", user);
    }

////////////////This user to their GuestList_Helper//////////////////////
    public ParseUser getGuest(){
        return getParseUser("guest");
    }

    public void setGuest(ParseUser user){
        put("guest", user);
    }

//////////////////On this date////////////////////////////////////
    public String getDate(){
        return getString("Date");
    }

    public void setDate(String date){
        put("Date", date);
    }

    public String getHostId(){
        return getString("hostId");
    }

    public void setHostId(String hostId){
        put("hostId", hostId);
    }

    public String getGuestId(){
        return getString("guestId");
    }

    public void setGuestId(String guestId){
        put("guestId", guestId);
    }

    ////////////////Query method to get Data////////////////////////////////
    public static ParseQuery<GuestList_Helper> getList(){
        return ParseQuery.getQuery(GuestList_Helper.class);
    }
}
