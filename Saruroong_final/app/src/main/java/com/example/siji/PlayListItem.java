package com.example.siji;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;

/**
 * Created by kitcoop on 2017-11-13.
 */


public class PlayListItem {


    private ArrayList<PlayListItem> playListItems;
    // xml 읽어서 객체화 시키는 클래스
    private LayoutInflater layoutInflater;
    // 액티비티에 관한 클래스
    private Context context;

    private int play_seq;
    private int category;
    private int kakaoID;
    private String subject;
    private String url;
    private String artist;
    private String lyrics;
    private String content;
    private String tag;
    private String date;
    private int recommend;

    public PlayListItem(int play_seq, String subject, String artist, String tag, int recommend) {
        this.play_seq = play_seq;
        this.subject = subject;
        this.artist = artist;
        this.tag = tag;
        this.recommend = recommend;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getPlay_seq() {
        return play_seq;
    }
    public void setPlay_seq(int play_seq) {
        this.play_seq = play_seq;
    }
    public int getCategory() {
        return category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
    public int getKakaoID() {
        return kakaoID;
    }
    public void setKakaoID(int kakaoID) {
        this.kakaoID = kakaoID;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getLyrics() {
        return lyrics;
    }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
