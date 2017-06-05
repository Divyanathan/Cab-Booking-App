package com.example.user.cabbookingapp.datbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 02/06/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME="cab_booking.db";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATA_BASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase pSqLiteDatabase) {

        CabRouteTable.createTable(pSqLiteDatabase);
        CabTimingTable.createTable(pSqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
