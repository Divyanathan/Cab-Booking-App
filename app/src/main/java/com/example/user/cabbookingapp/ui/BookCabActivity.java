package com.example.user.cabbookingapp.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.custom.RoundedTransformation;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

public class BookCabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);

        Picasso.with(this)
                .load(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_IMAGE_URL,"")+"0")
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 1))
                .into((ImageView)findViewById(R.id.book_cab_profile_image));
    }
}
