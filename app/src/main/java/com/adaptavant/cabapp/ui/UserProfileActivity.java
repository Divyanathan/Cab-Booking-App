package com.adaptavant.cabapp.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.common.CommonClass;
import com.adaptavant.cabapp.datbase.CabRouteTable;
import com.adaptavant.cabapp.datbase.CabTimingTable;
import com.adaptavant.cabapp.receiver.ReminderReciver;
import com.adaptavant.cabapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    Switch mReminderSwitch;
    TextView mSingOutTextView;
    TextView mReminderTextView;
    String mReminderTime = null;
    boolean mIsReminderOn = false;
    final int USER_PROFILE_PAGE_REQUEST_CODE = 3;
    AlarmManager mAlarmManager;
    PendingIntent mAlarmManagerPendingIntent;
    Calendar mCalender;
    CabTimingTable mTimingTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mReminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        mSingOutTextView = (TextView) findViewById(R.id.signOutTextView);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mTimingTable = new CabTimingTable(this);
        mTimingTable.open();

        //set the reminder switch value
        if (getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_REMINDER_ON, false)) {
            mReminderSwitch.setChecked(true);

        }

        //on click listener for switch
        mReminderSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mReminderSwitch.isChecked()) {
                    mReminderSwitch.setChecked(false);
                    final String lTimingId = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null);
                    if (lTimingId != null && !lTimingId.isEmpty()) {
                        final String[] lRiminderTiming = new String[]{"5 Mins Before", "15 Mins Before", "30 Mins Before", "1 Hour Before"};

                        AlertDialog lAlertDialog = new AlertDialog.Builder(UserProfileActivity.this, R.style.MyDialogTheme)
                                .setTitle("Set the Reminder timing")
                                .setSingleChoiceItems(lRiminderTiming, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int pPosition) {
                                        mIsReminderOn = true;
                                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                        mReminderTime = lRiminderTiming[pPosition];
                                        Log.d(TAG, "onClick: custom dialog" + lRiminderTiming[pPosition]);
                                    }
                                })
                                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (mIsReminderOn) {
                                            mReminderSwitch.setChecked(true);
                                            mCalender = Calendar.getInstance();
                                            //get the reminder timing
                                            String[] lReminderTimeInStr = mReminderTime.split("\\s");
                                            int lReminderTimeInInteger = Integer.parseInt(lReminderTimeInStr[0]);
                                            if (lReminderTimeInInteger == 1) {
                                                lReminderTimeInInteger = 60;
                                            }

                                            int lBookingTime = mTimingTable.getTheBookingTime(lTimingId);
                                            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean(UtililtyClass.IS_REMINDER_ON, true)
                                                    .putInt(UtililtyClass.USER_REMINDER_TIME, lBookingTime)
                                                    .putInt(UtililtyClass.NOTYFYING_TIME, lReminderTimeInInteger)
                                                    .commit();
                                            //set the reminder to book the cab
                                            Intent lNotifyBookIntent = new Intent(UserProfileActivity.this, ReminderReciver.class);
                                            lNotifyBookIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_BOOK);
                                            lNotifyBookIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, lBookingTime - lReminderTimeInInteger);
                                            CommonClass.setReminder(UserProfileActivity.this, lBookingTime - lReminderTimeInInteger, lNotifyBookIntent);


                                            Log.d(TAG, "Set the Alarm Manager  booking time " + lBookingTime + " time " + (lBookingTime - lReminderTimeInInteger) + " hour " + mCalender.get(Calendar.HOUR_OF_DAY) + " minutes " + mCalender.get(Calendar.MINUTE));

                                        }
                                        Log.d(TAG, "onClick: reminder time is setted " + mReminderTime);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create();
                        lAlertDialog.show();
                        lAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Please book the cab before setting reminder", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    //cancel the reminders
                    cancelReminder(UtililtyClass.NOTIFY_TO_BOOK_CODE);
                    cancelReminder(UtililtyClass.NOTIFY_TO_LEAVE_CODE);
                    cancelReminder(UtililtyClass.CLEAR_BOOKING_CODE);
                    mIsReminderOn = false;
                    getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(UtililtyClass.IS_REMINDER_ON, false)
                            .commit();
                    Log.d(TAG, "set reminder off: ");
                }
            }
        });

        //set the onclick listener for signout button
        mSingOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TextView lTitleText = new TextView(UserProfileActivity.this);
