package com.example.heizepalvin.streetrecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.*;


/**
 * Created by soyounguensoo on 2017-07-18.
 */

public class PlaylistActivity extends AppCompatActivity implements PlaylistAdpater.OnStartDragListener{

    private RecyclerView playlist;

    private ImageView finishButton;

    private ArrayList<PlaylistItem> playlistItems;


    private ItemTouchHelper touchHelper;

    private TextView insertBtn;
    private TextView playlistTitle;
    private LinearLayout playlistInsert;
    private Button playlistDelete;
    private Button playlistAllSelect;


    public static boolean insertMode = false;
    public static ArrayList<String> seleteList = new ArrayList();

    private PlaylistDatabase helper;
    private SQLiteDatabase db;
    private int version = 1;

    private PlaylistAdpater adapter;


    //컨트롤

    private LinearLayout controlPlayer;
    private ImageView controlImage;
    private TextView controlTitle;
    private TextView controlArtist;
    private ImageView controlPre;
    private ImageView controlPlay;
    private ImageView controlNext;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        playlistItems = new ArrayList<>();

        helper = new PlaylistDatabase(PlaylistActivity.this,"playlist",null,version);
        db = helper.getWritableDatabase();
        helper.select(db,"playlist",playlistItems);
        playlist = (RecyclerView) findViewById(R.id.playlistActList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        playlist.setHasFixedSize(true);
        playlist.setLayoutManager(layoutManager);
        adapter = new PlaylistAdpater(getApplicationContext(),playlistItems,R.layout.playlist_activity,this);
        PlaylistItemTouchHelperCallback callback = new PlaylistItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(playlist);
        playlist.setAdapter(adapter);
//        playlist.smoothScrollToPosition(GlobalApplication.getInstance().getServiceInterface().getCurrentPosition());
        playlist.scrollToPosition(GlobalApplication.getInstance().getServiceInterface().getCurrentPosition());
        adapter.notifyDataSetChanged();


        //재생목록 x 버튼

        finishButton = (ImageView) findViewById(R.id.playlistFinish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //편집 모드
        playlistDelete = (Button) findViewById(R.id.playlistDelete);
        playlistAllSelect = (Button)  findViewById(R.id.playlistAllSelect);
        playlistInsert = (LinearLayout) findViewById(R.id.playlistBottom);
        insertBtn = (TextView) findViewById(R.id.playlistInsert);
        playlistTitle = (TextView) findViewById(R.id.playlistTitle);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistTitle.getText().toString().equals("편집")){
                    playlistTitle.setText("PlayList");
                    insertBtn.setText("편집");
                    insertMode = false;
                    playlistInsert.setVisibility(View.GONE);
                    seleteList.clear();
//                    Log.e("insertMode?" , insertMode + " ??");
                    playlist.getAdapter().notifyDataSetChanged();

                } else {
                    playlistTitle.setText("편집");
                    insertBtn.setText("완료");
                    insertMode = true;
                    playlistInsert.setVisibility(View.VISIBLE);
//                    Log.e("insertMode?" , insertMode + " ??");
                }
            }
        });

        playlistDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);

                builder.setTitle("PlayList 삭제");
                builder.setMessage("정말 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (seleteList.size() != 0) {
                            for (int i = seleteList.size() - 1; i >= 0; i--) {
                                int selectRemove = Integer.parseInt(seleteList.get(i));
                                Log.e("selectRemove", selectRemove + "?");
                                if (playlistItems.size() <= selectRemove) {
                                    String itemTitle = playlistItems.get(playlistItems.size()-1).getTitle();
                                    playlistItems.remove(playlistItems.size()-1);
                                    helper.delete(db,itemTitle,playlistItems);
                                } else {
                                    String itemTitle = playlistItems.get(selectRemove).getTitle();
                                    playlistItems.remove(selectRemove);
                                    helper.delete(db,itemTitle,playlistItems);
                                }

                                String selectDelete = seleteList.get(i);
                                seleteList.remove(selectDelete);
                                playlist.getAdapter().notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(PlaylistActivity.this, "삭제할 곡을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });



                if(seleteList.size() == 0){
                    if(playlistItems.size() == 0){
                        Toast.makeText(PlaylistActivity.this, "플레이리스트에 곡이 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlaylistActivity.this, "삭제할 곡을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        playlistAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistAllSelect.getText().toString().equals("전체선택")){
                    playlistAllSelect.setText("선택해제");
                    if(playlistItems.size() == 0){
                        Toast.makeText(PlaylistActivity.this, "플레이리스트에 곡이 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        seleteList.clear();
                        for(int i = 0; i<playlistItems.size(); i++){
                            seleteList.add(String.valueOf(i));
                            playlist.getAdapter().notifyDataSetChanged();
                        }
                    }
                }  else {
                    playlistAllSelect.setText("전체선택");
                    seleteList.clear();
                    playlist.getAdapter().notifyDataSetChanged();
                }


            }
        });

        //컨트롤러

        controlPlayer = (LinearLayout) findViewById(R.id.playlistActControlPlayer);
        controlImage = (ImageView) findViewById(R.id.playlistActControlImage);
        controlTitle = (TextView) findViewById(R.id.playlistActControlTitle);
        controlArtist = (TextView) findViewById(R.id.playlistActControlArtist);
        controlPre = (ImageView) findViewById(R.id.playlistActControlPre);
        controlPlay = (ImageView) findViewById(R.id.playlistActControlPlay);
        controlNext = (ImageView) findViewById(R.id.playlistActControlNext);

        controlPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    Intent intent = new Intent(PlaylistActivity.this,PlayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });


        controlPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    GlobalApplication.getInstance().getServiceInterface().rewind();
                }
            }
        });

        controlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    GlobalApplication.getInstance().getServiceInterface().togglePlay();
                    if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                        controlPlay.setImageResource(R.drawable.pausewhite);
                    } else {
                        controlPlay.setImageResource(R.drawable.playwhite);
                    }
                }

            }
        });

        controlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    GlobalApplication.getInstance().getServiceInterface().forwardClick();
                }
            }
        });

        controlTitle.setSelected(true);

        registerBroadcast();
        updateUI();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("NEXT")){
                updateUI();
            } else if(intent.getAction().equals("TRUE")){
                updateUI();
            } else {
                updateUI();
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.NEXT);
        filter.addAction(BroadcastActions.isPlaying);
        filter.addAction(BroadcastActions.PLAY_START);
        registerReceiver(receiver,filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(receiver);
    }

    private void updateUI(){

        playlist.scrollToPosition(GlobalApplication.getInstance().getServiceInterface().getCurrentPosition());
        adapter.notifyDataSetChanged();

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            controlPlay.setImageResource(R.drawable.pausewhite);
        } else {
            controlPlay.setImageResource(R.drawable.playwhite);
        }

        PlaylistItem musicItem = GlobalApplication.getInstance().getServiceInterface().getMusicItem();
        if(musicItem != null){
            controlTitle.setText(musicItem.getTitle());
            controlArtist.setText(musicItem.getArtist());
            Glide.with(this).load(musicItem.getImgPath()).into(controlImage);
        }
    }

    @Override
    public void onStartDrag(PlaylistViewHolder holder) {
        if(insertMode){
            touchHelper.startDrag(holder);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        seleteList.clear();
        if(isFinishing()){
            insertMode = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
