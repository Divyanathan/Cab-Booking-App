package com.adaptavant.cabapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.util.UtililtyClass;

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreenActivity";
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mImageView=(ImageView) findViewById(R.id.cabImage);
//        Animation lAnimation= AnimationUtils.loadAnimation(this,R.anim.splash_screen_animation);
//        mImageView.setAnimation(lAnimation);
        Handler lHandler=new Handler();
        lHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent lnavigateToActivity = null;
                if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_USER_LOGED_IN, false)) {
                    lnavigateToActivity = new Intent(SplashScreenActivity.this, com.adaptavant.cabapp.ui.BookCabActivity.class);
                    startActivity(lnavigateToActivity);
                    finish();
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }else {
                    lnavigateToActivity = new Intent(SplashScreenActivity.this, com.adaptavant.cabapp.ui.LoginActivity.class);
                    startActivity(lnavigateToActivity);
                    finish();
                    overridePendingTransition(0,0);
                }


            }
        },1000);
    }
}
