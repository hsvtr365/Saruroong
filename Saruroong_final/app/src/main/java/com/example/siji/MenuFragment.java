package com.example.siji;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Admin on 31-05-2017.
 */

public class MenuFragment extends Fragment implements View.OnTouchListener {

    public static final String baseURL = "";
    private ProgressDialog progressDialog;
    ImageView profile;
    Bitmap bitmap;
    GestureDetector gestureDetector;
    String profileImage = "";
    String type;
    Uri imageuri = null;
    int kakaoID = 1;
    int condition =1;
    String nickname ="";
    TextView nicknameTV;
    TextView nowTV;
    String strCondition="";

    private View rootView;

    protected void requestMe() { //유저의 정보를 받아오는 함수

        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                KakaoBaseActivity kakao= new  KakaoBaseActivity();
                Logger.d("failed to get user info. msg=" + errorResult);
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //finish();
                } else {
                    //redirectLoginActivity();
                    kakao.KakaologinActivity();
                }
            }
            @Override//세션이 닫혀 실패한 경우
            public void onSessionClosed(ErrorResult errorResult) {
                KakaoBaseActivity kakao= new  KakaoBaseActivity();
                //redirectLoginActivity();
                System.out.println("MainActivity : 어머나 세션이 닫혀버렷내!");
                kakao.KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {
            }
            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                kakaoID = (int)userProfile.getId(); // userProfile에서 ID값을 가져옴
                System.out.println(kakaoID+" :카카오 아이듸");
                Logger.d("UserProfile : " + userProfile);
                /*try {String result  = new CustomTask().execute(kakaoID).get();}catch (Exception e) {}*/
                //* SplachActivity();*//*
                System.out.println(kakaoID+" : 아이디 CustomTask");
                readServer("userInfo");
                //readServer("userInfo");
            }
        }, propertyKeys,false);
        //? 위 저거는 뭐지?
    }

    ////////////////////////////////////////
    //////////////////// 온크리에이트
    /////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //kakaoID = getArguments().getInt("kakaoID");
        //System.out.println("과연 프래그먼트는 값을 제대로 받았을까?"+kakaoID);
        rootView = inflater.inflate(com.example.siji.R.layout.slide_menu, container, false);
        LinearLayout root = (LinearLayout) rootView.findViewById(com.example.siji.R.id.rootLayout);
        profile =(ImageView)rootView.findViewById(R.id.profile_image);
        nicknameTV =(TextView)rootView.findViewById(R.id.nicknameTV);
        nowTV = (TextView)rootView.findViewById(R.id.nowTV);
        // 썸네일이 널일 경우와 아닐경우를 분리하여 다음의 메소드로 넘어간다 메소드명 :
        //카카오아이디 전달받을 방법 생각해야한다 (인텐트가 아니기 때문)
        // 프래그먼트로 값을 전달하는 방법

         /* 프로필 이미지를 설정해주는 부분
        프로필 사진 드로어블에 등록하고
        null일 경우 그걸로 설정, 아닐 경우
        카톡 아듸값 웨어절로 불러서 셀렉트한
        프로필 경로 불러와서ㅓ 이미지 보여줄것 */

        requestMe();
        //profile.setImageURI(imageuri);

        ImageView iv1 = (ImageView)rootView.findViewById(com.example.siji.R.id.mypageView);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), MyPageActivity.class);
                intent1.putExtra("kakaoID",kakaoID);
                startActivity(intent1);
            }
        });

        ImageView iv2 = (ImageView)rootView.findViewById(com.example.siji.R.id.setView);
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), SetActivity.class);
                startActivity(intent2);
            }
        });
        return rootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
    /// 텍스트 뷰 셋 메소드

    private void textViewSet(){
        nicknameTV.setText(nickname);
        nowTV.setText(strCondition);
    }
    ///////// 이미지 뷰 셋 메소드
    private void imageViewSet(){
        Thread mthread = new Thread(){
            @Override
            public void run() {
                try{
                    // 아래 한 줄은 테스트용으로 사용자의 프로필 이미지 받으려면 아래 한 줄 주석처리 하세용
                    // profileImage="";
                    URL url = new URL(profileImage);
                    System.out.println("이미지뷰셋안의 유알엘"+url);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }catch (IOException e){
                }
            }
        };
        mthread.start();
        try{
            mthread.join();
            if(profileImage.equals("")){
                profile.setImageResource(R.drawable.thumb_null);
            }else {
                profile.setImageBitmap(bitmap);
            }
        }catch (InterruptedException e){}

     /*   URL url = new URL("http://k.kakaocdn.net/dn/wiLVv/btqiobRCIkN/z8hTwiXPK5yugNbe6hVraK/profile_110x110c.jpg");
        System.out.println("이미지뷰셋안의 유알엘"+url);
        URLConnection conn = url.openConnection();
        conn.connect();
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
        Bitmap bm = BitmapFactory.decodeStream(bis);
        bis.close();
        profile.setImageBitmap(bm);
    } catch (Exception e) {
    }
        /*
        if(profileImage.equals("")){
            profile.setImageResource(R.drawable.thumb_null);
        }else {
            imageuri=Uri.parse(profileImage);
            profile.setImageURI(imageuri);
            System.out.println("이미지뷰셋"+imageuri);
        }*/
    }

    private void readServer(String types) {
        System.out.println("메뉴 프래그먼트 리드서버안의 카카오아이디를 찍어보자"+kakaoID);
        type = types;
        System.out.println("메뉴 프래그먼트 리드서버안의 타입을 찍어보면 "+type);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url = null;
                try {
                    if(type.equals("profileImage")){
                        url = new URL("http://hsvtr365.cafe24.com/Slide.jsp?kakaoID="+kakaoID+"&type="+type);
                    }else if(type.equals("userInfo")){
                        url = new URL("http://hsvtr365.cafe24.com/Slide.jsp?kakaoID="+kakaoID+"&type="+type);
                    }else if(type.equals("diaryModify")){
                        url = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type=diaryModify&date=");
                    }
                    System.out.println("유알엘을 찍어본다"+url);
                    // 타입 모디파이일 경우
                    // 타입 딜리트일 경우 두가지 만들어줘야함

                    conn = (HttpURLConnection)url.openConnection();
                    if(conn != null) {
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            System.out.println("연결 성공");
                            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                            String data = null;
                            while((data = br.readLine()) != null) {
                                result.append(data);
                            }
                        } else {
                            System.out.println("서버 오류");
                        }
                    } else {
                        System.out.println("연결 불가");
                    }
                } catch (IOException e) {
                    System.out.println("[에러] : " + e.getMessage());
                } finally {
                    if(br != null) try { br.close(); } catch(IOException e) {
                        System.out.println("[에러] : " + e.getMessage());
                    }
                    if(conn != null) conn.disconnect();
                }
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                StringBuffer result = (StringBuffer)msg.obj;
                String json = result.toString();
                System.out.println(json);
                //profileImage = jsonObject.getString("image");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    profileImage = jsonObject.getString("image");
                    condition = jsonObject.getInt("condition");
                    nickname=jsonObject.getString("nickname");

                    //0 : 쉬는 중 / 1: 짝사랑/2:  썸타는 중/ 3: 행복한 연애 중/4:  슬픈 연애 중 / 5: 이별
                    System.out.println("컨디션 값"+condition);
                    if(condition==0){
                        strCondition = "쉬는 중";
                    }else if(condition==1){
                        strCondition = "짝사랑";
                    }else if(condition==2){
                        strCondition = "썸타는 중";
                    }else if(condition==3){
                        strCondition = "행복한 연애 중";
                    }else if(condition==4){
                        strCondition = "슬픈 연애 중";
                    }else if(condition==5){
                        strCondition = "이별";
                    }else if(condition==6){
                        strCondition = "당신의 연애상태는?";
                    }
                    System.out.println("핸들러 튜라이 안의 스트링컨디션"+strCondition);
                    textViewSet();
                    imageViewSet();
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    };
}
