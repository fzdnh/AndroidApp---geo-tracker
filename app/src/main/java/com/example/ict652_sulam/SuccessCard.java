package com.example.ict652_sulam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SuccessCard extends AppCompatActivity {

    CardView donebtn;
    Button donebtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_card);

        // Extract the "address" value from the Intent
        String address = getIntent().getStringExtra("address");
        // Extract the last 3 strings
        String lastThreeStrings = extractLastThreeStrings(address);
        TextView textView = findViewById(R.id.countrycode);
        textView.setText(lastThreeStrings);
        donebtn = findViewById(R.id.btn_done);
        donebtn2 = findViewById(R.id.doneButton);

        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

        donebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private String extractLastThreeStrings(String input) {
        // Split the input string based on ','
        String[] parts = input.split(",");

        // Check if there are at least 3 parts after splitting
        if (parts.length >= 3) {
            // Concatenate the last 3 parts with ','
            return parts[parts.length - 3].trim() +
                    ", " + parts[parts.length - 2].trim() +
                    ", " + parts[parts.length - 1].trim();
        } else {
            // Return the original input if there are not enough parts
            return input.trim();
        }
    }
}