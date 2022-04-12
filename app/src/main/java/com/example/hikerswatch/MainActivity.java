package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    TextView textLat, textLong, textAcc, textAlt, textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textLat = findViewById(R.id.textLatitude);
        textLong = findViewById(R.id.textLongitude);
        textAcc = findViewById(R.id.textAccuracy);
        textAddress = findViewById(R.id.textAddress);
        textAlt = findViewById(R.id.textAltitude);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {
            //Toast.makeText(MainActivity.this, "Hi! I'm updating..", Toast.LENGTH_SHORT).show();
            updateLocationInfo(location);
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, "Hi! I'm updating..1", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            //Toast.makeText(MainActivity.this, "Hi! I'm updating..2", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                //Toast.makeText(MainActivity.this, "Hi! I'm updating..3", Toast.LENGTH_SHORT).show();
                updateLocationInfo(lastKnownLocation);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, "Hi! I'm updating..4", Toast.LENGTH_SHORT).show();
            startListening();
        }
    }
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void updateLocationInfo (Location location) {
        textLat.setText("Latitude: " + Double.toString(location.getLatitude()));
        textLong.setText("Longitude: " + Double.toString(location.getLongitude()));
        textAcc.setText("Accuracy: " + Float.toString(location.getAccuracy()));
        textAlt.setText("Altitude: " + Float.toString((float) location.getAltitude()));
        String address = "Could not find address 😥";
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses !=  null && addresses.size() >0) {
                address = "Address:\n";
                if (addresses.get(0).getThoroughfare() != null) {
                    address += addresses.get(0).getThoroughfare() + " ";
                }
                if (addresses.get(0).getLocality() != null) {
                    address += addresses.get(0).getLocality() + "\n";
                }
                if (addresses.get(0).getAdminArea() != null) {
                    address += addresses.get(0).getAdminArea() + " ";
                }
                if (addresses.get(0).getPostalCode() != null) {
                    address += addresses.get(0).getPostalCode();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        textAddress.setText(address);
    }
}