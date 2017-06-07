package com.example.user.cabbookingapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.jdo.BookingTimeJDO;
import com.example.user.cabbookingapp.util.UtililtyClass;

import java.util.ArrayList;

/**
 * Created by user on 05/06/17.
 */

public class GridViewAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<BookingTimeJDO> mBookingTimeJDOArrayList;
    BookingTimeJDO mBookingTimeJDO;
    private static final String TAG = "GridViewAdapter";

    public GridViewAdapter(Context mContext, ArrayList<BookingTimeJDO> pBookingTimeJDO) {
        this.mContext = mContext;
        mBookingTimeJDOArrayList = pBookingTimeJDO;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View lGridView;
        LayoutInflater lLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView lTimingTextView;

        if (convertView == null) {
            lGridView = new View(mContext);
            lGridView = lLayoutInflater.inflate(R.layout.timing_layout, null);
        } else {
            lGridView = convertView;
        }

        mBookingTimeJDO = mBookingTimeJDOArrayList.get(position);
        lTimingTextView = (TextView) lGridView.findViewById(R.id.timingTextView);
        lTimingTextView.setText(mBookingTimeJDO.getDisplayTime());
        if (mBookingTimeJDO.getStatus().equalsIgnoreCase(UtililtyClass.CAB_AVAILABLE)) {

            lTimingTextView.setBackgroundResource(R.drawable.availble_timing);
            lTimingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            Log.d(TAG, "getView: available " + mBookingTimeJDO.getDisplayTime());

        } else if (mBookingTimeJDO.getStatus().equalsIgnoreCase(UtililtyClass.CAB_IS_NOT_AVAILABLE)) {

            lTimingTextView.setBackgroundResource(R.drawable.non_available_timings);
            lTimingTextView.setEnabled(false);
            lTimingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            Log.d(TAG, "getView: not available " + mBookingTimeJDO.getDisplayTime());

        } else if (mBookingTimeJDO.getStatus().equalsIgnoreCase(UtililtyClass.CAB_IS_BOOKED)) {

            lTimingTextView.setBackgroundResource(R.drawable.sected_timing);
            lTimingTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            Log.d(TAG, "getView: booked " + mBookingTimeJDO.getDisplayTime());
        }
        return lGridView;
    }

    @Override
    public int getCount() {
        return mBookingTimeJDOArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
