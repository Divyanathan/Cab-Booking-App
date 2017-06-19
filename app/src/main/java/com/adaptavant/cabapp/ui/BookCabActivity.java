package com.adaptavant.cabapp.ui;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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

import com.adaptavant.cabapp.GridViewAdapter;
import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.common.CommonClass;
import com.adaptavant.cabapp.custom.RoundedTransformation;
import com.adaptavant.cabapp.datbase.CabRouteTable;
import com.adaptavant.cabapp.datbase.CabTimingTable;
import com.adaptavant.cabapp.httphelper.HttpConnection;
import com.adaptavant.cabapp.httphelper.HttpUrlHelper;
import com.adaptavant.cabapp.jdo.BookingTimeJDO;
import com.adaptavant.cabapp.jdo.GetTodayBookingJDO;
import com.adaptavant.cabapp.receiver.ReminderReciver;
import com.adaptavant.cabapp.util.UtililtyClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.adaptavant.cabapp.R.id.profile_image;
import static com.adaptavant.cabapp.common.CommonClass.getTheHttpUrlHelper;

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
    int mBookingSlot;
    ProgressDialog mProgressDialog;
    String mRouteID = null;
    String mRouteTimingID;
    String mBookingStatus;
    CabRouteTable mRoutTable;
    CabTimingTable mTimingTable;

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

