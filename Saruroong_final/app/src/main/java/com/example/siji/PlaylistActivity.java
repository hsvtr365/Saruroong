package com.example.siji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class PlaylistActivity extends BaseActivity {

    boolean isFragmentLoaded;
    Fragment menuFragment;
    TextView title;
    ImageView menuButton;
    ListView listView;
    PlayListViewAdapter adapter;
    PlayListItem playListItem;
    int kakaoID = 0;
    String type="";
    int condition=0;
    String strcondition="";
    int play_seq = 0;
    int category= 0;
    String artist ="";
    String subject ="";
    String youtubeUrl = "";
    String tag="";
    String lyrics="";
    int recommend=0;
    ArrayList<PlayListItem> datas;
    ImageView playlist_BTN_tab1;
    ImageView playlist_BTN_tab2;
    ImageView playlist_BTN_tab3;
    ImageView playlist_BTN_tab4;
    ImageView playlist_BTN_tab5;
    ImageView playlist_BTN_tab6;


    // 리스트뷰 셋
    private void listViewSet(){
        listView = (ListView)findViewById(R.id.playlist_list);
        adapter = new PlayListViewAdapter(this, datas);
        listView.setAdapter(adapter);
    }


    ///////////////////////////////////////////////////////////
    /////////////       온 크리에이트
    ///////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAddlayout(R.layout.activity_playlist);

        Intent intent = getIntent();
        kakaoID=intent.getIntExtra("kakaoID",1);
        System.out.println("마이페이지가 겟받은 카카오 아이디"+kakaoID);

        //사용자의 컨디션을 먼저 불러온 다음에 리드서버를 불러야 한다.
        //리드서버(valueOfCondition);

        readServer("valueOfCondition");

        // strcondition = "" + condition;
        // readServer(strcondition);
        // 이걸 리드서버 부분에 넣어야 한다.
        listView = (ListView)findViewById(R.id.playlist_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //포지션을 통해 어레이리스트에 접근
                //System.out.println(boardRecords.get(position).get_id());
                Intent intent=new Intent(PlaylistActivity.this,PlaylistView.class);
                intent.putExtra("seq", datas.get(position).getPlay_seq());
                // 뷰 갈때 seq 보내준다
                startActivity(intent);
            }
        });

        FloatingActionButton writeBtn = (FloatingActionButton) findViewById(R.id.writePlaylist);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PlaylistActivity.this,PlaylistWrite.class);
                intent.putExtra("kakaoID",kakaoID);
                // 라이트 갈때 카카오 아이디 보내준다
                startActivity(intent);
            }
        });

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
                Intent intent = new Intent(PlaylistActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.playlist_BTN_tab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(0);
            }
        });
        findViewById(R.id.playlist_BTN_tab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(1);
            }
        });
        findViewById(R.id.playlist_BTN_tab3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(2);
            }
        });
        findViewById(R.id.playlist_BTN_tab4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(3);
            }
        });
        findViewById(R.id.playlist_BTN_tab5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(4);
            }
        });
        findViewById(R.id.playlist_BTN_tab6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer(5);
            }
        });

    }

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
        menuFragment = fm.findFragmentById(R.id.container_playlist);
        menuButton.setImageResource(R.drawable.ic_up_arrow);
        if (menuFragment == null) {
            menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
            fragmentTransaction.add(R.id.container_playlist, menuFragment);
            fragmentTransaction.commit();
        }
        isFragmentLoaded = true;
    }


    private void readServer(String types) {
        type=types;
        System.out.println("리드서버 실행은 되니?");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url= null;
                try {
                    /*if(type.equals("addRecommend")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=addRecommend&play_seq="+play_seq);
                    }else if(type.equals("playListDelete")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListDelete&play_seq="+play_seq);
                    }else if(type.equals("playListModify")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListModify&play_seq="+play_seq+"&category="+category+"&subject="+subject+"&url="+youtubeUrl+"&artist="+artist+"&lyrics="+lyrics+"&tag="+tag);
                    }else if(type.equals("playListView")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListView&play_seq="+play_seq);
                    }else if(type.equals("playListWrite")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListWrite&category="+category+"&kakaoID="+kakaoID+"&recommend=0&subject="+subject+"&url="+youtubeUrl+"&artist="+artist+"&lyrics="+lyrics+"&tag="+tag);
                    }else*/ if(type.equals("valueOfCondition"))  {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?kakaoID="+kakaoID+"&type=valueOfCondition");
                    }

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

    private void readServer(int conditions) {
        condition = conditions;
        System.out.println("리드서버 실행은 되니?");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url= null;
                try {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type="+condition);
                        System.out.println("리드서버 컨디션 값 들어가는게 실행되었다.");
                        System.out.println("리드서버 안 엘스 유알엘"+url);

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
                handler2.sendMessage(msg);
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
                ArrayList<PlayListItem> datas = new ArrayList<>();

                try {
                    // 데이터 만드는 부분

                    JSONObject jsonObject = new JSONObject(json);
                    /////////// 제이슨 오브젝트로 받는것의 이름이 "flag"라면 이라는 if문을 쓸 수 있을까요??!!!!!!
                    Iterator iter = jsonObject.keys();
                    String key = "";
                    while (iter.hasNext()) {
                        key = iter.next().toString();
                    }
                    System.out.println("키값"+key);

                    if(key.equals("condition")){
                        condition = jsonObject.getInt("condition");
                        System.out.println("핸들러에서 받은 컨디션값"+condition);
                        readServer(condition);
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                StringBuffer result = (StringBuffer)msg.obj;
                String json = result.toString();
                try {
                    // 데이터 만드는 부분
                        System.out.println("핸들러2가 실행되나");
                        JSONArray jsonArray = new JSONArray(json);
                        datas = new ArrayList<>();
                        for(int i=0 ; i<jsonArray.length() ; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            play_seq  = jsonObject.getInt("play_seq");
                            subject = jsonObject.getString("subject");
                            artist = jsonObject.getString("artist");
                            tag = jsonObject.getString("tag");
                            recommend  = jsonObject.getInt("recommend");

                            System.out.println("플레이 seq"+play_seq);
                            System.out.println("추천수:"+recommend);

                            PlayListItem playListItem = new PlayListItem(play_seq,subject,artist,tag,recommend);
                            datas.add(playListItem);
                        }
                        listViewSet();
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };
}
