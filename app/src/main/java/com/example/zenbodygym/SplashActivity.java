package com.example.zenbodygym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;


public class SplashActivity extends AppCompatActivity {
    Animation app_splash;
    ImageView logo;

    DatabaseReference reference,reference2;
    String USERNAME_KEY="usernamekey";
    String usernamekey="";
    String usernamekey_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getUsernameLocal();
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);

        logo = findViewById(R.id.logo);

        logo.startAnimation(app_splash);
    }

    public void getUsernameLocal()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY,MODE_PRIVATE);
        usernamekey_new= sharedPreferences.getString(usernamekey, "");
        if (usernamekey_new.isEmpty()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent moveIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(moveIntent);
                    finish();
                }
            }, 2000);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent moveIntent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(moveIntent);
                    finish();
                }
            },2000);
        }
    }
}
