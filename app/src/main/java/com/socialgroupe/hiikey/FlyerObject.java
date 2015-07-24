package com.socialgroupe.hiikey;

import com.parse.ParseFile;

/**
 * Created by lemonie on 6/15/15.
 */
public class FlyerObject {
    private String Category;
    private long bullId;
    private ParseFile Flyer;
    private BulletinObject bulletin;
    private boolean liked = false;

    public String getCategory(){return Category;}

    public void setCategory(String Category){
        this.Category=Category;
    }

    public void setBullId(long bId){
        this.bullId = bId;
    }

    public long getBullId(){
        return bullId;
    }

    public void setFlyer(ParseFile Flyer){
        this.Flyer = Flyer;
    }

    public ParseFile getFlyer(){
        return Flyer;
    }

    public void setBulletin(BulletinObject bo) {
        bulletin = bo;
    }

    public BulletinObject getBulletin() {
        return bulletin;
    }

    public void toggleLiked(){
        liked = !liked;
    }

    public boolean getLiked() {
        return liked;
    }

}
