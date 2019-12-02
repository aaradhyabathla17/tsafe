package com.techespo.tsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.techespo.tsafe.Database.MyDBHelper;

public class SplashActivity extends AppCompatActivity {
    Handler mhandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        mhandler.postDelayed(rn, 3000);
        MyDBHelper dbHelper = new MyDBHelper(getApplicationContext());

    }

    Runnable rn = new Runnable() {
        @Override
        public void run() {
            SharedPreferences splogin = getSharedPreferences("firstlogin", 0);
            if (splogin.getBoolean("isloginalready", false)) {
                Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
