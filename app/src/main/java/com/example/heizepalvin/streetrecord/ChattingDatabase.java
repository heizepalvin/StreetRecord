package com.example.heizepalvin.streetrecord;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-07-19.
 */

public class ChattingDatabase extends SQLiteOpenHelper {


    public ChattingDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table chatRoom (num integer primary key autoincrement, title text, lastMsg text, image text, time text, token integer, datePlusTime text, count integer);";
        String sql2 = "create table chatHistory (num integer primary key autoincrement, id text, msg text, time text, token integer, type text, date text);";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //채팅방 데이터베이스 저장

    public void insert(SQLiteDatabase db, String title, String lastMsg, String image, String time, int token, String datePlusTime){
        db.beginTransaction();
        try{

            String sql = "insert into chatRoom (title, lastMsg, image, time, token, datePlusTime, count) values('"+title+"','"+lastMsg+"','"+image+"','"+time+"',"+token+", '"+datePlusTime+"', 0);";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.endTransaction();
    }


    //메시지 저장 insert

    public void insert(SQLiteDatabase db, String id, String msg, String time, int token, String type, String date){
        db.beginTransaction();
        try{
            String sql = "insert into chatHistory (id, msg, time, token, type, date) values ('"+id+"','"+msg+"','"+time+"',"+token+",'"+type+"', '"+date+"')";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
        }
        db.endTransaction();
    }

    //채팅방 가져오는 sql문

    public void select (SQLiteDatabase db, String tablename, ArrayList<MusicChatActRoomListItem> items){
        db.beginTransaction();
        String sql = "select * from " + tablename + " order by num desc;";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                MusicChatActRoomListItem item = new MusicChatActRoomListItem();
                item.setTitle(cursor.getString(1));
                item.setLastMessage(cursor.getString(2));
                item.setImage(cursor.getString(3));
                item.setTime(cursor.getString(4));
                item.setToken(cursor.getInt(5));
                item.setCount(cursor.getInt(7));

                items.add(item);
                int number = cursor.getInt(0);
                String title = cursor.getString(1);
                String lastMsg = cursor.getString(2);
                String image = cursor.getString(3);
                String time = cursor.getString(4);
                int token = cursor.getInt(5);
                int count = cursor.getInt(7);

                Log.e("ChattingDB","number = " + number + " title = " + title + " lastMsg = " + lastMsg + " image = " + image + " time = " + time + " token = " + token  + " count = " + count);
            }
        }
        db.endTransaction();
    }
    //메시지왔을때 갱신하는 sql문

    public void receiveSelect (SQLiteDatabase db, String tablename, ArrayList<MusicChatActRoomListItem> items){
        db.beginTransaction();
        String sql = "select * from " + tablename + " order by datePlusTime desc;";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                MusicChatActRoomListItem item = new MusicChatActRoomListItem();
                item.setTitle(cursor.getString(1));
                item.setLastMessage(cursor.getString(2));
                item.setImage(cursor.getString(3));
                item.setTime(cursor.getString(4));
                item.setToken(cursor.getInt(5));
                item.setCount(cursor.getInt(7));
                items.add(item);
                int number = cursor.getInt(0);
                String title = cursor.getString(1);
                String lastMsg = cursor.getString(2);
                String image = cursor.getString(3);
                String time = cursor.getString(4);
                int token = cursor.getInt(5);
                int count = cursor.getInt(7);
            }
        }
        db.endTransaction();
    }


    //채팅 기록 가져오는 sql 문
    public void selectMsg (SQLiteDatabase db, ArrayList<ChatingItem> items, int token){
        db.beginTransaction();
//        String sql = "select * from " + tablename + " where token = "+token;
        String sql = "select * from chatHistory where token = "+token;
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                ChatingItem item = new ChatingItem();
                item.setId(cursor.getString(1));
                item.setUserChat(cursor.getString(2));
                item.setTime(cursor.getString(3));
                item.setToken(cursor.getInt(4));
                item.setType(cursor.getString(5));
                item.setDate(cursor.getString(6));
                items.add(item);

                String id = cursor.getString(1);
                String msg = cursor.getString(2);
                String time = cursor.getString(3);
                int tokens = cursor.getInt(4);
                String type = cursor.getString(5);
                String date = cursor.getString(6);
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

    //채팅방 목록 삭제

    public void delete (SQLiteDatabase db, int token){
        db.beginTransaction();
        String sql = "delete from chatRoom where token = "+token;
        String sql2 = "delete from chatHistory where token = " + token;

        db.execSQL(sql);
        db.execSQL(sql2);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update (SQLiteDatabase db, String tableName, String msg, int token, String time, String datePlusTime){

        db.beginTransaction();

        String sql = "update "+tableName+" set lastMsg = '"+msg+"' where token = "+token;
        String sql2 = "update "+tableName+" set time = '"+time+"' where token = "+token;
        String sql3 = "update "+tableName+" set datePlusTime = '"+datePlusTime+"' where token = "+token;
        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    // chatRoom 테이블 카운트 업데이트 하는 부분

    public void countUpdate (SQLiteDatabase db, int token, int type){

        db.beginTransaction();

        if(type == 1){
            //type이 1일 경우 카운트 +1
            String sql = "update chatRoom set count = count+1 where token = "+token;
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();
        } else if(type == 2){
            //type이 2일 경우 카운트 0
            String sql = "update chatRoom set count = 0 where token = " + token;
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }



}
