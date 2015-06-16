package com.socialgroupe.hiikey;

import com.parse.ParseFile;

/**
 * Created by lemonie on 6/15/15.
 */
public class AllFlyers {
    private String Category;
    private String userId;
    private ParseFile Flyer;

    public String getCategory(){
        return Category;
    }

    public void setCategory(String Category){
        this.Category=Category;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return userId;
    }

    public void setFlyer(ParseFile Flyer){
        this.Flyer = Flyer;
    }

    public ParseFile getFlyer(){
        return Flyer;
    }
}
