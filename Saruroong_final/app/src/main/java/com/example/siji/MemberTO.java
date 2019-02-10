package com.example.siji;

/**
 * Created by com on 2017-11-09.
 */

public class MemberTO {

    private int memno;
    private int kakaoid;
    private String nickname;
    private String thumbnail_image;
    private String created;
    private int conditon;
    private String accessdate;
    private int mlevel;
    private int mlock;
    private String lock_no;

    public int getMemno() {
        return memno;
    }

    public void setMemno(int memno) {
        this.memno = memno;
    }

    public int getKakaoid() {
        return kakaoid;
    }

    public void setKakaoid(int kakaoid) {
        this.kakaoid = kakaoid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getConditon() {
        return conditon;
    }

    public void setConditon(int conditon) {
        this.conditon = conditon;
    }

    public String getAccessdate() {
        return accessdate;
    }

    public void setAccessdate(String accessdate) {
        this.accessdate = accessdate;
    }

    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

    public int getMlock() {
        return mlock;
    }

    public void setMlock(int mlock) {
        this.mlock = mlock;
    }

    public String getLock_no() {
        return lock_no;
    }

    public void setLock_no(String lock_no) {
        this.lock_no = lock_no;
    }
}
