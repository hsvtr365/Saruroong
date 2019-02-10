package com.example.siji;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CookieSplashActivity extends AppCompatActivity {

    ImageView imageView;
    AnimationDrawable drawable;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cookie_splash);

        imageView = (ImageView) findViewById(R.id.splash1);
        drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.setOneShot(true);
        drawable.start();


        findViewById(R.id.splash1).postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.stop();

                    Intent intent = new Intent(CookieSplashActivity.this, CookieResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
            }
        }, 1100);
    }
}
