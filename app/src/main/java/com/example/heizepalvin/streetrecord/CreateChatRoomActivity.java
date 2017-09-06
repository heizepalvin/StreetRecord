package com.example.heizepalvin.streetrecord;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.inspector.database.ContentProviderSchema;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;

/**
 * Created by soyounguensoo on 2017-08-23.
 */

public class CreateChatRoomActivity extends AppCompatActivity {

    private EditText createTitle;
    private TextView titleCount;
    private Spinner genreSelect;
    private EditText createGenre;
    private TextView genreCount;
    private Button createChatBtn;
    private Button createChatCancelBtn;
    private Button createImgBtn; // 채팅방 이미지 설정 버튼
    private ImageView createRoomImg; //채팅방 이미지

    private static final int PICK_FROM_CAMERA = 0; //카메라로 촬영해서 가져왔을 때
    private static final int PICK_FROM_ALBUM = 1; // 앨범에서 가져왔을 때
    private static final int CROP_FROM_IMAGE = 2; //CROP해서 이미지를 가져왔을 때

    private Uri mImageCaptureUri; // 임시로 사용할 파일 경로를 가져올 때 사용

    private String absolutePath;

    private File tempFile;

    // 권한 설정 변수

    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101; // 권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단 break없이 진행 합니다.
                // 실제 코드에서는 좀 더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {


                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");

