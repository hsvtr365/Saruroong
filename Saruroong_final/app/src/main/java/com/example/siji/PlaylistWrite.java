package com.example.siji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaylistWrite extends AppCompatActivity {
    ImageView playlist_write_tab1;
    ImageView playlist_write_tab2;
    ImageView playlist_write_tab3;
    ImageView playlist_write_tab4;
    ImageView playlist_write_tab5;
    ImageView playlist_write_tab6;

    int category=0;
    int kakaoID =0;
    String subject="";
    String youtubeUrl ="";
    String artist ="";
    String lyrics = "";
    String tag ="";
    String longUrl="";

    EditText playlist_write_singer;
    EditText playlist_write_song;
    EditText playlist_write_lyrics;
    EditText playlist_write_reason;
    EditText playlist_write_url;
    TextView playlist_write_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_write);

        Intent intent = getIntent();
        kakaoID=intent.getIntExtra("kakaoID",1);



        //=을 포함하고 있을 경우와 e/ 포함하고 있을 경우 다르게 적용

        if(longUrl.contains("=")){
            int idx1 = longUrl.indexOf("=");
            youtubeUrl=longUrl.substring(idx1+1);
        }else if(longUrl.contains("e/")){
            int idx2 = longUrl.indexOf("e/");
            youtubeUrl=longUrl.substring(idx2+1);
        }

        findViewById(R.id. playlist_write_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlist_write_singer=(EditText)findViewById(R.id.playlist_write_singer);
                artist = playlist_write_singer.getText().toString();

                playlist_write_song=(EditText)findViewById(R.id.playlist_write_song);
                subject = playlist_write_song.getText().toString();

                playlist_write_lyrics=(EditText)findViewById(R.id.playlist_write_lyrics);
                lyrics = playlist_write_lyrics.getText().toString();

                playlist_write_reason=(EditText)findViewById(R.id.playlist_write_reason);
                tag = playlist_write_reason.getText().toString();

                playlist_write_url=(EditText)findViewById(R.id.playlist_write_url);
                longUrl = playlist_write_url.getText().toString();
                System.out.println("아티스트"+artist);
                System.out.println("url"+longUrl);
                System.out.println("서브젝트"+subject);
                readServer();
            }
        });

        findViewById(R.id.playlist_write_tab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 0;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 쉬는 중", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.playlist_write_tab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 1;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 짝사랑", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.playlist_write_tab3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 2;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 썸타는 중", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.playlist_write_tab4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 3;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 행복한 연애 중", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.playlist_write_tab5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 4;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 슬픈 연애 중", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.playlist_write_tab6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = 5;
                Toast.makeText(PlaylistWrite.this, "카테고리 : 이별", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readServer() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader br = null;
                StringBuffer result = new StringBuffer();

                try {
                    URL url
                            = new URL("http://hsvtr365.cafe24.com/Playlist.jsp?type=playListWrite&category="+category+"&kakaoID="+kakaoID+"&recommend=0&subject="+subject+"&url="+youtubeUrl+"&artist="+artist+"&lyrics="+lyrics+"&tag="+tag);
                    conn = (HttpURLConnection)url.openConnection();
                    System.out.println("롸이트 url"+url);
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
                        Intent intent = new Intent(PlaylistWrite.this, PlaylistActivity.class);
                        intent.putExtra("kakaoID",kakaoID);
                        startActivity(intent);
                        finish();
                    } else {
                        //Toast.makeText(DiaryWriteActivity.this, "저장에 실패했습니다 마치 제 인생처럼...", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    };
}
