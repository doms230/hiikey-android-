package com.socialgroupe.hiikey;

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

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Yaning on 6/14/15.
 * Edited by Marcos
 */
public class Customlv_Adapter extends BaseAdapter implements StickyListHeadersAdapter{

    // Declare Variables
    LayoutInflater inflater;
    Context mContext;
    private List<FlyerObject> flyers = null;
    private ArrayList<FlyerObject> arraylist;


    public Customlv_Adapter(Context context, List<FlyerObject> flyers){
        mContext = context;
        this.flyers = flyers;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<FlyerObject>();
        this.arraylist.addAll(flyers);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
        HeaderViewHolder holder;

        if(convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.activity_row_bulletin, viewGroup, false);
            holder.bullName = (TextView) convertView.findViewById(R.id.tvBulletinName);
            //holder.userId = (TextView) convertView.findViewById(R.id.tvCreatorName);
            holder.bulletin = (ParseImageView) convertView.findViewById(R.id.ivBulletin);
            //holder.subscribe = (Button) convertView.findViewById(R.id.subButton);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        BulletinObject bo = flyers.get(position).getBulletin();

        String headerText = bo.getName();
        String nameText = bo.getCreator();

        holder.bulletin.setParseFile(bo.getPic());
        holder.bulletin.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
            }
        });
        // ************ new *********************



        // ************ new end *****************

        holder.bullName.setText(headerText);
        //holder.userId.setText(nameText);
        /*
        holder.subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Subscribed",Toast.LENGTH_LONG).show();
            }
        });
        */

        return convertView;
    }

    @Override
    public long getHeaderId(int i) {
        return flyers.get(i).getBullId();
    }

    private class ViewHolder{
        ParseImageView Flyer;
    }

    private class HeaderViewHolder {
        ParseImageView bulletin;
        TextView bullName;
        //TextView userId;
        //Button subscribe;
    }

    @Override
    public int getCount(){
        return flyers.size();
    }

    @Override
    public FlyerObject getItem(int position){
        return flyers.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_hiikey, null);

            // Locate the TextViews and ParseImageView in row_hiikey.xml
            holder.Flyer=(ParseImageView) view.findViewById(R.id.ivflyermain);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        // The placeholder will be used before and during the fetch, to be replaced by
        // the fetched image data
        //holder.Flyer.setPlaceholder(getResources().getDrawable(R.drawable.placeholder()));
        holder.Flyer.setParseFile(flyers.get(position).getFlyer());
        holder.Flyer.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
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
