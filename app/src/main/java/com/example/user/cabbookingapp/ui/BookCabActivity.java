package com.example.user.cabbookingapp.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.adapter.GridViewAdapter;
import com.example.user.cabbookingapp.common.CommonClass;
import com.example.user.cabbookingapp.custom.RoundedTransformation;
import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.BookingTimeJDO;
import com.example.user.cabbookingapp.reciver.ReminderReciver;
import com.example.user.cabbookingapp.service.RoutAndTimingService;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.user.cabbookingapp.common.CommonClass.getTheHttpUrlHelper;

public class BookCabActivity extends AppCompatActivity {

    GridView mGridView;
    GridViewAdapter mGridViewAdapter;
    TextView mFromLocationTextView, mToLoactionTextView, mLableTextView, mBookingServiceTextView;
    ArrayList<BookingTimeJDO> mBookingTimeArraList = new ArrayList<>();
    ImageView mProfileImage, mSwapLocatoin;
    LinearLayout mBookingInformationLayout;
    LinearLayout mFromLocationLayout;
    LinearLayout mToLocationLayout;
    CardView mLocationCardView;
    Button mBookingButton;
    int mHour, mMinute, mTime;
    String mDate;
    final int SEARCH_FROM_LOCATION_CODE = 1;
    final int SEARCH_TO_LOCATION_CODE = 2;
    final int USER_PROFILE_PAGE_REQUEST_CODE = 3;
    boolean mIsLocationSelectedByUser = false;
    boolean mDoNotSetPreferredTiming = false;
    int mBookingSlot;
    ProgressDialog mProgressDialog;
    String mRouteID = null;
    String mRouteTimingID;
    String mBookingStatus;
    CabRouteTable mRoutTable;
    CabTimingTable mTimingTable;
    String[] mTimingID = {

            "sec1b8c40-cd8c-468c-aa8e-3d7dfae69d26",
            "s119530ce-cdf8-4047-a15a-1059c4ae8110",
            "s93255bd8-1294-4189-8464-153f4eb50cb0",
            "s96971ece-2458-421f-84e9-0d5f4567770d",
            "s5b9b43ba-9929-4921-91d7-0668e45667dd",
            "sc77e8b5d-dc74-4578-a012-64899f3a6d3e",
            "s24035e52-b0ad-4859-b5a3-be9036738ce4",
            "se9c88e51-2716-4361-8904-1d682a661259"
    };
    String[] mDisplayTimings = {

            "8:00 PM",
            "10:00 PM",
            "10:30 PM",
            "12:30 AM",
            "5:00 AM",
            "6:30 AM",
            "7:00 AM",
            "8:00 AM"

    };
    int[] mBookingTimgs = {
            1080,
            1200,
            1230,
            1350,
            1380,
            1380,
            1380,
            1380
    };

    private static final String TAG = "BookCabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);

        mGridView = (GridView) findViewById(R.id.timingsGridView);
        mFromLocationTextView = (TextView) findViewById(R.id.fromLoctionTextView);
        mToLoactionTextView = (TextView) findViewById(R.id.toLoctionTextView);
        mLableTextView = (TextView) findViewById(R.id.bookingTitleTextView);
        mSwapLocatoin = (ImageView) findViewById(R.id.doubleArrowImageView);
        mBookingServiceTextView = (TextView) findViewById(R.id.bookingTimeTextView);
        mBookingButton = (Button) findViewById(R.id.booking_button);
        mLocationCardView = (CardView) findViewById(R.id.locationCardView);
        mBookingInformationLayout = (LinearLayout) findViewById(R.id.bookingInformationLayout);
        mFromLocationLayout = (LinearLayout) findViewById(R.id.fromLocationLayout);
        mToLocationLayout = (LinearLayout) findViewById(R.id.toLocationLayout);
        mRoutTable = new CabRouteTable(this);
        mTimingTable = new CabTimingTable(this);
        mRoutTable.open();
        mTimingTable.open();

