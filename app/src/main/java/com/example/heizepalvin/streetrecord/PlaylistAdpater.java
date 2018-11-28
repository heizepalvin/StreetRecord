package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.heizepalvin.streetrecord.PlaylistActivity.insertMode;
import static com.example.heizepalvin.streetrecord.PlaylistActivity.seleteList;

/**
 * Created by soyounguensoo on 2017-07-18.
 */

public class PlaylistAdpater extends RecyclerView.Adapter<PlaylistViewHolder> implements PlaylistItemTouchHelperCallback.OnItemMoveListener, PlaylistItemTouchHelperCallback.OnListItemClickListener{

    Context context;
    ArrayList<PlaylistItem> items;
    int layout;

    //드래그 데이터베이스 업데이트

    PlaylistDatabase helper;
    SQLiteDatabase db;

    @Override
    public void onListItemClick(View itemView,int position) {
        if(insertMode) {
            if (seleteList.size() == 0) {
                String addPosition = String.valueOf(position);
                seleteList.add(addPosition);
                itemView.setBackgroundColor(Color.rgb(204, 204, 153));
            } else {
                String selectPosition = String.valueOf(position);
                boolean select = seleteList.contains(selectPosition);
                if (select) {
                    itemView.setBackgroundColor(Color.WHITE);
                    String removeSelect = String.valueOf(position);
                    seleteList.remove(removeSelect);
                } else {
                    String addPosition = String.valueOf(position);
                    seleteList.add(addPosition);
                    itemView.setBackgroundColor(Color.rgb(204, 204, 153));
                }
            }
        }
    }


    public interface OnStartDragListener{
        void onStartDrag(PlaylistViewHolder holder);
    }

    private final OnStartDragListener mStartDragListener;



    public PlaylistAdpater(Context context, ArrayList<PlaylistItem> items, int layout, OnStartDragListener listener){
        this.context = context;
        this.items = items;
        this.layout = layout;
        this.mStartDragListener = listener;
    }




    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_activity_item,null);
        PlaylistViewHolder holder = new PlaylistViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            if(GlobalApplication.getInstance().getServiceInterface().getCurrentPosition() == position){
                holder.playing.setImageResource(R.drawable.playing);
                String textColor = "#8041D9";
                holder.title.setTextColor(Color.parseColor(textColor));
                holder.title.setTextSize(20);
                holder.artist.setTextColor(Color.parseColor(textColor));
            } else {
                holder.playing.setImageResource(0);
                holder.title.setTextColor(Color.BLACK);
                holder.title.setTextSize(18);
                String textColor = "#8C8C8C";
                holder.artist.setTextColor(Color.parseColor(textColor));
            }
        } else {
            holder.playing.setImageResource(0);
            holder.title.setTextColor(Color.BLACK);
            holder.title.setTextSize(18);
            String textColor = "#8c8c8c";
            holder.artist.setTextColor(Color.parseColor(textColor));
        }

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalApplication.getInstance().getServiceInterface().setPlaylist(items);
                GlobalApplication.getInstance().getServiceInterface().play(position);
            }
        });

        holder.playlistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalApplication.getInstance().getServiceInterface().setPlaylist(items);
                GlobalApplication.getInstance().getServiceInterface().play(position);
            }
        });

        holder.title.setText(items.get(position).getTitle());
        holder.artist.setText(items.get(position).getArtist());
        holder.reorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){
                    mStartDragListener.onStartDrag(holder);
                    Log.e("onTouch","onTouch");
                }
                return false;
            }
        });


        Glide.with(context).load(items.get(position).getImgPath()).override(100,100).into(holder.albumImg);
        if(insertMode) {
            if (!seleteList.isEmpty()) {
                for (int i = 0; i < seleteList.size(); i++) {
                    String select = seleteList.get(i);
                    if (select.equals(String.valueOf(position))) {
                        holder.playlistItem.setBackgroundColor(Color.rgb(204, 204, 153));
                        break;
                    } else {
                        holder.playlistItem.setBackgroundColor(Color.WHITE);
                    }
                }
            } else {
                holder.playlistItem.setBackgroundColor(Color.WHITE);
            }
        } else {
            holder.playlistItem.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition,toPosition);
        helper = new PlaylistDatabase(context,"playlist",null,1);
        db  = helper.getWritableDatabase();
        helper.update(db,items,fromPosition,toPosition);
        ArrayList<PlaylistItem> item = new ArrayList<>();
        helper.select(db,"playlist",item);
        GlobalApplication.getInstance().getServiceInterface().setPlaylist(item,toPosition);

    }




}