//                lTitleText.setText("Are u want to Sign out");
//                lTitleText.setTextSize(20);
//                lTitleText.setPadding(15,10,10,0);
//                lTitleText.setGravity(Gravity.CENTER);
//                lTitleText.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.cancel_booking_color));
                new AlertDialog.Builder(UserProfileActivity.this, R.style.Sign_out_theme)
                        .setTitle("Are you want to Sign out?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                                        .edit()
                                        .clear()
                                        .commit();
                                CabTimingTable lCabTimingTable = new CabTimingTable(UserProfileActivity.this);
                                lCabTimingTable.open();
                                lCabTimingTable.deleteTimingTable();
                                lCabTimingTable.close();

                                CabRouteTable lCabRouteTable = new CabRouteTable(UserProfileActivity.this);
                                lCabRouteTable.open();
                                lCabRouteTable.deleteRouteTable();
                                lCabTimingTable.close();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                                    finishAffinity();
                                    Intent lIntent = new Intent(UserProfileActivity.this, LoginActivity.class);
                                    startActivity(lIntent);
                                }else {

                                }
//                                Intent lIntent = new Intent(UserProfileActivity.this, LoginActivity.class);
//                                lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(lIntent);
//                                finish();
                                Log.d(TAG, "Sign out button click : " + getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_NAME, null));
                            }
                        })
                        .create()
                        .show();

            }
        });

        //set the back button
        Toolbar lToolbar = (Toolbar) findViewById(R.id.toolBarTitle);
        setSupportActionBar(lToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set the title and title color
        CollapsingToolbarLayout lCollapsingToolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolBar);
        lCollapsingToolBarLayout.setTitle(getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_NAME, "no_name"));
        lCollapsingToolBarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        lCollapsingToolBarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

        //set the size of the title
        lCollapsingToolBarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
        lCollapsingToolBarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        SharedPreferences lSharedPrefrence = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);

        try {

            //set the profile image
            String lImageUrl = lSharedPrefrence.getString(UtililtyClass.USER_IMAGE_URL, "");
            lImageUrl = lImageUrl.substring(0, (lImageUrl.length()) - 6);
            Log.d(TAG, "onCreate: " + lImageUrl);

                Picasso.with(this)
                        .load(lImageUrl)
                        .placeholder(R.drawable.user_image)
                        .resize(600, 600)
                        .into((ImageView) findViewById(R.id.profile_image));

            }catch (Exception e){
                e.printStackTrace();
                Picasso.with(this).load(R.drawable.user_image).into((ImageView) findViewById(R.id.profile_image));
            }
            //set the email address
            ((TextView) findViewById(R.id.emailTextView)).setText(lSharedPrefrence.getString(UtililtyClass.USER_LOGIN_ID, "no_id"));
            Log.d(TAG, "onCreate: ");
    }

    //navigate to feedback activity
    public void feedBackButton(View pView) {
        Intent lIntent = new Intent(UserProfileActivity.this, FeedBackActivity.class);
        startActivity(lIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    //cancel the reminder
    void cancelReminder(int pPendingIntentRequestCode) {
        mAlarmManagerPendingIntent = PendingIntent.getBroadcast(UserProfileActivity.this, pPendingIntentRequestCode, new Intent(UserProfileActivity.this, ReminderReciver.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(mAlarmManagerPendingIntent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: ");
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
//        finish();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            supportFinishAfterTransition();
//            finishAfterTransition();
//        }else{
//            finish();
//        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
//        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            return true;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                supportFinishAfterTransition();
//                finishAfterTransition();
//                return true;
//            }else{
//                finish();
//                return true;
//            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
