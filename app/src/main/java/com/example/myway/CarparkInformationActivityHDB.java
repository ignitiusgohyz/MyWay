package com.example.myway;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CarparkInformationActivityHDB extends AppCompatActivity {

    ImageButton back;
    TextView address;
    TextView carpark_type;
    TextView parking_system;
    TextView gantry_height;
    TextView free_parking;
    TextView night_parking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carpark_information_activity);
        back = findViewById(R.id.backButton);
        address = findViewById(R.id.address);
        carpark_type = findViewById(R.id.carpark_type);
        parking_system = findViewById(R.id.parking_system);
        gantry_height = findViewById(R.id.gantry_height);
        free_parking = findViewById(R.id.free_parking);
        night_parking = findViewById(R.id.night_parking);

        Bundle receive = getIntent().getExtras();
        address.setText(receive.getString("address"));
        carpark_type.setText(receive.getString("carpark_type"));
        parking_system.setText(receive.getString("parking_system"));
        gantry_height.setText(receive.getString("gantry_height"));
        free_parking.setText(receive.getString("free_parking"));
        night_parking.setText(receive.getString("night_parking"));

        back.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}