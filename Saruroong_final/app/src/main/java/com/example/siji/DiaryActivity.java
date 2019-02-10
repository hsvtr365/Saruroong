package com.example.siji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends BaseActivity {

    LineChart chart;
    LineDataSet dataSet = null;
    LineData lineData = null;

    boolean isFragmentLoaded;
    Fragment menuFragment;
    ImageView menuButton;

    int kakaoID=1;
    ArrayList<String> chartrecords = null;

    /////////////////////////////////////////////////////////////
    //                  온 크리에이트 시작!                      //
    /////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAddlayout(R.layout.diary_list_activity);
        System.out.println("다이어리액티비티가 실행은 됐다");
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

        findViewById(R.id.title_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        kakaoID = intent.getIntExtra("kakaoID",1);
        System.out.println("다이어리액티비티에서 인텐트받은 아이디"+kakaoID);

        // formatter에서 지정된 포맷 형식을 어떻게 가져올지 찾아볼 것
        // SQL문 통해서 각 날짜별 감정 값 /일기 번호들을 가져와야 한다.

        // 그래서 아래의 entries.add(new Entry(감정값f,i)
        // 포문으로 돌려서 작성해야 할듯하다

        // Mysqㅣ-> jsp에서 받아온 값 중 날짜값과
        //chartrecords = new ArrayList<String>();

        /*
         for(int i=305; i<=315; i++) {
            if(i%3==0) {
                entries.add(new Entry(i, -8));
            }else if(i%3==1){
                entries.add(new Entry(i, 0));
            }else if(i%3==2){
                entries.add(new Entry(i, 8));
            }
        }*/
        ;
        //db 연동 및 차트 데이터 입력!
        readServer();
        // 포매터에서 설정한 것 불러오기
        //lineData.setValueFormatter(new DiaryFormatter());
    }

    //    List<Entry> entries = new ArrayList<Entry>();

    private void drawChart(List<Entry> entries) {
        chart = (LineChart) findViewById(R.id.chart);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true); // 터치활성화
        chart.setDragEnabled(true); // 차트 드래그 활성화
        chart.setScaleXEnabled(true); // x축 스케일링 활성화

        dataSet = new LineDataSet(entries, "Label");
        lineData = new LineData();
        lineData.addDataSet(dataSet);

        chart.setData(lineData);
        chart.invalidate(); // refresh
        dataSet.setLineWidth(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // x 축을 포매터 설정대로 바꾸기
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DiaryFormatter(chart));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisMinimum(-10f);
        yAxis.setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        // 각 라벨 클릭 시 이벤트
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                System.out.println(entry.getX());
                Intent intent = new Intent(DiaryActivity.this, DiaryViewActivity.class);
                intent.putExtra("day",entry.getX());
                intent.putExtra("kakaoID",kakaoID);
                startActivityForResult(intent, 2);
            }
            @Override
            public void onNothingSelected() {
            }
        });

        /*
        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawAxisLine(false);
        right.setDrawGridLines(false);
        */

        ////////////////////////////////////////// 일기쓰기 액티비티로 이동하는 버튼 이벤트 ////////////////////////////////////////////////////////
        findViewById(R.id.diaryWriteBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, DiaryWriteActivity.class);
                System.out.println("롸이트버튼 리스너 안 카카오 아이디값"+kakaoID);
                intent.putExtra("kakaoID",kakaoID);
                startActivityForResult(intent, 1);
            }
        });


        ///////////////////////////////////////////// 디비 연동 관련 /////////////////////////////////////////////////////////////
        /*
        String strkakaoID = ""+kakaoID;
        try {
            String result  = new CustomTask().execute(strkakaoID,"diaryList").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } */
    }
    /////////////////////////////////////////////  온 액티비티리절트 /////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 일기 쓰기에서 돌아왔을 경우
       if(requestCode==1) {
            if(resultCode==RESULT_OK){
                // int emotion= data.getIntExtra("emotion",0);
                // int doy= data.getIntExtra("day",1);
                Toast.makeText(DiaryActivity.this, "일기쓰기에서 돌아오기가 되고있다" , Toast.LENGTH_SHORT).show();
                readServer();
                System.out.println("일기쓰기에서 돌아옴");
                // 이 안에 드로우차트있다.
            }
        }

        if(requestCode==2) {
            if(resultCode==RESULT_OK){
                // int emotion= data.getIntExtra("emotion",0);
                // int doy= data.getIntExtra("day",1);
                Toast.makeText(DiaryActivity.this, "뷰에서 돌아오셧군여..." , Toast.LENGTH_SHORT).show();
                readServer();
                System.out.println("뷰에서 돌아옴");
                // 이 안에 드로우차트있다.
            }
        }
    }

    /////////////////////////////////////////////   JSP로 정보를 보내고, 받는 클래스 ///////////////////////////////////////////////////////

    private void readServer() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();

                try {
                    URL url = new URL("http://hsvtr365.cafe24.com/Diary.jsp?kakaoID="+kakaoID+"&type=diaryList");
                    System.out.println("리스트url 출력"+url);
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
                List<Entry> entries = new ArrayList<>();

                try {
                    ArrayList<String> records = new ArrayList<String>();
                    JSONArray jsonArray = new JSONArray(json);
                    for(int i=0 ; i<jsonArray.length() ; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int date1  = jsonObject.getInt("date");
                        int mood1  = jsonObject.getInt("mood");
                        //여기서 차트 x축(날짜)과 y축(기분)이 입력된다!
                        System.out.println("date:"+date1);
                        System.out.println("mood:"+mood1);
                        entries.add(new Entry(date1,mood1));
                    }
                    drawChart(entries);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };

    public void hideFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
        menuButton.setImageResource(R.drawable.ic_menu);
        isFragmentLoaded = false;
        //title.setText("Main Activity");
    }
    public void loadFragment(){
        FragmentManager fm = getSupportFragmentManager();
        menuFragment = fm.findFragmentById(R.id.container_diary_list);
        menuButton.setImageResource(R.drawable.ic_up_arrow);
        if(menuFragment == null){
            menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
            fragmentTransaction.add(R.id.container_diary_list,menuFragment);
            fragmentTransaction.commit();
        }

        isFragmentLoaded = true;
    }
}
