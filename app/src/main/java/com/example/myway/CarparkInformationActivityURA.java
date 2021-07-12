package com.example.myway;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CarparkInformationActivityURA extends AppCompatActivity {

    ImageButton back;
    TextView address;
    TextView parking_system;
    TextView remarks;
    TextView rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_information_ura);

        back = findViewById(R.id.backButton);
        address = findViewById(R.id.ura_address);
        parking_system = findViewById(R.id.carpark_type_ura);
        remarks = findViewById(R.id.carpark_remark_ura);
        rates = findViewById(R.id.carpark_ura_rates);

        Bundle receive = getIntent().getExtras();
        address.setText(receive.getString("address"));
        parking_system.setText(receive.getString("parking_system"));
        remarks.setText(receive.getString("remarks"));
        rates.setMovementMethod(new ScrollingMovementMethod());
        rates.setText(receive.getString("rates"));

        back.setOnClickListener(v -> finish());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}