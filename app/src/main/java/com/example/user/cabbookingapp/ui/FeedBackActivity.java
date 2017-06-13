package com.example.user.cabbookingapp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.common.CommonClass;
import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.service.RoutAndTimingService;
import com.example.user.cabbookingapp.util.UtililtyClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.user.cabbookingapp.common.CommonClass.getTheHttpUrlHelper;

public class FeedBackActivity extends AppCompatActivity {

    private static final String TAG = "FeedBackActivity";
    ProgressDialog mProgressDialog;
    TextView mFeedbackEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        mFeedbackEditText=(EditText)findViewById(R.id.feedbadkEditText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, "onCreate: ");

        //registering the BroadCastReceiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mFeedBackResponseReciver,new IntentFilter(UtililtyClass.FEED_BACK_RECIVER));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.submit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.MyDialogTheme);
            mProgressDialog.setMessage("Sending FeedBack");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        if (item.getItemId() == R.id.submit_feedback) {

            CabTimingTable lCabTingTable=new CabTimingTable(this);
            CabRouteTable lCabRoutTable=new CabRouteTable(this);
            lCabTingTable.open();
            lCabRoutTable.open();
            if (CommonClass.isDataAvailable(this)) {
                if (!mFeedbackEditText.getText().toString().isEmpty()) {

                    InputMethodManager linputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    linputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                    mProgressDialog.show();
                    SharedPreferences lSharedPrefrence=getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE);
//
                    String lVersion= null;
                    try {
                        lVersion = getPackageManager().getPackageInfo(this.getPackageName(),0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    StringBuilder lFeedbackSB=new StringBuilder();
                    lFeedbackSB.append(mFeedbackEditText.getText().toString().trim()
                            + "<br><br>");
                    lFeedbackSB
                            .append("============ Device Info ============ <br>");
                    lFeedbackSB.append("Device ID - "
                            + "<b>"+ Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID) + "</b>"+"<br>");
                    lFeedbackSB.append("Device - " +"<b>"+ android.os.Build.DEVICE
                            +"</b>"+ "<br>");
                    lFeedbackSB.append("Model - " + "<b>"+android.os.Build.MODEL
                            +"</b>"+ "<br>");
                    lFeedbackSB.append("Android Sdk Release - "
                            + "<b>"+android.os.Build.VERSION.RELEASE + "</b>"+"<br>");
                    lFeedbackSB.append("Android Sdk Version - "
                            + "<b>"+android.os.Build.VERSION.SDK_INT +"</b>"+ "<br><br>");
                    lFeedbackSB
                            .append("============ Application Info ============ <br>");
                    lFeedbackSB.append("Application version - "
                            +"<b>"+ lVersion +"</b>"+ "<br>");
                    lFeedbackSB.append("User Name - "
                            + "<b>"+lSharedPrefrence.getString(UtililtyClass.USER_LOGIN_ID,null) +"</b>"+ "<br>");
                    lFeedbackSB.append("User Booked Time - "
                            +"<b>"+lCabTingTable.getServiceName(lSharedPrefrence.getString(UtililtyClass.USER_PREFERED_SERVICE,null)) + "</b>"+"<br>");
                    lFeedbackSB.append("User Booked Location - "
                            + "<b>"+lCabRoutTable.getRouteName(lSharedPrefrence.getString(UtililtyClass.USRE_PREFERED_STAFF,null)) +"</b>"+ "<br>");
                    lFeedbackSB.append("Date and Timezone - "
                            + "<b>"+new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                            Locale.ENGLISH).format(new Date())+"</b>");

                    Log.d(TAG, "Sending Feedback: feed back message "+lFeedbackSB);
                    String lRequestHeader = "loopKey=" + "agtzfmxvb3BhYmFja3IRCxIETG9vcBiAgICy0YqbCww" + "&"
                            + "user_email=" + lSharedPrefrence.getString(UtililtyClass.USER_LOGIN_ID, null) + "&"
                            + "card_desc=" + lFeedbackSB + "&"
                            + "tag=" + "feedBack" + "&"
                            + "user_name=" + lSharedPrefrence.getString(UtililtyClass.USER_NAME, null) + "&"
                            + "card_title=" + "Android Cab App Feedback" + "&";

                    Log.d(TAG, "Sending Feedback: request header "+lRequestHeader);
                    HttpUrlHelper lFeedBackHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.FEED_BACK_URL, "POST", lRequestHeader, UtililtyClass.HEADER_URL_ENCODED_CONTENT_TYEPE);
                    Intent lFeedBackIntent = new Intent(FeedBackActivity.this, RoutAndTimingService.class);
                    lFeedBackIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lFeedBackHttpUrlHelper);
                    lFeedBackIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.FEED_BACK_API);
                    startService(lFeedBackIntent);
                    Log.d(TAG, "onOptionsItemSelected: send the feedback");
                } else {
                    Toast.makeText(this, "Feedback Test Should not be empty", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //broadcast receiver to get the feedback response
    BroadcastReceiver mFeedBackResponseReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.cancel();
            mFeedbackEditText.setText("");
            Toast.makeText(context, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show();
        }
    };
}
