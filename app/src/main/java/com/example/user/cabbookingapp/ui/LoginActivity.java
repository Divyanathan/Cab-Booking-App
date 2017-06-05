package com.example.user.cabbookingapp.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.httphelper.HttpConnection;
import com.example.user.cabbookingapp.httphelper.HttpUrlHelper;
import com.example.user.cabbookingapp.jdo.ContentTypeJDO;
import com.example.user.cabbookingapp.jdo.CustomerJDO;
import com.example.user.cabbookingapp.jdo.UserDetailJDO;
import com.example.user.cabbookingapp.jdo.UserProfilePic;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Singing in");

        mSinginButton = (Button) findViewById(R.id.googleSignIn);
        mSinginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataAvailable()) {
                    mProgressDialog.show();
                    startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null), REQ_CODE);
                    Log.d(TAG, "googleSingIn: ");
                }else {
                    Toast.makeText(LoginActivity.this, "No Internet is available", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent pIntent) {
        super.onActivityResult(pRequestCode, pResultCode, pIntent);


        if (pRequestCode == REQ_CODE) {

            mProgressDialog.dismiss();
            Log.d(TAG, "onActivityResult:  google signin");
            if (pIntent != null) {
                new GoogleSignInClass().execute(pIntent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                Log.d(TAG, "onActivityResult: " + pIntent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            }
        }
    }


    void navigateToUser(String pContactDeatils) {

        ObjectMapper lObjectMapper = new ObjectMapper();
        UserDetailJDO lUserJDO = new UserDetailJDO();
        try {
            lUserJDO = lObjectMapper.readValue(pContactDeatils, UserDetailJDO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent lUserIntent = new Intent(LoginActivity.this, SyncingActivity.class);
        lUserIntent.putExtra(UtililtyClass.USER_INTENT, lUserJDO.getCustomer());
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

            startActivity(lUserIntent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_bottom);
        } else {
            Log.d(TAG, "navigateToUser: " + "authentication Failed");
        }

    }

    void navigateToUser(){

        Intent lUserIntent=new Intent(LoginActivity.this,BookCabActivity.class);
        startActivity(lUserIntent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class GoogleSignInClass extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... pParams) {


            /**
             * getting google Account Access Token Using GoogleAuthUtil
             */
            String lAccountName = pParams[0];
            String scopes = "oauth2:profile email";
            try {
                mToken = GoogleAuthUtil.getToken(getApplicationContext(), lAccountName, scopes);

                Log.d(TAG, "doInBackground: " + mToken);
                mProgressDialog.cancel();
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }


            /**
             * send the post request to get the Account details to the Rest Api
             */
            JSONObject lJsonObject = new JSONObject();
            try {
                lJsonObject.put(UtililtyClass.CONTACT_CODE, mToken);
                lJsonObject.put(UtililtyClass.COMPANY_KEY, "084adf34-b48d-4bb7-8795-6a827025a57c");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lJsonObject.toString();
            ArrayList<ContentTypeJDO> lContentType = new ArrayList<>();
//
//            ContentTypeJDO lContentTypeJDo = new ContentTypeJDO();
//            lContentTypeJDo.setKey("Content-Type");
//            lContentTypeJDo.setValue("application/json");
//            lContentType.add(lContentTypeJDo);
//            HttpUrlHelper lHttpHelper = new HttpUrlHelper();
//
//            lHttpHelper.setUrl(UtililtyClass.CONTACT_URL);
//            lHttpHelper.setPayload(lJsonObject.toString());
//            lHttpHelper.setHttpRequetMethod("POST");
//            lHttpHelper.setContentType(lContentType);

            HttpUrlHelper lProfilePicHttpUrlHelper=getTheProfilePic();

            HttpConnection lHttpConnection = new HttpConnection();
            String lResponse = lHttpConnection.getTheResponse(lProfilePicHttpUrlHelper);

            Log.d(TAG, "doInBackground:  profile pic "+lResponse);

            ObjectMapper lObjecMaper=new ObjectMapper();
            try {
                UserProfilePic lUerProfilePic=lObjecMaper.readValue(lResponse,UserProfilePic.class);
                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE)
                        .edit()
                        .putString(UtililtyClass.USER_IMAGE_URL,lUerProfilePic.getImage().get("url"))
                        .commit();
                navigateToUser();
                Log.d(TAG, "doInBackground: url"+lUerProfilePic.getImage().get("url"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lResponse;
        }

        @Override
        protected void onPostExecute(String pResult) {
            super.onPostExecute(pResult);
            Log.d(TAG, "onPostExecute: " + pResult);
            if (pResult != null) {
//                navigateToUser(pResult);
                Log.d(TAG, "onPostExecute: ");
            }
        }
    }

    public boolean isDataAvailable(){
        ConnectivityManager lConnectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo lActiveNetworkInfo = lConnectivityManager.getActiveNetworkInfo();
        if(lActiveNetworkInfo != null){
            return true;
        }
        return false;
    }

    HttpUrlHelper getTheProfilePic(){

        ArrayList<ContentTypeJDO> lContentType = new ArrayList<>();
        ContentTypeJDO lContentTypeJDo = new ContentTypeJDO();
        lContentTypeJDo.setKey("Content-Type");
        lContentTypeJDo.setValue("application/json");
        lContentType.add(lContentTypeJDo);
        HttpUrlHelper lHttpHelper = new HttpUrlHelper();

        lHttpHelper.setUrl("https://www.googleapis.com/plus/v1/people/me?access_token="+mToken);
        lHttpHelper.setHttpRequetMethod("GET");
        lHttpHelper.setContentType(lContentType);

        return lHttpHelper;
    }

}
