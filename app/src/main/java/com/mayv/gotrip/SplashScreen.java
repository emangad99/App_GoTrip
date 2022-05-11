package com.mayv.gotrip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    ProgressBar pBar;
    int progress;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        pBar = findViewById(R.id.pBar);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        };
        startProgressListener();
    }

    private void checkUser() {
        SharedPreferences shared = getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        boolean userLoggedBefore = shared.getBoolean("UserLoggedBefore", false);
        Intent intent;
        if(userLoggedBefore){
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }else{
            intent = new Intent(SplashScreen.this, LoginRegisterActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    public void startProgressListener() {
        progress = pBar.getProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progress<100){
                    progress += 4;
                    handler.post((new Runnable() {
                        @Override
                        public void run() {
                            pBar.setProgress(progress);
                        }
                    }));
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(runnable, 50);
            }
        }).start();
    }
}