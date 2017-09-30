package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-09-14.
 */

public class ChatActNavigationDrawerAdapter  extends BaseAdapter{

    private ArrayList<NavigationDrawerItem> items;
    private LayoutInflater inflater;
    private int layout;

    public ChatActNavigationDrawerAdapter(Context context, int layout, ArrayList<NavigationDrawerItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    private class ViewHolder{
        private TextView userID;
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
            holder.userID = (TextView) convertView.findViewById(R.id.chatActSideUser);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userID.setText(items.get(position).getUser());


        return convertView;
    }
}
