package com.example.user.cabbookingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.adapter.GridViewAdapter;
import com.example.user.cabbookingapp.custom.RoundedTransformation;
import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.BookingTimeJDO;
import com.example.user.cabbookingapp.service.RoutAndTimingService;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.user.cabbookingapp.common.CommonClass.getTheHttpUrlHelper;

public class BookCabActivity extends AppCompatActivity {

    GridView mGridView;
    GridViewAdapter mGridViewAdapter;
    TextView mFromLocationTextView, mToLoactionTextView;
    ArrayList<BookingTimeJDO> mBookingTimeArraList = new ArrayList<>();
    ImageView mProfileImage, mSwapLocatoin;
    int mHour, mMinute, mTime;
    final int SEARCH_FROM_LOCATION_CODE = 1;
    final int SEARCH_TO_LOCATION_CODE = 2;
    int mBookingSlot;
    CabTimingTable mTimingTable;
    String mRouteID;
    String mBookingStatus;
    String[] mTimingID = {
            "s5b9b43ba-9929-4921-91d7-0668e45667dd",
            "sc77e8b5d-dc74-4578-a012-64899f3a6d3e",
            "s24035e52-b0ad-4859-b5a3-be9036738ce4",
            "se9c88e51-2716-4361-8904-1d682a661259",
            "s5c971e56-de2d-46e6-a109-d3fd1c9345c0",
            "sec1b8c40-cd8c-468c-aa8e-3d7dfae69d26",
            "s119530ce-cdf8-4047-a15a-1059c4ae8110",
            "s93255bd8-1294-4189-8464-153f4eb50cb0",
            "s96971ece-2458-421f-84e9-0d5f4567770d",};
    String[] mDisplayTimings = {
            "5:00 AM",
            "6:30 AM",
            "7:00 AM",
            "8:00 AM",
            "6:30 PM",
            "8:00 PM",
            "10:00 PM",
            "10:30 PM",
            "12:30 AM",};
    int[] mBookingTimgs = {
            1380,
            1380,
            1380,
            1380,
            990,
            1080,
            1200,
            1320,
            1350
    };


    private static final String TAG = "BookCabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);

        mGridView = (GridView) findViewById(R.id.timingsGridView);
        mFromLocationTextView = (TextView) findViewById(R.id.fromLoctionTextView);
        mToLoactionTextView = (TextView) findViewById(R.id.toLoctionTextView);
        mSwapLocatoin = (ImageView) findViewById(R.id.doubleArrowImageView);

        //timing table
        mTimingTable = new CabTimingTable(this);
        mTimingTable.open();

        //set the adapter for recycler view
        if (mGridViewAdapter == null) {
            mGridViewAdapter = new GridViewAdapter(this, mBookingTimeArraList);
            mGridView.setAdapter(mGridViewAdapter);

        }

