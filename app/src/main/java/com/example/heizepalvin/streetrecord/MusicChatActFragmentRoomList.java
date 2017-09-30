package com.example.heizepalvin.streetrecord;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class MusicChatActFragmentRoomList extends Fragment {

    private ChattingDatabase dbHelper;
    private SQLiteDatabase db;

    //채팅방 리스트뷰
    private ListView myChatRoomList;
    public static MusicChatActRoomListAdapter fragmentRoomListAdapter;
    public static ArrayList<MusicChatActRoomListItem> fragmentRoomListItems;

    public MusicChatActFragmentRoomList(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.music_chat_fragment_second,container,false);

        dbHelper = new ChattingDatabase(getContext(),"chattingDB",null,1);
        db = dbHelper.getWritableDatabase();
        fragmentRoomListItems = new ArrayList<>();
        dbHelper.select(db,"chatRoom",fragmentRoomListItems);
        myChatRoomList = (ListView) layout.findViewById(R.id.musicChatActRoomList);
        fragmentRoomListAdapter = new MusicChatActRoomListAdapter(getContext(),R.layout.music_chat_my_room_list,fragmentRoomListItems);
        myChatRoomList.setAdapter(fragmentRoomListAdapter);

        myChatRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent intent = new Intent(getActivity(),ChatingActivity.class);
                intent.putExtra("title",fragmentRoomListItems.get(position).getTitle());
                intent.putExtra("token",fragmentRoomListItems.get(position).getToken());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentRoomListAdapter.notifyDataSetChanged();
    }
}
