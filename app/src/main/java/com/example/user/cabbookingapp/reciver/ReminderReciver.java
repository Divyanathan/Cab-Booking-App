package com.example.user.cabbookingapp.reciver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.ui.BookCabActivity;
import com.example.user.cabbookingapp.util.UtililtyClass;

import java.util.Calendar;

/**
 * Created by user on 08/06/17.
 */

public class ReminderReciver extends BroadcastReceiver {
    private static final String TAG = "ReminderReciver";

    @Override
    public void onReceive(Context context, Intent intent) {


        Calendar lCalender = Calendar.getInstance();
        int lHour = lCalender.get(Calendar.HOUR_OF_DAY) * 60;
        int lMinute = lCalender.get(Calendar.MINUTE);
        int lTimeInMinute = lHour + lMinute;
        int lReminderTime = intent.getIntExtra(UtililtyClass.USER_REMINDER_TIME, 0) + 1;
        boolean lIsCabBooked = context.getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE).getBoolean(UtililtyClass.IS_CAB_BOOKED, false);
        String lNotificationMessage = intent.getStringExtra(UtililtyClass.REMINDER_NOTIFACTION);
        Log.d(TAG, "onReceive: " + lCalender.getTime());
        Log.d(TAG, "onReceive: current time in mins " + lTimeInMinute + " reminder time in minutes " + lReminderTime);
        Log.d(TAG, "onReceive: notify message " + lNotificationMessage);
        Log.d(TAG, "onReceive: is cab booked " + lIsCabBooked);
        try {
            //clear the booking details
            if (lNotificationMessage.equals(UtililtyClass.NOTIFICATION_CLEAR_BOOKING_DETAILS)) {
                Log.d(TAG, "onReceive: clear the booking");
                //if cab booked clear the bookin information
                if (lIsCabBooked) {
                    context.getSharedPreferences(UtililtyClass.MY_SHARED_PREFRENCE, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(UtililtyClass.IS_CAB_BOOKED, false)
                            .commit();

                    //send the receiver to clear the booking information
                    Intent lClearBookingIntent = new Intent(UtililtyClass.CAB_BOOKING_RECIVER);
                    lClearBookingIntent.putExtra(UtililtyClass.CAB_BOOKING_INTENT, UtililtyClass.CLEAR_BOOKING);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(lClearBookingIntent);
                }
            }
            //Notify the user to book the cab
            else if (lNotificationMessage.equals(UtililtyClass.NOTIFICATION_TO_BOOK)) {

                Log.d(TAG, "onReceive: notify the user to book the cab");
                if (!lIsCabBooked && lTimeInMinute <= lReminderTime) {
                    notifyUSer(context, intent.getStringExtra(UtililtyClass.REMINDER_NOTIFACTION));
                }
            }
            //Notify the user to catch the cab
            else {
                Log.d(TAG, "onReceive: notify the user to catch the cab");
                if (lIsCabBooked && lTimeInMinute <= lReminderTime) {
                    notifyUSer(context, intent.getStringExtra(UtililtyClass.REMINDER_NOTIFACTION));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send the notification
    void notifyUSer(Context pContext, String pNotificationMsg) {
        Notification lNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            lNotification = new Notification.Builder(pContext)
                    .setContentTitle("Cab Booking notification")
                    .setContentText(pNotificationMsg)
                    .setSmallIcon(R.mipmap.car_logo)
                    .setContentIntent(PendingIntent.getActivity(pContext, 2, new Intent(pContext, BookCabActivity.class), PendingIntent.FLAG_ONE_SHOT))
                    .build();
        }
        NotificationManager lNotificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        lNotificationManager.notify(2, lNotification);
    }
}