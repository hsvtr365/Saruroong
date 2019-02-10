package com.example.siji.message;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siji.BaseActivity;
import com.example.siji.MainActivity1;
import com.example.siji.MenuFragment;
import com.example.siji.R;

public class MessageMainActivity extends BaseActivity {

    private ViewPager vp;
    private LinearLayout ll;
    private Context mContext = null;
    private int img =0;

    boolean isFragmentLoaded;
    Fragment menuFragment;
    TextView title;
    ImageView menuButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAddlayout(R.layout.message_main);

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
                Intent intent = new Intent(MessageMainActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        });









        vp = (ViewPager) findViewById(R.id.vp);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.msgWriteBtn01);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageMainActivity.this, MessageWriteActivity.class);
                startActivityForResult(intent,0);//0번 이라는 키값을 가짐
                finish();
                Toast.makeText(MessageMainActivity.this, "글쓰기 버튼 클릭",Toast.LENGTH_SHORT).show();
            }
        });

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(2);
        vp.setCurrentItem(0);//앱이 실행됬을때첫번째 페이지로 초기화 시키는 부분

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("ITPANGPANG","onPageScrolled : "+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("ITPANGPANG","onPageScrolled : "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("ITPANGPANG","onPageScrollStateChanged : "+state);
            }
        });









    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            Toast.makeText(MessageMainActivity.this, "선택",Toast.LENGTH_SHORT).show();
            int i = 0;
            while (i < 3) {
                if (tag == i) {
                    ll.findViewWithTag(i).setSelected(true);
                } else {
                    ll.findViewWithTag(i).setSelected(false);
                }
                i++;
            }
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter {//페이지 어뎁터
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // return MessageMainPageFragment.create(position);
            switch (position) {
                case 0:
                    return new MessageMainPageFragment();
                case 1:
                    return new MessageMainPageFragment2();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    //A엑티비티에서 B 엑티비티로 넘어갔다가 다시 A엑티비티로 돌아올때 사용하는 안드로이드에서 제공하는 기본메소드 입니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) { //write 엑티비티- 인텐트가 0인 거를 가져오는거임
            if(resultCode == RESULT_OK){}
        }
        if(requestCode == 1) {  //View 엑티비티- 인텐트가 1인 거를 가져오는거임
            if(resultCode == RESULT_OK){}
        }
        // FragmentTransaction ft = getFragmentManager().beginTransaction();
        /*
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach().attach(this).commit();*/
        //프래그먼트 새로고침 구문
      /*  Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();*/

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
        menuFragment = fm.findFragmentById(R.id.main_content);
        menuButton.setImageResource(R.drawable.ic_up_arrow);
        if (menuFragment == null) {
            menuFragment = new MenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_on, R.anim.slide_off);
            fragmentTransaction.add(R.id.main_content, menuFragment);
            fragmentTransaction.commit();
        }

        isFragmentLoaded = true;
    }

}