//        FirebaseCrash.report(new Exception("Test Exception "));


        //set the listener to select the timings
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!mBookingTimeArraList.get(position).getStatus().equals(UtililtyClass.CAB_IS_NOT_AVAILABLE)) {

                    mBookingSlot = position;
                    for (int i = 0; i < mBookingTimeArraList.size(); i++) {
                        if (i == position) {
                            mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_BOOKED);
                            mRouteTimingID = mBookingTimeArraList.get(i).getTimingID();
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

                setCabBookButton();
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
        mProfileImage = (ImageView) findViewById(profile_image);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lProfilePageIntent = new Intent(BookCabActivity.this, UserProfileActivity.class);
                startActivityForResult(lProfilePageIntent, USER_PROFILE_PAGE_REQUEST_CODE);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    ActivityOptionsCompat lActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(BookCabActivity.this, (View) findViewById(R.id.profile_image), "profile");
//                    startActivityForResult(lProfilePageIntent, USER_PROFILE_PAGE_REQUEST_CODE, lActivityOptionsCompat.toBundle());
//                    Log.d(TAG, "onClick: navigating to the user profile activity using shared element");
//                } else {
//                    startActivityForResult(lProfilePageIntent, USER_PROFILE_PAGE_REQUEST_CODE);
//                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//                    Log.d(TAG, "onClick: navigating to user profile ");
//                }
            }
        });


        //set the use profile pic
        SharedPreferences lPreference = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        try {
            if(lPreference.getString(UtililtyClass.USER_IMAGE_URL, null) == null){
                Picasso.with(this).load(R.drawable.user_image)
                        .resize(200, 200)
                        .transform(new RoundedTransformation(100, 1))
                        .into((ImageView) findViewById(R.id.profile_image));
            }else {
                Picasso.with(this)
                        .load(lPreference.getString(UtililtyClass.USER_IMAGE_URL, null))
                        .placeholder(R.drawable.user_image)
                        .resize(200, 200)
                        .transform(new RoundedTransformation(100, 1))
                        .into((ImageView) findViewById(R.id.profile_image));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //cancel the notification
        NotificationManager lNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.cancel(UtililtyClass.NOTIFICATION_ID);

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

        String lPreferredRoute = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USRE_PREFERED_STAFF, null);
        Log.d(TAG, "onResume: preferred route " + lPreferredRoute);

        if (lPreferredRoute != null && !lPreferredRoute.isEmpty()) {

            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, true)
                    .commit();

            Log.d(TAG, "onResume: is location selected " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false));
            if (CommonClass.isDataAvailable(BookCabActivity.this)) {
                new GetTodaysBooking().execute();
                Log.d(TAG, "onResume: get booking today");
            } else {

                //get the Timing Details From Db

                if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null) != null) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            getTimingDetailsFromDB();
                            setGridViewAdapter();
                            if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false)) {
                                changeValueForbookingCab();
                            }
                            if (!mIsLocationSelectedByUser) {
                                setLoction();
                            } else {
                                mIsLocationSelectedByUser = false;
                            }
                        }
                    });
                    Log.d(TAG, "onResume: setting preferred route");
                }
                setCabBookButton();
            }
            Log.d(TAG, "onResume: preferred rout is available");

        } else {
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, false)
                    .putBoolean(UtililtyClass.IS_TIME_SELCTED, false)
                    .commit();

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "resume : setting the time and location");
                    getTimingDetailsFromDB();
                    setGridViewAdapter();
                    setCabBookButton();
                }
            });
            Log.d(TAG, "onResume: showing the time ");
        }
    }


    //get today's booking
    class GetTodaysBooking extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog lProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProgressDialog = new ProgressDialog(BookCabActivity.this, R.style.MyDialogTheme);
            lProgressDialog.setMessage("Loading Please Wait");
            lProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            lProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                Log.d(TAG, "GetTodaysBooking get Today's Booking doInBackground: ");
                Calendar lCalender = Calendar.getInstance();
                DateFormat lDateFormate = new SimpleDateFormat("dd MMM yyyy");
                String lBookingDate = lDateFormate.format(lCalender.getTime());
                Log.d(TAG, "GetTodaysBooking booking date " + lBookingDate);

                //get timing details from DB
                getTimingDetailsFromDB();

                //set the cab booking json
                SharedPreferences lSharedPrefrence = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
                JSONObject lGetBookingBody = new JSONObject();
                lGetBookingBody.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, lSharedPrefrence.getString(UtililtyClass.USER_KEY, null));
                lGetBookingBody.put(UtililtyClass.BOOKIN_DATE, lBookingDate);
                lGetBookingBody.put(UtililtyClass.COMPANY_KEY, lSharedPrefrence.getString(UtililtyClass.COMPANY_KEY, null));

                //hitting the Api to check whether he booked the cab or not
                HttpUrlHelper lGetTodayBookingHelper = getTheHttpUrlHelper(UtililtyClass.GET_BOOKING_TODAY_URL, "POST", lGetBookingBody.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                String lResponse = new HttpConnection().getHttpResponse(lGetTodayBookingHelper);

                //store the response in JDO
                ObjectMapper lObjectMapper = new ObjectMapper();
                GetTodayBookingJDO lGetBookingJDO = lObjectMapper.readValue(lResponse, GetTodayBookingJDO.class);
                if (lGetBookingJDO.getBookingData().size() > 0) {
                    getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(UtililtyClass.IS_CAB_BOOKED, true)
                            .putString(UtililtyClass.USRE_PREFERED_STAFF, lGetBookingJDO.getBookingData().get(0).get("staffKey"))
                            .putString(UtililtyClass.USER_PREFERED_SERVICE, lGetBookingJDO.getBookingData().get(0).get("serviceKey"))
                            .commit();
                    Log.d(TAG, "get Today's Booking : " + "cab is booked time id " + lGetBookingJDO.getBookingData().get(0).get("serviceKey") + " route id " + lGetBookingJDO.getBookingData().get(0).get("staffKey"));
                    return true;
                } else {

                    Log.d(TAG, "get Today's Booking: " + "cab is not booked");
                    return false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean pIsCabBooked) {
            super.onPostExecute(pIsCabBooked);
            lProgressDialog.dismiss();
            setGridViewAdapter();
            if (pIsCabBooked) {
                if (!mIsLocationSelectedByUser)
                    setLoction();
                else
                    mIsLocationSelectedByUser = false;
                changeValueForbookingCab();
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, true)
                        .commit();
                Log.d(TAG, "onPostExecute: getting today's booking.. cab is booked");

            } else {
                if (!mIsLocationSelectedByUser)
                    setLoction();
                else
                    mIsLocationSelectedByUser = false;
                changeValuesForCaceledBooking();
                setGridViewAdapter();
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, false)
                        .commit();
                Log.d(TAG, "onPostExecute: getting today's booking.. cab is not booked");
            }
            setCabBookButton();
        }
    }

    //get the timing Details From the DataBase
    private void getTimingDetailsFromDB() {
        if (mBookingTimeArraList.size() == 0) {
            String lTimingID, lDisplayTime;
            int mBookingTiem;
            Cursor lCursor = mTimingTable.getCabTimingDetails();
            if (lCursor.getCount() > 0 && lCursor.moveToFirst()) {
                do {
                    lTimingID = lCursor.getString(lCursor.getColumnIndex(CabTimingTable.COLUMN_ID));
                    lDisplayTime = lCursor.getString(lCursor.getColumnIndex(CabTimingTable.COLUMN_SERVICE_NAME)).substring(0, 8);
                    mBookingTiem = lCursor.getInt(lCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG));
                    if (!lDisplayTime.equals("06:30 PM"))
                        mBookingTimeArraList.add(new BookingTimeJDO(lTimingID, lDisplayTime, mBookingTiem));
                } while (lCursor.moveToNext());
            }
        }
        Log.d(TAG, "getTimingDetailsFromDB: ");
    }


    class BookCabAsyncTask extends AsyncTask<Void, Void, String> {

        ProgressDialog lProgressDialog;
        String lRouteID;
        String lRouteTimingID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProgressDialog = new ProgressDialog(BookCabActivity.this, R.style.MyDialogTheme);
            lProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            lProgressDialog.setMessage("Booking Your Ride");
            lProgressDialog.setCancelable(false);
            lProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            lRouteTimingID = mBookingTimeArraList.get(mBookingSlot).getTimingID();
            Cursor lTimingCursor = mTimingTable.getTimingDetails(lRouteTimingID);
            if (lTimingCursor.getCount() > 0 && lTimingCursor.moveToFirst()) {

                JSONObject lAppointmentJson = new JSONObject();
                JSONObject lCustomerJSon = new JSONObject();
                JSONObject lCabBookingJson = new JSONObject();

                lRouteID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USRE_PREFERED_STAFF, null);
                try {
                    Log.d(TAG, "bookCabButton: " + lRouteID);
                    //set the appointment details
                    lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAR_TIME_LONG, "" + lTimingCursor.getInt(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_LONG)));
                    lAppointmentJson.put(UtililtyClass.CAB_BOOK_STATUS, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_STATUS)));
                    lAppointmentJson.put(UtililtyClass.CAB_BOOK_START_TIME_STRING, lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_START_DATE_STRIG)));
                    lAppointmentJson.put(UtililtyClass.CAB_BOOK_STAFF_KEY, lRouteID);
                    lAppointmentJson.put(UtililtyClass.CAB_BOOK_SERVICE_KEY, lRouteTimingID);
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

                    String lResponse = new HttpConnection().getHttpResponse(lGetContactHttpUrlHelper);
                    Log.d(TAG, "doInBackground: cab booking response " + lResponse);
                    return lResponse;


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "doInBackground: there is no data in database");
            }


            return null;
        }

        @Override
        protected void onPostExecute(String pResponse) {
            super.onPostExecute(pResponse);
            lProgressDialog.dismiss();
            if (pResponse != null && !pResponse.isEmpty()) {
                Toast.makeText(BookCabActivity.this, "Booking Confirmed", Toast.LENGTH_SHORT).show();
                int lBookingTime = mTimingTable.getTheBookingTime(lRouteTimingID);
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putString(UtililtyClass.USER_PREFERED_SERVICE, mRouteTimingID)
                        .putInt(UtililtyClass.USER_REMINDER_TIME, lBookingTime)
                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, true)
                        .commit();

                changeValueForbookingCab();
                Log.d(TAG, "onPostExecute: cut off time " + lBookingTime);
                if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_REMINDER_ON, false)) {

                    //set the reminder to book the cab
                    Intent lNotifyBookIntent = new Intent(BookCabActivity.this, ReminderReciver.class);
                    lNotifyBookIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_BOOK);
                    lNotifyBookIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.NOTYFYING_TIME, 0));
                    CommonClass.setReminder(BookCabActivity.this, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.NOTYFYING_TIME, 0), lNotifyBookIntent);

                    Log.d(TAG, "onReceive: set the reminder for user cut off time" + lBookingTime + " timing to notify " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0));
                    Log.d(TAG, "onReceive: time id " + lRouteTimingID);

                }
                Log.d(TAG, "onPostExecute: response true route id " + lRouteID + " timing id " + mRouteTimingID);
            } else {
                Toast.makeText(BookCabActivity.this, "Network Failure", Toast.LENGTH_SHORT).show();
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, false)
                        .commit();
                Log.d(TAG, "onPostExecute: response null");
            }
        }
    }

    class CancelBookingAsyncTask extends AsyncTask<Void, Void, String> {

        ProgressDialog lProgressDialoge;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProgressDialoge = new ProgressDialog(BookCabActivity.this, R.style.MyDialogTheme);
            lProgressDialoge.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            lProgressDialoge.setMessage("Cancel booking");
            lProgressDialoge.setCancelable(false);
            lProgressDialoge.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String lHttpHeader = "appointmentKey=" + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, null) + "&companyKey=" + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.COMPANY_KEY, null);
            HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.CANCELLING_URL, "POST", lHttpHeader, UtililtyClass.HEADER_URL_ENCODED_CONTENT_TYEPE);
            String lResponse = new HttpConnection().getHttpResponse(lGetContactHttpUrlHelper);
            return lResponse;
        }

        @Override
        protected void onPostExecute(String pRespose) {
            super.onPostExecute(pRespose);
            lProgressDialoge.dismiss();
            if (pRespose != null) {
                changeValuesForCaceledBooking();
                Toast.makeText(BookCabActivity.this, "Cancelled Booking", Toast.LENGTH_SHORT).show();


            }
        }
    }


    //set the gridView Adapter
    void setGridViewAdapter() {
        //set the adapter for recycler view
        Log.d(TAG, "setGridViewAdapter:");
        boolean lIsTimeSelected = false;

        for (int i = 0; i < mBookingTimeArraList.size(); i++) {
            BookingTimeJDO lBookingTimeJDO = mBookingTimeArraList.get(i);
            String lPreferredTiminingID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, "no_preferred_service");
            if (lPreferredTiminingID.equals(lBookingTimeJDO.getTimingID()) && lBookingTimeJDO.getBookingTime() >= getTime()) {

                mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_BOOKED);
                mRouteTimingID = mBookingTimeArraList.get(i).getTimingID();
                lIsTimeSelected = true;
                mBookingSlot = i;
                setTheTimingIsSelected();
                Log.d(TAG, "setGridViewAdapter: set the preferred timings timing it" + mBookingTimeArraList.get(i).getTimingID());

            } else if (lBookingTimeJDO.getBookingTime() >= getTime()) {
                mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_AVAILABLE);
            } else {
                mBookingTimeArraList.get(i).setStatus(UtililtyClass.CAB_IS_NOT_AVAILABLE);
            }
        }

        //set the Adapter
        if (mGridViewAdapter == null) {
            mGridViewAdapter = new GridViewAdapter(this, mBookingTimeArraList);
            mGridView.setAdapter(mGridViewAdapter);
        } else {
            mGridViewAdapter.notifyDataSetChanged();
        }

        //set whether the time is selected or not
        if (lIsTimeSelected) {
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_TIME_SELCTED, true)
                    .commit();
        } else {
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_TIME_SELCTED, false)
                    .commit();
        }
    }

    private void setLoction() {

        String lPreferredRoute = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USRE_PREFERED_STAFF, null);
        if (lPreferredRoute != null && !lPreferredRoute.isEmpty()) {

            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, true)
                    .commit();
            Log.d(TAG, "setLoction: is location selected " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false));

            //set  the location
            String lServiceName;
            String lRouteTimingID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null);
            Log.d(TAG, "setLoction: " + lRouteTimingID);
            Cursor lTimingCursor = mTimingTable.getTimingDetails(lRouteTimingID);
            if (lTimingCursor.getCount() > 0 && lTimingCursor.moveToFirst()) {

                String lRouteID = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USRE_PREFERED_STAFF, null);
                lServiceName = lTimingCursor.getString(lTimingCursor.getColumnIndex(CabTimingTable.COLUMN_SERVICE_NAME));
                if (lServiceName.toLowerCase().contains("Drop".toLowerCase())) {
                    mToLoactionTextView.setText(mRoutTable.getRouteName(lRouteID));
                    mFromLocationTextView.setText(UtililtyClass.COMPANY_LOCATION);
                } else {
                    mFromLocationTextView.setText(mRoutTable.getRouteName(lRouteID));
                    mToLoactionTextView.setText(UtililtyClass.COMPANY_LOCATION);
                }
            }
        } else {
            mFromLocationTextView.setText("Choose Location");
            mToLoactionTextView.setText("Choose Location");
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(UtililtyClass.IS_LOCATION_SELECTED, false)
                    .commit();
        }

    }

    //cab booking or cancelling
    public void bookCabButton(View pView) {

        if (CommonClass.isDataAvailable(BookCabActivity.this)) {
            Log.d(TAG, "bookCabButton: " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false));
            Log.d(TAG, "bookCabButton: is Location selected " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false));
            if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false)) {



                new AlertDialog.Builder(BookCabActivity.this, R.style.Sign_out_theme)
                        .setTitle("Are u want Cancel Booking?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CancelBookingAsyncTask().execute();
                          }
                        })
                        .create()
                        .show();
                Log.d(TAG, "bookCabButton: cancelling the cab");
            } else {
                if (!getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false)) {

                    Toast.makeText(this, "Please Select the location", Toast.LENGTH_SHORT).show();
                } else if (!getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_TIME_SELCTED, false)) {
                    Toast.makeText(this, "Please Select the Timing ", Toast.LENGTH_SHORT).show();
                } else if (mBookingTimeArraList.get(mBookingSlot).getBookingTime() >= getTime()) {
                    new BookCabAsyncTask().execute();
                } else {
                    setGridViewAdapter();
                    Log.d(TAG, "bookCabButton: selected time " + mBookingTimeArraList.get(mBookingSlot).getBookingTime() + " current time " + getTime());
                    Toast.makeText(this, "Sorry " + mBookingTimeArraList.get(mBookingSlot).getDisplayTime() + " is not available booking time ", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "bookCabButton: booking the cab");
            }
        } else {
            Toast.makeText(this, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
        }


    }


    //choose from location
    public void chooseFromLoction(View pView) {
        Log.d(TAG, "onClick: from location TextView");
        Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
        lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.FROM_LOCATION);
        startActivityForResult(lSearchLocationIntent, SEARCH_FROM_LOCATION_CODE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    //choose To location
    public void chooseToLocation(View pView) {

        Intent lSearchLocationIntent = new Intent(BookCabActivity.this, SearchLocationActivity.class);
        lSearchLocationIntent.putExtra(UtililtyClass.CHOOSE_LOCATION, UtililtyClass.TO_LOCATION);
        startActivityForResult(lSearchLocationIntent, SEARCH_TO_LOCATION_CODE);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putString(UtililtyClass.USRE_PREFERED_STAFF, mRouteID)
                        .commit();
                mFromLocationTextView.setText(data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mToLoactionTextView.setText(UtililtyClass.COMPANY_LOCATION);
                setTheLocationIsSelected();
                mIsLocationSelectedByUser = true;
                setCabBookButton();
            }
        } else if (requestCode == SEARCH_TO_LOCATION_CODE) {

            if (data != null) {
                Log.d(TAG, "onActivityResult: To location " + data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mRouteID = data.getStringExtra(UtililtyClass.ROUTE_ID);
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putString(UtililtyClass.USRE_PREFERED_STAFF, mRouteID)
                        .commit();
                mToLoactionTextView.setText(data.getStringExtra(UtililtyClass.ROUTE_NAME));
                mFromLocationTextView.setText(UtililtyClass.COMPANY_LOCATION);
                setTheLocationIsSelected();
                mIsLocationSelectedByUser = true;
                setCabBookButton();
            }
        }
//        } else if (requestCode == USER_PROFILE_PAGE_REQUEST_CODE) {
//            Intent lLonginPageIntent = new Intent(BookCabActivity.this, LoginActivity.class);
//            startActivity(lLonginPageIntent);
//            finish();
//        }
        Log.d(TAG, "onActivityResult: ");
    }


    //chang the color and Text of cab booking button
    void changeColorOfCabBookingButton(String pText, int pColorDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBookingButton.setBackground(ContextCompat.getDrawable(BookCabActivity.this, pColorDrawable));
        }
        mBookingButton.setText(pText);
    }

    //reciver to get the booking response from service
    BroadcastReceiver mCabBookingReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context pContext, Intent pIntent) {

//            //clear the booking once book has completed...this will get called once the reminder is on
//            if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.CLEAR_BOOKING)) {
//                changeValuesForCaceledBooking();
//                Log.d(TAG, "onReceive: clear booking");
//            }
//            //once the cab is booked set the reminder
//            else if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.BOOKING_CAB)) {
//                mProgressDialog.cancel();
//
//                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
//                        .edit()
//                        .putString(UtililtyClass.USRE_PREFERED_STAFF, mRouteID)
//                        .putString(UtililtyClass.USER_PREFERED_SERVICE, mRouteTimingID)
//                        .putBoolean(UtililtyClass.IS_CAB_BOOKED, true)
//                        .commit();
//
//                int lBookingTime = mTimingTable.getTheBookingTime(mRouteTimingID);
//
//                if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_REMINDER_ON, false)) {
//
//                    //set the reminder to book the cab
//                    Intent lNotifyBookIntent = new Intent(BookCabActivity.this, ReminderReciver.class);
//                    lNotifyBookIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_BOOK);
//                    lNotifyBookIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0));
//                    CommonClass.setReminder(BookCabActivity.this, lBookingTime - getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0), UtililtyClass.NOTIFY_TO_BOOK_CODE, lNotifyBookIntent);
//
//                    //set the reminder to notify the user 5 mins before the cab starts
//                    Intent lNotifyToLeave = new Intent(BookCabActivity.this, ReminderReciver.class);
//                    lNotifyToLeave.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_GO_TO_CAB);
//                    //115 represents 115 minutes which will be added to the cutoff time to remind the user to  leave the office so that they can catch the cab
//                    lNotifyToLeave.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 115);
//                    CommonClass.setReminder(BookCabActivity.this, lBookingTime + 115, UtililtyClass.NOTIFY_TO_LEAVE_CODE, lNotifyToLeave);
//
//                    //set the reminder to clear the booking info once it's done
//                    Intent lClearBookingEveryDay = new Intent(BookCabActivity.this, ReminderReciver.class);
//                    lClearBookingEveryDay.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS);
//                    lClearBookingEveryDay.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 120);
//                    CommonClass.setReminder(BookCabActivity.this, lBookingTime + 120, UtililtyClass.CLEAR_BOOKING_CODE, lClearBookingEveryDay);
//
//                    Log.d(TAG, "onReceive: set the reminder for user cut off time" + lBookingTime + " timing to notify " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME, 0));
//                    Log.d(TAG, "onReceive: time id " + mRouteTimingID);
//
//                } else {
//
//                    Log.d(TAG, "onReceive: reminder is off clear the booking once it's done");
//                    //set the reminder to clear the booking info once it's done
//                    Intent lClearBookingIntent = new Intent(BookCabActivity.this, ReminderReciver.class);
//                    lClearBookingIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS);
//                    lClearBookingIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime + 120);
//                    Calendar lCalender = Calendar.getInstance();
//                    lCalender.set(Calendar.HOUR_OF_DAY, (lBookingTime + 120) / 60);
//                    lCalender.set(Calendar.MINUTE, (lBookingTime + 120) % 60);
//                    lCalender.set(Calendar.SECOND, 0);
//                    lCalender.set(Calendar.MILLISECOND, 0);
//                    AlarmManager lAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    PendingIntent lPendingIntent = PendingIntent.getBroadcast(BookCabActivity.this, UtililtyClass.CLEAR_BOOKING_CODE, lClearBookingIntent, PendingIntent.FLAG_ONE_SHOT);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, lCalender.getTimeInMillis(), lPendingIntent);
//                    }
//
//                }
//
//                changeValueForbookingCab();
//
//                Log.d(TAG, "onReceive: cab booked");
//            } else if (pIntent.getStringExtra(UtililtyClass.CAB_BOOKING_INTENT).equals(UtililtyClass.GET_TODAY_BOOKING)) {
//
////                bookCab(pIntent.getBooleanExtra(UtililtyClass.IS_CAB_BOOKED_TODAY, false));
//            }
//            //will get call when the user presses cancel button
//            else {
//                mProgressDialog.cancel();
//                changeValuesForCaceledBooking();
//                Log.d(TAG, "onReceive: cancled booking");
//            }
        }
    };

    //set the button and layout when booking the cab
    void changeValueForbookingCab() {
        changeColorOfCabBookingButton("Cancel Booking", R.drawable.cancel_booking);
        mBookingInformationLayout.setVisibility(View.VISIBLE);
        mLocationCardView.setEnabled(false);
        mFromLocationLayout.setEnabled(false);
        mToLocationLayout.setEnabled(false);
        mSwapLocatoin.setVisibility(View.GONE);
        mLableTextView.setText("Booking Information");
        mBookingServiceTextView.setText("  " + mTimingTable.getServiceName(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, "no_service")));
        mSwapLocatoin.setEnabled(false);
        mGridView.setVisibility(View.GONE);
        Log.d(TAG, "changeValueForbookingCab: ");
    }

    //chang the button color once the cab is booked
    void changeValuesForCaceledBooking() {
        getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(UtililtyClass.IS_CAB_BOOKED, false)
                .commit();
        changeColorOfCabBookingButton("Book Cab", R.drawable.sected_timing);
        mBookingInformationLayout.setVisibility(View.GONE);
        mLocationCardView.setEnabled(true);
        mFromLocationLayout.setEnabled(true);
        mSwapLocatoin.setVisibility(View.VISIBLE);
        mToLocationLayout.setEnabled(true);
        mSwapLocatoin.setEnabled(true);
        mLableTextView.setText("Select Timings");
        mGridView.setVisibility(View.VISIBLE);
        Log.d(TAG, "changeValuesForCaceledBooking: ");
    }

    @Override
    protected void onDestroy() {
        mTimingTable.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCabBookingReciever);
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    void setCabBookButton() {
        SharedPreferences lSharedPrefrence = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        boolean lIsCabBooked = lSharedPrefrence.getBoolean(UtililtyClass.IS_CAB_BOOKED, false);
        if (!lIsCabBooked) {
            boolean lIsLocaionSelected = lSharedPrefrence.getBoolean(UtililtyClass.IS_LOCATION_SELECTED, false);
            boolean lISTimeSelected = lSharedPrefrence.getBoolean(UtililtyClass.IS_TIME_SELCTED, false);
            if (lIsLocaionSelected && lISTimeSelected) {
                changeColorOfCabBookingButton("Book Cab", R.drawable.sected_timing);
            } else {
                changeColorOfCabBookingButton("Book Cab", R.drawable.disable_booking_button);
            }
        }
    }


}
