package com.techespo.tsafe;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techespo.tsafe.Model.Trip;
import com.techespo.tsafe.Services.LocationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StartRideActivityOne extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE_FROM = 123;
    private static final int AUTOCOMPLETE_REQUEST_CODE_TO = 124;

    private TextView txtContact1, txtContact2;
    private final int RESULT_PICK_CONTACT1 = 101, RESULT_PICK_CONTACT2 = 102;
    ArrayList<String> contact_list = new ArrayList<String>();

    private static final int CAMERA_REQUEST_ONE = 1888;
    private static final int CAMERA_REQUEST_TWO = 1889;
    private static final int SMS_REQUEST = 1008;
    private static final int SMS_REQUEST_CODE = 1008;

    private static final int REQUEST_CODE_AUTOCOMPLETE_ADDRESS_ONE = 1900;
    private static final int REQUEST_CODE_AUTOCOMPLETE_ADDRESS_TWO = 1901;
    private ImageView imageView, img_one, img_two;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String contactOneName, contactTwoName, contactOnePhone, contactTwoPhone,
            imageOnePath, imgPathTwo, fromAddress, towAddress, time, fromatedTime;
    private EditText etSourceLocation, etDestination,vehicle_no;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyDNfbrmunFm-6J-ujtkaWzdwggkM-yzpRI";

    private DatabaseReference mDatabase;
    SharedPreferences splogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), GOOGLE_PLACES_API_KEY);

        setContentView(R.layout.activity_start_ride_one);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        splogin = getSharedPreferences("firstlogin", 0);

        txtContact1 = (TextView) findViewById(R.id.contact1);
        txtContact2 = (TextView) findViewById(R.id.contact2);
        img_one = (ImageView) findViewById(R.id.img_one);
        img_two = (ImageView) findViewById(R.id.img_two);
        etDestination = (EditText) findViewById(R.id.destination);
        etSourceLocation = (EditText) findViewById(R.id.source_location);
        vehicle_no = (EditText) findViewById(R.id.vehicle_no);

        ((ImageView) findViewById(R.id.id_img_from_location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createAutoCompleteIntentFromFromLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((ImageView) findViewById(R.id.id_img_to_location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAutoCompleteIntentToLocation();
            }
        });
        ((Button) findViewById(R.id.btn_img_one)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_ONE);
                }
            }
        });
        ((Button) findViewById(R.id.btn_img_two)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_TWO);
                    }
                }
            }
        });

        ((Button) findViewById(R.id.btn_start)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    fromAddress = etSourceLocation.getText().toString();
                    towAddress = etDestination.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = new Date();
                    SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                    fromatedTime = sdfs.format(d);
                    String date = sdf.format(d);
                    time = String.valueOf(d.getTime());
                    splogin = getSharedPreferences("firstlogin", 0);
                    String userId = splogin.getString("id", "");

                    SharedPreferences spOngoingTrip = getSharedPreferences("OnGoingRide", 0);
                    SharedPreferences.Editor editor = spOngoingTrip.edit();
                    editor.putString("contactOne", contactOnePhone);
                    editor.putString("contactTwo", contactTwoPhone);
                    editor.putString("contactOneName", contactOneName);
                    editor.putString("contactTwoName", contactTwoName);
                    editor.putString("from_address", fromAddress);
                    editor.putString("to_address", towAddress);
                    editor.putString("vehicleno", vehicle_no.getText().toString());
                    editor.putBoolean("ongoingtrip", true);
                    editor.commit();

                    Trip trip = new Trip();
                    trip.setContactone(contactOnePhone);
                    trip.setContacttwo(contactTwoPhone);
                    trip.setEndaddress(towAddress);
                    trip.setStartaddress(fromAddress);
                    trip.setUserid(userId);
                    trip.setTripstarttime(date+" "+time);
                    trip.setTripstatus(0);
                    trip.setTripId(time);
                    trip.setVehicleno(vehicle_no.getText().toString());

                    mDatabase.child("trips").child(time).setValue(trip);
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                    }else {
                        sendSMS(contactOnePhone, contactTwoPhone, vehicle_no.getText().toString(), fromAddress, towAddress, fromatedTime);
                        Intent serviceIntent = new Intent(StartRideActivityOne.this, LocationService.class);
                        serviceIntent.putExtra("tripid",time);
                        startService(serviceIntent);
                        finish();
                    }

                    //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            //Uri.parse("http://maps.google.com/maps?saddr=" + splogin.getString("fromLatLong", "20.344,34.34") + "&daddr=" + splogin.getString("toLatLong", "20.5666,45.345")));
                    //startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        ((ImageView) findViewById(R.id.id_img_contact_one)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT1);
            }
        });
        ((ImageView) findViewById(R.id.id_img_contact_two)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT2);
            }
        });
    }

    public void onSearchCalled() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences spOngoingTrip = getSharedPreferences("OnGoingRide", 0);
        SharedPreferences.Editor editor = spOngoingTrip.edit();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT1:
                    contactPicked1(data);
                    break;
                case RESULT_PICK_CONTACT2:
                    contactPicked2(data);
                    break;
                case CAMERA_REQUEST_ONE:
                    Bitmap photoOne = (Bitmap) data.getExtras().get("data");
                    img_one.setImageBitmap(photoOne);
                    break;
                case CAMERA_REQUEST_TWO:
                    Bitmap photoTwo = (Bitmap) data.getExtras().get("data");
                    img_two.setImageBitmap(photoTwo);
                    break;
            }
        } else {
            Log.e("SetupActivity", "Failed to pick contact");
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_FROM) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());
                editor.putString("fromLatLong", String.valueOf(place.getLatLng()));
                //tvLocationName.setText(place.getName());
                //tvPlaceId.setText(place.getId());
                //tvLatLon.setText(String.valueOf(place.getLatLng()));
                etSourceLocation.setText(place.getName());
                fromAddress = String.valueOf(place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_TO) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());
                editor.putString("toLatLong", String.valueOf(place.getLatLng()));
                //tvLocationName.setText(place.getName());
                //tvPlaceId.setText(place.getId());
                //tvLatLon.setText(String.valueOf(place.getLatLng()));
                etDestination.setText(place.getName());
                towAddress = String.valueOf(place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        editor.commit();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void contactPicked1(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            contactOneName = cursor.getString(nameIndex);
            contactOnePhone = cursor.getString(phoneIndex);
            txtContact1.setText(contactOneName + "\n" + contactOnePhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contactPicked2(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            contactTwoName = cursor.getString(nameIndex);
            contactTwoPhone = cursor.getString(phoneIndex);
            txtContact2.setText(contactTwoName + "\n" + contactTwoPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_ONE);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == SMS_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(contactOnePhone, contactTwoPhone, vehicle_no.getText().toString(), fromAddress, towAddress, time);
                Intent serviceIntent = new Intent(StartRideActivityOne.this, LocationService.class);
                startService(serviceIntent);
                finish();
            }else{
                Toast.makeText(this, "Send SMS permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createAutoCompleteIntentFromFromLocation() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(StartRideActivityOne.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_FROM);
    }

    private void createAutoCompleteIntentToLocation() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(StartRideActivityOne.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_TO);
    }
    private void sendSMS(String contactOne, String contactTwo, String vehicleno, String start,String end ,String time) {
        String Message = "Your friend "+splogin.getString("Name","")
                +" started journey vehicle no:"+vehicleno
                + ",start point:"+start+",end point:"+end +",time:"+time;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contactOne, null, Message, null, null);
        Toast.makeText(StartRideActivityOne.this, "Message Sent one", Toast.LENGTH_SHORT).show();
        SmsManager smsManagerForTwo = SmsManager.getDefault();
        smsManagerForTwo.sendTextMessage(contactTwo, null, Message, null, null);
        Toast.makeText(StartRideActivityOne.this, "Message Sent", Toast.LENGTH_SHORT).show();

    }
}