        //timing table
        mTimingTable = new CabTimingTable(this);
        mTimingTable.open();

        //set the adapter for recycler view
        if (mGridViewAdapter == null) {
            mGridViewAdapter = new GridViewAdapter(this, mBookingTimeArraList);
            mGridView.setAdapter(mGridViewAdapter);

        }

        //set the listener to select the timings
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!mBookingTimeArraList.get(position).getStatus().equals(UtililtyClass.CAB_IS_NOT_AVAILABLE)) {

                    mBookingSlot = position;
                    for (int i = 0; i < 8; i++) {
                        if (i == position) {
                            mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_BOOKED);
                            setTheTimingIsSelected();
                        } else if (mBookingTimeArraList.get(i).getBookingTime() >= getTime()) {
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
                startActivityForResult(lProfilePageIntent, USER_PROFILE_PAGE_REQUEST_CODE);
            }
        });


        //set the use profile pic
        Picasso.with(this)
                .load(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_IMAGE_URL, "") + "0")
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 1))
                .into((ImageView) findViewById(R.id.book_cab_profile_image));

        //set the adapter for gridview withe available timings
        setAdapter();

        //register the broadcast reciever
        LocalBroadcastManager.getInstance(this).registerReceiver(mCabBookingReciever, new IntentFilter(UtililtyClass.CAB_BOOKING_RECIVER));
        Log.d(TAG, "onCreate: ");
    }

    //get the current timing from the device
    int getTime() {
        Calendar lCalender = Calendar.getInstance();
        mHour = (int) lCalender.get(Calendar.HOUR_OF_DAY);
        mMinute = (int) lCalender.get(Calendar.MINUTE);
        mTime = (mHour * 60) + mMinute;
        return mTime;
    }

    /**
     * change the available timing
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getTime();

        //set the prefered location and timing for the user
        mRouteID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USRE_PREFERED_STAFF, null);

        //to check whether the cab is booked..if booked this block will get call
        if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false)) {

            changeValueForbookingCab();
        }

        // to set the preferred location and time of the user
        else if (mRouteID != null) {

            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, true)
                    .commit();
            setPreferredTimingAndLocaion();
        }
        //while log_in if the user dose not have any preferred location is part will get call
        else {
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, false)
                    .commit();
        }
    }

    // set the preferred location of the user
    private void setPreferredTimingAndLocaion() {

        Log.d(TAG, "onResume: route id" + mRouteID + "service id " + mRouteTimingID);

        //update the preferred location...don't update when the user select the location from list
        if (!mIsLocationSelectedByUser) {
            setLoction();
        }
        //once the location is selected by the user mark it as false
        else {
            mIsLocationSelectedByUser = false;
        }

        //set the timing
        setTiming();
        Log.d(TAG, "onResume: set the The Prefred location and Timing");
    }

    private void setTiming() {
        boolean lIsTimeSelected = false;
        mRouteTimingID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null);
        for (int i = 0; i < mTimingID.length; i++) {
            if (mRouteTimingID.equals(mTimingID[i]) && mBookingTimgs[i] >= getTime()) {

                if(!mDoNotSetPreferredTiming) {
                    mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_BOOKED);
                    lIsTimeSelected = true;
                    mBookingSlot = i;
                    setTheTimingIsSelected();
                    Log.d(TAG, "onResume: set the prefred timings");
                }else {
                    mDoNotSetPreferredTiming = false;
                }
            } else if (mBookingTimeArraList.get(i).getBookingTime() >= getTime()) {
                mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_AVAILABLE);
            } else {
                mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_NOT_AVAILABLE);
            }
        }
        mGridViewAdapter.notifyDataSetChanged();

        if (!lIsTimeSelected) {
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_TIME_SELCTED, false)
                    .commit();
            Log.d(TAG, "setTiming: time is not selected");
        }
    }

    private void setLoction() {
        //set  the location
        String lServiceName;
        mRouteTimingID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null);
        Cursor lTimingCursor = mTimingTable.getTimingDetails(mRouteTimingID);
        if (lTimingCursor.getCount() > 0 && lTimingCursor.moveToFirst()) {

            lServiceName = lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_SERVICE_NAME));
            if (lServiceName.toLowerCase().contains("Drop".toLowerCase())) {
                mToLoactionTextView.setText(mRoutTable.getRouteName(mRouteID));
                mFromLocationTextView.setText(UtililtyClass.COMPANY_LOCATION);
            } else {
                mFromLocationTextView.setText(mRoutTable.getRouteName(mRouteID));
                mToLoactionTextView.setText(UtililtyClass.COMPANY_LOCATION);
            }
        }

    }

    //cab booking or cancelling
    public void bookCabButton(View pView) {

        if (CommonClass.isDataAvailable(BookCabActivity.this)) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this, R.style.MyDialogTheme);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
            //if cab booked cancel
            if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false)) {

                mProgressDialog.setMessage("Cancel Booking");
                mProgressDialog.show();
                Log.d(TAG, "bookCabButton: cancelling the cab");
                String lHttpHeader = "appointmentKey=" + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, null) + "&companyKey=" + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.COMPANY_KEY, null);
                HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.CANCELLING_URL, "POST", lHttpHeader, UtililtyClass.HEADER_URL_ENCODED_CONTENT_TYEPE);
                Intent lGetContactIntent = new Intent(BookCabActivity.this, RoutAndTimingService.class);
                lGetContactIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetContactHttpUrlHelper);
                lGetContactIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.CANCEL_BOOKING_API);
                startService(lGetContactIntent);

            }
            //if cab is not booked cancel
            else {

                Log.d(TAG, "bookCabButton: booking the cab");
                SharedPreferences lSharedPref = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
                boolean lIsLocationSelected = lSharedPref.getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false);
                boolean lIsTimingSelected = lSharedPref.getBoolean(UtililtyClass.IS_TIME_SELCTED, false);

                Log.d(TAG, "bookCabButton:  before" + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_TIME_SELCTED, false));


                if (!lIsLocationSelected) {
                    Toast.makeText(this, "Please select the Location", Toast.LENGTH_SHORT).show();
                } else if (!lIsTimingSelected) {
                    Toast.makeText(this, "Please select the Timing", Toast.LENGTH_SHORT).show();
                } else if (getTime() >= mBookingTimgs[mBookingSlot]) {
                    //notify the user choose the other available timings
                    Toast.makeText(this, "Sorry " + mDisplayTimings[mBookingSlot] + " Cab is Not Available", Toast.LENGTH_SHORT).show();
                    mDoNotSetPreferredTiming = true;
                    setTiming();
                    getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(UtililtyClass.IS_TIME_SELCTED, false)
                            .commit();
                    Log.d(TAG, "bookCabButton: " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_TIME_SELCTED, false));
                } else {
                    mProgressDialog.setMessage("Booking Your Ride");
                    mProgressDialog.show();
                    try {
                        Calendar lCalender = Calendar.getInstance();
                        DateFormat lDateFormate = new SimpleDateFormat("dd MMM yyyy");
                        String lBookingDate = lDateFormate.format(lCalender.getTime());
                        Log.d(TAG, "bookCabButton: booking date " + lBookingDate);

                        //set the cab booking json
                        SharedPreferences lSharedPrefrence = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
                        JSONObject lGetBookingBody = new JSONObject();
                        lGetBookingBody.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, lSharedPrefrence.getString(UtililtyClass.USER_KEY, null));
                        lGetBookingBody.put(UtililtyClass.BOOKIN_DATE, lBookingDate);
                        lGetBookingBody.put(UtililtyClass.COMPANY_KEY, lSharedPrefrence.getString(UtililtyClass.COMPANY_KEY, null));

                        //hitting the Api to check whether he booked the cab or not
                        HttpUrlHelper lGetTodayBookingHelper = getTheHttpUrlHelper(UtililtyClass.GET_BOOKING_TODAY_URL, "POST", lGetBookingBody.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                        Intent lGetBookinIntent = new Intent(BookCabActivity.this, RoutAndTimingService.class);
                        lGetBookinIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetTodayBookingHelper);
                        lGetBookinIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.GET_TODAY_BOOKING_API);
                        Log.d(TAG, "bookCabButton: hit the booking today Api  url" + UtililtyClass.GET_BOOKING_TODAY_URL + "payload " + lGetBookingBody.toString());
                        startService(lGetBookinIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            Toast.makeText(this, "Pleas check the Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }


    //book the cab
    void bookCab(boolean pIsCabBookedToday) {
        //check whether internet is available or not

        SharedPreferences lSharedPref = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);

        //check the cab is booked.. if not book the cab
        if (!pIsCabBookedToday) {

            mRouteTimingID = mTimingID[mBookingSlot];
            if (getTime() <= mTimingTable.getTheBookinTime(mRouteTimingID)) {
                Cursor lTimingCursor = mTimingTable.getTimingDetails(mRouteTimingID);
                if (lTimingCursor.getCount() > 0 && lTimingCursor.moveToFirst()) {

                    JSONObject lAppointmentJson = new JSONObject();
                    JSONObject lCustomerJSon = new JSONObject();
                    JSONObject lCabBookingJson = new JSONObject();

                    try {
                        Log.d(TAG, "bookCabButton: " + mRouteID);
                        //set the appointment details
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAR_TIME_LONG, "" + lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG)));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_STATUS, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_STATUS)));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_START_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_STRIG)));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAFF_KEY, mRouteID);
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_SERVICE_KEY, mRouteTimingID);
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, "no_key"));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, "no_key"));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_END_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_STRIG)));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_F_KEY, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_COMPANY_KEY)));
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_DATE, "Date");
                        lAppointmentJson.put(UtililtyClass.CAB_BOOK_END_TIME_LONG, "" + lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_LONG)));

                        //set the customer details
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_STAR_TIME_LONG, "" + lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG)));
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_STATUS, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_STATUS)));
                        lCustomerJSon.put(UtililtyClass.CUSTOMER_NAME, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_NAME, "no_name"));
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, "no_key"));
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, "no_key"));
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_END_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_END_DATE_STRIG)));
                        lCustomerJSon.put(UtililtyClass.CUSTOMER_PHONE, "phone_number");
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_DATE, "Date");
                        lCustomerJSon.put(UtililtyClass.CUSTOMER_LOGIN_ID, getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_LOGIN_ID, "no_key"));
                        lCustomerJSon.put(UtililtyClass.CAB_BOOK_F_KEY, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_COMPANY_KEY)));

                        //set the cab booking json
                        lCabBookingJson.put(UtililtyClass.CAB_BOOK_APPOINTMENT, lAppointmentJson);
                        lCabBookingJson.put(UtililtyClass.CUSTOMER, lCustomerJSon);

                        HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.BOOKING_URL, "POST", lCabBookingJson.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                        Intent lGetContactIntent = new Intent(BookCabActivity.this, RoutAndTimingService.class);
                        lGetContactIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetContactHttpUrlHelper);
                        lGetContactIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.CAB_BOOKING_API);
                        Log.d(TAG, "bookCabButton: " + lCabBookingJson.toString());
                        startService(lGetContactIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {

            }


        }
        //notify the user...that has already booked the cab today
        else {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Sorry already you have booked the cab", Toast.LENGTH_SHORT).show();
        }
    }

    //choose from location
    public void chooseFromLoction(View pView) {
        Log.d(TAG, "onClick: from location Textview");
        Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
        lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.FROM_LOCATION);
        startActivityForResult(lSearchLocationIntent, SEARCH_FROM_LOCATION_CODE);
    }

    //choose To location
    public void chooseToLocation(View pView) {

        Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
        lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.TO_LOCATION);
        startActivityForResult(lSearchLocationIntent, SEARCH_TO_LOCATION_CODE);
        Log.d(TAG, "onClick: to location Textview");
    }

    //set the location has chosen..so the user can book the cab
    void setTheLocationIsSelected() {
        getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, true)
                .commit();
    }

    //set the location has chosen..so the user can book the cab
    void setTheTimingIsSelected() {
        getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(UtililtyClass.IS_TIME_SELCTED, true)
                .commit();
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
                setTheLocationIsSelected();
                mIsLocationSelectedByUser = true;
            }
        } else if (requestCode == SEARCH_TO_LOCATION_CODE) {

            if (data != null) {
                Log.d(TAG, "onActivityResult: To location " + data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mRouteID = data.getStringExtra(UtililtyClass.ROUTE_ID);
                mToLoactionTextView.setText(data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mFromLocationTextView.setText(UtililtyClass.COMPANY_LOCATION);
                setTheLocationIsSelected();
                mIsLocationSelectedByUser = true;
            }
        } else if (requestCode == USER_PROFILE_PAGE_REQUEST_CODE) {
            Intent lLonginPageIntent = new Intent(BookCabActivity.this, LoginActivity.class);
            startActivity(lLonginPageIntent);
            finish();
        }
        Log.d(TAG, "onActivityResult: ");
    }

    //set the adapter for gridview for available timings
    void setAdapter() {

        mBookingTimeArraList.clear();
        for (int i = 0; i < 8; i++) {
            Log.d(TAG, "setAdapter: timings" + mBookingTimgs[i] + " " + getTime());
            if (mBookingTimgs[i] > getTime()) {
                mBookingStatus = UtililtyClass.CAB_AVAILABLE;
            } else {
                mBookingStatus = UtililtyClass.CAB_IS_NOT_AVAILABLE;
            }
            mBookingTimeArraList.add(new BookingTimeJDO(mTimingID[i], mDisplayTimings[i], mBookingTimgs[i], mBookingStatus));
        }
        mGridViewAdapter.notifyDataSetChanged();
        Log.d(TAG, "setAdapter: ");
    }

    //chang the color and Text of cab booking button
    void changeColorOfCabBookingButton(String pText, int pColor) {
        mBookingButton.setBackgroundColor(ContextCompat.getColor(BookCabActivity.this, pColor));
        mBookingButton.setText(pText);
    }

    //reciver to get the booking response from service
    BroadcastReceiver mCabBookingReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context pContext, Intent pIntent) {

            //clear the booking once book has completed...this will get called once the reminder is on
            if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.CLEAR_BOOKING)) {
                chngeValuesForCaceledBooking();
                Log.d(TAG, "onReceive: clear booking");
            }
            //once the cab is booked set the reminder
            else if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.BOOKING_CAB)) {
                mProgressDialog.cancel();

                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putString(UtililtyClass.USRE_PREFERED_STAFF, mRouteID)
                        .putString(UtililtyClass.USER_PREFERED_SERVICE, mRouteTimingID)
                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, true)
                        .commit();

                int lBookingTime = mTimingTable.getTheBookinTime(mRouteTimingID);

                if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_REMINDER_ON, false)) {

                    //set the reminder to book the cab
                    Intent lNotifyBookIntent = new Intent(BookCabActivity.this, ReminderReciver.class);
                    lNotifyBookIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_BOOK);
                    lNotifyBookIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0));
                    CommonClass.setReminder(BookCabActivity.this, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0), UtililtyClass.NOTIFY_TO_BOOK_CODE, lNotifyBookIntent);

                    //set the reminder to notify the user 5 mins before the cab starts
                    Intent lNotifyToLeave = new Intent(BookCabActivity.this, ReminderReciver.class);
                    lNotifyToLeave.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_GO_TO_CAB);
                    //115 represents 115 minutes which will be added to the cutoff time to remind the user to  leave the office so that they can catch the cab
                    lNotifyToLeave.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 115);
                    CommonClass.setReminder(BookCabActivity.this, lBookingTime + 115, UtililtyClass.NOTIFY_TO_LEAVE_CODE, lNotifyToLeave);

                    //set the reminder to clear the booking info once it's done
                    Intent lClearBookingEveryDay = new Intent(BookCabActivity.this, ReminderReciver.class);
                    lClearBookingEveryDay.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS);
                    lClearBookingEveryDay.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 120);
                    CommonClass.setReminder(BookCabActivity.this, lBookingTime + 120, UtililtyClass.CLEAR_BOOKING_CODE, lClearBookingEveryDay);

                    Log.d(TAG, "onReceive: set the reminder for user cut off time" + lBookingTime + " timing to notify " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0));
                    Log.d(TAG, "onReceive: time id " + mRouteTimingID);

                } else {

                    Log.d(TAG, "onReceive: reminder is off clear the booking once it's done");
                    //set the reminder to clear the booking info once it's done
                    Intent lClearBookingIntent = new Intent(BookCabActivity.this, ReminderReciver.class);
                    lClearBookingIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS);
                    lClearBookingIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 120);
                    Calendar lCalender = Calendar.getInstance();
                    lCalender.set(Calendar.HOUR_OF_DAY, (lBookingTime + 120) / 60);
                    lCalender.set(Calendar.MINUTE, (lBookingTime + 120) % 60);
                    lCalender.set(Calendar.SECOND, 0);
                    lCalender.set(Calendar.MILLISECOND, 0);
                    AlarmManager lAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent lPendingIntent = PendingIntent.getBroadcast(BookCabActivity.this, UtililtyClass.CLEAR_BOOKING_CODE, lClearBookingIntent, PendingIntent.FLAG_ONE_SHOT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, lCalender.getTimeInMillis(), lPendingIntent);
                    }

                }

                changeValueForbookingCab();

                Log.d(TAG, "onReceive: cab booked");
            } else if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.GET_TODAY_BOOKING)) {

                bookCab(pIntent.getBooleanExtra(UtililtyClass.IS_CAB_BOOKED_TODAY, false));
            }
            //will get call when the user presses cancel button
            else {
                mProgressDialog.cancel();
                chngeValuesForCaceledBooking();
                Log.d(TAG, "onReceive: cancled booking");
            }
        }
    };

    //set the button and layout when booking the cab
    void changeValueForbookingCab() {
        changeColorOfCabBookingButton("Cancel Booking", R.color.cancel_booking_color);
        mBookingInformationLayout.setVisibility(View.VISIBLE);
        mLocationCardView.setEnabled(false);
        mFromLocationLayout.setEnabled(false);
        mToLocationLayout.setEnabled(false);
        mLableTextView.setText("Booking Information");
        mBookingServiceTextView.setText("  " + mTimingTable.getServiceName(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null)));
        mSwapLocatoin.setEnabled(false);
        mGridView.setVisibility(View.GONE);
    }

    //chang the button color once the cab is booked
    void chngeValuesForCaceledBooking() {
        getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(UtililtyClass.IS_CAB_BOOKED, false)
                .commit();
        changeColorOfCabBookingButton("Book Cab", R.color.timing_color);
        mBookingInformationLayout.setVisibility(View.GONE);
        mLocationCardView.setEnabled(true);
        mFromLocationLayout.setEnabled(true);
        mToLocationLayout.setEnabled(true);
        mSwapLocatoin.setEnabled(true);
        mLableTextView.setText("Select Timings");
        mGridView.setVisibility(View.VISIBLE);
        setPreferredTimingAndLocaion();
    }

    @Override
    protected void onDestroy() {
        mTimingTable.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCabBookingReciever);
        super.onDestroy();
    }
}
