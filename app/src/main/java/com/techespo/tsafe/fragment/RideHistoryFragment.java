package com.techespo.tsafe.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techespo.tsafe.Model.Trip;
import com.techespo.tsafe.R;

import java.util.ArrayList;

public class RideHistoryFragment extends Fragment {

    ArrayList<RideInfo> list;
    RideHistoryAdapter rideHistoryAdapter;
    ListView listView;
    SharedPreferences splogin;

    public RideHistoryFragment() {
        // Required empty public constructor
    }

    public static RideHistoryFragment newInstance() {
        RideHistoryFragment fragment = new RideHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_ride_history, container, false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        splogin = getActivity().getSharedPreferences("firstlogin", 0);
        list = new ArrayList<>();
        listView = (ListView) rootview.findViewById(R.id.ride_history);
        Query query = reference.child("trips");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Trip trip = data.getValue(Trip.class);
                        if (trip.getTripstatus() == 0) {
                            if (splogin.getString("id", "").trim().equalsIgnoreCase(trip.getUserid().trim())) {
                                RideInfo rideInfo = new RideInfo();
                                rideInfo.setDate(trip.getTripstarttime());
                                rideInfo.setStartTime(trip.getTripstarttime());
                                rideInfo.setReachTime(trip.getTripstarttime());
                                rideInfo.setInitialLoc(trip.getStartaddress());
                                rideInfo.setFinalLoc(trip.getEndaddress());
                                list.add(rideInfo);
                            }
                        }
                    }
                    if (list != null && list.size() > 0) {
                        rideHistoryAdapter = new RideHistoryAdapter(list, getActivity());
                        listView.setAdapter(rideHistoryAdapter);
                        listView.setVisibility(View.VISIBLE);
                    } else
                        listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Getting trip list error ");
            }
        });
        return rootview;
    }
}
