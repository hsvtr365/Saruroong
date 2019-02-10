package com.example.siji;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class DiaryViewActivity extends AppCompatActivity {
    EditText diaryViewET;
    SeekBar seekBar;
    TextView cdTv;
    TextView viewMoodTV_Date;
    String text;
    int mood;
    int intday;
    // 세션에서 id 받는 작업 해야한다는것..
    int kakaoID = 1;
    String type= "";


    private int changeMoodTV(int mood){
        cdTv = (TextView)findViewById(R.id.viewMoodTV);
        mood+=10;
        System.out.println("체인지무드티비 메소드 안에서의 무드값"+mood);
        if(mood<=12 && mood>=8 ){
            cdTv.setText("ㅇ_ㅇ");
        }else if(mood<=7 && mood>=4){
            cdTv.setText("-_-");
        }else if(mood<=3 && mood>=1){
            cdTv.setText("ㅠㅠ");
        }else if(mood==0){
            cdTv.setText("죽고싶다");
        }else if(mood<=16 && mood>=13){
            cdTv.setText("우훗우훗");
        }else if(mood<=19 && mood>=17){
            cdTv.setText(">_< !!!!");
        }else if(mood==20){
            cdTv.setText("정말 행복해");
        }
        return mood;
    }
    // 시간 되면 1순위로 디비값으로 시크바 설정하는 것도 가져오쟝
    // 사용자 기기에서 txt 파일 읽어서 내용 가져오는 메소드 **********************************
    private void readDiary(int intday){
        try{
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir()+""+intday+".txt"));
            String readStr = "";
            String str = null;
            while(((str = br.readLine()) != null)){
                readStr += str +"\n";
            }
            br.close();
            diaryViewET.setText(readStr.substring(0, readStr.length()-1));
            // 에디트 텍스트에 파일 내용이 뜬다.
        }catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(DiaryViewActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDatetoTV(int intday){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, intday);
        Date date =calendar.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = format1.format(date);
        System.out.println(date1);
        viewMoodTV_Date=(TextView)findViewById(R.id.viewMoodTV_Date);
        viewMoodTV_Date.setText(""+date1+"일의 일기");
    }

    /////////////////////////////////////////////////////////////////////////////
    //  온
    //          크       리       에       이       트
    /////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_view_activity);

        /*findViewById(R.id.title_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryViewActivity.this, MainActivity1.class);
                startActivity(intent);
            }
        });*/

        Intent intent = getIntent();
        float day = intent.getExtras().getFloat("day");
        kakaoID = intent.getIntExtra("kakaoID",1);
        System.out.println("다이어리뷰에서 인텐트받은 카카오 아이디"+kakaoID);
        System.out.println("리스트에서 받은 데이값"+day);
        intday = (int)day;
        setDatetoTV(intday);

       diaryViewET = (EditText) findViewById(R.id.diaryView);

        //////////////////////////////////  싴바 파트   ///////////////////////////////////////

        // 싴바 설정
        seekBar = (SeekBar)findViewById(R.id.diaryViewseekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cdTv = (TextView)findViewById(R.id.viewMoodTV);
                mood= changeMoodTV(i-10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("스톱 :" + seekBar );
            }
        });

        findViewById(R.id.diaryModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = diaryViewET.getText().toString();
                try{
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() +""+intday+".txt", false));
                    bw.write(text);
                    bw.close();
                    Toast.makeText(DiaryViewActivity.this,"수정된 내용 txt에 저장됨", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DiaryViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mood-=10;
                System.out.println("클릭 리스너 안 무드값"+mood);

                readServer("diaryModify");
            }
        });

        ///////////////////////////////////////// 사용자 기기에 저장된 txt 내용을 불러오기
           // float화된 day를 int로 만든다(파일명으로 사용하기 위해)
        System.out.println("리드 다이어리를 위한 intday 출력"+intday);
        readDiary(intday);
        readServer("diaryView");
        // 수정 버튼 누를경우 이벤트 :


        // 삭제 버튼 누를 경우 이벤트 :
        findViewById(R.id.diaryDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 디비- 다이어리테이블- day 날짜, 멤버 아이디 선택해서 행 지우기
                File file = new File(getFilesDir(),""+intday+".txt");
                file.delete();
                Toast.makeText(DiaryViewActivity.this, "일기가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                readServer("diaryDelete");
            }
        });
    }

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
                    if(type.equals("diaryView")){
                        url = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type="+type+"&date="+intday);
                        System.out.println("리드서버타입 다이어리뷰 안 인티데이"+intday);
                    }else if(type.equals("diaryDelete")){
                        url = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type=diaryDelete&date="+intday);
                    }else if(type.equals("diaryModify")){
                        url = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type=diaryModify&date="+intday+"&mood="+mood);
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
                try {
                     JSONObject jsonObject = new JSONObject(json);
                     /////////// 제이슨 오브젝트로 받는것의 이름이 "flag"라면 이라는 if문을 쓸 수 있을까요??!!!!!!
                     Iterator iter = jsonObject.keys();
                     String key = "";

                     while (iter.hasNext()) {
                         key = iter.next().toString();
                     }
                     System.out.println("키값"+key);
                     if(key.equals("mood")){
                         mood = jsonObject.getInt("mood");
                         System.out.println("핸들러에서 받은 무드값"+mood);
                         changeMoodTV(mood);
                     }else if(key.equals("flag")) {
                            if (jsonObject.getInt("flag") == 4) {
                                Toast.makeText(DiaryViewActivity.this, "DB 수정되었습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (jsonObject.getInt("flag") == 5) {
                                Toast.makeText(DiaryViewActivity.this, "수정에 실패했습니다 마치 제 인생처럼...", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getInt("flag") == 2) {
                                Toast.makeText(DiaryViewActivity.this, "DB 일기가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (jsonObject.getInt("flag") == 3) {
                                Toast.makeText(DiaryViewActivity.this, "삭제에 실패했습니다 마치 제 인생처럼...", Toast.LENGTH_SHORT).show();
                            }
                        }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };
}
