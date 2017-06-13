package com.example.user.cabbookingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.jdo.RouteJDO;

import java.util.ArrayList;

/**
 * Created by user on 13/06/17.
 */

public class LocationAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<RouteJDO> mArrayList;
    TextView mRouteTextView;
    public LocationAdapter(Context pContext, ArrayList<RouteJDO> pArrayList) {

        mContext=pContext;
        mArrayList=pArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View lRoutView= LayoutInflater.from(mContext).inflate(R.layout.route_layout,parent,false);
        mRouteTextView=(TextView) lRoutView.findViewById(R.id.routeTextView);
        mRouteTextView.setText(mArrayList.get(position).getRoutName());
        return lRoutView;
    }


    @Override
    public int getCount() {
        return mArrayList.size();
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