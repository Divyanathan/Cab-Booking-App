package com.example.user.cabbookingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.custom.RoundedTransformation;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //set the back button
        Toolbar lToolbar = (Toolbar) findViewById(R.id.toolBarTitle);
        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set the title and title color
        CollapsingToolbarLayout lCollapsingToolBarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);
        lCollapsingToolBarLayout.setTitle(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_NAME,"no_name"));
        lCollapsingToolBarLayout.setExpandedTitleColor(ContextCompat.getColor(this,R.color.white));
        lCollapsingToolBarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.white));

        //set the size of the title
        lCollapsingToolBarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
        lCollapsingToolBarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//        Typeface lFont= Typer.set(this).getFont(Font.ROBOTO_MEDIUM);
//        lCollapsingToolBarLayout.setExpandedTitleTypeface(lFont);

        //set the profile image
        SharedPreferences lSharedPrefrence=getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        String lImageUrl=lSharedPrefrence.getString(UtililtyClass.USER_IMAGE_URL,"");
        lImageUrl=lImageUrl.substring(0,(lImageUrl.length())-6);
        Log.d(TAG, "onCreate: "+lImageUrl);
        Picasso.with(this)
                .load(lImageUrl)
                .resize(600, 600)
                .into((ImageView)findViewById(R.id.userProfilrImage));

        //set the email address
        ((TextView)findViewById(R.id.emailTextView)).setText(lSharedPrefrence.getString(UtililtyClass.USER_LOGIN_ID,"no_id"));
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
