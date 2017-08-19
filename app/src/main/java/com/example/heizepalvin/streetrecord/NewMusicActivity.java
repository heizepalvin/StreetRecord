package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;


/**
 * Created by soyounguensoo on 2017-06-23.
 */

public class NewMusicActivity extends AppCompatActivity {

    private ArrayList<NewMusicListItem> items;
    private NewMusicAdapter adapter;

    private SimpleSideDrawer mSlidingMenu;
    private String getJsonData;

    private ArrayList<MenuItem> menuData;


    private Button loginBtn;
    private Button logoutBtn;
    private TextView loginUser;

    private TextView allPlay;

    private SQLiteDatabase db;
    private PlaylistDatabase helper;

    //컨트롤

    private LinearLayout controlPlayer;
    private ImageView controlPlaylist;
    private ImageView controlImage;
    private TextView controlTitle;
    private TextView controlArtist;
    private ImageView controlPre;
    private ImageView controlPlay;
    private ImageView controlNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmusic_activity);
        //사이드 메뉴

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.left_menu);
        ImageButton menuBtn = (ImageButton) findViewById(R.id.newMusicSideMenu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggleLeftDrawer();
            }
        });

        menuData = new ArrayList<>();
        MenuItem myInfo = new MenuItem("내 정보",R.drawable.myinfo);
        MenuItem musicList = new MenuItem("플레이리스트",R.drawable.musiclist);
        menuData.add(myInfo);
        menuData.add(musicList);

        final ListView listView = (ListView) findViewById(R.id.menuList);

        final MenuAdapter adapter = new MenuAdapter(this, R.layout.menu_item, menuData);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    if(loginUser.getText().toString().equals("로그인을 해주세요.")){
                        Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(NewMusicActivity.this,PlaylistActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }  else if(position==0){
                    SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                    Boolean loginSet = pref.getBoolean("login",false);
                    String loginLink = pref.getString("loginLink","no");
                    if(loginSet){
                        if(loginLink.equals("no")){
                            Intent intent = new Intent(NewMusicActivity.this,MyinfoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(NewMusicActivity.this,MyInfoLinkActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //사이드 메뉴 끝





        //LOGO 버튼 이벤트

        final ImageView home = (ImageView) findViewById(R.id.newMusicHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewMusicActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        //db에서 리스트 가져오기

        getNewMusicList getData = new getNewMusicList();
        getData.execute("http://115.71.232.155/getNewMusic.php");
        items = new ArrayList<>();


        //login,logout 버튼
        loginBtn = (Button) findViewById(R.id.menuLoginBtn);
        loginUser = (TextView) findViewById(R.id.menuLoginText);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewMusicActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutBtn = (Button) findViewById(R.id.menuLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(NewMusicActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                logoutBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                loginUser.setText("로그인을 해주세요.");
                loginBoolean = false;
                if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                    GlobalApplication.getInstance().getServiceInterface().stop();
                    updateUI();
                    GlobalApplication.getInstance().getServiceInterface().getRemoveNotificationPlayer();
                }
            }
        });

        //전체재생 버튼

        allPlay = (TextView) findViewById(R.id.newMusicActAllPlay);
        helper = new PlaylistDatabase(getApplicationContext(),"playlist",null,1);
        db = helper.getWritableDatabase();
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    int count = 0;
                    try{
                        for(int i = items.size() - 1; i>=0; i--){
                            String title = items.get(i).getTitle();
                            String artist = items.get(i).getArtist();
                            String albumImg = items.get(i).getAlbumImg();
                            String musicURL = items.get(i).getMusicURL();
                            String lyrics = items.get(i).getLyrics();
                            String albumName = items.get(i).getAlbumName();
                            String date = items.get(i).getDate();
                            String genre = items.get(i).getGenre();
                            count = helper.search(db,title);
                            if(count != 1){
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);

                            } else {
                                helper.delete(db,title);
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);

                            }
                        }
                        if(count > 0){
                            Toast.makeText(NewMusicActivity.this, "중복되는 곡을 삭제하고 재생합니다.", Toast.LENGTH_SHORT).show();
                        }

                        ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>();
                        helper.select(db,"playlist",playlist);
                        GlobalApplication.getInstance().getServiceInterface().setPlaylist(playlist);
                        GlobalApplication.getInstance().getServiceInterface().play(0);

                    } catch (SQLiteException e){
                        e.printStackTrace();
                        Log.e("NewMusicActDatabaseException","데이터베이스를 가져올 수 없음.");
                    }
                } else {
                    Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //컨트롤러

        controlPlayer = (LinearLayout) findViewById(R.id.newMusicActControlPlayer);
        controlPlaylist = (ImageView) findViewById(R.id.newMusicActControlPlaylist);
        controlImage = (ImageView) findViewById(R.id.newMusicActControlImage);
        controlTitle = (TextView) findViewById(R.id.newMusicActControlTitle);
        controlArtist = (TextView) findViewById(R.id.newMusicActControlArtist);
        controlPre = (ImageView) findViewById(R.id.newMusicActControlPre);
        controlPlay = (ImageView) findViewById(R.id.newMusicActControlPlay);
        controlNext = (ImageView) findViewById(R.id.newMusicActControlNext);

        controlPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        Intent intent = new Intent(NewMusicActivity.this,PlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        controlPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    Intent intent = new Intent(NewMusicActivity.this,PlaylistActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        controlPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().rewind();
                    } else {
                        Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        controlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().togglePlay();
                        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                            controlPlay.setImageResource(R.drawable.pausewhite);
                        } else {
                            controlPlay.setImageResource(R.drawable.playwhite);
                        }
                    } else {
                        Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        controlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!controlTitle.getText().toString().equals("StreetRecord")){
                        if(loginBoolean){
                            GlobalApplication.getInstance().getServiceInterface().forwardClick();
                        } else {
                            Toast.makeText(NewMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
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
        Log.e("여기가 들어오는건지?","뉴뮤직디스트로이");
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
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        Boolean login = preferences.getBoolean("login",false);
        if(login){
            loginBtn.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            String user = preferences.getString("userID","user");
            if(user.equals("user")){
                String name = preferences.getString("userName","facebook");
                loginUser.setText(name+"님");
            } else {
                loginUser.setText(user+"님");
            }
        }
    }

    private class getNewMusicList extends AsyncTask<String,Void,String>{

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NewMusicActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){

                getJsonData = result;
                getNewMusic();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String serverUrl = params[0];

            try{

                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.connect();

                int responseCode = conn.getResponseCode();

                InputStream inputStream;

                if(responseCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e){
                Log.e("newMusicActivity", "doinbackground Exception : " + e );
                return null;
            }
        }
    }

    private void getNewMusic(){

        try{
            JSONObject jsonObject = new JSONObject(getJsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("newMusic");



            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                Log.e("newMusicActivity","title ? " + title);
                String artist = item.getString("artist");
                Log.e("newMusicActivity","artist ? "+ artist);
                String albumImg = item.getString("albumImg");
                Log.e("newMusicActivity","albumImg ? " + albumImg);
                String musicURL = item.getString("musicURL");
                Log.e("newMusicActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("newMusicActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("newMusicActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("newMusicActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("newMusicActivity","genre ? " + genre);

                NewMusicListItem list = new NewMusicListItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(list);

            }

            ListView listView = (ListView) findViewById(R.id.newMusicActivityList);
            adapter = new NewMusicAdapter(this,R.layout.newmusic_activity_item,items);
            listView.setAdapter(adapter);



        } catch (JSONException e){

            Log.e("newMusicActivity","jsonException : " + e);

        }



    }

}
