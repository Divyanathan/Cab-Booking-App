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
import com.example.user.cabbookingapp.ui.LoginActivity;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.cabbookingapp.common.CommonClass.getTheHttpUrlHelper;

/**
 * Created by user on 02/06/17.
 */

public class RestApiService extends IntentService {


    CabRouteTable mCabRouteTable = new CabRouteTable(this);
    CabTimingTable mCabTimingTable = new CabTimingTable(this);
    private static final String TAG = "RestApiService";

    public RestApiService() {
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

            HttpUrlHelper mHttpUrlHelper = new HttpUrlHelper();
            ObjectMapper mObjectMapper = new ObjectMapper();
            ArrayList<CustomerJDO> mUserDetailJDoArrayList = new ArrayList<>();
            ArrayList<TimingsJDO> mTimingJDOArrayList = new ArrayList<>();
            String lAccessToken = pIntent.getStringExtra(UtililtyClass.ACCESS_TOKEN);


            //get the Profile Pic of the User From Google
            HttpUrlHelper lProfilePicHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.PROFILE_PIC_URL + lAccessToken, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
            String lProfilePicResponse = new HttpConnection().getTheResponse(lProfilePicHttpUrlHelper);
            if (lProfilePicResponse != null && !lProfilePicResponse.isEmpty()) {
                storeProfilePic(lProfilePicResponse);
                Log.d(TAG, "onHandleIntent: Profile Pic Api");
            } else
                sendErrorMessage();

            //payload to get the contact details  from REST API
            JSONObject lJsonObject = new JSONObject();
            lJsonObject.put(UtililtyClass.CONTACT_CODE, lAccessToken);
            lJsonObject.put(UtililtyClass.COMPANY_KEY, "084adf34-b48d-4bb7-8795-6a827025a57c");
            lJsonObject.toString();
            //get the contact details From REST API
            HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.CONTACT_URL, "POST", lJsonObject.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
            String lContactResponse = new HttpConnection().getTheResponse(lGetContactHttpUrlHelper);
            if (lContactResponse != null && !lContactResponse.isEmpty()) {
                storeContactDetails(lContactResponse);
                Log.d(TAG, "onHandleIntent: Contact Api");
            } else
                sendErrorMessage();

            //get the Route
            HttpUrlHelper lGetTheRoute = getTheHttpUrlHelper(UtililtyClass.ROUT_URL, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
            String lRoutResonse = new HttpConnection().getTheResponse(lGetTheRoute);
            if (lRoutResonse != null && !lRoutResonse.isEmpty()) {
                mUserDetailJDoArrayList = mObjectMapper.readValue(lRoutResonse, new TypeReference<List<CustomerJDO>>() {});
                mCabRouteTable.addRoutDetails(mUserDetailJDoArrayList);
                Log.d(TAG, "onHandleIntent: Rout APi " + mUserDetailJDoArrayList.get(1).getName());
            } else
                sendErrorMessage();

            //get the Route Timings
            HttpUrlHelper lGetTimgsHelper = getTheHttpUrlHelper(UtililtyClass.TIMING_URL, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
            String lTimingResponse = new HttpConnection().getTheResponse(lGetTimgsHelper);
            if (lTimingResponse != null && !lTimingResponse.isEmpty()) {
                mTimingJDOArrayList = mObjectMapper.readValue(lTimingResponse, new TypeReference<List<TimingsJDO>>() {});
                mCabTimingTable.addTimings(mTimingJDOArrayList);
                Log.d(TAG, "onHandleIntent: Timing Api " + mTimingJDOArrayList.get(1).getColorcode());

                //send the broadcast receiver once all the operation is done
                Intent lIntent = new Intent(UtililtyClass.ROUT_RECIVER);
                lIntent.putExtra(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);

                //mark the user as logged in so that when we open the app for next time it will navigate to booking page
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_USER_LOGED_IN,true)
                        .commit();

            } else
                sendErrorMessage();

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
            String lEmail = lUerProfilePic.getUserEmailId().get(0).get("value");
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(UtililtyClass.USER_LOGIN_ID, lEmail)
                    .putString(UtililtyClass.USER_IMAGE_URL, lUerProfilePic.getUserImage().get("url"))
                    .commit();

            Log.d(TAG, "storeProfilePic: " + lEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send the error message..if something goes wrong whiling hitting api
    void sendErrorMessage() {
        Intent lIntent = new Intent(UtililtyClass.ROUT_RECIVER);
        lIntent.putExtra(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mCabRouteTable.close();
        mCabTimingTable.close();
        super.onDestroy();
    }


}
