package com.example.ict652_sulam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubmitRequest extends MainActivity {

    EditText numOfPeopleInput, description;
    Button submit, button1_2, button3_5, button6_10, button11plus, save, map;
    TextView addressTextView, latitudeTextView, longitudeTextView;
    DatabaseReference rootDatabaseref;
    FirebaseDatabase db;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;
    String reqid;

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the MapActivity
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MapActivity.RESULT_OK) {
                // Get data from the Intent
                String address = data.getStringExtra("address");
                String latitude = data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");

                // Display the data in the respective TextViews
                addressTextView.setText(address);
                latitudeTextView.setText(latitude);
                longitudeTextView.setText(longitude);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

        textView = findViewById(R.id.returnmenu);
        auth = FirebaseAuth.getInstance();
        button1_2 = findViewById(R.id.numofpeople1_2);
        button3_5 = findViewById(R.id.numofpeople3_5);
        button6_10 = findViewById(R.id.numofpeople6_10);
        button11plus = findViewById(R.id.numofpeople11plus);
        numOfPeopleInput = findViewById(R.id.numofpeople);
        description = findViewById(R.id.description);
        submit = findViewById(R.id.btn_submit);
        save = findViewById(R.id.btn_save);
        map = findViewById(R.id.btn_map);
        user = auth.getCurrentUser();
        addressTextView = findViewById(R.id.addressTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);

        save.setVisibility(View.GONE);
        submit.setVisibility(View.VISIBLE);

        editData();

        // Initialize all buttons
        initializeButton(button1_2);
        initializeButton(button3_5);
        initializeButton(button6_10);
        initializeButton(button11plus);

        rootDatabaseref = FirebaseDatabase.getInstance().getReference();

        button1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the value of the hidden input text when button1_2 is clicked
                numOfPeopleInput.setText("1-2");
                // Update the appearance of the button
                updateButtonAppearance(button1_2);
            }
        });

        button3_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the value of the hidden input text when button3_5 is clicked
                numOfPeopleInput.setText("3-5");
                // Update the appearance of the button
                updateButtonAppearance(button3_5);
            }
        });

        button6_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the value of the hidden input text when button6_10 is clicked
                numOfPeopleInput.setText("6-10");
                // Update the appearance of the button
                updateButtonAppearance(button6_10);
            }
        });

        button11plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the value of the hidden input text when button11plus is clicked
                numOfPeopleInput.setText("11 and more");
                // Update the appearance of the button
                updateButtonAppearance(button11plus);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the MapActivity with startActivityForResult
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
            }
        });

        db = FirebaseDatabase.getInstance();
        rootDatabaseref = db.getReference("requests");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nop = numOfPeopleInput.getText().toString();
                String desc = description.getText().toString();
                String add = extractValueAfterColon(addressTextView.getText().toString());
                String lat = extractValueAfterColon(latitudeTextView.getText().toString());
                String longi = extractValueAfterColon(longitudeTextView.getText().toString());

                if (!nop.isEmpty() && !desc.isEmpty() && !add.isEmpty() && !lat.isEmpty() && !longi.isEmpty()) {

                    // Generating a unique key for the person's data
                    String key = rootDatabaseref.push().getKey();

                    // Get the current time
                    String currentTime = getCurrentTime();

                    Request req = new Request(key, user.getUid(), nop, desc, add, lat, longi, currentTime);
                    rootDatabaseref.child("reqid").child(key).setValue(req);

                    // Clear input fields after successful save
                    numOfPeopleInput.setText("");
                    description.setText("");
                    addressTextView.setText("Address:");
                    latitudeTextView.setText("Latitude:");
                    longitudeTextView.setText("Longitude:");

                    initializeButton(button1_2);
                    initializeButton(button3_5);
                    initializeButton(button6_10);
                    initializeButton(button11plus);

                    // Optional: Show success message
                    Toast.makeText(getApplicationContext(), "Request submitted successfully!", Toast.LENGTH_SHORT).show();

                    // Pass the "address" value to SuccessCard activity
                    Intent intent = new Intent(getApplicationContext(), SuccessCard.class);
                    intent.putExtra("address", add);
                    startActivity(intent);

//                    // Create and show a notification
//                    FirebaseMessaging.getInstance().subscribeToTopic("Activity")
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    String msg = "Done";
//                                    if (!task.isSuccessful()) {
//                                        msg = "Failed";
//                                    }
//
//                                }
//                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //storing edited data
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nop = numOfPeopleInput.getText().toString();
                String desc = description.getText().toString();
                String add = extractValueAfterColon(addressTextView.getText().toString());
                String lat = extractValueAfterColon(latitudeTextView.getText().toString());
                String longi = extractValueAfterColon(longitudeTextView.getText().toString());

                if (!nop.isEmpty() && !desc.isEmpty() && !add.isEmpty() && !lat.isEmpty() && !longi.isEmpty()) {
                    // Assuming "requestsRef" is your DatabaseReference pointing to the "requests" node
                    DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("requests").child("reqid").child(reqid);

                    // Create a map to update only the specific fields you want to change
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("numofpeople", nop);
                    updateData.put("description", desc);
                    updateData.put("address", add);
                    updateData.put("lattitude", lat);
                    updateData.put("longitude", longi);

                    // Use updateChildren() to update the data in Firebase
                    requestsRef.updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Request updated successfully!", Toast.LENGTH_SHORT).show();
                                    // You can refresh your UI or perform other actions as needed
                                    Intent intent = new Intent(getApplicationContext(), CheckActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("RequestsAdapter", "Error updating request in Firebase", e);
                                    Toast.makeText(getApplicationContext(), "Failed to update request", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
}

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void editData() {
        if(getIntent().getBundleExtra("requestdata")!=null){
            Bundle bundle = getIntent().getBundleExtra("requestdata");
            reqid = bundle.getString("reqIdToEdit");
            description.setText(bundle.getString("description"));
            numOfPeopleInput.setText(bundle.getString("numofpeople"));
            addressTextView.setText("Address: "+bundle.getString("address"));
            latitudeTextView.setText("Latitude: "+bundle.getString("latitude"));
            longitudeTextView.setText("Longitude: "+bundle.getString("longitude"));

            save.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }
    }

    // Helper method to extract value after ':'
    private String extractValueAfterColon(String input) {
        // Split the input string based on ':'
        String[] parts = input.split(":");

        // Check if there are two parts after splitting
        if (parts.length == 2) {
            // Return the second part (value after ':')
            return parts[1].trim();
        } else {
            // Return the original input if ':' is not found
            return input.trim();
        }
    }

    private void initializeButton(Button button) {
        // Set the default text color for the button
        button.setTextColor(getResources().getColor(android.R.color.white)); // Replace with the desired color
    }
    private void updateButtonAppearance(Button clickedButton) {
        // Reset the appearance of all buttons
        initializeButton(button1_2);
        initializeButton(button3_5);
        initializeButton(button6_10);
        initializeButton(button11plus);

        // Set a different background color or appearance for the clicked button
        clickedButton.setTextColor(getResources().getColor(android.R.color.black));
    }

}