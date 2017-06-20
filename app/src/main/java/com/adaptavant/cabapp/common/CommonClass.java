package com.adaptavant.cabapp.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.adaptavant.cabapp.httphelper.HttpUrlHelper;
import com.adaptavant.cabapp.jdo.HttpHeaderJDO;
import com.adaptavant.cabapp.ui.UserProfileActivity;
import com.adaptavant.cabapp.util.UtililtyClass;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 02/06/17.
 */

public class CommonClass {

    //set the header and the request body
    public static HttpUrlHelper getTheHttpUrlHelper(String pUrl, String pRequestMethod, String pPayLoad, String pHeaderValue) {

        ArrayList<HttpHeaderJDO> lContentType = new ArrayList<>();
        HttpHeaderJDO lContentTypeJDo = new HttpHeaderJDO();
        lContentTypeJDo.setKey("Content-Type");
        lContentTypeJDo.setValue(pHeaderValue);
        lContentType.add(lContentTypeJDo);

        HttpUrlHelper lHttpHelper = new HttpUrlHelper();
        lHttpHelper.setUrl(pUrl);
        lHttpHelper.setHttpRequetMethod(pRequestMethod);
        lHttpHelper.setPayload(pPayLoad);
        lHttpHelper.setHttpHeader(lContentType);
        return lHttpHelper;
    }

    /**
     * Get Time from calendar
     * @param pTimeInMinutes the time in minutes
     * @return a calendar instance
     */
    public static Calendar getTheTimeFromCallender(int pTimeInMinutes) {

        Calendar lCalender = Calendar.getInstance();
        lCalender.set(Calendar.HOUR_OF_DAY, pTimeInMinutes / 60);
        lCalender.set(Calendar.MINUTE, pTimeInMinutes % 60);
        lCalender.set(Calendar.SECOND, 0);
        lCalender.set(Calendar.MILLISECOND, 0);
        return lCalender;
    }

    //set the reminder
    public static void setReminder(Context pContext, int pTime,Intent pIntent) {
        PendingIntent mAlarmManagerPendingIntent = PendingIntent.getBroadcast(pContext,UtililtyClass.NOTIFY_TO_BOOK_CODE , pIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar mCalender = CommonClass.getTheTimeFromCallender(pTime);
        AlarmManager lAlarmManager = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            lAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalender.getTimeInMillis(), mAlarmManagerPendingIntent);
        }else {
            lAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalender.getTimeInMillis(), mAlarmManagerPendingIntent);
        }
        Log.d("CommonClass", "setReminder : "+mCalender.getTimeInMillis());
    }

    //check is the internet is available or not
    public static boolean isDataAvailable(Context pContext) {
        ConnectivityManager lConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo lActiveNetworkInfo = lConnectivityManager.getActiveNetworkInfo();
        if (lActiveNetworkInfo != null) {
            return true;
        }
        return false;
    }

}

