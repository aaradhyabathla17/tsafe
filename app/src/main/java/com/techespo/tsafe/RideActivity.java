package com.techespo.tsafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RideActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int Location_Request = 500;
    ArrayList<LatLng> listpoints;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listpoints=new ArrayList<>();
    }


    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(RideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(RideActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(listpoints.size()==2)
                {
                    listpoints.clear();
                    mMap.clear();
                }
                //create marker
                    listpoints.add(latLng);
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(latLng);

                if(listpoints.size()==1)
                {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                }
                else
                {
                    //create 2nd marker
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);
                //TODO:request get direction code
                if(listpoints.size()==2)
                {
                    String url=getRequestUrl(listpoints.get(0),listpoints.get(1));
                }

            }

            private String getRequestUrl(LatLng origin, LatLng desti) {
             String str_org="origin="+origin.latitude+","+origin.longitude;
             String str_dest="destination="+desti.latitude+","+desti.longitude;
             String sensor="sensor=false";
             String mode="mode=driving";
             String param=str_org+"&"+str_dest+"&"+sensor+"&"+mode;
             String output="json";
             String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;
             return url;
            }
        });

    }
    public String requestDirection(String reqUrl) throws IOException {
      String response="";
        InputStream inputStream=null;
        HttpsURLConnection httpsURLConnection=null;
        try{
            URL url=new URL(reqUrl);
            httpsURLConnection=(HttpsURLConnection)url.openConnection();
            httpsURLConnection.connect();
            //get response result
            inputStream=httpsURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            StringBuffer stringbuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {
               stringbuffer.append(line);
            }
            response=stringbuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpsURLConnection.disconnect();

        }

      return response;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Location_Request:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);
               }
                break;
       }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
