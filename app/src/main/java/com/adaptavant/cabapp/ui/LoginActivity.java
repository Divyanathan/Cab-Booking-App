package com.adaptavant.cabapp.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.common.CommonClass;
import com.adaptavant.cabapp.service.RestApiService;
import com.adaptavant.cabapp.util.UtililtyClass;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

public class LoginActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener {

    Button mSignInButton;
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
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Signing in");
        mSignInButton = (Button) findViewById(R.id.googleSignIn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.isDataAvailable(LoginActivity.this)) {
                    mProgressDialog.show();
                    Intent lGoogleAccountIntent=AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
                    lGoogleAccountIntent.putExtra("overrideTheme", 1);
                    lGoogleAccountIntent.putExtra("overrideCustomTheme", 0);

                    startActivityForResult(lGoogleAccountIntent, REQ_CODE);
                    Log.d(TAG, "googleSignIn: ");
                } else {
                    Toast.makeText(LoginActivity.this, "Check your internet connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mReciverForRoutAndTime, new IntentFilter(UtililtyClass.ROUT_RECIVER));

    }


    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent pIntent) {
        super.onActivityResult(pRequestCode, pResultCode, pIntent);

        if (pRequestCode == REQ_CODE) {
            mProgressDialog.cancel();
            Log.d(TAG, "onActivityResult:  google signin");
            if (pIntent != null) {
                String lUserEmail = pIntent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                String lAccontType = pIntent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                Log.d(TAG, "onActivityResult: email " + lUserEmail + " account type " + lAccontType);
                Account lAccout = new Account(lUserEmail, lAccontType);
                if (lUserEmail.endsWith("adaptavantcloud.com") || lUserEmail.endsWith("a-cti.com") || lUserEmail.endsWith("full.co") || lUserEmail.endsWith("full.io")) {
                    new GoogleSignInAsyncTask().execute(lAccout);
                } else {
                    Toast.makeText(this, "Sorry you are not Authorized ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class GoogleSignInAsyncTask extends AsyncTask<Account, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Synchronizing");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Account... pAccount) {

            /**
             * getting google Account Access Token Using GoogleAuthUtil
             */
            Account lAccount = pAccount[0];
            String scopes = "oauth2:profile email";
            try {

                //get the access token for the user from the google
                mToken = GoogleAuthUtil.getToken(getApplicationContext(), lAccount, scopes);

                Intent lIntent = new Intent(LoginActivity.this, RestApiService.class);
                lIntent.putExtra(UtililtyClass.ACCESS_TOKEN, mToken);
                startService(lIntent);
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
            mProgressDialog.dismiss();
            if (intent.getBooleanExtra(UtililtyClass.IS_DATA_RETRIVED_COMPLETED, false)) {
                Intent lBookCabIntent = new Intent(LoginActivity.this, BookCabActivity.class);
                startActivity(lBookCabIntent);
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_bottom);
            } else {
                Toast.makeText(context, "Something went wrong Please Try again Later", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReciverForRoutAndTime);
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
