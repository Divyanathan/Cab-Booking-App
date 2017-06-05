package com.example.user.cabbookingapp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.jdo.ContentTypeJDO;
import com.example.user.cabbookingapp.service.RoutAndTimingService;
import com.example.user.cabbookingapp.util.UtililtyClass;

public class SyncingActivity extends AppCompatActivity {

        ProgressDialog mProgressDialog;
    private static final String TAG = "SyncingActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncking);

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("syncing");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        ContentTypeJDO lConactTypeJDO = new ContentTypeJDO();
        /**
         * start the service to get the route
         */
        Intent getRoutIntent = new Intent(SyncingActivity.this, RoutAndTimingService.class);
        getRoutIntent.putExtra(UtililtyClass.URL_INTENT, UtililtyClass.ROUT_URL);
        getRoutIntent.putExtra(UtililtyClass.HTTP_REQUEST_METHOD_INTENT, "GET");
        getRoutIntent.putExtra(UtililtyClass.GET_API_INTENT,UtililtyClass.ROUT_API);
        lConactTypeJDO.setKey("Content-Type");
        lConactTypeJDO.setValue("application/json");
        getRoutIntent.putExtra(UtililtyClass.HTTP_REQUEST_PROPERTIES_INTENT, lConactTypeJDO);
        startService(getRoutIntent);

        /**
         * star the service to get the timing route
         */
        Intent getTimingsIntent = new Intent(SyncingActivity.this, RoutAndTimingService.class);
        getTimingsIntent.putExtra(UtililtyClass.URL_INTENT, UtililtyClass.TIMING_URL);
        getTimingsIntent.putExtra(UtililtyClass.HTTP_REQUEST_METHOD_INTENT, "GET");
        getTimingsIntent.putExtra(UtililtyClass.GET_API_INTENT,UtililtyClass.TIMING_API);
        lConactTypeJDO.setKey("Content-Type");
        lConactTypeJDO.setValue("application/json");
        getTimingsIntent.putExtra(UtililtyClass.HTTP_REQUEST_PROPERTIES_INTENT, lConactTypeJDO);
        startService(getTimingsIntent);

        //register the receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReciverForRoutAndTime, new IntentFilter(UtililtyClass.GET_ROUT_RECIVER));

        Log.d(TAG, "onCreate: ");
    }


    BroadcastReceiver mReciverForRoutAndTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: ");
            closeTheACtivity();
            mProgressDialog.dismiss();
        }
    };

    private void closeTheACtivity() {
        Log.d(TAG, "closeTheACtivity: ");
        Intent lStarHomePageActivy = new Intent(SyncingActivity.this, UserActivity.class);
        startActivity(lStarHomePageActivy);
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReciverForRoutAndTime);
        super.onDestroy();
    }
}
