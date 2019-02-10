package com.example.siji;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DiaryWriteActivity extends AppCompatActivity {

    String today;
    String msg = "";
    // msg의 내용이 기본적으로 시스템의 오늘 날짜  일/월/년 으로 나오도록 설정
    Spinner spinner;
    String text;
    String type;

 
    int doy;
    int year, month, day, hour, minute;
    int mood;
    int spSeleted;
    int kakaoID =1;

    TextView tv;
    TextView cdTv;
    SeekBar seekBar;
    EditText diaryEt;
    FileOutputStream fos;

    // 오늘 날짜 문자열로 받아내기
    public String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        String str_date = df.format(new Date());
        return str_date;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            today = String.format("%d년 %d월 %d일", year,monthOfYear+1, dayOfMonth);

            Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
            localCalendar.set(year, monthOfYear, dayOfMonth);
            doy = localCalendar.get(Calendar.DAY_OF_YEAR);
            msg = ""+doy;
            tv = (TextView)findViewById(R.id.diaryWriteView);
            tv.setText(" "+today+"의 기분");
            Toast.makeText(DiaryWriteActivity.this, msg , Toast.LENGTH_SHORT).show();
        }
    };


    /////////////////////////////////////////////////////////////
    //                  온 크리에이트 시작!                      //
    /////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_write_activity);
        Intent intent = getIntent();
        kakaoID = intent.getIntExtra("kakaoID",1);
        System.out.println("다이어리롸이트액티비티에서 인텐트받은 카카오 아이디"+kakaoID);

        /*findViewById(R.id.title_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryWriteActivity.this, MainActivity1.class);
                startActivity(intent);
            }
        });*/

        //////////////////////////////////  날짜 파트   ///////////////////////////////////////
        // 오늘의 날짜 최상단에 표시
        tv = (TextView)findViewById(R.id.diaryWriteView);
        tv.setText(" "+getDateString()+"의 기분");

        // 그레고리안 달력 설정
        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // 데이트픽커가 뜨는 버튼 이벤트
        findViewById(R.id.changeDateBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(DiaryWriteActivity.this, dateSetListener, year, month, day).show();
            }
        });


        //////////////////////////////////  싴바 파트   ///////////////////////////////////////

        // 싴바 설정
        seekBar = (SeekBar)findViewById(R.id.moodseekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cdTv = (TextView)findViewById(R.id.moodTV);
                mood = i;
                if(mood<=12 && mood>=8 ){
                    cdTv.setText("ㅇ_ㅇ");
                }else if(mood<=7 && mood>=4){
                    cdTv.setText("-_-");
                }else if(mood<3 && mood>=1){
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("스톱 :" + seekBar );
            }
        });

        cdTv = (TextView)findViewById(R.id.moodTV);
        cdTv.setText("ㅇ_ㅇ");

        //////////////////////////////////  상태변경 파트   ///////////////////////////////////////
        // spinner
        // 현재 회원의 상태를 DB에서 불러와서 초기 값으로 지정할 것  - 멤버 jsp를 통할것
        spinner = (Spinner)findViewById(R.id.diaryCdtSpinner);

        // 상태 변경 버튼 이벤트
        //
        findViewById(R.id.diaryCdtchangeBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spSeleted= spinner.getSelectedItemPosition();
                Toast.makeText(DiaryWriteActivity.this,"선택된 포지션: "+spSeleted,Toast.LENGTH_SHORT).show();
                // DB 연결  - 스피너의 내용대로 디비 정보 변경- type:conditionModify - 멤버 jsp를 통할것
                // 토스트로 변경되었습니다 메시지까지 보내기
            }
        });

        //////////////////////////// 일기 내용 파트 ////////////////////////////////////////////

        // 일기 작성 완료 버튼 이벤트
        findViewById(R.id.writeDiaryBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaryEt = (EditText)findViewById(R.id.diaryET);
                text = diaryEt.getText().toString();

                try{
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() +""+doy+".txt", false));
                    bw.write(text);
                    bw.close();
                    Toast.makeText(DiaryWriteActivity.this,"저장완료", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DiaryWriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // 디비 연결
                readServer("diaryWrite");
                // 에디트텍스트 내의 내용을 사용자 기기에 저장]\
                // myFile 이라는 폴더를 만들고 권한을 준다.

                // 변수 msg에 저장된 내용을 리절트로 보내기

                /*
                // 인텐트 -> 다이어리 리스트
                Intent intent = new Intent();
                intent.putExtra("emotion",seekValue);
                intent.putExtra("day",doy);

                finish();*/
            }
        });
/*
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 디비 연결 Diary.jsp
                // 사용자 기기에 저장된 txt 내용을 불러오기
                TextView fileleader = (TextView)findViewById(R.id.fileleader);

                try{
                    BufferedReader br = new BufferedReader(new FileReader(getFilesDir()+""+doy+".txt"));
                    String readStr = "";
                    String str = null;
                    while(((str = br.readLine()) != null)){
                        readStr += str +"\n";
                    }
                    br.close();
                    fileleader.setText(readStr.substring(0, readStr.length()-1));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(DiaryWriteActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
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
                try {
                    URL url
                            = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type="+type+"&date="+doy+"&mood="+(mood-10));
                    conn = (HttpURLConnection)url.openConnection();
                    System.out.println("롸이트 유알엘"+url);
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
                    if(jsonObject.getInt("flag") == 0) {
                        Toast.makeText(DiaryWriteActivity.this, "일기가 저장되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("kakaoID",kakaoID);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(DiaryWriteActivity.this, "저장에 실패했습니다 마치 제 인생처럼...", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };
}
