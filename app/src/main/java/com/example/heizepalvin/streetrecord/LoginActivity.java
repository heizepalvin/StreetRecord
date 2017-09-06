package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.Logger;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;

/**
 * Created by soyounguensoo on 2017-06-28.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText idInput;
    private EditText pwdInput;
    private Button loginBtn;
    private Button joinBtn;

    private LoginButton facebookLogin;
    private CallbackManager callbackManager;

    SessionCallback callback;

    private Button fakeFacebook;
    private Button fakeKakao;
    private com.kakao.usermgmt.LoginButton kakaoLogin;


    private String facebookParamsEmail;
    private String facebookParamsBirth;
    private String facebookUserInfo;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //카카오톡 해시키 생성
//        try
//            PackageInfo info = getPackageManager().getPackageInfo("com.example.heizepalvin.streetrecord", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures){
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //카카오톡

        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });



        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        kakaoLogin = (com.kakao.usermgmt.LoginButton) findViewById(R.id.loginActKakao);


        //카카오톡 끝

        //페이크 이미지

        fakeFacebook = (Button) findViewById(R.id.loginActFakeFacebook);
        fakeKakao = (Button) findViewById(R.id.loginActFakeKakao);

        fakeFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin.performClick();
            }
        });

        fakeKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoLogin.performClick();
            }
        });

        //끝

        idInput = (EditText) findViewById(R.id.loginActID);
        pwdInput = (EditText) findViewById(R.id.loginActPwd);
        loginBtn = (Button) findViewById(R.id.loginActLoginBtn);
        joinBtn = (Button) findViewById(R.id.loginActJoinBtn);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,JoinActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberLogin login = new memberLogin();
                String id = idInput.getText().toString();
                String pwd = pwdInput.getText().toString();
                login.execute(id,pwd);
            }
        });

        //페이스북 연동
        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        final Boolean loginBool = preferences.getBoolean("login",false);
        if(!loginBool){
            LoginManager.getInstance().logOut();
        }

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) findViewById(R.id.loginActFacebook);
        facebookLogin.setReadPermissions("public_profile","user_friends","email","user_birthday");
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("토큰",loginResult.getAccessToken().getToken());
                Log.e("유저아이디",loginResult.getAccessToken().getUserId());
                Log.e("퍼미션 리스트",loginResult.getAccessToken().getPermissions()+"");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try{
                            SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("login",true);
                            editor.putString("userName",object.getString("name"));
                            editor.putString("token",object.getString("id"));
                            Log.e("name",object.getString("name"));
                            Log.e("user profile",object.toString());
                            Log.e("user아이디",object.getString("id"));
                            if(!object.toString().contains("email")){
                                Log.e("facebookLoginUserEmail","이메일이 없음");
                                facebookParamsEmail = "null";
                            } else{
                                Log.e("facebookLoginUserEmail","이메일이 있음");
//                                editor.putString("userEmail",object.getString("email"));
                                facebookParamsEmail = object.getString("email");
                            }

                            if(!object.toString().contains("birthday")){
                                Log.e("facebookLoginUserBirthday","생년월일이 없음");
                                facebookParamsBirth = "null";
                            } else {
                                Log.e("facebookLoginUserBirthday","생년월일이 있음");
//                                editor.putString("userBirthday",object.getString("birthday"));
                                String birthSplit[] = object.getString("birthday").split("/");
                                facebookParamsBirth = birthSplit[2] + birthSplit[0] + birthSplit[1];
                            }
                            editor.putString("loginLink","Facebook");
                            SharedPreferences pref2 = getSharedPreferences("facebook",MODE_PRIVATE);
                            Boolean facebookLogin = pref2.getBoolean("facebookLogin",false);
                            if(!facebookLogin){
                                memberLoginFaceBook fbLogin = new memberLoginFaceBook();
                                fbLogin.execute(object.getString("name"),"Facebook",object.getString("id"));
                                Log.e("페이스북로그인","페이스북로그인 false");
                            } else {
                                Toast.makeText(LoginActivity.this, "페이스북으로 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                Log.e("페이스북로그인","페이스북로그인 true");
                            }
                            loginBoolean = true;
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                            Bundle facebookData = getFacebookData(object);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });



    }

    private Bundle getFacebookData(JSONObject object){

        try{
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            try{
                URL profile_pic = new URL("https://graph.facebook.com/"+id+"/picture?width=200&height=150");
                bundle.putString("profile_pic",profile_pic.toString());
            }catch (MalformedURLException e){
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook",id);
            if(object.has("email")){
                bundle.putString("email",object.getString("email"));
            }
            if(object.has("gender")){
                bundle.putString("gender",object.getString("gender"));
            }
            if(object.has("birthday")){
                bundle.putString("birthday",object.getString("birthday"));
            }
            if(object.has("name")){
                bundle.putString("name",object.getString("name"));
            }
            return bundle;
        } catch (JSONException e){
            Log.e("json","Error parsing JSON");
        }
        return null;
    }



    private class SessionCallback implements ISessionCallback{

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Log.e("Failure",message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if(result == ErrorCode.CLIENT_ERROR_CODE){
                        finish();
                    } else {

                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile result) {
                    Log.e("kakaoLogin","카카오로그인이에요123");
                    SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("login",true);
                    editor.putString("userName",result.getNickname());
                    editor.putString("token", String.valueOf(result.getId()));
//                    editor.putString("userEmail",result.getEmail());
                    editor.putString("loginLink","KakaoTalk");
                    editor.commit();
                    SharedPreferences pref2 = getSharedPreferences("kakao",MODE_PRIVATE);
                    Boolean kakaoLogin = pref2.getBoolean("kakaoLogin",false);
                    if(!kakaoLogin) {
                        memberLoginKakao kakaoLoginDB = new memberLoginKakao();
                        kakaoLoginDB.execute(result.getNickname(), result.getEmail(), "KAKAO", String.valueOf(result.getId()));
                        Log.e("kakaoLogin","카카오로그인이에요");
                    } else {
                        Toast.makeText(LoginActivity.this, "카카오톡으로 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("kakaoLogin","카카오로그인이에요2");
                    }
                    Log.e("UserProfile", result.toString());
                    Log.e("UserEmail", result.getEmail());
                    Log.e("UserID", String.valueOf(result.getId()));
                    loginBoolean = true;
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

        }
    }



    private class memberLogin extends AsyncTask<String, Void, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                if(result.equals("로그인 되었습니다.")){
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("login",true);
                    editor.putString("userID",idInput.getText().toString());
                    editor.commit();
                    loginBoolean = true;
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    idInput.requestFocus();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/login.php";

            try{
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String id = params[0];
                String password = params[1];
                String info = "id="+id+"&password="+password;
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

                while((line=bufferedReader.readLine())!= null){
                    sb.append(line+"\n");
                }
                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("LoginActivity","Loging Exception : " + e);
                return null;
            }

        }
    }

    //카카오톡 로그인 db연동

    private class memberLoginKakao extends AsyncTask<String, Void, String>{



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if(s != null){
                if(s.equals("카카오톡으로 로그인 되었습니다.")){
                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = getSharedPreferences("kakao",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("kakaoLogin",true);
                    editor.commit();

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/loginKakao.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String userName = params[0];
                String userEmail = params[1];
                String userLink = params[2];
                String userToken = params[3];

                String userInfo = "userName="+userName+"&userEmail="+userEmail+"&userLink="+userLink+"&userToken="+userToken;
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(userInfo.getBytes("UTF-8"));
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

                Log.e("카카오톡로그인연동",sb.toString().trim());

                return sb.toString().trim();



            }catch (Exception e){

                Log.e("LoginActivity","Login Exception : " + e);
                return null;
            }
        }
    }

    //페이스북 로그인 db연동

    private class memberLoginFaceBook extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!= null){
                if(s.equals("페이스북으로 로그인 되었습니다.")){
                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = getSharedPreferences("facebook",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("facebookLogin",true);
                    editor.commit();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/login/loginFacebook.php";

            try{
                 URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String userName = params[0];
                String userLink = params[1];
                String userToken = params[2];
                if(!facebookParamsEmail.equals("null")){
                    if(!facebookParamsBirth.equals("null")){
                        facebookUserInfo = "userName="+userName+"&userEmail="+facebookParamsEmail+"&userBirth="+facebookParamsBirth+"&userLink="+userLink+"&userToken="+userToken;
                    } else {
                        facebookUserInfo = "userName="+userName+"&userEmail="+facebookParamsEmail+"&userLink="+userLink+"&userToken="+userToken;
                    }
                } else {
                    if(!facebookParamsBirth.equals("null")){
                        facebookUserInfo = "userName="+userName+"&userBirth="+facebookParamsBirth+"&userLink="+userLink+"&userToken="+userToken;
                    } else {
                        facebookUserInfo = "userName="+userName+"&userLink="+userLink+"&userToken="+userToken;
                    }
                }
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(facebookUserInfo.getBytes("UTF-8"));
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

                while ((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }
                bufferedReader.close();
                Log.e("memberLoginFacebook",sb.toString().trim());
                return sb.toString().trim();

            } catch (Exception e){
                Log.e("LoginActivity","facebookLogin Exception : " + e);
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode,data)){
            return;
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
