package com.example.heizepalvin.streetrecord;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class ReceiveThread extends Thread{

    private Socket r_socket;
    private TextView tv;
    private String receiveString;


    @Override

    public void run() {
        super.run();

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(r_socket.getInputStream()));



            while(true){

                receiveString = br.readLine();

            }



        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setSocket(Socket _socket){
        r_socket = _socket;
    }



}
