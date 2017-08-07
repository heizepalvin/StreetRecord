package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;

/**
 * Created by soyounguensoo on 2017-06-23.
 */

public class NewMusicRecyclerAdapter extends RecyclerView.Adapter<NewMusicRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<NewMusicList> items;
    int item_layout;

    private SQLiteDatabase db;
    private int version = 1;
    private PlaylistDatabase helper;

    public NewMusicRecyclerAdapter(Context context, ArrayList<NewMusicList> items, int item_layout){
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newmusic_main_item,null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(context).load(items.get(position).getAlbumImg()).override(100,100).into(holder.image);
        holder.title.setText(items.get(position).getTitle());
        holder.aritst.setText(items.get(position).getArtist());

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    try{
                        helper = new PlaylistDatabase(holder.itemView.getContext(),"playlist",null,version);
                        db = helper.getWritableDatabase();
                        int count = helper.search(db,items.get(position).getTitle());
                        Log.e("테스트테스트테스트","count ? " + count);
                        if(count != 1){
                            helper.insert(db,items.get(position).getTitle(),items.get(position).getArtist(),items.get(position).getAlbumImg(),items.get(position).getMusicURL(),items.get(position).getLyrics(), items.get(position).getAlbumName(),items.get(position).getDate(),items.get(position).getGenre());

                            Log.e("mainNewMusic","title ? " + items.get(position).getTitle());
                            Log.e("mainNewMusic","artist ? " + items.get(position).getArtist());
                            Log.e("mainNewMusic","albumImg ? " + items.get(position).getAlbumImg());
                        } else {
                            helper.delete(db,items.get(position).getTitle());
                            helper.insert(db,items.get(position).getTitle(),items.get(position).getArtist(),items.get(position).getAlbumImg(),items.get(position).getMusicURL(),items.get(position).getLyrics(), items.get(position).getAlbumName(),items.get(position).getDate(),items.get(position).getGenre());

                            Toast.makeText(holder.itemView.getContext(), "중복되는 곡을 삭제하고 재생합니다.", Toast.LENGTH_SHORT).show();
                            Log.e("테스트테스트테스트","들어옴 들어옴!");
                            Log.e("mainNewMusic","title ? " + items.get(position).getTitle());
                            Log.e("mainNewMusic","artist ? " + items.get(position).getArtist());
                            Log.e("mainNewMusic","albumImg ? " + items.get(position).getAlbumImg());

                        }

                        ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>();
                        helper.select(db,"playlist",playlist);
                        GlobalApplication.getInstance().getServiceInterface().setPlaylist(playlist);
                        GlobalApplication.getInstance().getServiceInterface().play(0);


                    }catch (SQLiteException e){
                        e.printStackTrace();
                        Log.e("mainNewMusicDatabaseException","데이터베이스를 가져올 수 없음.");
                    }
                } else {
                    Toast.makeText(context, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;
        TextView aritst;
        ImageButton play;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.newMusicImgItem);
            title = (TextView) itemView.findViewById(R.id.newMusicTitleItem);
            aritst = (TextView) itemView.findViewById(R.id.newMusicArtistItem);
            play = (ImageButton) itemView.findViewById(R.id.newMusicPlayBtn);
        }
    }
}
