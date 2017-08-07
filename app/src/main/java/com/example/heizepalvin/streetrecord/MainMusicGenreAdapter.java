package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-06-24.
 */

public class MainMusicGenreAdapter extends RecyclerView.Adapter<MainMusicGenreAdapter.ViewHolder> {

    Context context;
    ArrayList<MusicGenreList> items;
    int layout;


    public MainMusicGenreAdapter(Context context, ArrayList<MusicGenreList> items,int layout){
        this.context = context;
        this.items = items;
        this.layout = layout;
    }

    @Override
    public MainMusicGenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicgenre_main_item, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MainMusicGenreAdapter.ViewHolder holder, int position) {

        Glide.with(context).load(items.get(position).getImg()).override(100,100).into(holder.image);

        holder.title.setText(items.get(position).getGenre());

        holder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,MusicGenreActivity.class);
                String genre = holder.title.getText().toString();
                intent.putExtra("genre",genre);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;
        LinearLayout items;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.musicGenreImg);
            title = (TextView) itemView.findViewById(R.id.musicGenreTitle);
            items = (LinearLayout) itemView.findViewById(R.id.musicGenreItem);

        }
    }
}
