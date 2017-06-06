package com.example.user.cabbookingapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.ui.BookCabActivity;

/**
 * Created by user on 05/06/17.
 */

public class GridViewAdapter extends BaseAdapter {

    Context mContext;
    String[] mTimingString;

    public GridViewAdapter(Context mContext, String[] mTimingString) {
        this.mContext = mContext;
        this.mTimingString = mTimingString;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View lGridView;
        LayoutInflater lLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView lTimingButton;
        if (convertView == null) {
            lGridView = new View(mContext);
            lGridView = lLayoutInflater.inflate(R.layout.timing_layout, null);
        } else {
            lGridView = convertView;
        }
        lTimingButton = (TextView) lGridView.findViewById(R.id.timingTextView);
        lTimingButton.setText(mTimingString[position]);
        lTimingButton.setBackgroundResource(R.drawable.availble_timing);
        lTimingButton.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        return lGridView;
    }

    @Override
    public int getCount() {
        return mTimingString.length;
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
