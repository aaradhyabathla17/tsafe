package com.techespo.tsafe;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techespo.tsafe.Model.Trip;
import com.techespo.tsafe.Model.TripLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int User_Location_Code = 99;
    public Location lastlocation;
    public LatLng finalLocation;
    private Marker currentUserLocationMarker;
    Button SOS;
    TextView tv_start_stop_switch;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    DatabaseReference mDatabase;

    private Trip trip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        trip = (Trip) getIntent().getExtras().getSerializable("trip");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDatabase.child("TripLocation").child(trip.getTripId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TripLocation tripLocation = dataSnapshot.getValue(TripLocation.class);
                if (tripLocation != null) {
                    try {
                        mMap.clear();
                        LatLng latLng = new LatLng(Double.parseDouble(tripLocation.getLati()),
                                Double.parseDouble(tripLocation.getLongi()));
                        MarkerOptions markerOptionsStartPosition = new MarkerOptions();
                        markerOptionsStartPosition.position(latLng);
                        markerOptionsStartPosition.title("User Current Location");
                        markerOptionsStartPosition.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));
                        currentUserLocationMarker = mMap.addMarker(markerOptionsStartPosition);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomBy(8));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
        mMap.setMyLocationEnabled(true);
    }
}