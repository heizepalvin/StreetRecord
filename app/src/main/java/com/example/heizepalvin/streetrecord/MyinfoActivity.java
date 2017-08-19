package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by soyounguensoo on 2017-08-05.
 */

public class MyinfoActivity extends AppCompatActivity {

    private TextView infoId;
    private EditText infoPwd;
    private EditText infoPwd2;
    private TextView infoPwdConfirm;
    private TextView infoEmail;
    public EditText infoEmailEdit;
    private TextView infoBirth;
    private EditText infoBirthEdit;
    private TextView infoEmailConfirm;
    private Button infoBtn;
    private Button infoCancelBtn;
    private TextView infoEmailInputBtn;

    private String memberEmail;
    private String postData;

    private Boolean memberEmailBoolean = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_activity);

        infoId = (TextView) findViewById(R.id.infoLinkActName);
        infoPwd = (EditText) findViewById(R.id.infoLinkActEmailEdit);
        infoPwd2 = (EditText) findViewById(R.id.infoActPwd2);
        infoPwdConfirm = (TextView) findViewById(R.id.infoActPwdConfirm);
        infoEmail = (TextView) findViewById(R.id.infoActEmail);
        infoEmailEdit = (EditText) findViewById(R.id.infoActEmailEdit);
        infoEmailConfirm = (TextView) findViewById(R.id.infoActEmailConfirm);
        infoBirth = (TextView) findViewById(R.id.infoActBirth);
        infoBirthEdit = (EditText) findViewById(R.id.infoActBirthEdit);
        infoBtn = (Button) findViewById(R.id.infoActBtn);
        infoCancelBtn = (Button) findViewById(R.id.infoActCancel);
        infoEmailInputBtn = (TextView) findViewById(R.id.infoActEmailInputBtn);

        SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
        String userID = pref.getString("userID","null");
        if(!userID.equals("null")){
            infoId.setText(userID);
            getMemberInfo getInfo = new getMemberInfo();
            getInfo.execute(userID);
        }

        infoPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",infoPwd.getText().toString())){
                    infoPwdConfirm.setTextColor(Color.RED);
                    infoPwdConfirm.setText("비밀번호 형식이 맞지 않습니다.");
                } else {
                    if(infoPwd.getText().toString().equals(infoPwd2.getText().toString())){
                        infoPwdConfirm.setTextColor(Color.GREEN);
                        infoPwdConfirm.setText("비밀번호가 일치합니다.");
                    } else {
                        infoPwdConfirm.setTextColor(Color.RED);
                        infoPwdConfirm.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        infoPwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{8,20}$",infoPwd.getText().toString())){
                    infoPwdConfirm.setTextColor(Color.RED);
                    infoPwdConfirm.setText("비밀번호 형식이 맞지 않습니다.");
                } else {
                    if(infoPwd.getText().toString().equals(infoPwd2.getText().toString())){
                        infoPwdConfirm.setTextColor(Color.GREEN);
                        infoPwdConfirm.setText("비밀번호가 일치합니다.");
                    } else {
                        infoPwdConfirm.setTextColor(Color.RED);
                        infoPwdConfirm.setText("비밀번호가 일치하지 않습니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    infoEmailInputBtn.setTextColor(Color.GREEN);
                    infoEmailInputBtn.setText("올바른 이메일 형식입니다.");
                } else {
                    infoEmailInputBtn.setTextColor(Color.RED);
                    infoEmailInputBtn.setText("이메일 형식이 맞지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        infoEmailInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoEmailEdit.setVisibility(View.VISIBLE);
                infoEmail.setVisibility(View.GONE);
                infoEmailInputBtn.setText("이메일을 입력해주세요.");
                infoEmailConfirm.setVisibility(View.VISIBLE);
            }
        });

        infoEmailConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoEmailInputBtn.setText("이메일변경하기");
                String textColor = "#B7F0B1";
                infoEmailInputBtn.setTextColor(Color.parseColor(textColor));
                infoEmailEdit.setVisibility(View.GONE);
                infoEmail.setVisibility(View.VISIBLE);
                infoEmailConfirm.setVisibility(View.GONE);
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infoPwd.getText().toString().length() == 0){
                    Toast.makeText(MyinfoActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    infoPwd.requestFocus();
                } else if(infoPwd2.getText().toString().length() == 0){
                    Toast.makeText(MyinfoActivity.this, "비밀번호 확인 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    infoPwd2.requestFocus();
                } else if(infoEmailInputBtn.getText().toString().equals("이메일을 입력해주세요.") && infoEmailEdit.getText().toString().length() == 0){
                    Toast.makeText(MyinfoActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    infoEmailEdit.requestFocus();
                } else if(infoPwdConfirm.getText().toString().equals("비밀번호 형식이 맞지 않습니다.")){
                    Toast.makeText(MyinfoActivity.this, "비밀번호를 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    infoPwd.requestFocus();
                } else if(infoPwdConfirm.getText().toString().equals("비밀번호가 일치하지 않습니다.")){
                    Toast.makeText(MyinfoActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    infoPwd.requestFocus();
                } else if(infoEmailInputBtn.getText().toString().equals("이메일 형식이 맞지 않습니다.")){
                    Toast.makeText(MyinfoActivity.this, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    infoEmailEdit.requestFocus();
                } else if(infoEmailInputBtn.getText().toString().equals("이메일을 입력해주세요.")){
                    Toast.makeText(MyinfoActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    updateMemberInfo update = new updateMemberInfo();
                    String memberId = infoId.getText().toString();
                    String memberPassword = infoPwd.getText().toString();
                    if(infoEmailEdit.getText().toString().length() != 0){
                        String memberEmail = infoEmailEdit.getText().toString();
                        update.execute(memberId,memberPassword,memberEmail);
                        memberEmailBoolean = true;
                    } else {
                        update.execute(memberId,memberPassword);
                    }
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

    private class getMemberInfo extends AsyncTask<String,Void,String>{

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyinfoActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s != null){
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("memberInfo");

                    JSONObject item = jsonArray.getJSONObject(0);

                    String email = item.getString("email");
                    String birth = item.getString("birth");

                    infoEmail.setText(email);
                    infoBirth.setText(birth);


                } catch (JSONException e) {
                    Log.e("MyinfoActivity","JSON Exception = " + e);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/getMemberInfo.php";

            try{
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String id = params[0];
                String info = "id="+id;
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(info.getBytes("UTF-8"));
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

                while ((line=bufferedReader.readLine())!= null){
                    sb.append(line+"\n");
                }
                bufferedReader.close();

                Log.e("asdfasdf",sb.toString().trim());
                return sb.toString().trim();

            }catch (Exception e){

                Log.e("MyinfoActivity","Info exception : " + e);
                return null;
            }
        }
    }

    private class updateMemberInfo extends AsyncTask<String, Void, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(MyinfoActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            progressDialog.dismiss();
            Toast.makeText(MyinfoActivity.this, s , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyinfoActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/updateMemberInfo.php";

            String memberId = params[0];
            String memberPwd = params[1];
            if(memberEmailBoolean){
                memberEmail = params[2];
                memberEmailBoolean = false;
            }
            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();


                if(memberEmail != null){
                    postData = "id="+memberId+"&password="+memberPwd+"&email="+memberEmail;
                } else {
                    postData = "id="+memberId+"&password="+memberPwd;
                }

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
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
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();
            }catch (Exception e){

                Log.e("MyinfoActivity","Exception : " + e );
                return null;
            }

        }
    }

}
