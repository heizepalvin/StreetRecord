package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-06-21.
 */

public class SituationMusicAdapter extends BaseAdapter {

    private ArrayList<SituationMusicItem> items;
    private LayoutInflater inflater;
    private int layout;

    public SituationMusicAdapter(Context context, int layout, ArrayList<SituationMusicItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.items = items;
    }

    public class ViewHolder {
        public ImageView situationListImg;
        public TextView situationListTitle;
        public TextView situationListGenre;
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
            convertView = inflater.inflate(layout,parent, false);

            holder = new ViewHolder();
            holder.situationListImg = (ImageView) convertView.findViewById(R.id.situationListImg);
            holder.situationListTitle = (TextView) convertView.findViewById(R.id.situationListTitle);
            holder.situationListGenre = (TextView) convertView.findViewById(R.id.situationListGenre);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.situationListImg.setImageResource(items.get(position).getImage());
        Glide.with(convertView.getContext()).load(items.get(position).getMainImg()).override(100,100).into(holder.situationListImg);
        holder.situationListTitle.setText(items.get(position).getTitle());
        holder.situationListGenre.setText(items.get(position).getGenre());


        return convertView;
    }
}
