package com.example.heizepalvin.streetrecord;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class ChatingActivity extends AppCompatActivity {

    Socket socket;

    private EditText chatingInput;
    private Button chatingSendBtn;
    TextView chatView;

    BufferedReader br;
    BufferedWriter bw;

    PrintWriter sendWriter;

    private String sendString;


//    public static ArrayList<String> chatlist = new ArrayList<>();

    public static Handler chatingHandler;

    String receiveMsg;


    public static boolean s_threadBoolean = false;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        chatingInput = (EditText) findViewById(R.id.chatActInput);
        chatingSendBtn = (Button) findViewById(R.id.chatActSendBtn);
//        chatView = (TextView) findViewById(R.id.chatActView);



        //socket

        chatingSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!s_threadBoolean){
//                    s_threadBoolean = true;
//                    SendThread s_thread = new SendThread();
//                    s_thread.setSocket(socket);
//                    s_thread.setEditText(chatingInput);
//                    s_thread.start();
                    sendString = chatingInput.getText().toString();
                    Log.e("보내려고하는 메시지는?",sendString);
                    if(!sendString.equals("")){
                        messageSend messageSend = new messageSend();
                        messageSend.execute();
                        chatingInput.setText("");
                    }
//                }
            }
        });

//        socketGet getso = new socketGet();
//        getso.execute();

        chatingHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what){
                    case 1:
//                        chatView.setText(receiveMsg+"\n");
                        chatView.append(receiveMsg+"\n");
                        break;
                }
            }
        };

        ListView listView = (ListView) findViewById(R.id.chatActList);

        ArrayList<ChatingItem> chatlist = new ArrayList();

        for(int i = 0; i<10; i++){

            ChatingItem item = new ChatingItem("하이하이하이" + i);
            chatlist.add(item);

        }


        ChatingAdapter adapter = new ChatingAdapter(this,R.layout.chat_item,chatlist);

        listView.setAdapter(adapter);




    }

    private class messageSend extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            sendWriter.println(sendString);
            sendWriter.flush();

            return null;
        }
    }

    private class socketGet extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            receiveThread r_thread = new receiveThread();
            r_thread.setSocket(socket);
            Log.e("br이 널이냐?",br+"");
            r_thread.start();
            chatView.setText(s+"\n");

            Log.e("bw이 널이냐?",bw+"");
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                socket = new Socket("115.71.232.155",9999);


                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                sendWriter = new PrintWriter(socket.getOutputStream());


                String serverMsg = br.readLine();

                Log.e("여기서받는건가?",serverMsg);

                return serverMsg;
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }


        }
    }

    private class receiveThread extends Thread{

        private Socket r_socket;
        private TextView tv;
//        private String receiveString;


        @Override

        public void run() {
            super.run();

            try{

                BufferedReader br = new BufferedReader(new InputStreamReader(r_socket.getInputStream()));

                Log.e("리시브스레드","ㅁㄴㅇㄹ");


                while(true){

                    receiveMsg = br.readLine();
//                    Log.e("receiveㅇㅇㅇ",receiveString);
                    Message msg = chatingHandler.obtainMessage(1,receiveMsg);
                    chatingHandler.sendMessage(msg);

                }



            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void setSocket(Socket _socket){
            r_socket = _socket;
        }
    }



}