                this.grantUriPermission("com.android.camera",mImageCaptureUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,0);
                Log.e("list.get 0 " , list.get(0).activityInfo.packageName);
                grantUriPermission(list.get(0).activityInfo.packageName, mImageCaptureUri , Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                int size = list.size();
                if(size==0){
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else{

                    Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("crop","true");
//                    intent.putExtra("outputX",100); //CROP한 이미지의 x축 크기
//                    intent.putExtra("outputY",100); //CROP한 이미지의 y축 크기
                    intent.putExtra("aspectX",3); // CROP 박스의 X축 비율
                    intent.putExtra("aspectY", 3); // CROP 박스의 Y축 비율
                    intent.putExtra("scale",true);
                    File croppedFileName = null;

                    try{
                        croppedFileName = createImageFile();

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    File folder = new File(Environment.getExternalStorageDirectory()+"/StreetRecord/");
                    tempFile = new File(folder.toString(), croppedFileName.getName());

                    mImageCaptureUri = FileProvider.getUriForFile(CreateChatRoomActivity.this,"com.example.heizepalvin.streetrecord.provider", tempFile);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.putExtra("return-data",false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());


                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(res.activityInfo.packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION| Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(i,CROP_FROM_IMAGE);

                }
//                startActivityForResult(intent, CROP_FROM_IMAGE); // CROP_FROM_IMAGE case문으로 이동

                break;
            }

            case CROP_FROM_IMAGE:{

              try{
                  Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageCaptureUri);
                  Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);
//                  ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                  thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); // 이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

//                  Glide.with(this).load(bs.toByteArray()).asBitmap().into(createRoomImg);
                  createRoomImg.setImageBitmap(thumbImage);
                  getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));

              }catch (Exception e){
                  Log.e("ERROR", e.getMessage());
              }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chatroom_activity);

        checkPermissions();

        createTitle = (EditText) findViewById(R.id.createChatActTitle);
        titleCount = (TextView) findViewById(R.id.createChatActTitleCount);
        genreSelect = (Spinner) findViewById(R.id.createChatActGenreSpinner);
        createGenre = (EditText) findViewById(R.id.createChatActGenreEdit);
        genreCount = (TextView) findViewById(R.id.createChatActGenreCount);
        createChatBtn = (Button) findViewById(R.id.createChatActCreateBtn);
        createChatCancelBtn = (Button) findViewById(R.id.createChatActCancelBtn);
        createImgBtn = (Button) findViewById(R.id.createChatActImgBtn);
        createRoomImg = (ImageView) findViewById(R.id.createChatActImg);

        createTitle.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                titleCount.setText(createTitle.getText().toString().length() + "/50");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        genreSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("직접입력")){
                    createGenre.setVisibility(View.VISIBLE);
                    genreCount.setVisibility(View.VISIBLE);
                } else {
                    createGenre.setVisibility(View.GONE);
                    genreCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createGenre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                genreCount.setText(createGenre.getText().toString().length() + "/50");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createChatCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createTitle.getText().toString().length()==0){
                    Toast.makeText(CreateChatRoomActivity.this, "채팅방 이름을 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else if(createGenre.getVisibility() == View.VISIBLE){
                    if(createGenre.getText().toString().length() == 0){
                        Toast.makeText(CreateChatRoomActivity.this, "장르를 설정해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat formatNow = new SimpleDateFormat("yy/MM/dd HH:mm");
                    String formatDate = formatNow.format(date);

                    MusicChatItem item = new MusicChatItem(createTitle.getText().toString(),genreSelect.getSelectedItem().toString(),"1",date);
                    Log.e("채팅방몇개?",chatItems.size()+"");
                    chatItems.add(item);
                    Log.e("채팅방몇개?",chatItems.size()+"");
                    Intent intent = new Intent(CreateChatRoomActivity.this,MusicChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //이미지 설정 버튼 클릭 시
        createImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"앨범에서 사진 선택","사진 촬영","사진 삭제","취소"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateChatRoomActivity.this);

                alertDialogBuilder.setTitle("채팅방 이미지 설정"); // 다이얼로그 제목 설정

                // 다이얼로그 아이템 선택 시 이벤트
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //앨범에서 사진 가져오기
                        if(items[which].equals("앨범에서 사진 선택")){


                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent,PICK_FROM_ALBUM);
                            Toast.makeText(CreateChatRoomActivity.this, "앨범으로 이동합니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                        // 사진 촬영해서 가져오기
                        else if(items[which].equals("사진 촬영")) {

                            //카메라,외부저장소 권한 설정

//                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                    //임시로 사용할 파일의 경로를 생성
//                                    String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//                                    mImageCaptureUri = FileProvider.getUriForFile(CreateChatRoomActivity.this,"com.example.heizepalvin.streetrecord.provider",new File(Environment.getExternalStorageDirectory(),url));
//
//                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//                                    startActivityForResult(intent, PICK_FROM_CAMERA);
//                                    Toast.makeText(CreateChatRoomActivity.this, "두번째 선택 촬영", Toast.LENGTH_SHORT).show();
//                                    dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File photoFile = null;

                            try{
                                photoFile = createImageFile();
                            } catch (IOException e){
                                Toast.makeText(CreateChatRoomActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            if(photoFile!=null){
                                mImageCaptureUri = FileProvider.getUriForFile(CreateChatRoomActivity.this,"com.example.heizepalvin.streetrecord.provider",photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            }
                            dialog.dismiss();

                        }
                        // 사진 삭제하기 ( 기본 이미지로 )
                        else if(items[which].equals("사진 삭제")) {

                            createRoomImg.setImageResource(R.drawable.logos);
                            Toast.makeText(CreateChatRoomActivity.this, "세번째 선택 사진 삭제", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                        // 다이얼로그 닫기
                        else {

                            Toast.makeText(CreateChatRoomActivity.this, "네번째 선택 취소", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                // 다이얼로그 생성

                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기

                alertDialog.show();
            }

        });




    }

    //비트맵을 저장하는 부분
//
//    private void storeCropImage(Bitmap bitmap, String filePath){
//
//        //StreetRecord 폴더를 생성하여 이미지를 저장하는 방식이다.
//        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreetRecord";
//        File directory_StreetRecord = new File(dirPath);
//
//        //StreetRecord 디렉토리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
//        if(!directory_StreetRecord.exists()){
//            directory_StreetRecord.mkdir();
//        }
//
//        File copyFile = new File(filePath);
//        BufferedOutputStream out = null;
//
//        try{
//
//            copyFile.createNewFile();
//            out = new BufferedOutputStream(new FileOutputStream(copyFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);
//
//            //sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
//
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
//
//            out.flush();
//            out.close();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private boolean checkPermissions(){

        int result;

        List<String> permissionList = new ArrayList<>();
        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(this, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                // 사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            //권한이 추가 되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청한다.

            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]),MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MULTIPLE_PERMISSIONS:
                if(grantResults.length > 0){
                    for(int i = 0; i<permissions.length; i++){
                        if(permissions[i].equals(this.permissions[0])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else if(permissions[i].equals(this.permissions[1])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else if(permissions[i].equals(this.permissions[2])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }
                } else {
                    Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                return;

        }
    }

    private File createImageFile() throws IOException{

        //이미지 파일 만들기

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/StreetRecord/"); //streetRecord 경로에 이미지를 저장하기 위함
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        return image;
    }
}
