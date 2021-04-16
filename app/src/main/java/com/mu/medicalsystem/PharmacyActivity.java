package com.mu.medicalsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PharmacyActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap  mMap;

    private LocationManager locationManager;
    private android.location.LocationListener myLocationListener;
    Location location;
    ArrayList<Pharmacy> pharmacy_list = new ArrayList<>();
    boolean loaded = false;
    public static Pharmacy selected_pharmacy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(PharmacyActivity.this);
        checkLocation();

    }

    @SuppressLint("NewApi")
    public void checkLocation() {

        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }


        myLocationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location locationListener) {

                if (isGPSEnabled(PharmacyActivity.this)) {
                    if (locationListener != null) {
                        if (ActivityCompat.checkSelfPermission(PharmacyActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PharmacyActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                } else if (isInternetConnected(PharmacyActivity.this)) {
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if(location!=null && !loaded){
                    loaded = !loaded;
                    loadData();
                }else{
                   // Toast.makeText(PharmacyActivity.this, "Unable to extract Your location", Toast.LENGTH_SHORT).show();
                }



            }

            public void onProviderDisabled(String provider) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, myLocationListener);
    }
    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }
    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(PharmacyActivity.this, "Marker Clicked", Toast.LENGTH_SHORT).show();
                for (int i=0;i<pharmacy_list.size();i++){
                    Log.d("MyLocation", pharmacy_list.get(i).lat+"-----"+marker.getPosition().latitude+"");
                    if(pharmacy_list.get(i).lat.equals(marker.getPosition().latitude+"")  && (marker.getPosition().longitude+"").equals(pharmacy_list.get(i).lng)){
                        selected_pharmacy = pharmacy_list.get(i);
                        startActivity(new Intent(PharmacyActivity.this, com.mu.medicalsystem.Pharmacy.class));
                    }
                }
                return true;
            }
        });
        // Add a marker in Sydney and move the camera
    }

    void loadData(){
        String url = Constants.root+"pharm.php?key="+getIntent().getStringExtra("pharm")+"&lat="+location.getLongitude()+"&lon="+location.getLatitude();
        JsonObjectRequest requestForChangePass = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray history = response.getJSONArray("prescription");
                    pharmacy_list = new ArrayList<>();
                    for (int i=0;i<history.length();i++){
                        JSONObject obj = history.getJSONObject(i);
                        Pharmacy pres = new Pharmacy();
                        pres.id = obj.getString("id");
                        pres.name = obj.getString("name");
                        pres.email = obj.getString("email");
                        pres.phoneNumber = obj.getString("phoneNumber");
                        pres.address = obj.getString("address");
                        pres.lat = obj.getString("lng");
                        pres.lng = obj.getString("lat");
                        pharmacy_list.add(pres);
                    }

                    if (mMap!=null){
                        for (int i=0;i<pharmacy_list.size();i++){
                            LatLng sydney = new LatLng(Double.parseDouble(pharmacy_list.get(i).lat), Double.parseDouble(pharmacy_list.get(i).lng));
                            mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title(pharmacy_list.get(i).name));
                        }
                        LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 13));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PharmacyActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        Volley.newRequestQueue(PharmacyActivity.this).add(requestForChangePass);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        finish();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    class Pharmacy{
        String id, name,email,phoneNumber,address,lat,lng;
    }
}