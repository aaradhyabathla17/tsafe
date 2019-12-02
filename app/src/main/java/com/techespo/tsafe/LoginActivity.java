package com.techespo.tsafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.techespo.tsafe.Utils.showProgress;

public class LoginActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button Login;
    public TextView NewUser;
    private TextView ForgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NewUser = (TextView) findViewById(R.id.t_signup);
        NewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        init();

    }

    private void init() {
        Username = (EditText) findViewById(R.id.et_username);
        Password = (EditText) findViewById(R.id.et_password);
        Login = (Button) findViewById(R.id.btn_login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidateCheck()) {
                    getUserDataFromDataBase();
                }
            }
        });
    }

    private boolean isValidateCheck() {
        if (Username.getText().toString() == null || Username.getText().toString().trim().length() == 0) {
            Password.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }
        if (Password.getText().toString() == null || Password.getText().toString().trim().length() <= 0) {
            Password.setError(getResources().getString(R.string.empty_field_message));
            return false;
        }
        return true;
    }

    public void getUserDataFromDataBase() {
       /* SharedPreferences splogin = getSharedPreferences("firstlogin", 0);
        MyDBHelper myDBHelper = new MyDBHelper(this);
        myDBHelper.openDatabase();
        Cursor result = myDBHelper.getValidateUserData(Username.getText().toString(), Password.getText().toString());
        if (result == null) {
            Toast.makeText(this, "invalid username or password", Toast.LENGTH_LONG).show();
        } else if (result.moveToFirst()) {

            SharedPreferences.Editor editor = splogin.edit();
            editor.putInt("id", result.getInt(0));
            editor.putString("Name", result.getString(1));
            editor.putString("PhoneNo", result.getString(2));
            editor.putString("Email", result.getString(3));
            editor.putString("Gender", result.getString(5));
            editor.putString("Password", result.getString(4));
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
            intent.putExtra("id", result.getInt(0));
            intent.putExtra("Name", result.getString(1));
            intent.putExtra("Email", result.getInt(3));
            intent.putExtra("Password", result.getInt(4));
            intent.putExtra("Gender", result.getInt(5));
            intent.putExtra("PhoneNo", result.getInt(2));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_LONG);
        }
        myDBHelper.Mydb.close();*/
        signIn(Username.getText().toString(), Password.getText().toString());
    }

    private void signIn(String email, final String password) {
       final  ProgressDialog pd= showProgress(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("T-Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences splogin = getSharedPreferences("firstlogin", 0);
                            SharedPreferences.Editor editor=splogin.edit();
                            editor.putString("id",user.getUid());
                            editor.putString("Name",user.getDisplayName());
                            editor.putString("PhoneNo",user.getPhoneNumber());
                            editor.putString("Email",user.getEmail());
                            editor.putString("Gender","Male");
                            editor.putString("Password",password);
                            editor.putBoolean("isloginalready", true);
                            editor.commit();
                            Intent intent=new Intent(LoginActivity.this,HomePageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("T-Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                        }
                        pd.dismiss();
                    }
                });
    }
}