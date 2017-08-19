package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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

import com.facebook.stetho.dumpapp.GlobalOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by soyounguensoo on 2017-08-09.
 */

public class MyInfoLinkActivity extends AppCompatActivity {

    private TextView infoName;
    private TextView infoEmail;
    private EditText infoEmailEdit;
    private TextView infoEmailBtn;
    private TextView infoEmailCancel;
    private TextView infoBirth;
    private EditText infoBirthEdit;
    private TextView infoBirthBtn;
    private TextView infoBirthCancel;
    private TextView infoLink;
    private Button infoBtn;
    private Button infoCancelBtn;

    private Boolean emailEdit = false;
    private Boolean birthEdit = false;

    private String getJsonInfo;

    private String updateEmail;
    private String updateBirth;
    private String updateInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_activity_kakao_facebook);

        infoName = (TextView) findViewById(R.id.infoLinkActName);
        infoEmail = (TextView) findViewById(R.id.infoLinkActEmail);
        infoEmailEdit = (EditText) findViewById(R.id.infoLinkActEmailEdit);
        infoEmailBtn = (TextView) findViewById(R.id.infoLinkActEmailBtn);
        infoEmailCancel = (TextView) findViewById(R.id.infoLinkActEmailCancel);
        infoBirth = (TextView) findViewById(R.id.infoLinkActBirth);
        infoBirthEdit = (EditText) findViewById(R.id.infoLinkActBirthEdit);
        infoBirthBtn = (TextView) findViewById(R.id.infoLinkActBirthBtn);
        infoBirthCancel = (TextView) findViewById(R.id.infoLinkActBirthCancel);
        infoLink = (TextView) findViewById(R.id.infoLinkActLink);
        infoBtn = (Button) findViewById(R.id.infoLinkActBtn);
        infoCancelBtn = (Button) findViewById(R.id.infoLinkActCancelBtn);

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        String getName = preferences.getString("userName","null");
        if(!getName.equals("null")){
            infoName.setText(getName);
        }
        final String getToken = preferences.getString("token","null");
        if(!getToken.equals("null")){
            GetMemberInfoKF getInfo = new GetMemberInfoKF();
            getInfo.execute(getToken);
        }
        String getLink = preferences.getString("loginLink","null");
        if(!getLink.equals("null")){
            infoLink.setText(getLink);
        }
