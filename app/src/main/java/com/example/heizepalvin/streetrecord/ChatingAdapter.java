package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-09-07.
 */

public class ChatingAdapter extends BaseAdapter {

    private ArrayList<ChatingItem> items;
    private LayoutInflater inflater;
    private int layout;

    public ChatingAdapter (Context context, int layout, ArrayList<ChatingItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    private class ViewHolder {
        //얘는 채팅 내용
        private TextView chatData;
        //얘네는 구분선
        private View userChatLeft;
        private View userChatRight;
        //얘네는 아이템 전체 레이아웃
        private LinearLayout chatItemLayout;
        //시간
        private TextView chatItemLeftTime;
        private TextView chatItemRightTime;
        //아이디
        private TextView chatItemUserID;
        //이미지
        private ImageView chatItemImage;

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

            convertView = inflater.inflate(layout,parent,false);

            holder = new ViewHolder();
            holder.chatData = (TextView) convertView.findViewById(R.id.chatItemText);
            holder.userChatLeft = convertView.findViewById(R.id.userChatLeft);
            holder.userChatRight = convertView.findViewById(R.id.userChatRight);
            holder.chatItemLayout = (LinearLayout) convertView.findViewById(R.id.chatItemLayout);
            holder.chatItemLeftTime = (TextView) convertView.findViewById(R.id.chatItemLeftTime);
            holder.chatItemRightTime = (TextView) convertView.findViewById(R.id.chatItemRightTime);
            holder.chatItemUserID = (TextView) convertView.findViewById(R.id.chatItemUser);
            holder.chatItemImage = (ImageView) convertView.findViewById(R.id.chatItemImage);

        //이것은 받은 메시지 표시
        if(items.get(position).getType().equals("1")){

            if(items.get(position).getUserChat().contains("http://115.71.232.155")){

                holder.chatData.setVisibility(View.GONE);
                holder.chatItemUserID.setText(items.get(position).getId());
                holder.chatItemImage.setVisibility(View.VISIBLE);
                Glide.with(convertView.getContext()).load(items.get(position).getUserChat()).into(holder.chatItemImage);
                holder.chatItemRightTime.setText(items.get(position).getTime());
                holder.chatItemLayout.setGravity(Gravity.LEFT);
                holder.chatItemLeftTime.setVisibility(View.GONE);
                holder.userChatRight.setVisibility(View.GONE);
                holder.userChatLeft.setVisibility(View.GONE);

            } else {
                holder.chatData.setText(items.get(position).getUserChat());
                holder.chatItemUserID.setText(items.get(position).getId());
                holder.chatItemRightTime.setText(items.get(position).getTime());
                holder.chatData.setBackgroundResource(R.drawable.inbox2);
                holder.chatItemLayout.setGravity(Gravity.LEFT);
                holder.chatItemLeftTime.setVisibility(View.GONE);
                holder.userChatLeft.setVisibility(View.GONE);
                holder.userChatRight.setVisibility(View.GONE);
            }

        }
        //이것은 내가 보낸 메시지 표시
        else if(items.get(position).getType().equals("2")){

            if(items.get(position).getUserChat().contains("http://115.71.232.155")){

                holder.chatData.setVisibility(View.GONE);
                holder.chatItemImage.setVisibility(View.VISIBLE);
                Glide.with(convertView.getContext()).load(items.get(position).getUserChat()).into(holder.chatItemImage);
                holder.chatItemUserID.setVisibility(View.GONE);
                holder.chatItemLeftTime.setText(items.get(position).getTime());
                holder.chatItemLayout.setGravity(Gravity.RIGHT);
                holder.chatItemRightTime.setVisibility(View.GONE);
                holder.userChatLeft.setVisibility(View.GONE);
                holder.userChatRight.setVisibility(View.GONE);

            } else {
                holder.chatData.setText(items.get(position).getUserChat());
                holder.chatItemUserID.setVisibility(View.GONE);
                holder.chatItemLeftTime.setText(items.get(position).getTime());
                holder.chatData.setBackgroundResource(R.drawable.outbox2);
                holder.chatItemLayout.setGravity(Gravity.RIGHT);
                holder.chatItemRightTime.setVisibility(View.GONE);
                holder.userChatLeft.setVisibility(View.GONE);
                holder.userChatRight.setVisibility(View.GONE);
            }

        }

        //이것은 들어오거나 나갔을 때 표시
        else if(items.get(position).getType().equals("3")){

            holder.chatData.setText(items.get(position).getUserChat());
            holder.chatData.setGravity(Gravity.CENTER);
            holder.userChatLeft.setVisibility(View.VISIBLE);
            holder.userChatRight.setVisibility(View.VISIBLE);

            holder.chatItemUserID.setVisibility(View.GONE);
            holder.chatItemLeftTime.setVisibility(View.GONE);
            holder.chatItemRightTime.setVisibility(View.GONE);

        }


        return  convertView;
    }
}
