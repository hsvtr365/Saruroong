/**
 * Copyright 2014-2015 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.siji;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KakaoSignup extends KakaoBaseActivity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    String kakaoID = ""; // userProfile에서 ID값을 가져옴
    String kakaoNickname = "";     // Nickname 값을 가져옴
    String kakaoProfile = "";//11

    private  void diarySave(String kakaoID){
        try{
            //여기의 313은 임의의 doy다. 사용자의 설치일을 doy로 컨버팅 하여 저장할 수 있다면 베스트일것이다...
            BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() +"313.txt", false));
            bw.write("사루룽에 오신것을 환영합니다~ 당신의 사루룽을 응원한다눙여~");
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(KakaoSignup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("♥♥ KakaoSignup 수지");
        requestMe();
        Logger.d("수지수지"+1);

    }
    protected void requestMe() { //유저의 정보를 받아오는 함수
        List<String> propertyKeys = new ArrayList<String>();
        System.out.println("requestMe 수지수지");
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "수지 failed to get user info. msg=" + errorResult;
                System.out.println(message+"onFailure 수지");//이거 프린트 됨...
                Logger.d(message);
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    KakaologinActivity();
                }
            }
            @Override//세션이 닫혀 실패한 경우
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("onSessionClosed 수지");
                KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {
                System.out.println("onNotSignedUp  수지");
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                kakaoNickname = userProfile.getNickname();     // Nickname 값을 가져옴
                kakaoProfile = userProfile.getProfileImagePath();//원본 크기의 카카오톡 썸네일 프로필 이미지 URL

                System.out.println(kakaoID+" : 수지 onSuccess");
                System.out.println(kakaoNickname+" : 수지 onSuccess");
                System.out.println(kakaoProfile+" : 수지 onSuccess");
                Logger.d("UserProfile 수지 onSuccess : " + userProfile);

                try {
                    String result  = new CustomTask().execute(kakaoID, kakaoNickname, kakaoProfile).get();

                }catch (Exception e) {}


                MainActivity1();
            }
        }, propertyKeys,false);
    }

    /////////////////////////////////////////////   JSP로 정보를 보내고, 받는 클래스 ///////////////////////////////////////////////////////


    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg, sendMsg1;
        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br=null;
            StringBuffer result = new StringBuffer();
            System.out.println(kakaoID+" kakaoID/CustomTask 수지");
            diarySave(kakaoID);
            try {
                String data;

                URL url = new URL("http://hsvtr365.cafe24.com/member_insert.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "kakaoid="+kakaoID+"&nickname="+kakaoNickname+"&thumbnail_image="+kakaoProfile;
                osw.write(sendMsg);
                osw.flush();//flush : stream에 남아 있는 데이터를 강제로 내보내는 역할

                if(conn.getResponseCode() == conn.HTTP_OK) {
                    br = new BufferedReader(new  InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ((data = br.readLine()) != null) {
                        result.append(data);
                    }
                    // 리터값값
                    receiveMsg = result.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}