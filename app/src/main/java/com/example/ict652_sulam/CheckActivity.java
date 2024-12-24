package com.example.ict652_sulam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckActivity extends AppCompatActivity implements RequestsAdapter.OnRequestDeletedListener {

    Button returnmenu;
    RecyclerView rc;
    RequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        // Initialize adapter here
//        adapter = new RequestsAdapter(this, FirebaseDatabase.getInstance().getReference().child("requests"));

        returnmenu = findViewById(R.id.btnmenu);
        rc = findViewById(R.id.activity_recycler_view);

        rc.setLayoutManager((new LinearLayoutManager(this)));
        displayData();
        returnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestDeleted() {
        // Refresh the activity or reload data as needed
        displayData(); // Assuming displayData is a method that fetches and displays data
    }

    private void displayData() {
        // Get a reference to the "buddies" node
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("requests").child("reqid");

        // Create an empty ArrayList to store retrieved data
        final ArrayList<Request> modelArrayList = new ArrayList<>();

        // Initialize adapter here (if not initialized yet)
        if (adapter == null) {
            adapter = new RequestsAdapter(this, R.layout.singledata, modelArrayList, dbref);
            rc.setAdapter(adapter);
        }

        // Add a listener for data changes at the "requests" reference
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing data
                modelArrayList.clear();

                // Loop through each child snapshot (individual buddy data)
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Get the data values
                    String description = childSnapshot.child("description").getValue(String.class);
                    String numofpeople = childSnapshot.child("numofpeople").getValue(String.class);
                    String uid = childSnapshot.child("uid").getValue(String.class);
                    String address = childSnapshot.child("address").getValue(String.class);
                    String longitude = childSnapshot.child("longitude").getValue(String.class);
                    String lattitude = childSnapshot.child("lattitude").getValue(String.class);
                    String time = childSnapshot.child("time").getValue(String.class);

                    // Get the unique key for the current buddy
                    String key = childSnapshot.getKey();

                    // Convert data to your model object
                    modelArrayList.add(new Request(key, uid, numofpeople, description, address, longitude, lattitude, time));
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


}