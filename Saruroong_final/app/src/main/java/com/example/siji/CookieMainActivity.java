package com.example.siji;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Random;
import java.util.ResourceBundle;

public class CookieMainActivity extends AppCompatActivity {
    ImageView imageView;
    AnimationDrawable drawable;
    private Handler mHandler = new Handler();
    String suji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cookie_main);

        imageView = (ImageView) findViewById(R.id.CookieBtn2);
        drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.setOneShot(true);


        findViewById(R.id.CookieBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable mMyTask = new Runnable() {
                    @Override public void run() {
                        Intent intent = new Intent(CookieMainActivity.this, CookieSplashActivity.class);
                        startActivity(intent);
                        drawable.stop();
                    }
                };

                if(!drawable.isRunning()) {
                    drawable.start();
                    mHandler.postDelayed(mMyTask, 70);
                }
    }
        });
    }
}
