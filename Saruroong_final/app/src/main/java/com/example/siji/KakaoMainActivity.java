package com.example.siji;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KakaoMainActivity extends KakaoBaseActivity {
    String kakaoID = ""; // userProfile에서 ID값을 가져옴
    Long userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.start_main_a);
        System.out.println("★ MainActivity로 이동 수지");

        onClickUnlink();

    }
    //앱 연결 해제 메소드
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                        KakaoSignupClass();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        // redirectSignupActivity();
                                        KakaoSignupClass();

                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        System.out.println("MainActivity 탈퇴절차 확인 성공 수지");
                                        kakaoID = userId+"";
                                        try {String result  = new CustomTask().execute(kakaoID).get();}catch (Exception e) {}
                                        KakaologinActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.d("failed to get user info. msg=" + errorResult);
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    //redirectLoginActivity();
                    KakaologinActivity();
                }
            }
            @Override//세션이 닫혀 실패한 경우
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
                System.out.println("MainActivity : onSessionClosed 수지");
                KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {}

            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                System.out.println(kakaoID+" : 수지 onSuccess");
                Logger.d("UserProfile 수지 onSuccess : " + userProfile);

                /*try {String result  = new CustomTask().execute(kakaoID).get();}catch (Exception e) {}*/

                //* SplachActivity();*//*
                System.out.println(kakaoID+" : 수지 CustomTask");

            }
        }, propertyKeys,false);
    }


    //데이터 주고받기 메서드 쓰래드화
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg, sendMsg1;
        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br=null;
            StringBuffer result = new StringBuffer();
            System.out.println("MainActivity/CustomTask 수지" + kakaoID);
            try {
                String data;

                URL url = new URL("http://hsvtr365.cafe24.com/member_delete.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "kakaoid="+kakaoID;

                osw.write(sendMsg);
                osw.flush();//flush : stream에 남아 있는 데이터를 강제로 내보내는 역할

                if(conn.getResponseCode() == conn.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ((data = br.readLine()) != null) {
                        result.append(data);
                    }
                    // 리터값값
                    receiveMsg = result.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }
            } catch (MalformedURLException e) {e.printStackTrace();
            } catch (IOException e) {e.printStackTrace();}
            System.out.println("MainActivity/CustomTask 종료 수지 : " + kakaoID +" / receiveMsg : "+receiveMsg);
            return receiveMsg;
        }
    }
}
