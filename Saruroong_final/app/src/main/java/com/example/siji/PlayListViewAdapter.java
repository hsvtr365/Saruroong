package com.example.siji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kitcoop on 2017-11-13.
 */

public class PlayListViewAdapter extends BaseAdapter {
    // 데이터
    private ArrayList<PlayListItem> playListItems;
    // xml 읽어서 객체화 시키는 클래스
    private LayoutInflater layoutInflater;
    // 액티비티에 관한 클래스
    private Context context;


    public PlayListViewAdapter(Context context, ArrayList<PlayListItem> playListItems) {
        this.context = context;
        this.playListItems = playListItems;

        this.layoutInflater
                = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    // 데이터 갯수
    @Override
    public int getCount() {
        System.out.println("getCount() 호출");
        return  playListItems.size();
    }

    // 위치에 따라서 한개의 데이터를 가져오는 방법
    @Override
    public Object getItem(int position) {
        // 테스트
        return playListItems.get(position).getPlay_seq();
    }

    // 데이터의 아이디 값(의미 X)
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 한 행의 디자인과 데이터를 결합 방법 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        System.out.println("getView()호출" + position);
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.content_playlist_list, parent, false);
        }

        PlayListItem playItem = playListItems.get(position);

        TextView textView1 = (TextView)convertView.findViewById(R.id.playlist_singer);
        textView1.setText(playItem.getArtist());

        TextView textView2 = (TextView)convertView.findViewById(R.id.playlist_song);
        textView2.setText(playItem.getSubject());

        TextView textView3 = (TextView)convertView.findViewById(R.id.playlist_keyword);
        textView3.setText(playItem.getTag());

        TextView textView4 = (TextView)convertView.findViewById(R.id.playlist_recommend);
        textView4.setText(""+playItem.getRecommend());

       TextView textView5 = (TextView)convertView.findViewById(R.id.playlist_number);
       textView5.setText(""+playItem.getPlay_seq());

        //ImageView imageView = (ImageView)convertView.findViewById(R.id.);
        //imageView.setImageResource(myItem.getIcon());

        /*
        Button button = (Button)convertView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), position+"", Toast.LENGTH_SHORT).show();
            }
        });  */
        return convertView;
    }
}
