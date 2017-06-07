package com.example.user.cabbookingapp.datbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.cabbookingapp.jdo.TimingsJDO;

import java.util.ArrayList;

/**
 * Created by user on 02/06/17.
 */

public class CabTimingTable {

    public static final String TABLE_NAME = "timing_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SERVICE_NAME = "service_name";
    public static final String COLUMN_COLOR_CODE = "color_code";
    public static final String COLUMN_START_DATE_STRIG = "start_date_in_string";
    public static final String COLUMN_START_DATE_LONG = "start_date_in_long";
    public static final String COLUMN_END_DATE_STRIG = "end_date_in_string";
    public static final String COLUMN_END_DATE_LONG = "end_date_in_long";
    public static final String COLUMN_SERVICE_COST = "service_cost";
    public static final String COLUMN_COMPANY_KEY = "company_key";
    public static final String COULUMN_PROVIDER_LIST="provider_list";
    public static final String TAG = "CabTimingTable";
    Context mContext;
    DataBaseHelper mDataBaseHelper;
    SQLiteDatabase mSqliteDataBase;

    public CabTimingTable(Context mContext) {
        this.mContext = mContext;
    }


    public static void createTable(SQLiteDatabase pDataBase) {

        String lCreatTableQuery = "create table " + TABLE_NAME + "(" +
                COLUMN_ID + " text primary key ," +
                COLUMN_STATUS + " text ," +
                COLUMN_SERVICE_NAME + " text ," +
                COLUMN_COLOR_CODE + " text ," +
                COLUMN_START_DATE_STRIG + " text ," +
                COLUMN_START_DATE_LONG + " integer ," +
                COLUMN_END_DATE_STRIG + " text ," +
                COLUMN_END_DATE_LONG + " integer ," +
                COLUMN_SERVICE_COST + " text ," +
                COULUMN_PROVIDER_LIST + " text ," +
                COLUMN_COMPANY_KEY + " text " +
                ");";

        pDataBase.execSQL(lCreatTableQuery);
        Log.d(TAG, "createTable: ");
    }

    public void open() {
        mDataBaseHelper = new DataBaseHelper(mContext, null, null, 1);
        mSqliteDataBase = mDataBaseHelper.getWritableDatabase();
        Log.d(TAG, "open: ");
    }

    public void addTimings(ArrayList<TimingsJDO> pTimingArrayList) {
        mSqliteDataBase.beginTransaction();
        try {
            for (TimingsJDO lTimgingJDO : pTimingArrayList) {
                if (!isDataExsit(lTimgingJDO.getId())) {
                    ContentValues lContentValues = new ContentValues();
                    lContentValues.put(COLUMN_ID, lTimgingJDO.getId());
                    lContentValues.put(COLUMN_STATUS, lTimgingJDO.getStatus());
                    lContentValues.put(COLUMN_SERVICE_NAME, lTimgingJDO.getServiceName());
                    lContentValues.put(COLUMN_COLOR_CODE, lTimgingJDO.getColorcode());
                    lContentValues.put(COLUMN_START_DATE_STRIG, lTimgingJDO.getStartDateString());
                    lContentValues.put(COLUMN_START_DATE_LONG, lTimgingJDO.getStartDateLong());
                    lContentValues.put(COLUMN_END_DATE_STRIG, lTimgingJDO.getEndDateString());
                    lContentValues.put(COLUMN_END_DATE_LONG, lTimgingJDO.getEndDateLong());
                    lContentValues.put(COLUMN_SERVICE_COST, lTimgingJDO.getServiceCost());
                    lContentValues.put(COULUMN_PROVIDER_LIST,lTimgingJDO.getProvidersList().toString());
                    lContentValues.put(COLUMN_COMPANY_KEY, lTimgingJDO.getF_Key());
                    mSqliteDataBase.insert(TABLE_NAME,null,lContentValues);
                    Log.d(TAG, "addTimings: inserting the timing data");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSqliteDataBase.setTransactionSuccessful();
            mSqliteDataBase.endTransaction();
        }
    }
    
    public Cursor getTimingDetails(String pTimingID){
        
        return mSqliteDataBase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + "'" + pTimingID + "'", null);
    }

    public boolean isDataExsit(String pTimingId) {
        Log.d(TAG, "isDataExsit: ");
        Cursor lCursor = mSqliteDataBase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + "'" + pTimingId + "'", null);
        if (lCursor.getCount() > 0) {
            return true;
        }
        return false;
    }
    public void   close(){
        mDataBaseHelper.close();
    }
}
