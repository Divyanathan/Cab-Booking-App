package com.example.user.cabbookingapp.jdo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 15/06/17.
 */

public class ParcellableJDO implements Parcelable {



    protected ParcellableJDO(Parcel in) {
    }

    public static final Creator<ParcellableJDO> CREATOR = new Creator<ParcellableJDO>() {
        @Override
        public ParcellableJDO createFromParcel(Parcel in) {
            return new ParcellableJDO(in);
        }

        @Override
        public ParcellableJDO[] newArray(int size) {
            return new ParcellableJDO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
