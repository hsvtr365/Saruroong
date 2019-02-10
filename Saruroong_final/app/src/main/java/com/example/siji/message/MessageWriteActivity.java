package com.example.siji.message;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siji.KakaoBaseActivity;
import com.example.siji.R;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by siji on 2017-11-13.
 */
public class MessageWriteActivity extends KakaoBaseActivity {
    String kakaoID = "123";
    String content = "";
    String img = "이미지 경로 가데이터";//11
    String condition = "5";
    EditText editText;
    private static final int GALLERY_REQUEST_CODE = 0;
    TextView time;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<MyData> myDataset;
    TextView done, myphoto;

    int kakaoID2 =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_write);

        requestMe();
        //현재시간 가져오기
        //현재시간 구하기.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date1= new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        // nowDate 변수에 값을 저장한다.
        String formatDate1 = sdfNow.format(date1); //현재시간 스트링!
        System.out.println("수지 테스트2 : "+formatDate1);
        String formatDate[] =formatDate1.split("-");

        String munth = "";
        switch (formatDate[1]) {
            case "1": munth = "JANUARY"; break;
            case "2": munth = "FEBUARY"; break;
            case "3": munth = "MARCH"; break;
            case "4": munth = "APRIL"; break;
            case "5": munth = "MAY"; break;
            case "6": munth = "JUN"; break;
            case "7": munth = "JULY"; break;
            case "8": munth = "AUGUST"; break;
            case "9": munth = "SEPTEMBER"; break;
            case "10": munth = "OCTOBER"; break;
            case "11": munth = "NOVEMBER"; break;
            case "12": munth = "DECEMBER"; break;
            default:munth="오류!";
        }

        Calendar cal= Calendar.getInstance ();
        int y = Integer.parseInt(formatDate[0]);
        int m = Integer.parseInt(formatDate[1]);
        cal.set(Calendar.YEAR,y);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 24);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        String day = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case 0: day = "SUNDAY"; break;
            case 1: day = "MONDAY"; break;
            case 2: day = "TUESDAY"; break;
            case 3: day = "WEDNESDAY"; break;
            case 4: day = "THURSDAY"; break;
            case 5: day = "FRIDAY"; break;
            case 6: day = "SATURDAY"; break;
        }
        System.out.println("수지 오늘의 요일 "+day);
        time = (TextView)findViewById(R.id.msgWriteTV01);
        time.setText(munth+" / " +day +" / "+ formatDate[0] );
        done = (TextView) findViewById(R.id.msgWriteTV02);
        myphoto = (TextView) findViewById(R.id.msgPhotoTV01);
        editText=(EditText)findViewById(R.id.msgWriteItemET01);
        System.out.println("♥♥ 메세지 쓰기페이지 수지");


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = editText.getText().toString();
                System.out.println(content+"콘텐트 수지");
                if(content.equals("")){
                    Toast.makeText(MessageWriteActivity.this,"글을 써주세염",Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        String result  = new CustomTask().execute(kakaoID,content,img,condition).get();
                        if(result.equals("wrightok")) {
                            Toast.makeText(MessageWriteActivity.this,"글쓰기 완료",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MessageWriteActivity.this, MessageMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if(result.equals("levelhight")) {
                            Toast.makeText(MessageWriteActivity.this,"글쓰기 권한이 없습니다",Toast.LENGTH_SHORT).show();

                        } else if(result.equals("nullpoint")) {
                            Toast.makeText(MessageWriteActivity.this,"존재하지 않는 아이디",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MessageWriteActivity.this,"메세지를 띄웠어요~",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MessageWriteActivity.this, MessageMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }catch (Exception e) {}
                }
            }
        });

        myphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MessageWriteActivity.this,"포토 버튼 클릭 띠링",Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.msgWrightRV01);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        // mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mAdapter = new MyAdapter(myDataset);


        myDataset.add(new MyData("그림이라능", R.mipmap.a));
        myDataset.add(new MyData("2", R.mipmap.b));
        myDataset.add(new MyData("3", R.mipmap.c));
        myDataset.add(new MyData("4", R.mipmap.d));
        myDataset.add(new MyData("5", R.mipmap.e));
        myDataset.add(new MyData("6", R.mipmap.f));
        myDataset.add(new MyData("7", R.mipmap.g));
        myDataset.add(new MyData("8", R.mipmap.h));
