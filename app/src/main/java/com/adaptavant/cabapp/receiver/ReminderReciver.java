package com.adaptavant.cabapp.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adaptavant.cabapp.R;
import com.adaptavant.cabapp.common.CommonClass;
import com.adaptavant.cabapp.httphelper.HttpConnection;
import com.adaptavant.cabapp.httphelper.HttpUrlHelper;
import com.adaptavant.cabapp.jdo.GetTodayBookingJDO;
import com.adaptavant.cabapp.ui.BookCabActivity;
import com.adaptavant.cabapp.util.UtililtyClass;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.adaptavant.cabapp.common.CommonClass.getTheHttpUrlHelper;

/**
 * Created by user on 08/06/17.
 */

public class ReminderReciver extends BroadcastReceiver {
    private static final String TAG = "ReminderReciver";
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        new GetTodaysBooking().execute(intent);
    }


    //get today's booking
    class GetTodaysBooking extends AsyncTask<Intent, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Intent... pIntent) {

            try {

                boolean lIsCabBooked = false;
                Log.d(TAG, "GetTodaysBooking get Today's Booking doInBackground: ");
                Calendar lCalender = Calendar.getInstance();
                DateFormat lDateFormate = new SimpleDateFormat("dd MMM yyyy");
                String lBookingDate = lDateFormate.format(lCalender.getTime());
                Log.d(TAG, "GetTodaysBooking booking date " + lBookingDate);


                //set the cab booking json
                SharedPreferences lSharedPrefrence = mContext.getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
                JSONObject lGetBookingBody = new JSONObject();
                lGetBookingBody.put(UtililtyClass.CAB_BOOK_CUSTOMER_KEY, lSharedPrefrence.getString(UtililtyClass.USER_KEY, null));
                lGetBookingBody.put(UtililtyClass.BOOKIN_DATE, lBookingDate);
                lGetBookingBody.put(UtililtyClass.COMPANY_KEY, lSharedPrefrence.getString(UtililtyClass.COMPANY_KEY, null));

                //hitting the Api to check whether he booked the cab or not
                HttpUrlHelper lGetTodayBookingHelper = getTheHttpUrlHelper(UtililtyClass.GET_BOOKING_TODAY_URL, "POST", lGetBookingBody.toString(), UtililtyClass.HEADER_JSON_CONTENT_TYPE);
                String lResponse = new HttpConnection().getHttpResponse(lGetTodayBookingHelper);

                //store the response in JDO
                ObjectMapper lObjectMapper = new ObjectMapper();
                GetTodayBookingJDO lGetBookingJDO = lObjectMapper.readValue(lResponse, GetTodayBookingJDO.class);
                if (lGetBookingJDO.getBookingData().size() > 0) {
                    Log.d(TAG, "get Today's Booking : " + "cab is booked time id " + lGetBookingJDO.getBookingData().get(0).get("serviceKey") + " route id " + lGetBookingJDO.getBookingData().get(0).get("staffKey"));
                    lIsCabBooked = true;
                } else {

                    lIsCabBooked = false;
                    Log.d(TAG, "get Today's Booking: " + "cab is not booked");
                }


                int lHour = lCalender.get(Calendar.HOUR_OF_DAY) * 60;
                int lMinute = lCalender.get(Calendar.MINUTE);
                int lCurrentTimeInMinute = lHour + lMinute;
                int mReminderTime = pIntent[0].getIntExtra(UtililtyClass.USER_REMINDER_TIME, 0);


                String lNotificationMessage = pIntent[0].getStringExtra(UtililtyClass.REMINDER_NOTIFACTION);
                Log.d(TAG, "onReceive: " + lCalender.getTime());
                Log.d(TAG, "onReceive: current time in mins " + lCurrentTimeInMinute + " reminder time in minutes " + mReminderTime);
                Log.d(TAG, "onReceive: notify message " + lNotificationMessage);
                Log.d(TAG, "onReceive: is cab booked " + lIsCabBooked);

                SharedPreferences lSharedPreferrence = mContext.getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
                if (lNotificationMessage.equals(UtililtyClass.NOTIFICATION_TO_BOOK)) {
                    if (!lIsCabBooked & mReminderTime == lCurrentTimeInMinute) {
                        notifyUSer(mContext, lNotificationMessage);
                        Log.d(TAG, "onReceive: send the notification message to book the cab");
                    }
                    int lBookingCutoffTime = lSharedPreferrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0);
                    Log.d(TAG, "onReceive: cutoff time " + lBookingCutoffTime);
                    Intent lIntent = new Intent(mContext, ReminderReciver.class);
                    lIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_GO_TO_CAB);
                    //notify the user to catch the cab before 15 mins before
                    lIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, (lBookingCutoffTime + 120 - 15));
                    CommonClass.setReminder(mContext, (lBookingCutoffTime + 120 - 15), lIntent);
                    Log.d(TAG, "onReceive: set the reminder to notify the user to catch the cab--time " + (lBookingCutoffTime + 120 - 15));

                } else {

                    if (lIsCabBooked & mReminderTime == lCurrentTimeInMinute) {
                        notifyUSer(mContext, lNotificationMessage);
                        Log.d(TAG, "onReceive: send the notification message to catch the cab");
                    }
                    DateFormat lDate = new SimpleDateFormat("EEEE");
                    String lDay = lDate.format(lCalender.getTime());
                    Log.d(TAG, "doInBackground: is Friday --->" + lDay);
                    int lReminderTime;
                    if (lDay.equals("Friday")) {
                        //add 3 more to the calender so that it will remind the user on monday
                        lCalender = setCalender(3);
                        Log.d(TAG, "doInBackground: notifying time " + lSharedPreferrence.getInt(UtililtyClass.NOTYFYING_TIME, 0));
                        Log.d(TAG, "onReceive: notify the user to book the cab on monday--time");
                    } else {
                        //add one to the calender to notify the user on tmr
                        lCalender = setCalender(1);
                        Log.d(TAG, "doInBackground: notifying time " + lSharedPreferrence.getInt(UtililtyClass.NOTYFYING_TIME, 0));
                        Log.d(TAG, "onReceive:  notify the user to book the cab by tmr--time ");
                    }

                    Intent lIntent = new Intent(mContext, ReminderReciver.class);
                    lIntent.putExtra(UtililtyClass.REMINDER_NOTIFACTION, UtililtyClass.NOTIFICATION_TO_BOOK);
                    //notify the user to catch the cab before 15 mins before
                    lIntent.putExtra(UtililtyClass.USER_REMINDER_TIME, (lSharedPreferrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0) - lSharedPreferrence.getInt(UtililtyClass.NOTYFYING_TIME, 0)));

                    PendingIntent mAlarmManagerPendingIntent = PendingIntent.getBroadcast(mContext, UtililtyClass.NOTIFY_TO_BOOK_CODE, lIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager lAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, lCalender.getTimeInMillis(), mAlarmManagerPendingIntent);
                    } else {
                        lAlarmManager.set(AlarmManager.RTC_WAKEUP, lCalender.getTimeInMillis(), mAlarmManagerPendingIntent);
                    }
                    Log.d(TAG, "onReceive: set the reminder to notify " + (lSharedPreferrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0)));


                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        //set the calender to set the alarm tmr
        //if today is friday set the alarm on monday
        Calendar setCalender(int pIntervalDay) {
            SharedPreferences lSharedPrefrence= mContext.getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE);
            lSharedPrefrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0);
            int lBookingCutoffTime=lSharedPrefrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0)-lSharedPrefrence.getInt(UtililtyClass.NOTYFYING_TIME,0);
            Calendar lCalender = Calendar.getInstance();
            lCalender.set(Calendar.HOUR_OF_DAY, lBookingCutoffTime / 60);
            lCalender.set(Calendar.MINUTE, lBookingCutoffTime % 60);
            lCalender.set(Calendar.MILLISECOND, 0);
            lCalender.set(Calendar.SECOND, 0);
            lCalender.add(Calendar.DAY_OF_MONTH, pIntervalDay);

            Log.d(TAG, "setCalender: hour cut off time "+ lSharedPrefrence.getInt(UtililtyClass.USER_REMINDER_TIME, 0)+" "+(lBookingCutoffTime / 60)+" minute "+(lBookingCutoffTime % 60));
            return lCalender;
        }

    }

    /**
     *
     * @param pContext
     * @param pNotificationMsg  notification message to display
     *
     */
    void notifyUSer(Context pContext, String pNotificationMsg) {

//        Drawable lAppIconDrawble= ContextCompat.getDrawable(mContext,R.drawable.app_icon);
//        Bitmap lAppIconBitmap=((BitmapDrawable)lAppIconDrawble).getBitmap();
        final int PENDING_INTENT_ID = 5;
        Notification lNotification = null;
        Intent lIntent = new Intent(pContext, BookCabActivity.class);
        lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            lNotification = new Notification.Builder(pContext)
                    .setContentTitle("Cab Booking notification")
                    .setContentText(pNotificationMsg)
//                    .setLargeIcon(lAppIconBitmap)
                    .setSmallIcon(R.drawable.app_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(pContext, PENDING_INTENT_ID,lIntent, PendingIntent.FLAG_ONE_SHOT))
                    .build();
        }
        NotificationManager lNotificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.notify(UtililtyClass.NOTIFICATION_ID, lNotification);
    }


}