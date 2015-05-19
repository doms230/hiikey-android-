package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 4/4/2015.
 * Helper class for Parse Class Props
 */
@ParseClassName("Props")
public class Props_Helper extends ParseObject {
    public Props_Helper(){}

    /**
     * User
     */

    public ParseUser getUser(){
        return getParseUser("user");}

    public void setUser(ParseUser user){
        put("user", user);
    }

    /**
     * User's Props count
     */

    public int getProps(){return getInt("propCount");}

    public void setProps(int props){put("propCount", props);}


    /**
     * Is the User Verified
     */

    public boolean getVerification(){return getBoolean("isVerified");}

    public void setVerification(boolean verify){put("isVerified", verify);}

    /**
     *
     * set and get userId
     */

    public String getUserId(){return getString("userId");}

    public void setUserId(String userId){put("userId", userId);}

    public static ParseQuery<Props_Helper> getPropList() {
        return ParseQuery.getQuery(Props_Helper.class);
    }
}
