package com.example.ict652_sulam;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LastLocationRequest;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class trackerActivity extends AppCompatActivity {

    private Button actrack, trackloc;
    private boolean isTracking = false;

    private DatabaseReference rootDatabaseref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String key;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private SharedPreferences sharedPreferences;

    private boolean isGpsEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        actrack = findViewById(R.id.act_tracker);
        trackloc = findViewById(R.id.trackloc);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            key = user.getUid();
        }

        rootDatabaseref = FirebaseDatabase.getInstance().getReference("rtlocation");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setupLocationCallback();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore the tracking state from SharedPreferences
        isTracking = sharedPreferences.getBoolean("isTracking", false);
        updateButtonText();

        actrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTracking) {
                    startLocationTracking();
                } else {
                    stopLocationTracking();
                }
            }
        });

        trackloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), trackmap.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();

                    // Update the location in the Firebase Realtime Database
                    rootDatabaseref.child("rtloctid").child(key).child("latitude").setValue(latitude);
                    rootDatabaseref.child("rtloctid").child(key).child("longitude").setValue(longitude);
                    rootDatabaseref.child("rtloctid").child(key).child("rtloctid").setValue(key);
                }
            }
        };
    }

    private void updateButtonText() {
        actrack.setText(isTracking ? "Deactivate Tracking" : "Activate Tracking");
    }

    private void requestLocationUpdates(){
        LocationRequest request  = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(trackerActivity.this, "reachedupdateloca", Toast.LENGTH_SHORT).show();

            fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        }
    }

    private void startLocationTracking() {
        isTracking = true;
        isGpsEnabled = true;
        updateButtonText();
        // Save the tracking state to SharedPreferences
        sharedPreferences.edit().putBoolean("isTracking", true).apply();
        actrack.setText("Deactivate Tracking");

//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(5000); // Update every 5 seconds
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        if (isGpsEnabled) {
            requestLocationUpdates();
            Toast.makeText(getApplicationContext(), "Location tracking has been successfully activated", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationTracking() {
        isTracking = false;
        isGpsEnabled = false;
        // Save the tracking and GPS state to SharedPreferences
        sharedPreferences.edit().putBoolean("isTracking", false).putBoolean("isGpsEnabled", false).apply();
        actrack.setText("Activate Tracking");

        // Remove location updates only if GPS is enabled
        if (isGpsEnabled) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Remove the data from the Firebase Realtime Database
                            rootDatabaseref.child("rtloctid").child(key).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Location tracking has been successfully deactivated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        }
    }

}
