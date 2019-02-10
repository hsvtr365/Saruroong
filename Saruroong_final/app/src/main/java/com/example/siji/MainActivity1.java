package com.example.siji;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.siji.message.MessageMainActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends BaseActivity {

    boolean isFragmentLoaded;
    Fragment menuFragment;
    TextView title;
    ImageView menuButton;
    AnimationDrawable drawable1;
    AnimationDrawable drawable2;
    AnimationDrawable drawable3;
    AnimationDrawable drawable4;
    private Handler mHandler = new Handler();
    int kakaoID;

    //////////////////////////////////////////////////////////////
    //////// 유저의 정보를 받는 리퀘스트미
    //////////////////////////////////////////////////////////////


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
                //KakaoBaseActivity kakao= new  KakaoBaseActivity();
                //redirectLoginActivity();
                System.out.println("MainActivity : 어머나 세션이 닫혀버렷내!");
                KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {}

            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                kakaoID = (int)userProfile.getId(); // userProfile에서 ID값을 가져옴
                System.out.println(kakaoID+" :카카오 아이듸");
                Logger.d("UserProfile : " + userProfile);

                /*try {String result  = new CustomTask().execute(kakaoID).get();}catch (Exception e) {}*/
                //* SplachActivity();*//
                System.out.println(kakaoID+" : 아이디 CustomTask");
            }
        }, propertyKeys,false);
        //? 위 저거는 뭐지?
        MemberTO to = new MemberTO();
        to.setKakaoid(kakaoID);
    }

    /////////////////////////////////////////////////////////////////////
    ///////////  온 크 리 에 이 트 !!!!!!!!!!!!!!!!!!!!
    ////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAddlayout(R.layout.activity_main1);
        System.out.println("메인액티비티 리퀘스트미 실행 전의 카카오 아이디 값"+kakaoID);
        requestMe();
        System.out.println("메인액티비티 리퀘스트미 실행 후의 카카오 아이디 값"+kakaoID);

        menuButton = (ImageView) findViewById(R.id.menu_icon);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFragmentLoaded) {
                    loadFragment();
                    //title.setText("More");
                }
                else {
                    if (menuFragment != null) {
                        if (menuFragment.isAdded()) {
                            hideFragment();
                        }
                    }
                }
            }
        });

        ImageView gbdiary = (ImageView)findViewById(R.id.GBdiaryBtn1);
        ImageView letter = (ImageView)findViewById(R.id.MessageBtn1);
        ImageView playlist = (ImageView)findViewById(R.id.PlaylistBtn1);
        ImageView cookie = (ImageView)findViewById(R.id.CookieBtn1);

        drawable1 = (AnimationDrawable) gbdiary.getDrawable();
        drawable2 = (AnimationDrawable) letter.getDrawable();
        drawable3 = (AnimationDrawable) playlist.getDrawable();
        drawable4 = (AnimationDrawable) cookie.getDrawable();

        drawable1.setOneShot(true);
        drawable2.setOneShot(true);
        drawable3.setOneShot(true);
        drawable4.setOneShot(true);


        findViewById(R.id.GBdiaryBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable mMyTask = new Runnable() {
                    @Override public void run() {
                        Intent intent = new Intent(MainActivity1.this, DiaryActivity.class);
                        intent.putExtra("kakaoID", kakaoID);
                        System.out.println("메인1 일" +
                                "기버튼리스너 안의 카카오 아이디값"+kakaoID);
                        startActivity(intent);
                        drawable1.stop();
                    }
                };

                // 3초후에 실행 private Runnable mMyTask = new Runnable() { @Override public void run() { // 실제 동작 } };
                if(!drawable1.isRunning()) {
                    drawable1.start();
                    mHandler.postDelayed(mMyTask, 690);
                }
                /*if(!drawable.isRunning()) {
                    drawable.start();
                    if(drawable.isRunning()){
                        startActivity(new Intent (MainActivity1.this, DiaryActivity.class));
                    }
                }*/



            }
        });

        findViewById(R.id.MessageBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable mMyTask = new Runnable() {
                    @Override public void run() {
                        startActivity(new Intent(MainActivity1.this, MessageMainActivity.class));
                        drawable2.stop();
                    }
                };

                // 3초후에 실행 private Runnable mMyTask = new Runnable() { @Override public void run() { // 실제 동작 } };
                if(!drawable2.isRunning()) {
                    drawable2.start();
                    mHandler.postDelayed(mMyTask, 740);
                }
            }
        });

        findViewById(R.id.PlaylistBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable mMyTask = new Runnable() {
                    @Override public void run() {
                        Intent intent = new Intent(MainActivity1.this, PlaylistActivity.class);
                        intent.putExtra("kakaoID", kakaoID);
                        startActivity(intent);
                        drawable3.stop();
                    }
                };

                if(!drawable3.isRunning()) {
                    drawable3.start();
                    mHandler.postDelayed(mMyTask, 850);
                }
            }
        });



        findViewById(R.id.CookieBtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mMyTask = new Runnable() {
                    @Override public void run() {
                        Intent intent = new Intent(MainActivity1.this,CookieMainActivity.class);
                        startActivity(intent);
                        drawable4.stop();
                    }
                };

                if(!drawable4.isRunning()) {
                    drawable4.start();
                    mHandler.postDelayed(mMyTask, 780);
                }
            }
        });

    }

    public void hideFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
        menuButton.setImageResource(R.drawable.ic_menu);
        isFragmentLoaded = false;
    }

    public void loadFragment(){
        FragmentManager fm = getSupportFragmentManager();
        menuButton.setImageResource(R.drawable.ic_up_arrow);
        menuFragment = fm.findFragmentById(R.id.container_main);

        if(menuFragment == null){
            menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
            fragmentTransaction.add(R.id.container_main,menuFragment);
            fragmentTransaction.commit();
        }else {
            menuFragment = fm.findFragmentById(R.id.container_main);
            /*//번들로 카카오 아이디 전달하기
            Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
            bundle.putInt("kakaoID", kakaoID); // key , value
            menuFragment.setArguments(bundle);*/
        }
        isFragmentLoaded = true;
    }
    protected void KakaologinActivity() {
        final Intent intent = new Intent(this, KakaologinA.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
