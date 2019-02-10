package com.example.siji;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
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

/**
 * Created by Shin on 2017-11-10.
 */

public class SetActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{
    Intent intent;
    String kakaoID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_perf);

        Preference pBack = (Preference)findPreference("back_pref");
        SwitchPreference spLock = (SwitchPreference)findPreference("lock_pref");
        Preference pDiaryBackup = (Preference)findPreference("diary_backup_pref");
        Preference pLogout = (Preference)findPreference("logout_pref");
        Preference pNotice = (Preference)findPreference("notice_pref");
        Preference pMemberHelp = (Preference)findPreference("member_help_pref");
        Preference pAppInfo = (Preference)findPreference("app_info_pref");
        Preference pRule = (Preference)findPreference("rule_pref");
        Preference pExit = (Preference)findPreference("app_exit_pref");

        pBack.setOnPreferenceClickListener(this);
        spLock.setOnPreferenceClickListener(this);
        pDiaryBackup.setOnPreferenceClickListener(this);
        pLogout.setOnPreferenceClickListener(this);
        pNotice.setOnPreferenceClickListener(this);
        pMemberHelp.setOnPreferenceClickListener(this);
        pAppInfo.setOnPreferenceClickListener(this);
        pRule.setOnPreferenceClickListener(this);
        pExit.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // 도움말 선택시
        switch (preference.getKey()){
            case "back_pref" :
                intent = new Intent(SetActivity.this, MainActivity1.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            case "lock_pref" :

                break;
            case "diary_backup_pref" :

                break;
            case "logout_pref" :
                System.out.println("수지수지 로그아웃 버튼 눌룸");
                onClickLogout();
                break;
            case "notice_pref" :

                break;
            case "member_help_pref" :

                break;
            case "app_info_pref" :
                System.out.println("수지수지 app_info_pref 버튼 눌룸");
                intent = new Intent(SetActivity.this, SetAppInfo.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            case "rule_pref" :
                intent = new Intent(SetActivity.this, SetPrivacy.class);
                startActivityForResult(intent, 0);
                finish();
                break;
            case "app_exit_pref" :
                onClickUnlink();

               /* intent = new Intent(SetActivity.this, KakaoMainActivity.class);
                startActivityForResult(intent, 0);
                finish();*/

                System.out.println("MainActivity 탈퇴 버튼 클릭 수지");
                break;

            default:
                System.out.println("메롱 수지");
        }
        return false;

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
                                        KakaoSignupClass();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        KakaoSignupClass();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        requestMe();
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
    //로그아웃 메서드
    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                KakaologinActivity();
            }
        });
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
                KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {}

            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                System.out.println(kakaoID+" : 수지 onSuccess");
                Logger.d("UserProfile 수지 onSuccess : " + userProfile);

                try {String result  = new CustomTask().execute(kakaoID).get();}catch (Exception e) {}

                KakaologinActivity();//성공하면 종료!

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
            System.out.println(kakaoID+"★★★★★★★ kakaoID/CustomTask 수지");
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
            return receiveMsg;
        }
    }

    //이동메서드
    protected void KakaoSignupClass() {
        final Intent intent = new Intent(this, KakaoSignup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    protected void KakaologinActivity() {
        final Intent intent = new Intent(this, KakaologinA.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}