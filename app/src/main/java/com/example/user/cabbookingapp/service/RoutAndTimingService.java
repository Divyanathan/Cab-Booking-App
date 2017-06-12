package com.example.user.cabbookingapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.httphelper.HttpConnection;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.CustomerJDO;
import com.example.user.cabbookingapp.jdo.GetTodayBookingJDO;
import com.example.user.cabbookingapp.jdo.TimingsJDO;
import com.example.user.cabbookingapp.jdo.UserDetailJDO;
import com.example.user.cabbookingapp.jdo.UserProfilePic;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 02/06/17.
 */

public class RoutAndTimingService extends IntentService {


    HttpUrlHelper mHttpUrlHelper = new HttpUrlHelper();

    ObjectMapper mObjectMapper = new ObjectMapper();
    ArrayList<CustomerJDO> mUserDetailJDoArrayList = new ArrayList<>();
    ArrayList<TimingsJDO> mTimingJDOArrayList = new ArrayList<>();
    CabRouteTable mCabRouteTable = new CabRouteTable(this);
    CabTimingTable mCabTimingTable = new CabTimingTable(this);

    private static final String TAG = "RoutAndTimingService";

    public RoutAndTimingService() {
        super("Cab booking Aapp ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mCabRouteTable.open();
        mCabTimingTable.open();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent pIntent) {

        try {

            HttpConnection mHttpConnection = new HttpConnection();
            mHttpUrlHelper = (HttpUrlHelper) pIntent.getSerializableExtra(UtililtyClass.HTTP_URL_HELPER);

            //hitting the REST Api and get the response
            String mResponse = mHttpConnection.getTheResponse(mHttpUrlHelper);
            String getApi = pIntent.getStringExtra(UtililtyClass.GET_API_INTENT);
            Log.d(TAG, "onHandleIntent: " + getApi);

            if (mResponse != null) {

                //get the route
                if (getApi.equals(UtililtyClass.ROUT_API)) {

                    mUserDetailJDoArrayList = mObjectMapper.readValue(mResponse, new TypeReference<List<CustomerJDO>>() {
                    });
                    mCabRouteTable.addRoutDetails(mUserDetailJDoArrayList);
                    Log.d(TAG, "onHandleIntent: Rout APi " + mUserDetailJDoArrayList.get(1).getName());

                }

                //get the timing
                else if (pIntent.getStringExtra(UtililtyClass.GET_API_INTENT).equals(UtililtyClass.TIMING_API)) {

                    mTimingJDOArrayList = mObjectMapper.readValue(mResponse, new TypeReference<List<TimingsJDO>>() {
                    });
                    getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, true)
                            .commit();
                    mCabTimingTable.addTimings(mTimingJDOArrayList);
                    Log.d(TAG, "onHandleIntent: Timing Api " + mTimingJDOArrayList.get(1).getColorcode());

                }

                //get the Contact
                else if (getApi.equals(UtililtyClass.CONTACT_API)) {

                    Log.d(TAG, "onHandleIntent: contact response " + mResponse);
                    storeContactDetails(mResponse);

                }
                //get the Profile image of the user
                else if (getApi.equals(UtililtyClass.PROFILE_API)) {

                    Log.d(TAG, "onHandleIntent: profile pic response " + mResponse);
                    storeProfilePic(mResponse);

                }
                //book the cab
                else if (getApi.equals(UtililtyClass.CAB_BOOKING_API)) {

                    Log.d(TAG, "onHandleIntent: cab booking api response " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_KEY, "no_key") + " " + mResponse);

                    Intent lBookingIntent = new Intent(UtililtyClass.CAB_BOOKING_RECIVER);
                    lBookingIntent.putExtra(UtililtyClass.CAB_BOOKING_INTENT, UtililtyClass.BOOKING_CAB);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(lBookingIntent);
                }
                //cancel the cab
                else if (getApi.equals(UtililtyClass.CANCEL_BOOKING_API)) {

                    Log.d(TAG, "onHandleIntent: cancel booking api response " + mResponse);
                    Intent lCancelBookingIntent = new Intent(UtililtyClass.CAB_BOOKING_RECIVER);
                    lCancelBookingIntent.putExtra(UtililtyClass.CAB_BOOKING_INTENT, UtililtyClass.CANCEL_BOOKING);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(lCancelBookingIntent);
                }
                //get today's booking details
                else if (getApi.equals(UtililtyClass.GET_TODAY_BOOKING_API)) {

                    ObjectMapper lObjectMapper = new ObjectMapper();
                    GetTodayBookingJDO lGetBookingJDO = lObjectMapper.readValue(mResponse, GetTodayBookingJDO.class);
                    lGetBookingJDO.getBookingData().size();
                    Intent lGetTodayBookingIntent = new Intent(UtililtyClass.CAB_BOOKING_RECIVER);
                    lGetTodayBookingIntent.putExtra(UtililtyClass.CAB_BOOKING_INTENT, UtililtyClass.GET_TODAY_BOOKING);
                    //if the size of data is grater than one means cab booked today
                    if (lGetBookingJDO.getBookingData().size() > 0)
                        lGetTodayBookingIntent.putExtra(UtililtyClass.IS_CAB_BOOKED_TODAY, true);
                    else
                        lGetTodayBookingIntent.putExtra(UtililtyClass.IS_CAB_BOOKED_TODAY, false);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(lGetTodayBookingIntent);
                    Log.d(TAG, "onHandleIntent: size of the data " + lGetBookingJDO.getBookingData().size());

                }
                else if (getApi.equals(UtililtyClass.FEED_BACK_API)){
                    Log.d(TAG, "onHandleIntent: feedback Response "+mResponse);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UtililtyClass.FEED_BACK_RECIVER));
                }
            }
            //when we get the null response this block will get call
            else {
                Toast.makeText(this, "Network Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //store the contact details in shared prefrence
    void storeContactDetails(String pContactDeatils) {

        ObjectMapper lObjectMapper = new ObjectMapper();
        UserDetailJDO lUserJDO = new UserDetailJDO();
        try {
            lUserJDO = lObjectMapper.readValue(pContactDeatils, UserDetailJDO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lUserJDO.getResponse().equalsIgnoreCase("true")) {

            CustomerJDO lCustomerJdo = lUserJDO.getCustomer();
            Log.d(TAG, "storeContactDetails: " + lUserJDO.getCustomer().getKey());

            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(UtililtyClass.USER_NAME, lCustomerJdo.getName())
                    .putString(UtililtyClass.USER_KEY, lCustomerJdo.getKey())
                    .putString(UtililtyClass.USER_STATUS, lCustomerJdo.getStatus())
                    .putString(UtililtyClass.USER_PREFERED_SERVICE, lCustomerJdo.getPreferredService())
                    .putString(UtililtyClass.USRE_PREFERED_STAFF, lCustomerJdo.getPreferedStaff())
                    .putString(UtililtyClass.USER_VEHICAL_STATUS, lCustomerJdo.getVehiclePassStatus())
                    .putString(UtililtyClass.USER_LAST_NAME, lCustomerJdo.getLastName())
                    .putString(UtililtyClass.USER_LOGIN_ID, lCustomerJdo.getLoginId())
                    .putString(UtililtyClass.USER_FIRST_NAME, lCustomerJdo.getFirstName())
                    .putString(UtililtyClass.COMPANY_KEY, lCustomerJdo.getF_Key())
                    .commit();
        } else {
            Log.d(TAG, "storeContactDetails: " + "authentication Failed");
        }

    }

    void storeProfilePic(String pProfilePicResponse) {

        ObjectMapper lObjecMaper = new ObjectMapper();
        try {
            UserProfilePic lUerProfilePic = lObjecMaper.readValue(pProfilePicResponse, UserProfilePic.class);
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(UtililtyClass.USER_LOGIN_ID, lUerProfilePic.getUserEmailId().get(0).get("value"))
                    .putString(UtililtyClass.USER_IMAGE_URL, lUerProfilePic.getUserImage().get("url"))
                    .commit();

            Intent lgoogleLoginIntent = new Intent(UtililtyClass.GOOGLE_LOGIN_RECIVER);
            Log.d(TAG, "storeProfilePic: " + lUerProfilePic.getUserEmailId().get(0).get("value"));
            if (lUerProfilePic.getUserEmailId().get(0).get("value").contains("adaptavantcloud") || lUerProfilePic.getUserEmailId().get(0).get("value").contains("a-cti")) {

                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_VALID_USER, true)
                        .commit();
                lgoogleLoginIntent.putExtra(UtililtyClass.IS_VALID_USER, true);
            }
            Log.d(TAG, "storeProfilePic: url" + lUerProfilePic.getUserImage().get("url"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(lgoogleLoginIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Intent lIntent = new Intent(UtililtyClass.ROUT_RECIVER);
        if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, MODE_PRIVATE).getBoolean(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, false)) {
            lIntent.putExtra(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, true);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);
        Log.d(TAG, "onDestroy: ");
        mCabRouteTable.close();
        mCabTimingTable.close();
        super.onDestroy();
    }


}
