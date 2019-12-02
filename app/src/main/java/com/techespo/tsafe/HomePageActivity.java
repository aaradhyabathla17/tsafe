package com.techespo.tsafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.techespo.tsafe.Model.Trip;
import com.techespo.tsafe.fragment.ContactUsFragment;
import com.techespo.tsafe.fragment.DefaultFragment;
import com.techespo.tsafe.fragment.DeleteAccountFragment;
import com.techespo.tsafe.fragment.ProfileFragment;
import com.techespo.tsafe.fragment.RideHistoryFragment;
import com.techespo.tsafe.fragment.SettingsFragment;
import com.techespo.tsafe.fragment.aboutFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.OnFragmentInteractionListener, DeleteAccountFragment.OnFragmentInteractionListener{
    //FloatingActionButton fab;
    //FloatingActionButton fab1;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int PoliceNumber = 100;
    private static final int WomebHelplineNumber = 1091;
    private static final int AmbulanceNumber = 101;
    private int MyNumber;
    private String userId;
    String Name, PhoneNo, Password, Email, Gender;
    SharedPreferences splogin;
    TextView tvName, tvEmail;
    CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private static final int SMS_REQUEST_CODE = 1008;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.overridePendingTransition(R.anim.left_to_right_animation,
                R.anim.right_to_left_anim);
        splogin = getSharedPreferences("firstlogin", 0);
        userId = splogin.getString("id", "");
        Name = splogin.getString("Name", "Dummy");
        PhoneNo = splogin.getString("PhoneNo", "");
        Password = splogin.getString("Password", "");
        Email = splogin.getString("email", "test@test.com");
        Gender = splogin.getString("gender", "");
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colour_white));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colour_white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        splogin = getSharedPreferences("firstlogin", 0);

        try {
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
            tvName = (TextView) headerView.findViewById(R.id.Username);
            tvName.setText(splogin.getString("Name", "Test"));
            tvEmail = (TextView) headerView.findViewById(R.id.textView_Email);
            tvEmail.setText(splogin.getString("Email", "Test@gmail.com"));
            profileImage = (CircleImageView) headerView.findViewById(R.id.imageViewProfile);
            String image = splogin.getString("imageProfile", null);
            profileImage.setImageBitmap(decodeBase64(image));

        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment, new DefaultFragment()).commit();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token Fail", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        Log.d("DOne", token);
                        Toast.makeText(HomePageActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
            //     new SettingsFragment().newInstance(String.valueOf(userId),Name,Password,PhoneNo,Email)).addToBackStack(null).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment, new RideHistoryFragment().newInstance()).addToBackStack(null).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
                    new ProfileFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
                    new RideHistoryFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
                    new aboutFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_women) {
            makephonecall(WomebHelplineNumber);
        } else if (id == R.id.nav_trips) {
            showOngoingTrips();
        } else if (id == R.id.nav_Police) {
            makephonecall(PoliceNumber);
        } else if (id == R.id.nav_Emergency) {
            //SharedPreferences onGoingRide = getSharedPreferences("OnGoingRide", 0);
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
            } else {
                SharedPreferences spOngoingTrip = getSharedPreferences("OnGoingRide", 0);
                sendSMS(spOngoingTrip.getString("contactOne", null), spOngoingTrip.getString("contactTwo", null), spOngoingTrip.getString("vehicleno", "12344"), spOngoingTrip.getString("from_address", null));
            }
//            makephonecall(Integer.parseInt(onGoingRide.getString("contactOne", "100")));
            //makephonecall(Integer.parseInt(onGoingRide.getString("contactTwo", "100")));
        }

        else if (id == R.id.nav_ambulance) {
            makephonecall(AmbulanceNumber);
        } else if (id == R.id.nav_contactUs) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
                    new ContactUsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_logout) {
            SharedPreferences splogin = getSharedPreferences("firstlogin", 0);
            SharedPreferences.Editor editor = splogin.edit();
            editor.clear();
            editor.commit();
            mAuth.signOut();
            Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_Settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contanerFragment,
                    new SettingsFragment().newInstance(String.valueOf(userId), Name, Password, PhoneNo, Email)).addToBackStack(null).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makephonecall(MyNumber);
            } else {
                Toast.makeText(HomePageActivity.this, "Reqest Denied", Toast.LENGTH_SHORT).show();
            }

        }
        if(requestCode==SMS_REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                SharedPreferences spOngoingTrip = getSharedPreferences("OnGoingRide", 0);
                sendSMS(spOngoingTrip.getString("contactOne", null), spOngoingTrip.getString("contactTwo", null), spOngoingTrip.getString("vehicleno", "12344"), spOngoingTrip.getString("from_address", null));
            }
            }
        }

    private void makephonecall(int mnumber) {
        MyNumber = mnumber;

        if (ContextCompat.checkSelfPermission(HomePageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            String dial = "tel:" + mnumber;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
            startActivity(intent);
        }
    }

    public void onFragmentInteraction() {
        splogin = getSharedPreferences("firstlogin", 0);
        Name = splogin.getString("Name", null);
        PhoneNo = splogin.getString("PhoneNo", null);
        Email = splogin.getString("Email", null);
        Gender = splogin.getString("Gender", null);
        tvName.setText(Name);
        tvEmail.setText(Email);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void sendSMS(String contactOne, String contactTwo, String vehicleno, String start ) {

        String Message = "Your friend "+ splogin.getString("Name","")
                +" is in danger "+"\n"+ "vehicle no: "+vehicleno
                +"\n"+ "start point: "+start+"\n"+"Please try to contact your friend as soon as possible with the last updated loaction of your friend" ;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contactOne, null, Message, null, null);
        Toast.makeText(HomePageActivity.this, "Message Sent one", Toast.LENGTH_SHORT).show();
        SmsManager smsManagerForTwo = SmsManager.getDefault();
        smsManagerForTwo.sendTextMessage(contactTwo, null, Message, null, null);
        Toast.makeText(HomePageActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

    }

    private void showOngoingTrips() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HomePageActivity.this, android.R.layout.select_dialog_singlechoice);
        final ArrayList<Trip> tripList = new ArrayList<Trip>();

        Query query = reference.child("trips");//.orderByChild("tripstatus").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Trip trip = data.getValue(Trip.class);
                        if (trip.getTripstatus() == 0) {
                            // if (PhoneNo.trim().equalsIgnoreCase(trip.getContactone().trim()) ||
                            //         PhoneNo.trim().equalsIgnoreCase(trip.getContacttwo().trim())) {
                            arrayAdapter.add("Trip:" + trip.getStartaddress() + "/" + trip.getEndaddress());
                            tripList.add(trip);
                            //}
                        }
                    }
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(HomePageActivity.this);
                    builderSingle.setIcon(R.mipmap.app_logo);
                    builderSingle.setTitle("Select One Name:-");
                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(HomePageActivity.this, MapsActivity.class);
                            intent.putExtra("trip", tripList.get(which));
                            startActivity(intent);
                           /* String strName = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(HomePageActivity.this);
                            builderInner.setMessage(strName);
                            builderInner.setTitle("Select trip to track.");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();*/
                        }
                    });
                    builderSingle.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Getting trip list error ");
            }
        });

    }
}
