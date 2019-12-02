package com.techespo.tsafe.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.techespo.tsafe.Database.MyDBHelper;
import com.techespo.tsafe.MapsActivity;
import com.techespo.tsafe.R;
import com.techespo.tsafe.StartRideActivityOne;
import com.techespo.tsafe.adapter.AddViewPagerAdapter;

public class OngoingRideFragment extends Fragment {
    private TextView txtFrom, txtTo;
    private String fromAddress, toAddress;
    private int id;
    private LinearLayout ll_ongoing_ride;

    ViewPager viewPager;
    int images[] = {R.drawable.slider_img_one, R.drawable.slider_img_three, R.drawable.tracking_image_two, R.drawable.slider_img_one};
    AddViewPagerAdapter myCustomPagerAdapter;
    private Button btnNewRide;

    public OngoingRideFragment() {
        // Required empty public constructor
    }

    public static aboutFragment newInstance(String param1, String param2) {
        aboutFragment fragment = new aboutFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ongoing_fragment, container, false);

        txtFrom = (TextView) view.findViewById(R.id.txt_from);
        txtTo = (TextView) view.findViewById(R.id.txt_to);
        ll_ongoing_ride = (LinearLayout) view.findViewById(R.id.ll_ongoing_ride);
        viewPager = (ViewPager)view.findViewById(R.id.vp_add);
        btnNewRide = (Button) view.findViewById(R.id.startNewRide_home);
        btnNewRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StartRideActivityOne.class);
                startActivity(intent);

            }
        });

        myCustomPagerAdapter = new AddViewPagerAdapter(getActivity(), images);
        viewPager.setAdapter(myCustomPagerAdapter);

        Button btnTrack = (Button) view.findViewById(R.id.btn_track);
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("for", "track");
                intent.putExtra("from_address", fromAddress);
                intent.putExtra("to_address", toAddress);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        getUserDataFromDataBase();
        return view;
    }

    public void getUserDataFromDataBase() {
        MyDBHelper myDBHelper = new MyDBHelper(getActivity());
        myDBHelper.openDatabase();
        Cursor result = myDBHelper.getCurrentRide();
        if (result == null) {
            ll_ongoing_ride.setVisibility(View.GONE);
            btnNewRide.setVisibility(View.VISIBLE);
        //    Toast.makeText(getActivity(), "Sorry, no data found.", Toast.LENGTH_LONG).show();
        } else if (result.moveToFirst()) {
            ll_ongoing_ride.setVisibility(View.VISIBLE);
            btnNewRide.setVisibility(View.GONE);
            try {
                txtTo.setText(result.getInt(0));
                txtFrom.setText(result.getInt(0));
            } catch (Exception e) {
                ll_ongoing_ride.setVisibility(View.GONE);
                btnNewRide.setVisibility(View.VISIBLE);
             //   Toast.makeText(getActivity(), "Sorry, no data found.", Toast.LENGTH_LONG).show();
            }
        } else {
            ll_ongoing_ride.setVisibility(View.GONE);
            btnNewRide.setVisibility(View.VISIBLE);
          //  Toast.makeText(getActivity(), "Sorry, no data found.", Toast.LENGTH_LONG).show();
        }
        myDBHelper.Mydb.close();
    }
}
