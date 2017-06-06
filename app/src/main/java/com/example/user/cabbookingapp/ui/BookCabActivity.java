package com.example.user.cabbookingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.adapter.GridViewAdapter;
import com.example.user.cabbookingapp.custom.RoundedTransformation;
import com.example.user.cabbookingapp.jdo.UserProfilePic;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

public class BookCabActivity extends AppCompatActivity {

    GridView mGridView;
    GridViewAdapter mGridViewAdapter;
    ImageView mProfileImage;
    String[] mTimings = {
            "4:00 AM",
            "5:00 AM",
            "6:00 AM",
            "7:00 AM",
            "8:00 AM",
            "9:00 AM",
            "10:00 AM",
            "11:00 AM",
            "12:00 AM",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);
        mGridView = (GridView) findViewById(R.id.timingsGridView);
        if (mGridViewAdapter == null) {
            mGridViewAdapter=new GridViewAdapter(this, mTimings);
            mGridView.setAdapter(mGridViewAdapter);

        }
        mGridViewAdapter.notifyDataSetChanged();
        mProfileImage = (ImageView) findViewById(R.id.book_cab_profile_image);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lProfilePageIntent = new Intent(BookCabActivity.this, UserProfileActivity.class);
                startActivity(lProfilePageIntent);
            }
        });

        Picasso.with(this)
                .load(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_IMAGE_URL, "") + "0")
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 1))
                .into((ImageView) findViewById(R.id.book_cab_profile_image));
    }
}