//        final SharedPreferences pref2 = getSharedPreferences("userInfo",MODE_PRIVATE);
//        final Boolean infoBoolean =  pref2.getBoolean("userLoginInfo",false);
//        if(infoBoolean){
//            Log.e("뭐지 진짜","1");
//            String userName = pref2.getString("saveName","");
//            String userEmail = pref2.getString("saveEmail","");
//            String userBirthday = pref2.getString("saveBirthday","");
//            String loginLink = pref2.getString("saveLink","");
//
//            if(!userName.equals("")){
//                infoName.setText(userName);
//            }
//            if(!userEmail.equals("")){
//                infoEmail.setText(userEmail);
//            }
//            if(!userBirthday.equals("")){
//                infoBirth.setText(userBirthday);
//                infoBirthBtn.setVisibility(View.GONE);
//            }
//            if(!loginLink.equals("")){
//                infoLink.setText(loginLink);
//            }
//        } else {
//            Log.e("뭐지 진짜","2");
//            final SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
//            String userName = pref.getString("userName","");
//            String userEmail = pref.getString("userEmail","");
//            final String userBirthday = pref.getString("userBirthday","");
//            String loginLink = pref.getString("loginLink","");
//
//            if(!userName.equals("")){
//                infoName.setText(userName);
//            }
//            if(!userEmail.equals("")){
//                infoEmail.setText(userEmail);
//                infoEmailBtn.setVisibility(View.GONE);
//            }
//            if(!userBirthday.equals("")){
//                String birth[]= userBirthday.split("/");
//                infoBirth.setText(birth[2]+birth[0]+birth[1]);
//                infoBirthBtn.setVisibility(View.GONE);
//            }
//            if(!loginLink.equals("")){
//                infoLink.setText(loginLink);
//            }
//        }
//





        infoEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoEmailCancel.setVisibility(View.VISIBLE);
                infoEmailEdit.setVisibility(View.VISIBLE);
                emailEdit = true;
                infoEmail.setVisibility(View.GONE);
                infoEmailBtn.setText("이메일을 입력해주세요.");
            }
        });

        infoEmailCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoEmailCancel.setVisibility(View.GONE);
                infoEmailBtn.setText("이메일설정하기");
                String textColor = "#B7F0B1";
                infoEmailBtn.setTextColor(Color.parseColor(textColor));
                infoEmailEdit.setVisibility(View.GONE);
                infoEmail.setVisibility(View.VISIBLE);
                emailEdit = false;
            }
        });

        infoEmailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirm = infoEmailEdit.getText().toString();
                if(Patterns.EMAIL_ADDRESS.matcher(confirm).matches()){
                    infoEmailBtn.setTextColor(Color.GREEN);
                    infoEmailBtn.setText("올바른 이메일 형식입니다.");
                } else{
                    infoEmailBtn.setTextColor(Color.RED);
                    infoEmailBtn.setText("이메일 형식이 맞지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        infoBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoBirthBtn.setVisibility(View.GONE);
                infoBirthCancel.setVisibility(View.VISIBLE);
                infoBirth.setVisibility(View.GONE);
                infoBirthEdit.setVisibility(View.VISIBLE);
                birthEdit = true;
            }
        });

        infoBirthCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoBirthBtn.setVisibility(View.VISIBLE);
                infoBirthCancel.setVisibility(View.GONE);
                infoBirthEdit.setVisibility(View.GONE);
                infoBirth.setVisibility(View.VISIBLE);
                birthEdit = false;
            }
        });



        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //이메일 Edit 창이 켜져 있고 확인 버튼을 눌렀을 때

                if(infoEmailEdit.getVisibility()==View.VISIBLE){
                    if(infoEmailBtn.getText().toString().equals("이메일 형식이 맞지 않습니다.")){
                        Toast.makeText(MyInfoLinkActivity.this, "이메일 형식을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                        infoEmailEdit.requestFocus();
                    } else {
                        if(infoEmailBtn.getText().toString().equals("이메일을 입력해주세요.")){
                            Toast.makeText(MyInfoLinkActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            infoEmailEdit.requestFocus();
                        }
                    }
                    if(infoBirthEdit.getVisibility() == View.VISIBLE){
                        if(infoBirthEdit.getText().toString().length() < 8){
                            Toast.makeText(MyInfoLinkActivity.this, "생년월일을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                            infoBirthEdit.requestFocus();
                        } else if(infoBirthEdit.getText().toString().length() == 0){
                            Toast.makeText(MyInfoLinkActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if(infoBirthEdit.getVisibility() == View.VISIBLE){
                    //생년월일 Edit 창이 켜져 있고 확인 버튼을 눌렀을 때
                    if(infoBirthEdit.getText().toString().length()==0){
                        Toast.makeText(MyInfoLinkActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        infoBirthEdit.requestFocus();
                    } else if(infoBirthEdit.getText().toString().length()<8){
                        Toast.makeText(MyInfoLinkActivity.this, "생년월일을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                        infoBirthEdit.requestFocus();
                    }
                }

                if(infoEmailEdit.getVisibility() == View.VISIBLE &&infoEmailBtn.getText().toString().equals("올바른 이메일 형식입니다.")){
                    if(infoBirthEdit.getVisibility() == View.VISIBLE && infoBirthEdit.getText().toString().length()==8){
                        //둘다 수정 했을 때

                        updateEmail = infoEmailEdit.getText().toString();
                        updateBirth = infoBirthEdit.getText().toString();

                        updateUserInfo info = new updateUserInfo();
                        info.execute(getToken);

                    } else if(infoBirthEdit.getVisibility() == View.GONE) {
                        //이메일만 수정했을 때

                        updateEmail = infoEmailEdit.getText().toString();
                        updateBirth = "null";

                        updateUserInfo info = new updateUserInfo();
                        info.execute(getToken);

                    }

                } else if(infoEmailEdit.getVisibility() == View.GONE){
                    if(infoBirthEdit.getVisibility() == View.VISIBLE && infoBirthEdit.getText().toString().length() == 8){
                        //생년월일만 수정했을때

                        updateEmail = "null";
                        updateBirth = infoBirthEdit.getText().toString();

                        updateUserInfo info = new updateUserInfo();
                        info.execute(getToken);
                    }
                }  else if(infoBirthEdit.getVisibility() == View.GONE && infoEmailEdit.getVisibility() == View.GONE){
                    //아무것도 수정하지 않았을 때
                    finish();
                }

            }
        });


        infoCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private class GetMemberInfoKF extends AsyncTask<String,Void,String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyInfoLinkActivity.this,"잠시만 기다려주세요.",null,true,true);


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s!=null){
                getJsonInfo = s;
                getJsonUserInfo();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/myInfoKF.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String userToken = params[0];

                String token = "token="+userToken;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(token.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

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

                while ((line=bufferedReader.readLine())!= null){
                    sb.append(line+"\n");
                    Log.e("sb", String.valueOf(sb));
                }
                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("MyInfoLinkActivity","userInfo Exception : " + e);
                return null;
            }
        }
    }

    private void getJsonUserInfo(){
        try{

            JSONObject jsonObject = new JSONObject(getJsonInfo);
            JSONArray jsonArray = jsonObject.getJSONArray("myInfoKF");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString("name");
                Log.e("getJsonUserInfo",name);
                String email = item.getString("email");
                Log.e("getJsonUserInfo",email);
                String birth = item.getString("birth");
                Log.e("getJsonUserInfo",birth);

                if(!email.equals("null")){
                    infoEmail.setText(email);
                    infoEmailBtn.setText("이메일변경하기");
                }
                if(!birth.equals("null")){
                    infoBirth.setText(birth);
                    infoBirthBtn.setVisibility(View.GONE);
                }
            }


        }catch (JSONException e){
            Log.e("jsonInfo","JSONException : " + e);
        }
    }

    private class updateUserInfo extends AsyncTask<String,Void,String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyInfoLinkActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s!=null){
                if(s.equals("회원정보수정이 완료되었습니다.")){
                    Toast.makeText(MyInfoLinkActivity.this, s, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MyInfoLinkActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MyInfoLinkActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/userInfoUpdate.php";
            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String token = params[0];
                if(!updateEmail.equals("null")){
                    if(!updateBirth.equals("null")){
                        updateInfo = "email="+updateEmail+"&birth="+updateBirth+"&token="+token;
                    } else {
                        updateInfo = "email="+updateEmail+"&token="+token;
                    }
                } else {
                    if(!updateBirth.equals("null")){
                        updateInfo = "birth="+updateBirth+"&token="+token;
                    }
                }

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(updateInfo.getBytes("UTF-8"));
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
                String line;

                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }
                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("MyInfoLinkActivity","InfoUpdate Exception : " + e);
                return null;
            }

        }
    }
}
