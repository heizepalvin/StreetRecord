package com.example.heizepalvin.streetrecord;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class MusicChatActFragmentHome extends Fragment {

    private RecyclerView recyclerView;



    public MusicChatActFragmentHome(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.music_chat_fragment_first,container,false);


        Collections.sort(chatItems, new MusicChatActFragmentHome.MiniComparator());

        recyclerView = (RecyclerView) layout.findViewById(R.id.musicChatActRecycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        Log.e("채팅방몇개?",chatItems.size()+"");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        MusicChatItem item = new MusicChatItem("안녕하세요","발라드","1",date);
        chatItems.add(item);

        recyclerView.setAdapter(new MusicChatAdapter(getApplicationContext(), chatItems, R.layout.music_chat_activity));

        return layout;
    }


    private class MiniComparator implements Comparator<MusicChatItem> {

        @Override
        public int compare(MusicChatItem o1, MusicChatItem o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    }
}
