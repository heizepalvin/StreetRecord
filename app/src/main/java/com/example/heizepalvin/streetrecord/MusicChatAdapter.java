package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by soyounguensoo on 2017-08-22.
 */

public class MusicChatAdapter extends RecyclerView.Adapter<MusicChatAdapter.ViewHolder> {

    Context context;
    ArrayList<MusicChatItem> items;
    int item_layout;
    private ChattingDatabase dbHelper;
    private SQLiteDatabase db;

    public MusicChatAdapter(Context context, ArrayList<MusicChatItem> items, int item_layout){
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_chat_cardview,null);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final MusicChatItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(holder.itemView.getContext());

                alertBuilder.setTitle("오픈채팅방 참여");
                alertBuilder.setMessage("채팅방에 참여하시겠습니까?").setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNegativeButton("참여", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //사용자 로컬 데이터베이스에 저장하기

                        dbHelper = new ChattingDatabase(context,"chattingDB",null,1);
                        db = dbHelper.getWritableDatabase();
//                        dbHelper.onCreate(db);
//                        db.execSQL("drop table chatRoom;");
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat curtimeFormat = new SimpleDateFormat("a hh:mm");
                        String time = curtimeFormat.format(date);
                        SimpleDateFormat datePlusTime = new SimpleDateFormat("yy/MM/dd a hh:mm:ss");
                        String datePlusTimes = datePlusTime.format(date);
                        dbHelper.insert(db,item.getTitle(),"",item.getImage(),time,item.getNum(),datePlusTimes);

                        //사용자 로컬 데이터베이스에 저장하기 끝

                        Intent intent = new Intent(context,ChatingActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("title",item.getTitle());
                        intent.putExtra("token",item.getNum());
                        context.startActivity(intent);
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();

            }
        });

        String calTime = calculateTime(item.getTime());
        holder.time.setText(calTime);
        holder.genre.setText(item.getGenre());
        holder.memberNumber.setText(item.getMemberCount() + "");
        Glide.with(context).load(item.getImage()).into(holder.chatRoomImage);
        if(item.getImage() ==null ){
            Glide.with(context).load(R.drawable.logoface).into(holder.chatRoomImage);
        }


    }





    @Override
    public int getItemCount() {

           return items.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView genre;
        TextView memberNumber;
        TextView time;
        CardView cardView;
        ImageView chatRoomImage;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.musicChatActTitle);
            genre = (TextView) itemView.findViewById(R.id.musicChatActGenre);
            memberNumber = (TextView) itemView.findViewById(R.id.musicChatActNum);
            time = (TextView) itemView.findViewById(R.id.musicChatActTime);
            cardView = (CardView) itemView.findViewById(R.id.musicChatActCardView);
            chatRoomImage = (ImageView) itemView.findViewById(R.id.musicChatActImage);
        }

    }

    private static class TIME_MAXIMUM
    {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public String calculateTime(Date date)
    {

        long curTime = System.currentTimeMillis();
        long regTime = date.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC)
        {
            // sec
//            msg = diffTime + "초전";
            msg = "방금 전";
        }
        else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN)
        {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분전";
        }
        else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR)
        {
            // hour
            msg = (diffTime ) + "시간전";
        }
//        else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY)
//        {
//            // day
//            msg = (diffTime ) + "일전";
//        }
//        else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH)
//        {
//            // day
//            msg = (diffTime ) + "달전";
//        }
        else
        {
//            msg = (diffTime) + "년전";
            SimpleDateFormat formatNow = new SimpleDateFormat("yy/MM/dd");
            String now = formatNow.format(date);
            msg = now;
        }

        return msg;
    }


}
