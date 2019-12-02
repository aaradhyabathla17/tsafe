package com.techespo.tsafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.techespo.tsafe.Utils.showProgress;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPhoneNo;
    private EditText etCPassword;
    private EditText etName;
    private TextView tvAHAcc;
    private ArrayAdapter<String> defaultAdapter;
    private Spinner spinner;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etName = (EditText) findViewById(R.id.et_username);
        etPhoneNo = (EditText) findViewById(R.id.et_phoneno);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCPassword = (EditText) findViewById(R.id.et_cpassword);
        tvAHAcc = (TextView) findViewById(R.id.t_ahacc);
        tvAHAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        Button signup = (Button) findViewById(R.id.btn_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidateCheck()) {
                    saveUserData();
                }
            }
        });
        final String[] arrayOfGender = {"Other", "Male", "Female", "Select Gender"};
        defaultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayOfGender);
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(defaultAdapter);
        spinner.setSelection(3);
    }

    private void saveUserData() {
        mAuth = FirebaseAuth.getInstance();
       /* MyDBHelper myDBHelper = new MyDBHelper(this);
        myDBHelper.openDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", etName.getText().toString());
        values.put("Email", etEmail.getText().toString());
        values.put("PhoneNo", etPhoneNo.getText().toString());
        values.put("Password", etPassword.getText().toString());
        values.put("Gender", spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString());
        long res = myDBHelper.insert("Users", values);

        myDBHelper.close();*/
        createAccount(etEmail.getText().toString(), etPassword.getText().toString());
    }

    private boolean isValidateCheck() {
        if (etName.getText().toString() == null || etName.getText().toString().trim().length() <= 0) {
            etName.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }
        if (etEmail.getText().toString() == null || etEmail.getText().toString().trim().length() <= 0) {
            etEmail.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }
        if (etPassword.getText().toString() == null || etPassword.getText().toString().trim().length() <= 0) {
            etPassword.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }

        if (etCPassword.getText().toString() == null || etCPassword.getText().toString().trim().length() <= 0) {
            etCPassword.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }
        if (etCPassword.getText().toString().trim().equals(etPassword.getText().toString().trim()) == false) {
            etCPassword.setError(getResources().getString(R.string.incorrect_match));
            return false;
        }
        return true;
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        final ProgressDialog pd = showProgress(this);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("T-safe", "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "User created successfully.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("T-safe", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        pd.dismiss();
                    }
                });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etName.getText().toString())
                    .setPhotoUri(Uri.parse(etPhoneNo.getText().toString()))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("T-Signup", "User profile updated.");
                            }
                        }
                    });
        }
    }
}