/*        myDataset.add(new MyData("9", R.mipmap.c));
        myDataset.add(new MyData("10", R.mipmap.a));
        myDataset.add(new MyData("11", R.mipmap.b));
        myDataset.add(new MyData("12", R.mipmap.c));
        myDataset.add(new MyData("13", R.mipmap.a));
        myDataset.add(new MyData("14", R.mipmap.b));
        myDataset.add(new MyData("15", R.mipmap.c));*/

        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector gestureDetector = new GestureDetector(MessageWriteActivity.this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {return true;}
        });

       mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e){
                Log.d(TAG,"onInterceptTouchEvent");
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child!=null&&gestureDetector.onTouchEvent(e)) {
                    Log.d(TAG,"LayoutPosition=>"+rv.findViewHolderForLayoutPosition(rv.getChildLayoutPosition(child)));
                    Log.d(TAG, "getChildViewHolder=>" + rv.getChildViewHolder(child).itemView);//**//*
                    Log.d(TAG,"getChildAdapterPosition=>" + rv.getChildAdapterPosition(child));
                    int str = rv.getChildAdapterPosition(child);//포지션값인데 ㅠㅠ

                    ImageView myImage = (ImageView) findViewById(R.id.msgBackIV01);
                    ImageView myImage2 = (ImageView) findViewById(R.id.msgBackIV02);

                  switch (str) {
                        case 0:
                            myImage.setImageResource(R.mipmap.a);
                            break;
                        case 1:// editText.setBackgroundResource(R.mipmap.b);
                            myImage.setImageResource(R.mipmap.b);
                        break;
                        case 2: //editText.setBackgroundResource(R.mipmap.c);
                            myImage.setImageResource(R.mipmap.c);
                        break;
                        case 3: //editText.setBackgroundResource(R.mipmap.d);
                            myImage.setImageResource(R.mipmap.d);
                        break;
                        case 4: //editText.setBackgroundResource(R.mipmap.e);
                            myImage.setImageResource(R.mipmap.e);
                        break;
                        case 5: //editText.setBackgroundResource(R.mipmap.f);
                            myImage.setImageResource(R.mipmap.f);
                        break;
                        case 6: //editText.setBackgroundResource(R.mipmap.g);
                            myImage.setImageResource(R.mipmap.g);
                        break;
                        case 7: //editText.setBackgroundResource(R.mipmap.h);
                            myImage.setImageResource(R.mipmap.h);
                        break;
                        default:
                    }
                    Toast.makeText(getApplication(),str+": 포지션 값", Toast.LENGTH_SHORT).show();
                    img=str+"";
               }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e){Log.d(TAG,"onTouchEvent");}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){Log.d(TAG,"onRequestDisallowInterceptTouchEvent");}
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

                System.out.println(kakaoID+" : 수지 onSuccess");
                Logger.d("UserProfile 수지 onSuccess : " + userProfile);

               // try {String result  = new CustomTask().execute(kakaoID, content, img,condition).get();}catch (Exception e) {}


                //MainActivity();
            }
        }, propertyKeys,false);
    }
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg, sendMsg1;
        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br=null;
            StringBuffer result = new StringBuffer();
            System.out.println(kakaoID+" kakaoID/CustomTask 수지");

            try {
                String data;

                URL url = new URL("http://hsvtr365.cafe24.com/message_insert.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "kakaoid="+kakaoID+"&content="+content+"&img="+img+"&condition="+condition;
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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}

class MyData {
    public String text;
    public int img;

    public MyData(String text, int img) {
        this.text = text;
        this.img = img;
    }
}
