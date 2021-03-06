package com.example.user.cabbookingapp.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.common.CommonClass;
import com.example.user.cabbookingapp.datbase.CabRouteTable;
import com.example.user.cabbookingapp.datbase.CabTimingTable;
import com.example.user.cabbookingapp.reciver.ReminderReciver;
import com.example.user.cabbookingapp.util.UtililtyClass;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    Switch mReminderSwitch;
    TextView mSingOutTextView;
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
        mCalender = Calendar.getInstance();
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
                    final String[] lRiminderTiming = new String[]{"5 Mins Before", "15 Mins Before", "30 Mins Before", "1 Hour Before"};
                    new AlertDialog.Builder(UserProfileActivity.this, R.style.MyDialogTheme)
                            .setTitle("Set the Reminder timing")
                            .setSingleChoiceItems(lRiminderTiming, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pPosition) {
                                    mIsReminderOn = true;
                                    mReminderTime = lRiminderTiming[pPosition];
                                    Log.d(TAG, "onClick: custom dialog" + lRiminderTiming[pPosition]);
                                }
                            })
                            .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mIsReminderOn) {
                                        mReminderSwitch.setChecked(true);
                                        getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                                                .edit()
                                                .putBoolean(UtililtyClass.IS_REMINDER_ON, true)
                                                .commit();
                                        String lTimingId = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getString(UtililtyClass.USER_PREFERED_SERVICE, null);
                                        if (lTimingId != null) {

                                            //get the reminder timing
                                            String[] lReminderTimeInStr = mReminderTime.split("\\s");
                                            int lReminderTimeInInteger = Integer.parseInt(lReminderTimeInStr[0]);
                                            if (lReminderTimeInInteger == 1) {
                                                lReminderTimeInInteger = 60;
                                            }
                                            getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putInt(UtililtyClass.USER_REMINDER_TIME,lReminderTimeInInteger)
                                                    .commit();

                                            Log.d(TAG, "onClick: "+getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE,Context.MODE_PRIVATE).getInt(UtililtyClass.USER_REMINDER_TIME,0));
                                            int lBookingTime = mTimingTable.getTheBookinTime(lTimingId);

                                            //set the reminder to book the cab
                                            Intent lNotifyBookIntent=new Intent(UserProfileActivity.this,ReminderReciver.class);
                                            lNotifyBookIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION,UtililtyClass.NOTIFICATION_TO_BOOK);
                                            lNotifyBookIntent.putExtra(UtililtyClass.USER_REMINDER_TIME,lBookingTime - lReminderTimeInInteger);
                                            CommonClass.setReminder(UserProfileActivity.this,lBookingTime - lReminderTimeInInteger, UtililtyClass.NOTIFY_TO_BOOK_CODE,lNotifyBookIntent);

                                            //set the reminder to notify the user 5 mins before the cab starts
                                            Intent lNotifyToLeave=new Intent(UserProfileActivity.this,ReminderReciver.class);
                                            lNotifyToLeave.putExtra(UtililtyClass.REMINDER_NOTIFACTION,UtililtyClass.NOTIFICATION_TO_GO_TO_CAB);
                                            //115 represents 115 minutes which will be added to the cutoff time to remind the user to  leave the office so that they can catch the cab
                                            lNotifyToLeave.putExtra(UtililtyClass.USER_REMINDER_TIME,lBookingTime+115);
                                            CommonClass.setReminder(UserProfileActivity.this,lBookingTime + 115, UtililtyClass.NOTIFY_TO_LEAVE_CODE,lNotifyToLeave);

                                            //set the reminder to clear the booking info once it's done
                                            Intent lClearBookingIntent=new Intent(UserProfileActivity.this,ReminderReciver.class);
                                            lClearBookingIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION,UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS);
                                            lClearBookingIntent.putExtra(UtililtyClass.USER_REMINDER_TIME,lBookingTime+120);
                                            CommonClass.setReminder(UserProfileActivity.this,lBookingTime + 120, UtililtyClass.CLEAR_BOOKING_CODE,lClearBookingIntent);

                                            Log.d(TAG, "Set the Alarm Manager " + lBookingTime + " time in ms " + (lBookingTime - lReminderTimeInInteger) + " " + mCalender.get(Calendar.HOUR_OF_DAY) + " " + mCalender.get(Calendar.MINUTE));
                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "Please book the cab before setting reminder", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    Log.d(TAG, "onClick: reminder time is setted " + mReminderTime);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else {

                    //cancel the reminders
                    cancelReminder(UtililtyClass.NOTIFY_TO_BOOK_CODE);
                    cancelReminder(UtililtyClass.NOTIFY_TO_LEAVE_CODE);
                    cancelReminder(UtililtyClass.CLEAR_BOOKING_CODE);

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
                new AlertDialog.Builder(UserProfileActivity.this, R.style.MyDialogTheme)
                        .setTitle("Are u want to Sign out")
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
                                finish();
                                setResult(USER_PROFILE_PAGE_REQUEST_CODE);
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

        //set the profile image
        SharedPreferences lSharedPrefrence = getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
        String lImageUrl = lSharedPrefrence.getString(UtililtyClass.USER_IMAGE_URL, "");
        lImageUrl = lImageUrl.substring(0, (lImageUrl.length()) - 6);
        Log.d(TAG, "onCreate: " + lImageUrl);
        Picasso.with(this)
                .load(lImageUrl)
                .resize(600, 600)
                .into((ImageView) findViewById(R.id.userProfilrImage));

        //set the email address
        ((TextView) findViewById(R.id.emailTextView)).setText(lSharedPrefrence.getString(UtililtyClass.USER_LOGIN_ID, "no_id"));
        Log.d(TAG, "onCreate: ");
    }

    //navigate to feedback activity
    public void feedBackButton(View pView){
        Intent lIntent=new Intent(UserProfileActivity.this,FeedBackActivity.class);
        startActivity(lIntent);
    }
    //cancel the reminder
    void cancelReminder(int pPendingIntentRequestCode){
        mAlarmManagerPendingIntent = PendingIntent.getBroadcast(UserProfileActivity.this, pPendingIntentRequestCode, new Intent(UserProfileActivity.this,ReminderReciver.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(mAlarmManagerPendingIntent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
