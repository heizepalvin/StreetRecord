package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;

/**
 * Created by soyounguensoo on 2017-06-28.
 */

public class WeekArtistActivityAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<WeekArtistItem> items;

    private SQLiteDatabase db;
    private int version = 1;
    private PlaylistDatabase helper;

    public WeekArtistActivityAdapter(Context context, int layout, ArrayList<WeekArtistItem> items){

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.items = items;

    }

    public class Viewholder{

        public ImageView albumImg;
        public TextView title;
        public TextView artist;
        public ImageButton play;
        public ImageButton info;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Viewholder holder;

        if(convertView == null){

            convertView = inflater.inflate(layout,parent,false);

            holder = new Viewholder();
            holder.albumImg = (ImageView) convertView.findViewById(R.id.weekActListImg);
            holder.title = (TextView) convertView.findViewById(R.id.weekActListTitle);
            holder.artist = (TextView) convertView.findViewById(R.id.weekActListArtist);
            holder.play = (ImageButton) convertView.findViewById(R.id.weekActListPlayBtn);
            holder.info = (ImageButton) convertView.findViewById(R.id.weekActListInfo);

            convertView.setTag(holder);

        } else {
           holder = (Viewholder) convertView.getTag();
        }

        Glide.with(convertView.getContext()).load(items.get(position).getAlbumImg()).override(50,50).into(holder.albumImg);
        holder.title.setText(items.get(position).getTitle());
        holder.artist.setText(items.get(position).getArtist());

        final View finalConvertView = convertView;
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){

                try{
                    helper = new PlaylistDatabase(finalConvertView.getContext(),"playlist",null,version);
                    db = helper.getWritableDatabase();
                    int count = helper.search(db,items.get(position).getTitle());
                    Log.e("테스트테스트테스트","count ? " + count);
                    if(count != 1){
                        helper.insert(db,items.get(position).getTitle(),items.get(position).getArtist(),items.get(position).getAlbumImg(),items.get(position).getMusicURL(),items.get(position).getLyrics(), items.get(position).getAlbumName(),items.get(position).getDate(),items.get(position).getGenre());

                        Log.e("weekArtistAct","title ? " + items.get(position).getTitle());
                        Log.e("weekArtistAct","artist ? " + items.get(position).getArtist());
                        Log.e("weekArtistAct","albumImg ? " + items.get(position).getAlbumImg());
                    } else {
                        helper.delete(db,items.get(position).getTitle());
                        helper.insert(db,items.get(position).getTitle(),items.get(position).getArtist(),items.get(position).getAlbumImg(),items.get(position).getMusicURL(),items.get(position).getLyrics(), items.get(position).getAlbumName(),items.get(position).getDate(),items.get(position).getGenre());

                        Toast.makeText(finalConvertView.getContext(), "중복되는 곡을 삭제하고 재생합니다.", Toast.LENGTH_SHORT).show();
                        Log.e("테스트테스트테스트","들어옴 들어옴!");
                        Log.e("weekArtistAct","title ? " + items.get(position).getTitle());
                        Log.e("weekArtistAct","artist ? " + items.get(position).getArtist());
                        Log.e("weekArtistAct","albumImg ? " + items.get(position).getAlbumImg());
                    }

                    ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>();
                    helper.select(db,"playlist",playlist);
                    GlobalApplication.getInstance().getServiceInterface().setPlaylist(playlist);
                    GlobalApplication.getInstance().getServiceInterface().play(0);

                }catch (SQLiteException e){
                    e.printStackTrace();
                    Log.e("weekArtistActDatabaseException","데이터베이스를 가져올 수 없음.");
                }
              } else {
                    Toast.makeText(finalConvertView.getContext(), "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
            }

            }

        });

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(finalConvertView.getContext(), MusicInfoActivity.class);
                intent.putExtra("title",items.get(position).getTitle());
                intent.putExtra("artist",items.get(position).getArtist());
                intent.putExtra("albumName",items.get(position).getAlbumName());
                intent.putExtra("albumImg",items.get(position).getAlbumImg());
                intent.putExtra("date",items.get(position).getDate());
                intent.putExtra("genre",items.get(position).getGenre());
                intent.putExtra("lyrics",items.get(position).getLyrics());
                finalConvertView.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
