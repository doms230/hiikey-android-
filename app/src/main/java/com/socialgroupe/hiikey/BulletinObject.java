package com.socialgroupe.hiikey;

import com.parse.ParseFile;

/**
 * Created by Marcos on 6/17/2015.
 */
public class BulletinObject {
    private ParseFile pic;
    private String name;
    private String creator;
    private long id;

    public BulletinObject(ParseFile p, String bname, String cname, long id) {
        pic = p;
        name = bname;
        creator = cname;
        this.id = id;
    }

    public BulletinObject(){

    }

    public String getName(){
        return name;
    }

    public String getCreator(){
        return creator;
    }

    public void setName(String bname) {
        name = bname;
    }

    public void setCreator(String cname) {
        creator = cname;
    }

    public void setPic(ParseFile bp) {
        pic = bp;
    }

    public ParseFile getPic() {
        return pic;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
