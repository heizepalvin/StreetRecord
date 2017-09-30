package com.example.heizepalvin.streetrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by soyounguensoo on 2017-08-22.
 */

public class MusicChatActivity extends AppCompatActivity {

    private LinearLayout createChatRoom;
    static ArrayList<MusicChatItem> chatItems = new ArrayList<>();;
    private BottomNavigationView navigationView;
    public static ViewPager vp;

    private MenuItem prevBottomNavigation;

    private pagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_chat_activity);



        createChatRoom = (LinearLayout) findViewById(R.id.musicChatActCreateChat);

        vp = (ViewPager) findViewById(R.id.musicChatActViewPager);

        adapter = new pagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);


        vp.setCurrentItem(0);

        createChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicChatActivity.this,CreateChatRoomActivity.class);
                startActivity(intent);
            }
        });


        navigationView = (BottomNavigationView) findViewById(R.id.musicChatActBottom);

        prevBottomNavigation = navigationView.getMenu().getItem(0);


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_one:
                        vp.setCurrentItem(0);
                        return true;
                    case R.id.action_two:
                        vp.setCurrentItem(1);
                        return true;
//                    case R.id.action_three:
//                        vp.setCurrentItem(2);
//                        return true;
                }
                return false;
            }
        });


        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(navigationView != null){
                    navigationView.setSelected(false);
                }

                prevBottomNavigation = navigationView.getMenu().getItem(position);
                prevBottomNavigation.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }




    private class pagerAdapter extends FragmentStatePagerAdapter{

        public pagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new MusicChatActFragmentHome();
                case 1:
                    return new MusicChatActFragmentRoomList();
//                case 2:
//                    return new MusicChatActFragmentFriendList();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
                Log.e("여긴 몇번감","ㅁㄴㅇㄹ");

            if(object instanceof MusicChatActFragmentRoomList){
                return POSITION_NONE;
            } else if(object instanceof MusicChatActFragmentHome){
                return POSITION_NONE;
            } else {
                return super.getItemPosition(object);
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                Log.e("MusicChatActivity","Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}



