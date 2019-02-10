package com.example.siji;

import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;


public class KakaologinA extends KakaoBaseActivity {
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_login);
        System.out.println("♥♥ KakaologinA 수지");
        //액션바 있으면 가져와
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {//연결성공시 redirectSignupActivit 호츌
            KakaoSignupClass();
            Logger.e("★카카오 로그인 엑티비티/세션연결성공 수지");
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {//세션연결  실패. 로그찍고 뒤로감.
            if(exception != null) {
                Logger.e("★"+exception+"★카카오 로그인 엑티비티/세션연결실패 수지");
                KakaoSignupClass();
            }
        }
    }
}