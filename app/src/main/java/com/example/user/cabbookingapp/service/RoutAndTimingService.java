package com.example.user.cabbookingapp.service;

import android.app.IntentService;
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
    HttpConnection mHttpConnection = new HttpConnection();
    ObjectMapper mObjectMapper = new ObjectMapper();
    ArrayList<CustomerJDO> mUserDetailJDoArrayList = new ArrayList<>();
    ArrayList<TimingsJDO> mTimingJDOArrayList=new ArrayList<>();
    String mResponse;
    CabRouteTable mCabRouteTable = new CabRouteTable(this);
    CabTimingTable mCabTimingTable=new CabTimingTable(this);

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
            mHttpUrlHelper.setUrl(pIntent.getStringExtra(UtililtyClass.URL_INTENT));
            mHttpUrlHelper.setHttpRequetMethod(pIntent.getStringExtra(UtililtyClass.HTTP_REQUEST_METHOD_INTENT));
            ArrayList<ContentTypeJDO> lContentTypeJDOArrayList = new ArrayList<>();
            lContentTypeJDOArrayList.add((ContentTypeJDO) pIntent.getSerializableExtra(UtililtyClass.HTTP_REQUEST_PROPERTIES_INTENT));
            mHttpUrlHelper.setContentType(lContentTypeJDOArrayList);

            //hitting the REST Api and get the response
            mResponse = mHttpConnection.getTheResponse(mHttpUrlHelper);
            String getApi=pIntent.getStringExtra(UtililtyClass.GET_API_INTENT);
            Log.d(TAG, "onHandleIntent: "+getApi);
            //check is this rout api if yes store the value in route table
            if (getApi.equals(UtililtyClass.ROUT_API)) {
                mUserDetailJDoArrayList = mObjectMapper.readValue(mResponse, new TypeReference<List<CustomerJDO>>() {
                });
                mCabRouteTable.addRoutDetails(mUserDetailJDoArrayList);
                Log.d(TAG, "onHandleIntent: Rout APi " + mUserDetailJDoArrayList.get(1).getName());
            }
            //check is this timing api if yes store the value in timing table
            if (pIntent.getStringExtra(UtililtyClass.GET_API_INTENT).equals(UtililtyClass.TIMING_API)) {
                mTimingJDOArrayList=mObjectMapper.readValue(mResponse, new TypeReference<List<TimingsJDO>>() {});
                mCabTimingTable.addTimings(mTimingJDOArrayList);
                Log.d(TAG, "onHandleIntent: Timing Api " + mTimingJDOArrayList.get(1).getColorcode());
            }
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
