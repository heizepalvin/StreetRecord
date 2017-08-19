package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;


/**
 * Created by soyounguensoo on 2017-06-28.
 */

public class JoinActivity extends AppCompatActivity {

    private EditText id;
    private EditText pwd;
    private EditText pwd2;
    private EditText email;
    private EditText birth;
    private TextView pwdConfirm;
    private Button joinBtn;
    private TextView idConfirm;
    private TextView emailConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        id = (EditText) findViewById(R.id.infoLinkActName);
        pwd = (EditText) findViewById(R.id.infoLinkActEmailEdit);
        pwd2 = (EditText) findViewById(R.id.infoActPwd2);
        email = (EditText) findViewById(R.id.infoActEmail);
        birth = (EditText) findViewById(R.id.infoActBirth);
        idConfirm = (TextView) findViewById(R.id.joinActIdConfirm);
        pwdConfirm = (TextView) findViewById(R.id.joinActPwdConfirm);
        joinBtn = (Button) findViewById(R.id.joinActBtn);
        emailConfirm = (TextView) findViewById(R.id.joinActEmailConfirm);

        id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(id.getText().toString().replace(" ","").equals("")){
                        idConfirm.setText("사용할 수 없는 ID입니다.");
                        idConfirm.setTextColor(Color.RED);
                    } else {
                        IDConfirmData data = new IDConfirmData();
                        String confirm = id.getText().toString();
                        data.execute(confirm);
                    }
                }
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",pwd.getText().toString())){
                    pwdConfirm.setTextColor(Color.RED);
                    pwdConfirm.setText("비밀번호 형식이 맞지 않습니다.");
                } else {

                    if(pwd.getText().toString().equals(pwd2.getText().toString())){
                        pwdConfirm.setTextColor(Color.GREEN);
                        pwdConfirm.setText("비밀번호가 일치합니다.");
                    } else {
                        pwdConfirm.setTextColor(Color.RED);
                        pwdConfirm.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",pwd.getText().toString())){
                    pwdConfirm.setTextColor(Color.RED);
                    pwdConfirm.setText("비밀번호 형식이 맞지 않습니다.");
                } else {

                    if(pwd.getText().toString().equals(pwd2.getText().toString())){
                        pwdConfirm.setTextColor(Color.GREEN);
                        pwdConfirm.setText("비밀번호가 일치합니다.");
                    } else {
                        pwdConfirm.setTextColor(Color.RED);
                        pwdConfirm.setText("비밀번호가 일치하지 않습니다.");
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirm = email.getText().toString();
                if(Patterns.EMAIL_ADDRESS.matcher(confirm).matches()){
                    emailConfirm.setTextColor(Color.GREEN);
                    emailConfirm.setText("올바른 이메일 형식입니다.");
                } else {
                    emailConfirm.setTextColor(Color.RED);
                    emailConfirm.setText("이메일 형식이 맞지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(id.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    id.requestFocus();

                } else if(pwd.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                } else if(pwd2.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "비밀번호 확인 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    pwd2.requestFocus();
                } else if(email.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else if(birth.getText().toString().length() == 0){
                    Toast.makeText(JoinActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    birth.requestFocus();
                } else if(idConfirm.getText().toString().equals("이미 사용하고 있는 ID입니다.")){
                    Toast.makeText(JoinActivity.this, "다른 아이디를 사용해주세요.", Toast.LENGTH_SHORT).show();
                    id.requestFocus();
                } else if(pwdConfirm.getText().toString().equals("비밀번호 형식이 맞지 않습니다.")){
                    Toast.makeText(JoinActivity.this, "비밀번호를 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                } else if(pwdConfirm.getText().toString().equals("비밀번호가 일치하지 않습니다.")){
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                } else if(emailConfirm.getText().toString().equals("이메일 형식이 맞지 않습니다.")){
                    Toast.makeText(JoinActivity.this, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else if(birth.getText().toString().length() != 8){
                    Toast.makeText(JoinActivity.this, "생년월일을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                    birth.requestFocus();
                } else if(idConfirm.getText().toString().equals("사용할 수 없는 ID입니다.")){
                    Toast.makeText(JoinActivity.this, "사용할 수 없는 ID입니다.", Toast.LENGTH_SHORT).show();
                    id.requestFocus();
                }
                else{
                    joinMember join = new joinMember();
                    String memberID = id.getText().toString();
                    String memberPwd = pwd.getText().toString();
                    String memberEmail = email.getText().toString();
                    String memberBirth = birth.getText().toString();
                    join.execute(memberID,memberPwd,memberEmail,memberBirth);
                }
//                if(!idConfirm.getText().toString().equals("이미 사용하고 있는 ID입니다.") && pwdConfirm.getText().toString().equals("비밀번호가 일치합니다.")
//                        && !emailConfirm.getText().toString().equals("이메일 형식이 맞지 않습니다.")){
//                    joinMember join = new joinMember();
//                    String memberID = id.getText().toString();
//                    String memberPwd = pwd.getText().toString();
//                    String memberEmail = email.getText().toString();
//                    String memberBirth = birth.getText().toString();
//                    join.execute(memberID,memberPwd,memberEmail,memberBirth);
//                }

            }
        });


    }

    private class IDConfirmData extends AsyncTask<String,Void,String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(!id.getText().toString().equals("")){
                if(result.equals("이미 사용하고 있는 ID입니다.")){
                    idConfirm.setTextColor(Color.RED);
                    idConfirm.setText(result);
                } else {
                    idConfirm.setTextColor(Color.GREEN);
                    idConfirm.setText(result);
                }
            } else {
                idConfirm.setText("아이디를 입력해주세요.");
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(JoinActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected String doInBackground(String... params) {

            String id = "id="+params[0];

            String serverURL = "http://115.71.232.155/login/idConfirm.php";
            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();
                Log.e("들어옴","들어옴");

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(id.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();

                InputStream inputStream;
                if(responseCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();


            }catch (Exception e){
                Log.e("joinAct","Exception ?" + e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    private class joinMember extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(JoinActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Toast.makeText(JoinActivity.this, result, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(JoinActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/register.php";

            String memberID = params[0];
            String memberPwd = params[1];
            String memberEmail = params[2];
            String memberBirth = params[3];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String postData = "id="+memberID+"&password="+memberPwd+"&email="+memberEmail+"&birth="+memberBirth;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if(responseCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else{
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e){

                Log.e("JoinActivity","Exception : "+ e);
                return null;
            }

        }
    }
}
