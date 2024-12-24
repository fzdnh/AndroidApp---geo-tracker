package com.example.ict652_sulam;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.OnMapReadyCallback;


public class trackmap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button returnButton;

    private DatabaseReference rtLocationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackmap);

//        rtLocationRef = FirebaseDatabase.getInstance().getReference().child("rtlocation").child("rtloctid");
//        rtLocationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    String rtlocid = childSnapshot.getKey();
//
//                }
//
//            }
//        });

        // Initialize the Firebase Realtime Database reference
        rtLocationRef = FirebaseDatabase.getInstance().getReference().child("rtlocation").child("rtloctid");
        returnButton = findViewById(R.id.returnButton);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this); // Ensure this class implements OnMapReadyCallback

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), trackerActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.bus);

        // Remove existing markers
        mMap.clear();

        // Attach a ValueEventListener to get updates when the location changes
        rtLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int position = 1; // Initialize position counter

                if(dataSnapshot.exists()){
                    for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                        // Assuming there is only one set of latitude and longitude values in each generated key
                        Double lat = locationSnapshot.child("latitude").getValue(Double.class);
                        Double longi = locationSnapshot.child("longitude").getValue(Double.class);

                        if (lat != null && longi != null) {
                            LatLng location = new LatLng(lat, longi);

                            // Add a new marker with the marker title "Bus 1", "Bus 2", and so on
                            mMap.addMarker(new MarkerOptions().position(location).title("Bus " + position).icon(customIcon));

                            // Move the camera to the new marker's position and zoom
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f)); // Adjust the zoom level (e.g., 10f)

                            // Increment position for the next marker
                            position++;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors in reading the data
                Log.e(TAG, "Error reading location data", databaseError.toException());
            }
        });
    }
}