package com.example.heizepalvin.streetrecord;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.heizepalvin.streetrecord.ChatingActivity.s_threadBoolean;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class SendThread extends Thread{

    private Socket m_socket;
    private EditText et;

    @Override
    public void run() {
        super.run();
        try{
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream()));

            PrintWriter sendWriter = new PrintWriter(m_socket.getOutputStream());

            String sendString = et.getText().toString();

                    if(!sendString.equals("")){
                        sendWriter.println(sendString);
                        sendWriter.flush();
                    }
        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public void setSocket(Socket _socket){
        m_socket = _socket;
    }

    public void setEditText(EditText _edittext){
        et = _edittext;
    }
}
