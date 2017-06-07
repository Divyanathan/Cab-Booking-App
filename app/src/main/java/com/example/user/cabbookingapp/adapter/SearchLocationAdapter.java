package com.example.user.cabbookingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.cabbookingapp.R;
import com.example.user.cabbookingapp.jdo.RouteJDO;

import java.util.ArrayList;

/**
 * Created by user on 07/06/17.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.SearchLocationHolder> {


     Context mContext;
    ArrayList<RouteJDO> mLocationArrayList;

    public SearchLocationAdapter(Context mContext, ArrayList<RouteJDO> mLocationArrayList) {
        this.mContext = mContext;
        this.mLocationArrayList = mLocationArrayList;
    }

    class SearchLocationHolder extends RecyclerView.ViewHolder {

        TextView sLocationText;
        public SearchLocationHolder(View pSearchLocatoinView) {
            super(pSearchLocatoinView);
            sLocationText=(TextView) pSearchLocatoinView.findViewById(R.id.routeTextView);
        }
    }

    @Override
    public SearchLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View lSearLoactonView= LayoutInflater.from(mContext).inflate(R.layout.route_layout,parent,false);
        return new SearchLocationHolder(lSearLoactonView);
    }

    @Override
    public void onBindViewHolder(SearchLocationHolder pSearchLocatoinHolder, int pPosition) {

        pSearchLocatoinHolder.sLocationText.setText(mLocationArrayList.get(pPosition).getRoutName());

    }

    @Override
    public int getItemCount() {
        return mLocationArrayList.size();
    }


}
