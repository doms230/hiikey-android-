package com.socialgroupe.hiikey;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.parse.ParseUser;

/**What happens when a navigation object is clicked anywhere in the application
 * Created by Marcos Bottenbley on 6/11/2015.
 */
public class DrawerItemClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        selectItem(position, view.getContext());
    }

    private void selectItem(int position, Context context) {
        switch (position) {
            case 0:
                Intent intent0 = new Intent(context, MyProfile.class);
                context.startActivity(intent0);
                break;
            case 1:
                Intent intent1 = new Intent("com.socialgroupe.SUBBULLETIN");
                context.startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(context,Bulletin.class);
                context.startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(context, SavedFlyers_Frag.class);
                context.startActivity(intent3);
                break;

            case 4:
                Intent intent4 = new Intent(context, Promotion.class);
                context.startActivity(intent4);

            case 5:
                ParseUser.logOut();

                Toast.makeText(context,
                        "Sign out Successful", Toast.LENGTH_SHORT).show();

                Intent intent6 = new Intent(context, Splash.class);
                context.startActivity(intent6);
                break;
            default:
                Log.d("Nav drawer", "null position");
        }
    }
}