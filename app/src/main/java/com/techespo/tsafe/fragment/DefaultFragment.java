package com.techespo.tsafe.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techespo.tsafe.Model.Trip;
import com.techespo.tsafe.R;
import com.techespo.tsafe.Services.LocationService;
import com.techespo.tsafe.StartRideActivityOne;
import com.techespo.tsafe.adapter.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/*
 * to handle interaction events.
 * Use the {@link DefaultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultFragment extends Fragment implements OnMapReadyCallback {//}, LocationListener, GoogleApiClient.ConnectionCallbacks,
    //GoogleApiClient.OnConnectionFailedListener {
    Button CreateNewRide, btn_stopride;
    //private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final int User_Location_Code = 99;
    //public Location lastlocation;
    private Marker currentUserLocationMarker;
    private View rootView;
    GoogleMap myMap;
    MapView mMapView;
    private static final int SMS_REQUEST_CODE = 1008;
    private String contactOneName, contactTwoName, contactOnePhone, contactTwoPhone,
            imageOnePath, imgPathTwo, fromAddress, towAddress, time, fromatedTime;
    DatabaseReference reference;
    SharedPreferences splogin;
    SharedPreferences spOngoingTrip;
    Trip trip;
    String ongoingTripKey;
    String userId;
    String endTime;

    ArrayList markerPoints = new ArrayList();

    public DefaultFragment() {
    }

    public static DefaultFragment newInstance() {
        DefaultFragment fragment = new DefaultFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    private void sendSMS(String contactOne, String contactTwo, String vehicleno, String start,String end ) {

        String Message = "Your friend "+ splogin.getString("Name","")
                +" ended journey with"+"\n"+ "vehicle no: "+vehicleno
                +"\n"+ "start point: "+start+"\n"+" end point: "+end ;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contactOne, null, Message, null, null);
        Toast.makeText(getActivity(), "Message Sent one", Toast.LENGTH_SHORT).show();
        SmsManager smsManagerForTwo = SmsManager.getDefault();
        smsManagerForTwo.sendTextMessage(contactTwo, null, Message, null, null);
        Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_SHORT).show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        try {

            splogin = getActivity().getSharedPreferences("firstlogin", 0);
            spOngoingTrip = getActivity().getSharedPreferences("OnGoingRide", 0);
            userId = splogin.getString("id", "");
            reference = FirebaseDatabase.getInstance().getReference();
            rootView = inflater.inflate(R.layout.fragment_default, container, false);
            MapsInitializer.initialize(getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

            CreateNewRide = (Button) rootView.findViewById(R.id.btn_NewRide);
            btn_stopride = (Button) rootView.findViewById(R.id.btn_stopride);
            if (spOngoingTrip.getBoolean("ongoingtrip", false)) {
                CreateNewRide.setVisibility(View.INVISIBLE);
                btn_stopride.setVisibility(View.VISIBLE);
            } else {
                CreateNewRide.setVisibility(View.VISIBLE);
                btn_stopride.setVisibility(View.INVISIBLE);
            }
            CreateNewRide.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StartRideActivityOne.class);
                    startActivity(intent);
                }
            });
            btn_stopride.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Do you want to stop ride?");
                    builder.setIcon(R.mipmap.app_logo);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            try {
                                if (trip != null) {
                                    trip.setTripstatus(1);
                                    reference.child("trips").child(trip.getTripId()).setValue(trip);
                                }
                                SharedPreferences.Editor editor = spOngoingTrip.edit();
                                editor.putBoolean("ongoingtrip", false);
                                editor.commit();
                                CreateNewRide.setVisibility(View.VISIBLE);
                                btn_stopride.setVisibility(View.INVISIBLE);
                                Intent serviceIntent = new Intent(getActivity(), LocationService.class);
                                getActivity().stopService(serviceIntent);
                                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (getActivity().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                            }else {
                                SharedPreferences spOngoingTrip = getActivity().getSharedPreferences("OnGoingRide", 0);
                                sendSMS(spOngoingTrip.getString("contactOne",null), spOngoingTrip.getString("contactTwo",null), spOngoingTrip.getString("vehicleno","12344"), spOngoingTrip.getString("from_address",null), spOngoingTrip.getString("to_address",null));
                            }

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });


            trip = new Trip();
            Query query = reference.child("trips").orderByChild("tripstatus").equalTo(0);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (userId.trim().equalsIgnoreCase(data.getValue(Trip.class).getUserid().trim())) {
                                trip = data.getValue(Trip.class);
                                ongoingTripKey = data.getKey();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Getting trip list error ");
                }
            });

        } catch (InflateException e) {
            Log.e(TAG, "Inflate exception");
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        try {
            if (spOngoingTrip.getBoolean("ongoingtrip", false)) {
                CreateNewRide.setVisibility(View.INVISIBLE);
                btn_stopride.setVisibility(View.VISIBLE);
            } else {
                CreateNewRide.setVisibility(View.VISIBLE);
                btn_stopride.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            myMap.clear();
                            LatLng latLng = new LatLng(Double.parseDouble(latitude),
                                    Double.parseDouble(longitude));
                            MarkerOptions markerOptionsStartPosition = new MarkerOptions();
                            markerOptionsStartPosition.position(latLng);
                            markerOptionsStartPosition.title("User Current Location");
                            markerOptionsStartPosition.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));
                            currentUserLocationMarker = myMap.addMarker(markerOptionsStartPosition);
                            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            myMap.animateCamera(CameraUpdateFactory.zoomBy(16));
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkUserLocationPermission();

        }else
        {
            myMap.setMyLocationEnabled(true);
        }
        /*LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    myMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                myMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // buildGoogleAPIClient();
            myMap.setMyLocationEnabled(true);
        }*/
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, User_Location_Code);
            }
            return false;
        }
        return true;
    }

  /*  protected synchronized void buildGoogleAPIClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

    }*/

    /*@Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentUserLocationMarker = myMap.addMarker(markerOptions);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        myMap.animateCamera(CameraUpdateFactory.zoomBy(12));
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //if (googleApiClient == null) {
                        //    buildGoogleAPIClient();
                        //}
                        myMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

/*
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null)
                myMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=AIzaSyDNfbrmunFm-6J-ujtkaWzdwggkM-yzpRI";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
