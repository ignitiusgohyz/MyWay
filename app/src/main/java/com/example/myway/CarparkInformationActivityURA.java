package com.example.myway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

public class CarparkInformationActivityURA extends AppCompatActivity {

    ImageButton back;
    TextView address;
    TextView parking_system;
    TextView remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_information_ura);

        back = findViewById(R.id.backButton);
        address = findViewById(R.id.ura_address);
        parking_system = findViewById(R.id.carpark_type_ura);
        remarks = findViewById(R.id.carpark_remark_ura);

        Bundle receive = getIntent().getExtras();
        address.setText(receive.getString("address"));
        parking_system.setText(receive.getString("parking_system"));
        remarks.setText(receive.getString("remarks"));

        back.setOnClickListener(v -> finish());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}