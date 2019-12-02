package com.techespo.tsafe.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.techespo.tsafe.Database.MyDBHelper;
import com.techespo.tsafe.R;


public class ChangePasswordFragment extends Fragment {

    EditText OldPassword;
    EditText NewPassword;
    EditText ConfirmNewPassword;
    Button Save;
    MyDBHelper myDBHelper = new MyDBHelper(getActivity());
    private String mParam1;
    private String mParam2;
    private  static final String ARG_PARAM1="param1" ;
    private  static final String ARG_PARAM2 = "param2";
    private  static final String ARG_PARAM3 = "param2";
    public   static final String ARG_PARAM4 = "param2";
    private static final String ARG_PARAM5 = "param2";


    
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2,String param3,String param4,String param5){
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,param1);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v= inflater.inflate(R.layout.fragment_change_password, container, false);
       NewPassword=(EditText) v.findViewById(R.id.et_new_password);
       OldPassword=(EditText) v.findViewById(R.id.et_old_password);
       ConfirmNewPassword=(EditText) v.findViewById(R.id.et_confirm_new_password);
       Save=(Button) v.findViewById(R.id.btn_save_password_change);
       Save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (isvalidate()){

             /*  myDBHelper.openDatabase();*/
                      MyDBHelper myDBHelper = new MyDBHelper(getActivity());
                       myDBHelper.openDatabase();
                       ContentValues values = new ContentValues();
                       values.put("Password", NewPassword.getText().toString());
                       long res = myDBHelper.updateRecord("Users", values,ARG_PARAM1);
                       myDBHelper.close();



             /*      myDBHelper.updateOneColumn("Users", , ARG_PARAM1, "Password", NewPassword.getText().toString());
               myDBHelper.close();*/
               getFragmentManager().beginTransaction().replace(R.id.contanerFragment, SettingsFragment.newInstance(ARG_PARAM1,ARG_PARAM2,ARG_PARAM3,ARG_PARAM4,ARG_PARAM5)).commit();
           }
       }
       });
       return v;
    }
    public boolean isvalidate() {
        if(OldPassword.getText().toString().equals(ARG_PARAM3)==false)
        {
            Toast.makeText(getActivity(),"INCORRECT OLD PASSWORD",Toast.LENGTH_SHORT).show();
            OldPassword.setError("Incorrect Old Password");
            return false;
        }


        if (NewPassword.getText().toString().trim().equals(ConfirmNewPassword.getText().toString().trim()) == false) {
            Toast.makeText(getActivity(),"INCORRECT OLD PASSWORD",Toast.LENGTH_SHORT).show();
            ConfirmNewPassword.setError("Password does not match");
            return false;
        }
        return true;
    }
}
