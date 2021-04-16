package com.mu.medicalsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Pharmacy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy2);

        ((TextView) findViewById(R.id.pharmacy_name)).setText(PharmacyActivity.selected_pharmacy.name);
        ((TextView) findViewById(R.id.pharmacy_email)).setText(PharmacyActivity.selected_pharmacy.email);
        ((TextView) findViewById(R.id.pharmacy_phone)).setText(PharmacyActivity.selected_pharmacy.phoneNumber);
        ((TextView) findViewById(R.id.pharmacy_address)).setText(PharmacyActivity.selected_pharmacy.address);

        ((Button) findViewById(R.id.order_prescription)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Pharmacy.this, ShippingDetails.class));
            }
        });
    }
}