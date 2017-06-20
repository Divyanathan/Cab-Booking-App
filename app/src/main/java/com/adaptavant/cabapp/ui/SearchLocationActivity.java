package com.adaptavant.cabapp.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.adaptavant.cabapp.LocationAdapter;
import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.common.CommonClass;
import com.adaptavant.cabapp.datbase.CabRouteTable;
import com.adaptavant.cabapp.jdo.RouteJDO;
import com.adaptavant.cabapp.util.UtililtyClass;

import java.util.ArrayList;

public class SearchLocationActivity extends AppCompatActivity {


    EditText mSearchEditText;
    ImageView mMicImageView, mSearchImageView;
    ListView mListView;
    LocationAdapter mSearchLocationAdapter;
    CabRouteTable mRooteTable;
    ArrayList<RouteJDO> mRoutArraylist = new ArrayList<>();
    ArrayList<RouteJDO> mTempRoutArrayList = new ArrayList<>();
    String mSetLocation;
    boolean mIsResultFound = true;
    final int SEARCH_FROM_LOCATION_CODE = 1;
    final int SEARCH_TO_LOCATION_CODE = 2;
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
        mListView = (ListView) findViewById(R.id.routeListView);

        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);
        lLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mSetLocation = getIntent().getStringExtra(UtililtyClass.CHOOSE_LOCATION);
        /**
         * set tht on item click listener for recycler view
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsResultFound) {
                    hideKeyboard();
                    Log.d(TAG, "onItemClick: " + mTempRoutArrayList.get(position).getRoutID());
                    Intent lLoctionResultIntnet = new Intent();
                    lLoctionResultIntnet.putExtra(UtililtyClass.ROUTE_NAME, mTempRoutArrayList.get(position).getRoutName());
                    lLoctionResultIntnet.putExtra(UtililtyClass.ROUTE_ID, mTempRoutArrayList.get(position).getRoutID());
                    if (mSetLocation.equals(UtililtyClass.FROM_LOCATION)) {
                        setResult(SEARCH_FROM_LOCATION_CODE, lLoctionResultIntnet);
                    } else {
                        setResult(SEARCH_TO_LOCATION_CODE, lLoctionResultIntnet);
                    }
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                }
            }
        });


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
                Log.d(TAG, "onTextChanged: " + pText + " " + mRoutArraylist.size());
                String lSearchText = pText.toString().toLowerCase();
                mTempRoutArrayList.clear();
                boolean lisResultFound = false;
                for (int i = 0; i < mRoutArraylist.size(); i++) {
                    if (mRoutArraylist.get(i).getRoutName().toLowerCase().contains(lSearchText)) {
                        mTempRoutArrayList.add(mRoutArraylist.get(i));
                        lisResultFound = true;
                        Log.d(TAG, "onTextChanged: true");
                    } else {
                        Log.d(TAG, "onTextChanged: false");
                    }
                }
                if (lisResultFound) {
                    mIsResultFound = true;
                } else {
                    mIsResultFound = false;
                    mTempRoutArrayList.add(new RouteJDO("No Match Found ", "no_id"));
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

                RouteJDO lRouteJDO = new RouteJDO(lRoutCursor.getString(lRoutCursor.getColumnIndex(CabRouteTable.COLUMN_ROUTE_NAME)), lRoutCursor.getString(lRoutCursor.getColumnIndex(CabRouteTable.COLUMN_ID)));

                mRoutArraylist.add(lRouteJDO);
            } while (lRoutCursor.moveToNext());

            if (mSearchLocationAdapter == null) {
                mTempRoutArrayList.addAll(mRoutArraylist);
                mSearchLocationAdapter = new LocationAdapter(this, mTempRoutArrayList);
                mListView.setAdapter(mSearchLocationAdapter);
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
                mSearchEditText.setSelection(lResult.get(0).length());
                for (int i = 0; i < lResult.size(); i++) {
                    Log.d(TAG, "onActivityResult: data " + lResult.get(i));
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        return true;
    }

     void hideKeyboard() {
        InputMethodManager linputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        linputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
