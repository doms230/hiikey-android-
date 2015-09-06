package com.socialgroupe.hiikeyandroid;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Dominic on 4/20/2015.
 */

    @ParseClassName("Subscribe_remove")
    public class Subscribe_Helper_delete extends ParseObject {

        /*****************************************/

        public void setBulletinSubscriber(ParseUser v) {
            put("user", v);
        }

        /************************************/

        public void setBulletinId(String v){
            put("bulletinId", v);
        }

        /***************************************/

        public void setBulletinTitle(String bullTitle){
            put("bulletinTitle", bullTitle);
        }
        /*********************************************/
}
