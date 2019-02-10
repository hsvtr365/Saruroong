package com.example.siji.message;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siji.R;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by siji on 2017-11-11.
 */

public class MessageMainPageFragment extends Fragment {

    private ArrayList<MessageItem> MessageItems1;
    private ArrayList<MessageItem> MessageItems2;
    public MessageAdapter adapter;
    public MessageAdapter adapter2;
    ListView listView;

    int kakaoID = 0; // userProfile에서 ID값을 가져옴
    String kakaoNickname = "가짜수지";     // Nickname 값을 가져옴
    String kakaoProfile = "프로필이미지";//11
    String type ="";
    String msg_no ="";

    TextView textView1;

    public static MessageMainPageFragment create(int pageNumber) {
        MessageMainPageFragment fragment = new MessageMainPageFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mPageNumber = getArguments().getInt("page");
        requestMe();


       // listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String suji =MessageItems1.get(position).toString();
                System.out.println(suji);

                Toast.makeText(getActivity(),"글을 써주세염",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("메세지를 삭제 하시겠습니까?")
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //삭제 문구
                                String type ="sdelete";
                                try {
                                    String result  = new CustomTask().execute(kakaoID+"", type).get();

                                }catch (Exception e) {}


                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                .show();
            }
        });
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.message_fragment, container, false);
        textView1 = (TextView)rootView.findViewById(R.id.msgMainBarTV01);
        listView  = (ListView)rootView.findViewById(R.id.msgMainLV01) ;
        textView1.setText("보낸 메세지");

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//단일선택명령어
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                 msg_no =MessageItems1.get(position).getMsg_no();
                System.out.println(msg_no+"  msg_no");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("메세지를 삭제 하시겠습니까?")
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //삭제 문구

                                type ="sdelete";
                                try {
                                    String result  = new CustomTask().execute(kakaoID+"", type,msg_no).get();

                                }catch (Exception e) {}


                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });





        return rootView;
    }



    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg, sendMsg1;
        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br=null;
            StringBuffer result = new StringBuffer();
            System.out.println(kakaoID+" kakaoID"+type+"CustomTask 수지"+msg_no);
            try {
                String data;

                URL url = new URL("http://hsvtr365.cafe24.com/message_delete3.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //해당 게시물 seq받아야 하는데?

                sendMsg = "kakaoid="+kakaoID+"&type="+type+"&msg_no="+msg_no;
                osw.write(sendMsg);
                osw.flush();//flush : stream에 남아 있는 데이터를 강제로 내보내는 역할

                if(conn.getResponseCode() == conn.HTTP_OK) {
                    br = new BufferedReader(new  InputStreamReader(conn.getInputStream(), "UTF-8"));
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
            readServer();
            return receiveMsg;
        }
    }

    private void readServer() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();

                try {
                    URL url = new URL("http://hsvtr365.cafe24.com/message_view.jsp?kakaoid="+kakaoID);
                    conn = (HttpURLConnection)url.openConnection();
                    if(conn != null) {
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            System.out.println("연결 성공 readServer()");
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
                    if(br != null) try { br.close(); } catch(IOException e) {}
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

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                StringBuffer result = (StringBuffer)msg.obj;
                String json = result.toString();
                try {
                    MessageItems1 = new ArrayList<MessageItem>();
                    MessageItems2 = new ArrayList<MessageItem>();
                    JSONObject jsonOutObject = new JSONObject(json);
                    JSONArray jsonArray1 = jsonOutObject.getJSONArray("getmsg");
                    //JSONArray jsonArray2 = jsonOutObject.getJSONArray("givemsg");

                    for(int i=0 ; i<jsonArray1.length() ; i++) {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                        String msg_no1 = jsonObject1.getString("msg_no");
                        String kakaoid1 = jsonObject1.getString("kakaoid");
                        String content1 = jsonObject1.getString("content");
                        String img1 = jsonObject1.getString("img");
                        String condition1 = jsonObject1.getString("condition");
                        String send_date1 = jsonObject1.getString("send_date");
                        String recipient1 = jsonObject1.getString("recipient");
                        String check1 = jsonObject1.getString("check");
                        String sdelete1 = jsonObject1.getString("sdelete");
                        String rdelete1 = jsonObject1.getString("rdelete");

                        if(check1=="0"){
                            check1="아직 읽지않음";
                        }else {
                            check1="읽음";
                        }
                        if(recipient1=="1"){
                        }else {
                            recipient1="없음";
                        }

                        MessageItems1.add(new MessageItem(content1,send_date1,check1,"",img1,msg_no1));

                    }
                    System.out.println(MessageItems1.size()+"김수지  MessageItems1 사이즈");
                    adapter = new MessageAdapter(getContext(),MessageItems1);
                   // adapter2 = new MessageAdapter(getContext(),MessageItems2);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
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
                   // finish();
                } else {
                   // KakaologinActivity();
                }
            }
            @Override//세션이 닫혀 실패한 경우
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("onSessionClosed 수지");
                //KakaologinActivity();
            }
            @Override
            public void onNotSignedUp() {
                System.out.println("onNotSignedUp  수지");
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //사용자 정보 요청이 성공한 경우로 사용자 정보 객체를 받습니다.
                String suji= String.valueOf(userProfile.getId());
                kakaoID =Integer.parseInt(suji);
                // userProfile에서 ID값을 가져옴
                kakaoNickname = userProfile.getNickname();     // Nickname 값을 가져옴
                kakaoProfile = userProfile.getThumbnailImagePath();//110px * 110px 크기의 카카오톡 썸네일 프로필 이미지 URL

                System.out.println(kakaoID+" : 수지 onSuccess");
                System.out.println(kakaoNickname+" : 수지 onSuccess");
                System.out.println(kakaoProfile+" : 수지 onSuccess");
                Logger.d("UserProfile 수지 onSuccess : " + userProfile);
                readServer();
            }
        }, propertyKeys,false);
    }
/*    protected void KakaologinActivity() {
        final Intent intent = new Intent(this, KakaologinA.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }*/


}