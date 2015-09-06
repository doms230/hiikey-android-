package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 4/15/2015.
 * Helper class to load the bulletins
 */
@ParseClassName("Bulletin")
public class Bulletin_Helper extends ParseObject {
    public Bulletin_Helper(){}

    /**user Jaunt**/

    public void setUser(ParseUser user){
        put("user", user);
    }

    /**userId***/

    public void setUserId(String userId){
        put("userId", userId);
    }

    public String getUserId(){
        return getString("userId");
    }

    /**Category of public post**/
    public void setCategory(String category){put("Category",category);}
    public String getCategory(){return getString("Category");}

    /** Flyer Picture **/
    public void setFlyerPic(ParseFile flyerPic){put("Flyer", flyerPic);}
    public ParseFile getFlyerPic(){return getParseFile("Flyer");}


    /***Bulletin Picture ****/

    public void setBulletinPic(ParseFile bulletinPic){
        put("bulletinPic", bulletinPic);
    }

    public ParseFile getBulletinPic(){
        return getParseFile("bulletinPic");
    }

    /****Bulletin Name****/

    public void setBulletinName(String bulletinName){
        put("bulletinName", bulletinName);
    }

    public String getBulletinName(){
        return getString("bulletinName");
    }

    /******Bulletin Location********/

    public void setBulletinLocation(ParseGeoPoint location){
        put("bulletinLocation", location);
    }

    public ParseGeoPoint getBulletinLocation(){
        return getParseGeoPoint("bulletinLocation");
    }

    /******Bulletin Address******/

    public void setBulletinAddress(String address){
        put("bulletinAddress", address);
    }

    public String getBulletinAdress(){
        return getString("bulletinAddress");
    }

    /*********Bulletin Open/close setting************************/

    public void setBulletinSetting(String setting){
        put("setting", setting);
    }

    public String getBulletinSetting(){
        return getString("setting");
    }

    /***********Bulletin show/hide title/name setting*****************************/
    public void setShowHideSetting(String showHideSetting){
        put("showhide", showHideSetting);
    }

    public String getShowHideSetting(){
        return getString("showhide");
    }

    /*********Query Listener********/

    public static ParseQuery<Bulletin_Helper> getQuery() {
        return ParseQuery.getQuery(Bulletin_Helper.class);
    }

}