        //set the listener to choose from location
        mFromLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: from location Textview");
                Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
                lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.FROM_LOCATION);
                startActivityForResult(lSearchLocationIntent, SEARCH_FROM_LOCATION_CODE);
            }
        });

        //set the listener to choose to location
        mToLoactionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
                lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.TO_LOCATION);
                startActivityForResult(lSearchLocationIntent, SEARCH_TO_LOCATION_CODE);
                Log.d(TAG, "onClick: to location Textview");
            }
        });

        //set the listener to select the timings
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!mBookingTimeArraList.get(position).getStatus().equals(UtililtyClass.CAB_IS_NOT_AVAILABLE)) {

                    mBookingSlot = position;
                    for (int i = 0; i < 9; i++) {
                        if (i == position) {
                            mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_BOOKED);
                        } else if (mBookingTimeArraList.get(i).getBookingTime() > mTime) {
                            mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_AVAILABLE);
                        } else {
                            mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_NOT_AVAILABLE);
                        }
                    }

                    Log.d(TAG, "onItemClick: true ");
                    mGridViewAdapter.notifyDataSetChanged();
                }
                Log.d(TAG, "onItemClick:  always");
            }
        });

        //set the listener to swap the location
        mSwapLocatoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lFromLocation, lToLocation;
                lFromLocation = mFromLocationTextView.getText().toString();
                lToLocation = mToLoactionTextView.getText().toString();
                mFromLocationTextView.setText(lToLocation);
                mToLoactionTextView.setText(lFromLocation);
            }
        });

        //set the listener to navigate to the user profile activity
        mProfileImage = (ImageView) findViewById(R.id.book_cab_profile_image);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lProfilePageIntent = new Intent(BookCabActivity.this, UserProfileActivity.class);
                startActivity(lProfilePageIntent);
            }
        });

        //get the current timing from the device
        Calendar lCalender = Calendar.getInstance();
        mHour = (int) lCalender.get(Calendar.HOUR_OF_DAY);
        mMinute = (int) lCalender.get(Calendar.MINUTE);
        mTime = (mHour * 60) + mMinute;
        Log.d(TAG, "onCreate: " + " " + mHour + " " + mMinute + " " + mTime);

        //set the use profile pic
        Picasso.with(this)
                .load(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_IMAGE_URL, "") + "0")
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 1))
                .into((ImageView) findViewById(R.id.book_cab_profile_image));

        //set the adapter for gridview withe available timings
        setAdapter();

        Log.d(TAG, "onCreate: ");
    }

    public void bookCab(View pView) {

        String lTimingID = mTimingID[mBookingSlot];

        Cursor lTimingCursor = mTimingTable.getTimingDetails(lTimingID);
        if (lTimingCursor.getCount() > 0 && lTimingCursor.moveToFirst()) {

            JSONObject lAppointmentJson = new JSONObject();
            JSONObject lCustomerJSon = new JSONObject();
            JSONObject lCabBookingJson=new JSONObject();

            try {
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAR_TIME_LONG, ""+lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG)));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_STATUS, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_STATUS)));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_START_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_STRIG)));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAFF_KEY,mRouteID);
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_SERVICE_KEY,lTimingID);
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY,"no_key"));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY,"no_key"));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_END_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_STRIG)));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_F_KEY, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_COMPANY_KEY)));
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_DATE, "Date");
                lAppointmentJson.put(UtililtyClass.CAB_BOOK_END_TIME_LONG, ""+lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_LONG)));

                lCustomerJSon.put(UtililtyClass.CAB_BOOK_STAR_TIME_LONG,""+lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG)));
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_STATUS, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_STATUS)));
                lCustomerJSon.put(UtililtyClass.CUSTOMER_NAME,getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_NAME,"no_name"));
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY,"no_key"));
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY,"no_key"));
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_END_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_STRIG)));
                lCustomerJSon.put(UtililtyClass.CUSTOMER_PHONE, "phone_number");
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_DATE, "Date");
                lCustomerJSon.put(UtililtyClass.CUSTOMER_LOGIN_ID, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getString(UtililtyClass.USER_LOGIN_ID,"no_key"));
                lCustomerJSon.put(UtililtyClass.CAB_BOOK_F_KEY, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_COMPANY_KEY)));

                lCabBookingJson.put(UtililtyClass.CAB_BOOK_APPOINTMENT,lAppointmentJson);
                lCabBookingJson.put(UtililtyClass.CUSTOMER,lCustomerJSon);


                HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.BOOKING_URL, "POST", lCabBookingJson.toString());
                Intent lGetContactIntent = new Intent(BookCabActivity.this, RoutAndTimingService.class);
                lGetContactIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetContactHttpUrlHelper);
                lGetContactIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.CONTACT_API);
                Log.d(TAG, "bookCab: "+lCabBookingJson.toString());
                startService(lGetContactIntent);

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_FROM_LOCATION_CODE) {
            if (data != null) {
                Log.d(TAG, "onActivityResult: From location " + data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mRouteID = data.getStringExtra(UtililtyClass.ROUTE_ID);
                mFromLocationTextView.setText(data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mToLoactionTextView.setText(UtililtyClass.COMPANY_LOCATION);
            }
        }
        if (requestCode == SEARCH_TO_LOCATION_CODE) {

            if (data != null) {
                Log.d(TAG, "onActivityResult: To location " + data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mRouteID = data.getStringExtra(UtililtyClass.ROUTE_ID);
                mToLoactionTextView.setText(data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mFromLocationTextView.setText(UtililtyClass.COMPANY_LOCATION);
            }
        }
        Log.d(TAG, "onActivityResult: ");
    }

    //set the adapter for gridview for available timings
    void setAdapter() {

        for (int i = 0; i < 9; i++) {

            Log.d(TAG, "setAdapter: timings" + mBookingTimgs[i] + " " + mTime);
            if (mBookingTimgs[i] > mTime) {
                mBookingStatus = UtililtyClass.CAB_AVAILABLE;
            } else {
                mBookingStatus = UtililtyClass.CAB_IS_NOT_AVAILABLE;
            }
            mBookingTimeArraList.add(new BookingTimeJDO(mTimingID[i], mDisplayTimings[i], mBookingTimgs[i], mBookingStatus));
        }
        mGridViewAdapter.notifyDataSetChanged();

        Log.d(TAG, "setAdapter: ");
    }

    @Override
    protected void onDestroy() {
        mTimingTable.close();
        super.onDestroy();
    }
}
