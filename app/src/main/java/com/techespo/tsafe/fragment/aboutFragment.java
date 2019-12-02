package com.techespo.tsafe.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techespo.tsafe.R;


public class aboutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match



    public aboutFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
