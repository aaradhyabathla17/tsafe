package com.techespo.tsafe.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techespo.tsafe.R;

import java.util.ArrayList;

public class RideHistoryAdapter extends BaseAdapter {
    private ArrayList<RideInfo> list;
    private Context context;

    public RideHistoryAdapter(ArrayList<RideInfo> list,Context c)
    {
        this.list=list;
        this.context=c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ride_history_row, null);
        }
        final TextView date=(TextView) v.findViewById(R.id.date);
        date.setText(list.get(position).getDate());
        final TextView startTime=(TextView) v.findViewById(R.id.start_time);
        date.setText(list.get(position).getStartTime());

        final TextView reachTime=(TextView) v.findViewById(R.id.reach_time);
        date.setText(list.get(position).getReachTime());
        final TextView startLoc=(TextView) v.findViewById(R.id.initial_location);
        date.setText(list.get(position).getInitialLoc());
        final TextView finalLoc=(TextView) v.findViewById(R.id.final_location);
        date.setText(list.get(position).getFinalLoc());

        return v;
    }
}
