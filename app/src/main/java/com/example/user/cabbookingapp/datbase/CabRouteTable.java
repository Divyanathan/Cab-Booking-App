package com.example.user.cabbookingapp.datbase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.cabbookingapp.jdo.CustomerJDO;

import java.util.ArrayList;

/**
 * Created by user on 02/06/17.
 */

public class CabRouteTable {

    private static final String TABLE_NAME = "route_table";
    private static final String COLUMN_ID = "routt_id";
    private static final String COLUMN_ROUTE_NAME = "route_name";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMC_COMPANY_ID = "company_id";
    private static final String COLUMC_VEHICAL_STATUS = "vehical_status";
    private static final String TAG = "CabRouteTable";
    DataBaseHelper mDataBseHelper;
    SQLiteDatabase mSqliteDataBase;
    Context mContext;

    public CabRouteTable(Context mContext) {
        this.mContext = mContext;
    }

    public static void createTable(SQLiteDatabase pDataBase) {
        String lCreatTableQuery = "create table " + TABLE_NAME + "(" +
                COLUMN_ID + " text primary key ," +
                COLUMN_ROUTE_NAME + " text ," +
                COLUMN_STATUS + " text ," +
                COLUMC_COMPANY_ID + " text ," +
                COLUMC_VEHICAL_STATUS + " text" +
                ");";

        pDataBase.execSQL(lCreatTableQuery);
    }

    /**
     * Opening an tabele
     */
    public void open() {

        mDataBseHelper = new DataBaseHelper(mContext, null, null, 1);
        mSqliteDataBase = mDataBseHelper.getWritableDatabase();

    }

    public void addRoutDetails(ArrayList<CustomerJDO> pCustomerJDOArrayList) {
        mSqliteDataBase.beginTransaction();
        try {

            for (CustomerJDO lCustomerJDO : pCustomerJDOArrayList) {
                if (!isDataExsit(lCustomerJDO.getId())) {
                    ContentValues lContentValue = new ContentValues();
                    lContentValue.put(COLUMN_ID, lCustomerJDO.getId());
                    lContentValue.put(COLUMN_ROUTE_NAME, lCustomerJDO.getName());
                    lContentValue.put(COLUMN_STATUS, lCustomerJDO.getStatus());
                    lContentValue.put(COLUMC_COMPANY_ID, lCustomerJDO.getF_Key());
                    lContentValue.put(COLUMC_VEHICAL_STATUS, lCustomerJDO.getVehiclePassStatus());
                    mSqliteDataBase.insert(TABLE_NAME, null, lContentValue);
                    Log.d(TAG, "addRoutDetails: Insert_RoutData");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSqliteDataBase.setTransactionSuccessful();
            mSqliteDataBase.endTransaction();
        }

    }

    public boolean isDataExsit(String pRoutID) {

        Cursor lContactCursor = mSqliteDataBase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + "'" + pRoutID + "'", null);

        Log.d(TAG, "isDataExsit: check the rout is exisit or not");
        if (lContactCursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public void close() {

        mDataBseHelper.close();
    }
}
