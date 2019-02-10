package com.example.siji;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class MyPageActivity extends BaseActivity {

    boolean isFragmentLoaded;
    Fragment menuFragment;
    TextView title;
    ImageView menuButton;
    ImageView img;
    int kakaoID;
    EditText nickET;
    String type;
    String nickname;
    int condition;
    TextView diaryCdtchangeBTN;
    Spinner diaryCdtSpinner;
    String image;
    Bitmap bitmap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initAddlayout(R.layout.activity_my_page);

        img = (ImageView)findViewById(R.id.profile_image);
        nickET =(EditText)findViewById(R.id.mypage_nick);

        Intent intent = getIntent();
        kakaoID=intent.getIntExtra("kakaoID",1);
        System.out.println("마이페이지가 겟받은 카카오 아이디"+kakaoID);

        readServer("userInfoRead");

        menuButton = (ImageView) findViewById(R.id.menu_icon);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFragmentLoaded) {
                    loadFragment();
                    //title.setText("More");
                } else {
                    if (menuFragment != null) {
                        if (menuFragment.isAdded()) {
                            hideFragment();
                        }
                    }
                }
            }
        });

        findViewById(R.id.title_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        });
        //스피너 값 받아오기
            diaryCdtSpinner =(Spinner)findViewById(R.id.diaryCdtSpinner);
            diaryCdtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // i가 포지션
                    condition=i;
                    System.out.println("선택된 컨디션 값 "+ condition);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

                    // 변경 버튼 클릭시
            diaryCdtchangeBTN=(TextView) findViewById(R.id.diaryCdtchangeBTN);
            diaryCdtchangeBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nickname =nickET.getText().toString();
                    readServer("userInfoWrite");
                }
            });
    }

    // 유저 인포셋
    private void userInfoSet(){
            nickET.setText(nickname);
            diaryCdtSpinner.setSelection(condition);

            Thread mthread = new Thread(){
            @Override
            public void run() {
                try{
                    // 아래 한 줄은 테스트용으로 사용자의 프로필 이미지 받으려면 아래 한 줄 주석처리 하세용
                    // profileImage="";
                    URL url = new URL(image);
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
            if(image.equals("")){
                img.setImageResource(R.drawable.thumb_null);
            }else {
                img.setImageBitmap(bitmap);
            }
        }catch (InterruptedException e){}
    }

    ////리드서버 (디비연동)

    private void readServer(String types) {
        type = types;
        System.out.println("리드서버안의 타입을 찍어보면 "+type);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url = null;
                try {
                    if(type.equals("userInfoRead")){
                        url = new URL("http://hsvtr365.cafe24.com/Mypage.jsp?kakaoID="+kakaoID+"&type="+type);
                    }else if(type.equals("userInfoWrite")){
                        url = new URL("http://hsvtr365.cafe24.com/Mypage.jsp?kakaoID="+kakaoID+"&type="+type+"&nick="+nickname+"&condition="+condition);
                    }else if(type.equals("profileReload")) {
                        url = new URL("http://hsvtr365.cafe24.com/Mypage.jsp?kakaoID=" + kakaoID + "&type=" + type + "&nick" + nickname + "&condition+" + condition);
                    }
                    System.out.println("유알엘을 찍어본다"+url);

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
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    /////////// 제이슨 오브젝트로 받는것의 이름이 "flag"라면 이라는 if문을 쓸 수 있을까요??!!!!!!
                    Iterator iter = jsonObject.keys();
                    String key = "";

                    while (iter.hasNext()) {
                        key = iter.next().toString();
                    }
                    System.out.println("키값"+key);

                    if(key.equals("flag")) {
                        if (jsonObject.getInt("flag") == 0) {
                            Toast.makeText(MyPageActivity.this, "DB 수정되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (jsonObject.getInt("flag") == 1) {
                            Toast.makeText(MyPageActivity.this, "수정에 실패했습니다 마치 제 인생처럼...", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        nickname = jsonObject.getString("nickname");
                        condition = jsonObject.getInt("condition");
                        image = jsonObject.getString("image");
                        userInfoSet();
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };

    public void hideFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
        menuButton.setImageResource(R.drawable.ic_menu);
        isFragmentLoaded = false;
        //title.setText("Main Activity");
    }

    public void loadFragment() {
        FragmentManager fm = getSupportFragmentManager();
        menuFragment = fm.findFragmentById(R.id.container_mypage);
        menuButton.setImageResource(R.drawable.ic_up_arrow);
        if (menuFragment == null) {
            menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
            fragmentTransaction.add(R.id.container_mypage, menuFragment);
            fragmentTransaction.commit();
        }
        isFragmentLoaded = true;
    }
}
