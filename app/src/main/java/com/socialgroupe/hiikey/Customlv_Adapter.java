package com.socialgroupe.hiikey;

import android.content.Context;
import android.util.Log;
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
 * Created by lemonie on 6/14/15.
 */
public class Customlv_Adapter extends BaseAdapter {

    // Declare Variables
    LayoutInflater inflater;
    Context mContext;
    private List<AllFlyers> flyers = null;
    private ArrayList<AllFlyers> arraylist;


    public Customlv_Adapter(Context context, List<AllFlyers> flyers){
        mContext = context;
        this.flyers = flyers;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<AllFlyers>();
        this.arraylist.addAll(flyers);
    }

    public class ViewHolder{
        TextView Category;
        TextView userId;
        ParseImageView Flyer;
    }

    @Override
    public int getCount(){
        return flyers.size();
    }

    @Override
    public AllFlyers getItem(int position){
        return flyers.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_hiikey, null);

            // Locate the TextViews and ParseImageView in row_hiikey.xml
            holder.Category = (TextView) view.findViewById(R.id.textView1);
            holder.userId = (TextView) view.findViewById(R.id.textView2);
            holder.Flyer=(ParseImageView) view.findViewById(R.id.imageView2);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set the results into TextViews
        holder.Category.setText(flyers.get(position).getCategory());
        holder.userId.setText(flyers.get(position).getUserId());

        // The placeholder will be used before and during the fetch, to be replaced by
        // the fetched image data
        //holder.Flyer.setPlaceholder(getResources().getDrawable(R.drawable.placeholder()));
        holder.Flyer.setParseFile(flyers.get(position).getFlyer());
        holder.Flyer.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Log.v("LOG!!!!","Log!!");
            }
        });

        //Listen for ListView Item Click
        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Send single item click data to SignleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                // Pass all data rank

            }
        });
        */
        return view;
    }
}
