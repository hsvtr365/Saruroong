package com.example.siji;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class PlaylistView extends YouTubeBaseActivity {
    int play_seq = 1;
    private static final String TAG = "PlaylistView";

    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;

    TextView playlist_view_singer;
    TextView playlist_view_song;
    TextView playlist_view_lyrics;
    TextView playlist_write_reason;
    TextView playlist_view_edit;
    TextView playlist_view_delete;
    TextView playlist_view_like;

    String type;
    int flag;
    String subject="";
    String artist="";
    String lyrics="";
    String tag="";
    String youtubeUrl="";
    String coreurl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_view);

        Intent intent = getIntent();
        play_seq=intent.getIntExtra("seq",1);
        System.out.println("플레이리스트 뷰에서 받은 seq"+play_seq);
        readServer(play_seq);

        //에디트 버튼
        playlist_view_edit=(TextView)findViewById(R.id.playlist_view_edit);

        playlist_view_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "작성자만 글을 수정할 수 있습니다", 5000).setActionTextColor (Color.parseColor("#FF0000"))
                        .setAction("YES", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).show();
            }
        });

        //딜리트 버튼
        playlist_view_delete=(TextView)findViewById(R.id.playlist_view_delete);
        playlist_view_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "작성자만 글을 삭제할 수 있습니다", 5000).setActionTextColor (Color.parseColor("#FF0000"))
                        .setAction("YES", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        }).show();
            }
        });

    }

    private void youtubeset(String youtubeUrl) {
       // coreurl = youtubeUrl.substring(18,youtubeUrl.length());
        //System.out.println("들어갈 url"+coreurl);
        coreurl=youtubeUrl;
        Log.d(TAG,"onCreate: Starting.");
        mYouTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubePlay);

        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onClick: Done initializing.");

                youTubePlayer.loadVideo(coreurl);
                System.out.println("유투브에 들어가는 url"+coreurl);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onClick: Fail to initializing.");
            }
        };
        mYouTubePlayerView.initialize(YouTubeConfig.getApiKey(), mOnInitializedListener);
    }

    private void textViewSet(){
        playlist_view_singer=(TextView)findViewById(R.id.playlist_view_singer);
        playlist_view_song=(TextView)findViewById(R.id.playlist_view_song);
        playlist_view_lyrics=(TextView)findViewById(R.id.playlist_view_lyrics);
        playlist_write_reason=(TextView)findViewById(R.id.playlist_write_reason);

        playlist_view_like=(TextView)findViewById(R.id.playlist_view_like);

        playlist_view_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readServer("addRecommend");
            }
        });

        playlist_view_singer.setText(artist);
        playlist_view_song.setText(subject);
        playlist_view_lyrics.setText(lyrics);
        playlist_write_reason.setText(tag);

    }

    private void readServer(int seq) {
        play_seq = seq;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url= null;
                try {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListView&play_seq="+play_seq);
                    System.out.println("url 출력"+url);
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
                handler1.sendMessage(msg);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    private void readServer(String types) {
        //추천수 올리기
        type=types;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();
                URL url= null;
                try {
                    if(type.equals("addRecommend")) {
                        url = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=addRecommend&play_seq="+play_seq);
                    }
                    System.out.println("url 출력"+url);
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

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                StringBuffer result = (StringBuffer)msg.obj;
                String json = result.toString();
                ArrayList<PlayListItem> datas = new ArrayList<>();
                System.out.println("핸들러 1이 실행은 되니?");
                try {
                    // 데이터 만드는 부분
                    //ArrayList<String> records = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(json);

                            subject = jsonObject.getString("subject");
                            artist = jsonObject.getString("artist");
                            lyrics = jsonObject.getString("lyrics");
                            tag = jsonObject.getString("tags");
                            youtubeUrl = jsonObject.getString("url");

                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
                youtubeset(youtubeUrl);
                System.out.println("트라이 안"+youtubeUrl);
                //lyrics.replace("_","/n");
                textViewSet();


            }
        }
    };

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                StringBuffer result = (StringBuffer)msg.obj;
                String json = result.toString();
                ArrayList<PlayListItem> datas = new ArrayList<>();

                try {
                    // 데이터 만드는 부분
                    //ArrayList<String> records = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(json);
                    /////////// 제이슨 오브젝트로 받는것의 이름이 "flag"라면 이라는 if문을 쓸 수 있을까요??!!!!!!
                    Iterator iter = jsonObject.keys();
                    String key = "";
                    while (iter.hasNext()) {
                        key = iter.next().toString();
                    }
                    System.out.println("키값"+key);

                    if(key.equals("flag")){
                        flag = jsonObject.getInt("flag");
                        System.out.println("핸들러에서 받은 플래그값"+flag);
                        if(flag==6){
                            Toast.makeText(PlaylistView.this,"추천되었습니다",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };
}
