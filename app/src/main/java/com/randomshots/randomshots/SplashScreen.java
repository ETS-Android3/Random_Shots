package com.randomshots.randomshots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class SplashScreen extends AppCompatActivity {

    Animation rotate;
    Animation slideRight;
    ImageView logo;
    LinearLayout splashLinear;
    public static SharedPreferences sharedPreferences;
    public static boolean isDarkModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences=this.getSharedPreferences("com.randomshots.randomshots", MODE_PRIVATE);
        isDarkModeOn=sharedPreferences.getBoolean("darkMode",false);
        System.out.println("DarkMode:"+isDarkModeOn);
        if(isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        rotate= AnimationUtils.loadAnimation(this,R.anim.rotate);
        logo=(ImageView) findViewById(R.id.logo);
        logo.startAnimation(rotate);
        slideRight=AnimationUtils.loadAnimation(this,R.anim.right_slide);
        splashLinear=(LinearLayout) findViewById(R.id.splashLinear);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            splashLinear.startAnimation(slideRight);
            splashLinear.setVisibility(View.GONE);
            }
        },250);

        final Intent intent=new Intent(this,MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },3000);
    }
}