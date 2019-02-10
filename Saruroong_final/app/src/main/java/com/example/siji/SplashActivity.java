package com.example.siji;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.sql.SQLOutput;

public class SplashActivity extends KakaoBaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_splash);


        Log.d("requestAccessTokenInfo","실행");
        //requestAccessTokenInfo(); 토큰 여기서 받을 필요 없지.
        Log.d("requestAccessTokenInfo","종료");

        getAppKeyHash();

        //카카오 로그인이 되어있는지 안되어있는지 검사합니다.
        findViewById(R.id.splash_Layout).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Session.getCurrentSession().checkAndImplicitOpen()) {//로그인이 안되있을경우 로그인페이지로
                    Intent intent = new Intent(SplashActivity.this, KakaologinA.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity1.class);//로긴 되있을경우 홈으로
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }, 1500);
    }

    private  void requestAccessTokenInfo(){
        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {}

            @Override
            public void onNotSignedUp() {// not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
                super.onFailure(errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse result) {
                long userId = result.getUserId();
                System.out.println("수지 requestAccessTokenInfo "+userId);
                Logger.d("this access token is for userId 수지=" + userId);

                long expiresInMilis = result.getExpiresInMillis();
                System.out.println("수지 requestAccessTokenInfo "+expiresInMilis);
                Logger.d("this access token expires after 수지" + expiresInMilis + " milliseconds.");
            }
        });
    }

    private void getAppKeyHash() {
        System.out.println("겟앱키해쉬");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                System.out.println("hash"+something);
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}