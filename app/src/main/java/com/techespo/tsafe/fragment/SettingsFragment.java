package com.techespo.tsafe.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.techespo.tsafe.R;


public class SettingsFragment extends Fragment  {
    Button ChangePassword;
    Button EditProfile;
    Button DeleteAccount;
    private String mParam1;
    private String mParam2;
    private  static final String KEY_VAR_ID="id" ;
    private  static final String ARG_PARAM2 = "param2";
    private  static final String ARG_PARAM3 = "param2";
    public   static final String ARG_PARAM4 = "param2";
    private static final String ARG_PARAM5 = "param2";


    // TODO: Rename and change types of parameters

    public static SettingsFragment newInstance(String param1, String param2,String param3,String param4,String param5) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_VAR_ID,param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(KEY_VAR_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        EditProfile=(Button)v.findViewById(R.id.btn_edit_profile);
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.contanerFragment,new ProfileFragment()).commit();
            }
        });
        DeleteAccount=(Button) v.findViewById(R.id.btn_delete_account);
        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.contanerFragment,new DeleteAccountFragment()).commit();
            }
        });
        ChangePassword=(Button) v.findViewById(R.id.btn_change_password);
        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.contanerFragment,  ChangePasswordFragment.newInstance(KEY_VAR_ID,ARG_PARAM2,ARG_PARAM3,ARG_PARAM4,ARG_PARAM5)).commit();
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event

}
