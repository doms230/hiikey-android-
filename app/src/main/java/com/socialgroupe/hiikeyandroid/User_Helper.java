package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Yaning on 9/26/15.
 */

@ParseClassName("User")
public class User_Helper extends ParseObject {

    public User_Helper(){

    }

    public void setUsername(String username){
        put("username", username);
    }

    public String getUsername(){
        return getString("username");
    }

    public void setUserPic(ParseFile file){
        put("Profile", file);
    }

    public ParseFile getUserPic(){
        return getParseFile("Profile");
    }

    // Query Listener
    public static ParseQuery<User_Helper> getQuery(){
        return ParseQuery.getQuery(User_Helper.class);
    }

}
