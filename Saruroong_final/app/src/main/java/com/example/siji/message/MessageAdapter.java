package com.example.siji.message;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.siji.R;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {
    private ArrayList<MessageItem> datas;//데이터
    private LayoutInflater layoutInflater;//XML읽어 객체화 시키는 클래스
    private Context context;//메인엑티비티에 관한 클라스 오지구요 지리구요 수정각이구요~~

    //생성자
    public MessageAdapter(Context context, ArrayList<MessageItem> datas) {
        this.datas = datas;
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override//전체 데이터 갯수
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position).getDate1();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //http://hsvtr365.blog.me/221108080899?Redirect=Log&from=postView
    @Override//한행 디자인
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView =layoutInflater.inflate(R.layout.message_fragment_item,parent,false);
        }

        //데이터 입력구간
        TextView tv1 = (TextView)convertView.findViewById(R.id.messageRowsTV01);
        TextView tv2 = (TextView)convertView.findViewById(R.id.messageRowsTV02);
        TextView tv3 = (TextView)convertView.findViewById(R.id.messageRowsTV03);
        TextView tv4 = (TextView)convertView.findViewById(R.id.messageRowsTV04);
        ImageView iv1 =(ImageView)convertView.findViewById(R.id.msgMainIV01);
        MessageItem messageItem = datas.get(position);

        tv1.setText(messageItem.getContext());
        tv2.setText(messageItem.getDate1());
        tv3.setText(messageItem.getDate2());
        tv4.setText(messageItem.getDate3());
        // iv1.setBackground(messageItem.getImage());

        switch (messageItem.getImage()) {
            case "0": iv1.setImageResource(R.mipmap.a);
                break;
            case "1":iv1.setImageResource(R.mipmap.b);
                break;
            case "2":iv1.setImageResource(R.mipmap.c);
                break;
            case "3":iv1.setImageResource(R.mipmap.d);
                break;
            case "4":iv1.setImageResource(R.mipmap.e);
                break;
            case "5":iv1.setImageResource(R.mipmap.f);
                break;
            case "6":iv1.setImageResource(R.mipmap.g);
                break;
            case "7":iv1.setImageResource(R.mipmap.h);
                break;

            default:
        }



        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}

class MessageItem {
    private String context;
    private String date1;
    private String date2;
    private String date3;
    private String image;
    private String msg_no;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMsg_no() {
        return msg_no;
    }

    public void setMsg_no(String msg_no) {
        this.msg_no = msg_no;
    }

    public MessageItem(String context, String date1, String date2, String date3, String image, String msg_no) {
        this.context = context;
        this.date1 = date1;
        this.date2 = date2;
        this.date3 = date3;
        this.image = image;
        this.msg_no = msg_no;
    }
}
