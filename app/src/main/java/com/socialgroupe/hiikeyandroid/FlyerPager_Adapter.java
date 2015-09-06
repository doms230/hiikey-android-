package com.socialgroupe.hiikeyandroid;

import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Dominic on 8/15/15.
 */
public class FlyerPager_Adapter extends FragmentStatePagerAdapter {

        private ArrayList<String> flyerFile = new ArrayList<>();
        private ArrayList<String> bulletinNameList = new ArrayList<>();
        private ArrayList<String> flyerIdList = new ArrayList<>();
        private ArrayList<String> flyerHashtagList = new ArrayList<>();
        private int NUM_ITEMS;

        public FlyerPager_Adapter(android.app.FragmentManager fm){
            super(fm);
        }

        public void setFlyerFile(ArrayList<String> file){
            flyerFile = file;
        }

        public void setBulletinName(ArrayList<String> bulletinName){
            bulletinNameList = bulletinName;
        }

        public void setFlyerId(ArrayList<String> flyerId){
            flyerIdList = flyerId;
        }

        public void setFlyerHashtag(ArrayList<String> flyerHashtag){
            flyerHashtagList = flyerHashtag;
        }

        public void setNUM_ITEMS(int num_items){
            NUM_ITEMS = num_items;
        }

        @Override
        public Fragment getItem(int position) {
            return Home_fragment.newInstance(position,
                    flyerFile, bulletinNameList, flyerIdList, flyerHashtagList) ;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
