package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-06-20.
 */

public class MenuAdapter extends BaseAdapter {

    private ArrayList<MenuItem> items;
    private LayoutInflater inflater;
    private int layout;

    public MenuAdapter(Context context, int layout, ArrayList<MenuItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    public class ViewHolder {
        public TextView menuTitle;
        public ImageView menuImg;
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
            convertView = inflater.inflate(layout, parent, false);

            holder = new ViewHolder();
            holder.menuTitle = (TextView) convertView.findViewById(R.id.menuTitle);
            holder.menuImg = (ImageView) convertView.findViewById(R.id.menuImg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.menuTitle.setText(items.get(position).getTitle());
        holder.menuImg.setImageResource(items.get(position).getImage());

        return convertView;
    }
}
