package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-09-15.
 */

public class MusicChatActRoomListAdapter extends BaseAdapter {

    private ArrayList<MusicChatActRoomListItem> items;
    private int layout;
    private LayoutInflater inflater;

    public MusicChatActRoomListAdapter(Context context, int layout, ArrayList<MusicChatActRoomListItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    private class ViewHolder {
        private TextView title;
        private ImageView image;
        private TextView time;
        private TextView lastMessage;
        private TextView count;
        private FrameLayout framelayout;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(layout,parent,false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.musicChatActMyRoomTitle);
            holder.image = (ImageView) convertView.findViewById(R.id.musicChatActMyRoomImg);
            holder.time = (TextView) convertView.findViewById(R.id.musicChatActMyRoomTime);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.musicChatActMyRoomMsg);
            holder.count = (TextView) convertView.findViewById(R.id.musicChatActMyRoomCount);
            holder.framelayout = (FrameLayout) convertView.findViewById(R.id.musicChatActMyRoomFrameLayout);

            convertView.setTag(holder);
        } else{
            holder= (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        Glide.with(convertView.getContext()).load(items.get(position).getImage()).into(holder.image);
        if(items.get(position).getImage().equals("null")){
            Glide.with(convertView.getContext()).load(R.drawable.logoface).into(holder.image);
        }
        Log.e("뭔데여기",items.get(position).getImage());
        holder.time.setText(items.get(position).getTime());
        holder.lastMessage.setText(items.get(position).getLastMessage());
        holder.count.setText(String.valueOf(items.get(position).getCount()));
        if(items.get(position).getCount() == 0){
            holder.framelayout.setVisibility(View.INVISIBLE);
        } else {
            holder.framelayout.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
