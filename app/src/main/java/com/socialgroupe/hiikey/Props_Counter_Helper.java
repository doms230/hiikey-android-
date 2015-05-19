package com.socialgroupe.hiikey;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Dominic on 4/14/2015.
 * Helper class to keep track of props
 */
@ParseClassName("PropsCounter")
public class Props_Counter_Helper extends ParseObject{
    public Props_Counter_Helper(){}


    /**
     * host Id
     */

    public String getHostId(){return getString("hostId");}

    public void setHostId(String hostId){put("hostId", hostId);}

    /**
     * guest Id
     */

    public String getGuestId(){return getString("guestId");}

    public void setGuestId(String guestId){put("guestId", guestId);}

    /**
     * flyerId
     */
    public String getFlyerId(){return getString("flyerId");}

    public void setFlyerId(String flyerId){put("flyerId", flyerId);}

    /**
     * thumbs up or thumbs down
     */

    public String getThumb(){return getString("thumb");}

    public void setThumb(String thumb){put("thumb", thumb);}

    /**
     * Query stuff
     */

    public static ParseQuery<Props_Counter_Helper> getQuery() {
        return ParseQuery.getQuery(Props_Counter_Helper.class);
    }
}
