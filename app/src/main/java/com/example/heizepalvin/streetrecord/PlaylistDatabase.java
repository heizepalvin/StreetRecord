package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by soyounguensoo on 2017-07-19.
 */

public class PlaylistDatabase extends SQLiteOpenHelper {


    public PlaylistDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table playlist (id integer primary key autoincrement, title text, artist text, albumImg text, musicURL text, lyrics text, albumName text, date text, genre text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SQLiteDatabase db, String title, String artist, String albumImg,String musicURL,String lyrics, String albumName, String date, String genre ){
        db.beginTransaction();
        try{

            String sql = "insert into playlist (title, artist, albumImg, musicURL, lyrics, albumName, date, genre) values('"+title+"','"+artist+"', '"+albumImg+"', '" +musicURL+"', '" + lyrics+"', '"+albumName+"', '"+date+"', '"+genre+"' )";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.endTransaction();
    }

    public void select (SQLiteDatabase db, String tablename, ArrayList<PlaylistItem> items){
        db.beginTransaction();
        String sql = "select * from " + tablename + " order by id desc;";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                PlaylistItem item = new PlaylistItem();
                item.setTitle(cursor.getString(1));
                item.setArtist(cursor.getString(2));
                item.setImgPath(cursor.getString(3));
                item.setMusicURL(cursor.getString(4));
                item.setLyrics(cursor.getString(5));
                item.setAlbumName(cursor.getString(6));
                item.setDate(cursor.getString(7));
                item.setGenre(cursor.getString(8));
                items.add(item);
                int number = cursor.getInt(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String image = cursor.getString(3);
                String musicURL = cursor.getString(4);
                String lyrics = cursor.getString(5);
                String albumName = cursor.getString(6);
                String date = cursor.getString(7);
                String genre = cursor.getString(8);
//                Log.e("PlaylistDB","number = " + number + " title = " + title + " artist = " + artist + " image = " + image + " musicURL = " + musicURL + " lyrics = " + lyrics + " albumName = " + albumName + " date = " + date + " genre = " + genre);
            }
        }
        db.endTransaction();
    }

    public int search (SQLiteDatabase db, String title){
        db.beginTransaction();
        String sql = "select * from playlist where title = '" + title + "'" ;
        Cursor cursor = db.rawQuery(sql,null);
        int count = cursor.getCount();
        db.endTransaction();
        return count;
    }

    public void delete (SQLiteDatabase db, String title, ArrayList<PlaylistItem> items){
        db.beginTransaction();
        String sql = "delete from playlist where title = '"+title+"'";
        String sql2 = "update sqlite_sequence set seq = " + items.size();
        db.execSQL(sql);
        db.execSQL(sql2);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void delete (SQLiteDatabase db, String title){
        db.beginTransaction();
        String sql = "delete from playlist where title = '"+title+"'";
        db.execSQL(sql);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update (SQLiteDatabase db,ArrayList<PlaylistItem> items,int fromPosition, int toPosition){
        db.beginTransaction();
        String sql = "select id from playlist where title = '" + items.get(toPosition).getTitle() + "'";
        String sql2 = "select id from playlist where title = '" + items.get(fromPosition).getTitle()+ "'";
        Cursor cursor = db.rawQuery(sql,null);
        Cursor cursor2 = db.rawQuery(sql2,null);
        cursor.moveToFirst();
        cursor2.moveToFirst();
        int selectId = cursor.getInt(0);
        int selectId2 = cursor2.getInt(0);
        String updateTitle = "update playlist set title = '" + items.get(fromPosition).getTitle() + "' where id = " + selectId;
        String updateTitle2 = "update playlist set title = '" + items.get(toPosition).getTitle() + "' where id = " + selectId2;
        String updateArtist = "update playlist set artist = '" + items.get(fromPosition).getArtist() + "' where id = " + selectId;
        String updateArtist2 = "update playlist set artist = '" + items.get(toPosition).getArtist() + "' where id = " + selectId2;
        String updateAlbumImg = "update playlist set albumImg = '" + items.get(fromPosition).getImgPath() + "' where id = " + selectId;
        String updateAlbumImg2 = "update playlist set albumImg = '" + items.get(toPosition).getImgPath() + "' where id = " + selectId2;
        String updateURL = "update playlist set musicURL = '" + items.get(fromPosition).getMusicURL() + "' where id = " + selectId;
        String updateURL2 = "update playlist set musicURL = '" + items.get(toPosition).getMusicURL() + "' where id = " + selectId2;
        String updateLyrics = "update playlist set lyrics = '"+ items.get(fromPosition).getLyrics() + "' where id = " + selectId;
        String updateLyrics2 = "update playlist set lyrics = '" + items.get(toPosition).getLyrics() + "' where id = " + selectId2;
        String updateAlbumName = "update playlist set albumName = '" + items.get(fromPosition).getAlbumName() + "' where id = " + selectId;
        String updateAlbumName2 = "update playlist set albumName = '" + items.get(toPosition).getAlbumName() + "' where id = " + selectId2;
        String updateDate = "update playlist set date = '" + items.get(fromPosition).getDate() + "' where id = " + selectId;
        String updateDate2 = "update playlist set date = '" + items.get(toPosition).getDate() + "' where id = " + selectId2;
        String updateGenre = "update playlist set genre = '" + items.get(fromPosition).getGenre() + "' where id =" + selectId;
        String updateGenre2 = "update playlist set genre = '" + items.get(toPosition).getGenre() + "' where id =" + selectId2;

        db.execSQL(updateTitle);
        db.execSQL(updateTitle2);
        db.execSQL(updateArtist);
        db.execSQL(updateArtist2);
        db.execSQL(updateAlbumImg);
        db.execSQL(updateAlbumImg2);
        db.execSQL(updateURL);
        db.execSQL(updateURL2);
        db.execSQL(updateLyrics);
        db.execSQL(updateLyrics2);
        db.execSQL(updateAlbumName);
        db.execSQL(updateAlbumName2);
        db.execSQL(updateDate);
        db.execSQL(updateDate2);
        db.execSQL(updateGenre);
        db.execSQL(updateGenre2);

        db.setTransactionSuccessful();
        db.endTransaction();
    }


}
