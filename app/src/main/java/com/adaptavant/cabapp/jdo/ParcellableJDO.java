package com.adaptavant.cabapp.jdo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by user on 15/06/17.
 *
 */


/**
 * This is for testing purpose
 */
public class ParcellableJDO implements Parcelable {


    String  Test;
    private static final String TAG = "ParcellableJDO";
    protected ParcellableJDO(Parcel in) {
        Test=in.readString();
        Log.d(TAG, "ParcellableJDO: ");
    }

    public static final Creator<ParcellableJDO> CREATOR = new Creator<ParcellableJDO>() {
        @Override
        public ParcellableJDO createFromParcel(Parcel in) {
            Log.d(TAG, "createFromParcel: ");
            return new ParcellableJDO(in);
        }

        @Override
        public ParcellableJDO[] newArray(int size) {
            Log.d(TAG, "newArray: ");
            return new ParcellableJDO[size];
        }
    };

    @Override
    public int describeContents() {
        Log.d(TAG, "describeContents: ");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel: ");
    }
}
