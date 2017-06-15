package com.example.user.cabbookingapp.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.common.CommonClass;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.service.RoutAndTimingService;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.user.cabbookingapp.common.CommonClass.getTheHttpUrlHelper;


public class LoginActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener {

    Button mSinginButton;
    ProgressDialog mProgressDialog;
    private static final String TAG = "LoginActivity";
    private int REQ_CODE = 101;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_USER_LOGED_IN, false));
        if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_USER_LOGED_IN, false)) {

            Intent lBookCabIntent = new Intent(LoginActivity.this, BookCabActivity.class);
            startActivity(lBookCabIntent);
            finish();
            Log.d(TAG, "onCreate: User logged in already");

        }

        mProgressDialog = new ProgressDialog(this, R.style.MyDialogTheme);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Singing in");

        mSinginButton = (Button) findViewById(R.id.googleSignIn);
        mSinginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.isDataAvailable(LoginActivity.this)) {
                    mProgressDialog.show();
                    startActivityForResult(AccountPicker.zza(null, null, new String[]{"com.google"}, false, null, null, null, null, false, 1, 0), REQ_CODE);
                    Log.d(TAG, "googleSingIn: ");
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet is available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //register the receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mGoogleLoginReceiver, new IntentFilter(UtililtyClass.GOOGLE_LOGIN_RECIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReciverForRoutAndTime, new IntentFilter(UtililtyClass.ROUT_RECIVER));

    }


    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent pIntent) {
        super.onActivityResult(pRequestCode, pResultCode, pIntent);

        if (pRequestCode == REQ_CODE) {

            mProgressDialog.cancel();
            Log.d(TAG, "onActivityResult:  google signin");
            if (pIntent != null) {
                mProgressDialog.show();
                new GoogleSignInClass().execute(pIntent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class GoogleSignInClass extends AsyncTask<String, Void, String> {


        ProgressDialog lProgressDialog = new ProgressDialog(LoginActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProgressDialog.setMessage("Syncronizing");
            lProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected String doInBackground(String... pParams) {


            /**
             * getting google Account Access Token Using GoogleAuthUtil
             */
            String lAccountName = pParams[0];
            String scopes = "oauth2:profile email";
            try {

                //get the access token for the user from the google
                mToken = GoogleAuthUtil.getToken(getApplicationContext(), lAccountName, scopes);

                //get the Profile Pic of the User From Google
                HttpUrlHelper lProfilePicHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.PROFILE_PIC_URL + mToken, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                Intent lgetProfilePicHelper = new Intent(LoginActivity.this, RoutAndTimingService.class);
                lgetProfilePicHelper.putExtra(UtililtyClass.HTTP_URL_HELPER, lProfilePicHttpUrlHelper);
                lgetProfilePicHelper.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.PROFILE_API);
                startService(lgetProfilePicHelper);

                Log.d(TAG, "doInBackground: access token " + mToken);

            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String pResult) {
            super.onPostExecute(pResult);
//            mProgressDialog.cancel();
            Log.d(TAG, "onPostExecute: " + pResult);
        }
    }


    //receiver for for route
    BroadcastReceiver mReciverForRoutAndTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, false)) {
                mProgressDialog.cancel();
                //mark the user as loged in
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(UtililtyClass.IS_USER_LOGED_IN, true)
                        .commit();
                Intent lBookCabIntent = new Intent(LoginActivity.this, BookCabActivity.class);
                startActivity(lBookCabIntent);
                finish();
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_bottom);
            }
        }
    };

    //google login receiver to check whether the uer is valid or not
    BroadcastReceiver mGoogleLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent pIntent) {

            Log.d(TAG, "mGoogleLoginReceiver: ");

            if (pIntent.getBooleanExtra(UtililtyClass.IS_VALID_USER, false)) {

                Log.d(TAG, "mGoogleLoginReceiver: valid user");
                try {
                    //payload to get the contact details  from REST API
                    JSONObject lJsonObject = new JSONObject();
                    lJsonObject.put(UtililtyClass.CONTACT_CODE, mToken);
                    lJsonObject.put(UtililtyClass.COMPANY_KEY, "084adf34-b48d-4bb7-8795-6a827025a57c");
                    lJsonObject.toString();

                    //get the contact details From REST API
                    HttpUrlHelper lGetContactHttpUrlHelper = getTheHttpUrlHelper(UtililtyClass.CONTACT_URL, "POST", lJsonObject.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                    Intent lGetContactIntent = new Intent(LoginActivity.this, RoutAndTimingService.class);
                    lGetContactIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetContactHttpUrlHelper);
                    lGetContactIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.CONTACT_API);
                    startService(lGetContactIntent);


                    //get the Route
                    HttpUrlHelper lGetTheRoute = getTheHttpUrlHelper(UtililtyClass.ROUT_URL, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                    Intent lGetTheRouteIntent = new Intent(LoginActivity.this, RoutAndTimingService.class);
                    lGetTheRouteIntent.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetTheRoute);
                    lGetTheRouteIntent.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.ROUT_API);
                    startService(lGetTheRouteIntent);

                    //get the Route Timings
                    HttpUrlHelper lGetTimgsHelper = getTheHttpUrlHelper(UtililtyClass.TIMING_URL, "GET", "no_pay_load", UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                    Intent lGetTimingsHelper = new Intent(LoginActivity.this, RoutAndTimingService.class);
                    lGetTimingsHelper.putExtra(UtililtyClass.HTTP_URL_HELPER, lGetTimgsHelper);
                    lGetTimingsHelper.putExtra(UtililtyClass.GET_API_INTENT, UtililtyClass.TIMING_API);
                    startService(lGetTimingsHelper);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.cancel();
                Toast.makeText(context, "You Are Not Autherized to Book the Cab ", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReciverForRoutAndTime);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGoogleLoginReceiver);

        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
