package com.example.user.cabbookingapp.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.adapter.SearchLocationAdapter;
import com.example.user.cabbookingapp.common.CommonClass;
import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.jdo.RouteJDO;
import com.example.user.cabbookingapp.listener.RecyclerItemClickListener;
import com.example.user.cabbookingapp.util.UtililtyClass;

import java.util.ArrayList;

public class SearchLocationActivity extends AppCompatActivity {


    EditText mSearchEditText;
    ImageView mMicImageView, mSearchImageView;
    RecyclerView mRecylerView;
    SearchLocationAdapter mSearchLocationAdapter;
    CabRouteTable mRooteTable;
    ArrayList<RouteJDO> mRoutArraylist = new ArrayList<>();
    ArrayList<RouteJDO> mTempRoutArrayList = new ArrayList<>();
    String mSetLocation;
    final int SEARCH_FROM_LOCATION_CODE=1;
    final int SEARCH_TO_LOCATION_CODE=2;
    private final int SPEECH_RECOGNIZAION_CODE = 3;
    private static final String TAG = "SearchLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        mRooteTable = new CabRouteTable(this);
        mRooteTable.open();

        mSearchEditText = (EditText) findViewById(R.id.searchEditText);
        mMicImageView = (ImageView) findViewById(R.id.micImageView);
        mSearchImageView = (ImageView) findViewById(R.id.searchImageView);
        mRecylerView = (RecyclerView) findViewById(R.id.routeRecyclerView);

        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);
        lLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecylerView.setLayoutManager(lLayoutManager);

        mSetLocation=getIntent().getStringExtra(UtililtyClass.CHOOSE_LOCATION);
        /**
         * set tht on item click listener for recycler view
         */
        mRecylerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d(TAG, "onItemClick: "+mTempRoutArrayList.get(position).getRoutID());
                Intent lLoctionResultIntnet=new Intent();
                lLoctionResultIntnet.putExtra(UtililtyClass.ROUTE_NAME,mTempRoutArrayList.get(position).getRoutName());
                lLoctionResultIntnet.putExtra(UtililtyClass.ROUTE_ID,mTempRoutArrayList.get(position).getRoutID());
                if(mSetLocation.equals(UtililtyClass.FROM_LOCATION)) {
                    setResult(SEARCH_FROM_LOCATION_CODE, lLoctionResultIntnet);
                }else {
                    setResult(SEARCH_TO_LOCATION_CODE,lLoctionResultIntnet);
                }
                finish();
            }
        }));

        /**
         * set the recycler view Adapter with Route
         */
        getRoute();

        mMicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.isDataAvailable(SearchLocationActivity.this)) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak");
                    try {
                        startActivityForResult(intent, SPEECH_RECOGNIZAION_CODE);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(),
                                "speak",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchLocationActivity.this, "Please Check ur internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * set  searching option
         */
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence pText, int start, int before, int count) {

                Log.d(TAG, "onTextChanged: " + pText+" "+mRoutArraylist.size());
                String lSearchText = pText.toString().toLowerCase();
                mTempRoutArrayList.clear();
                for (int i = 0; i < mRoutArraylist.size(); i++) {
                    if (mRoutArraylist.get(i).getRoutName().toLowerCase().contains(lSearchText)) {
                        mTempRoutArrayList.add(mRoutArraylist.get(i));
                        Log.d(TAG, "onTextChanged: true");
                    }else {
                        Log.d(TAG, "onTextChanged: false");
                    }
                }

                mSearchLocationAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //get rout from the database
    void getRoute() {

        Cursor lRoutCursor = mRooteTable.getRoute();
        if (lRoutCursor.getCount() > 0) {
            lRoutCursor.moveToFirst();
            do {

                RouteJDO lRouteJDO=new RouteJDO(lRoutCursor.getString(lRoutCursor.getColumnIndex(CabRouteTable.COLUMN_ROUTE_NAME)),lRoutCursor.getString(lRoutCursor.getColumnIndex(CabRouteTable.COLUMN_ID)));

                mRoutArraylist.add(lRouteJDO);
            } while (lRoutCursor.moveToNext());

            if (mSearchLocationAdapter == null) {
                mTempRoutArrayList.addAll(mRoutArraylist);
                mSearchLocationAdapter = new SearchLocationAdapter(this, mTempRoutArrayList);
                mRecylerView.setAdapter(mSearchLocationAdapter);
            } else {
                mSearchLocationAdapter.notifyDataSetChanged();
            }

        }
        Log.d(TAG, "getRoute: getting root form the data base");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_RECOGNIZAION_CODE) {
            Log.d(TAG, "onActivityResult: " + data);
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> lResult = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mSearchEditText.setText(lResult.get(0));
                for (int i = 0; i < lResult.size(); i++) {
                    Log.d(TAG, "onActivityResult: data " + lResult.get(i));
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
