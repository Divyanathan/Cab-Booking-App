package com.example.user.cabbookingapp.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.user.cabbookingapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mImageView=(ImageView) findViewById(R.id.cabImage);
        Animation lAnimation= AnimationUtils.loadAnimation(this,R.anim.splash_screen_animation);
        mImageView.setAnimation(lAnimation);
        Handler lHandler=new Handler();
        lHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent lnavigateToActivity=new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(lnavigateToActivity);
                finish();
                overridePendingTransition(R.anim.enter_from_bottom_animation,R.anim.exit_to_left);

            }
        },1000);
    }
}
