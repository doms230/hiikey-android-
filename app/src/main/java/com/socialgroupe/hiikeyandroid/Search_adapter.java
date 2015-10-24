package com.socialgroupe.hiikeyandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaning on 9/26/15.
 */
public class Search_adapter extends BaseAdapter {

    LayoutInflater inflater;
    Context mContext;
    List<Bulletin_Helper> bulletins_search = null;
    ArrayList<Bulletin_Helper> array_list;

    public Search_adapter(Context context, List<Bulletin_Helper> bulletins_search){
        mContext = context;
        this.bulletins_search = bulletins_search;
        inflater = LayoutInflater.from(mContext);
        this.array_list = new ArrayList<Bulletin_Helper>();
        this.array_list.addAll(bulletins_search);
    }

    public class ViewHolder{
        TextView bulletin_name;
        ParseImageView bulletin_image;
    }

    @Override
    public int getCount(){
        return bulletins_search.size();
    }

    @Override
    public Bulletin_Helper getItem(int position){
        return bulletins_search.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent){
        final ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.search_listview_item, null);

            // Locate the TextViews and ParseImageView
            //holder.bulletin_name = (TextView) view.findViewById(R.id.tvtry);
            holder.bulletin_image = (ParseImageView) view.findViewById(R.id.bulletinpic);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        //holder.bulletin_name.setText(bulletins_search.get(position).getBulletinName());
        holder.bulletin_image.setParseFile(bulletins_search.get(position).getBulletinPic());
        holder.bulletin_image.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {

            }
        });
        return view;
    }


}
