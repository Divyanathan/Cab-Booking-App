package com.example.user.cabbookingapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.httphelper.HttpConnection;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.ContentTypeJDO;
import com.example.user.cabbookingapp.jdo.CustomerJDO;
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
        super("Get Rout AND Timings");
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
            //setting the HttpContention Component using HttpHelper class
//            mHttpUrlHelper.setUrl(pIntent.getStringExtra(UtililtyClass.URL_INTENT));
//            mHttpUrlHelper.setHttpRequetMethod(pIntent.getStringExtra(UtililtyClass.HTTP_REQUEST_METHOD_INTENT));
//            ArrayList<ContentTypeJDO> lContentTypeJDOArrayList = new ArrayList<>();
//            lContentTypeJDOArrayList.add((ContentTypeJDO) pIntent.getSerializableExtra(UtililtyClass.HTTP_REQUEST_PROPERTIES_INTENT));
//            mHttpUrlHelper.setContentType(lContentTypeJDOArrayList);

            HttpConnection mHttpConnection = new HttpConnection();
            mHttpUrlHelper = (HttpUrlHelper) pIntent.getSerializableExtra(UtililtyClass.HTTP_URL_HELPER);

            //hitting the REST Api and get the response
            String mResponse = mHttpConnection.getTheResponse(mHttpUrlHelper);
            String getApi = pIntent.getStringExtra(UtililtyClass.GET_API_INTENT);

            Log.d(TAG, "onHandleIntent: " + getApi);
            //check is this rout api if yes store the value in route table

            if (getApi.equals(UtililtyClass.ROUT_API)) {
                mUserDetailJDoArrayList = mObjectMapper.readValue(mResponse, new TypeReference<List<CustomerJDO>>() {
                });
                mCabRouteTable.addRoutDetails(mUserDetailJDoArrayList);
                Log.d(TAG, "onHandleIntent: Rout APi " + mUserDetailJDoArrayList.get(1).getName());
            }
            //check is this timing api if yes store the value in timing table
            else if (pIntent.getStringExtra(UtililtyClass.GET_API_INTENT).equals(UtililtyClass.TIMING_API)) {
                mTimingJDOArrayList = mObjectMapper.readValue(mResponse, new TypeReference<List<TimingsJDO>>() {
                });
                mCabTimingTable.addTimings(mTimingJDOArrayList);
                Log.d(TAG, "onHandleIntent: Timing Api " + mTimingJDOArrayList.get(1).getColorcode());
            } else if (getApi.equals(UtililtyClass.CONTACT_API)) {
                Log.d(TAG, "onHandleIntent: contact response " + mResponse);
                storeContactDetails(mResponse);
            } else if (getApi.equals(UtililtyClass.PROFILE_API)) {
                Log.d(TAG, "onHandleIntent: profile pic response " + mResponse);
                storeProfilePic(mResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void storeContactDetails(String pContactDeatils) {

        ObjectMapper lObjectMapper = new ObjectMapper();
        UserDetailJDO lUserJDO = new UserDetailJDO();
        try {
            lUserJDO = lObjectMapper.readValue(pContactDeatils, UserDetailJDO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Intent lUserIntent = new Intent(LoginActivity.this, SyncingActivity.class);
//        lUserIntent.putExtra(UtililtyClass.USER_INTENT, lUserJDO.getCustomer());
        if (lUserJDO.getResponse().equalsIgnoreCase("true")) {

            CustomerJDO lCustomerJdo = lUserJDO.getCustomer();
            Log.d(TAG, "navigateToUser: " + lUserJDO.getCustomer().getName());

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
                    .putString(UtililtyClass.USER_JSON, pContactDeatils)
                    .commit();

//            startActivity(lUserIntent);
//            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_bottom);
        } else {
            Log.d(TAG, "navigateToUser: " + "authentication Failed");
        }

    }

    void storeProfilePic(String pProfilePicResponse) {

        ObjectMapper lObjecMaper = new ObjectMapper();
        try {
            UserProfilePic lUerProfilePic = lObjecMaper.readValue(pProfilePicResponse, UserProfilePic.class);
            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(UtililtyClass.USER_IMAGE_URL, lUerProfilePic.getImage().get("url"))
                    .commit();
            Log.d(TAG, "doInBackground: url" + lUerProfilePic.getImage().get("url"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UtililtyClass.GET_ROUT_RECIVER));
        Log.d(TAG, "onDestroy: ");
        mCabRouteTable.close();
        mCabTimingTable.close();
        super.onDestroy();
    }
}
