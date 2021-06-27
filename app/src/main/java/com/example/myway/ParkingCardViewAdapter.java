package com.example.myway;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class ParkingCardViewAdapter extends RecyclerView.Adapter<ParkingCardViewAdapter.ParkingCardViewHolder> {

    private ArrayList<ParkingCardView> parkingCardViewArrayList;
    private static String username;

    public static class ParkingCardViewHolder extends RecyclerView.ViewHolder {
        private TextView location;
        private TextView carpark_availability;
        private TextView price_calculator;
        private CardView cardView;
        private TextView longitude;
        private TextView latitude;
        private ImageButton arrowDropDown;


        public ParkingCardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.destination_1);
            carpark_availability = itemView.findViewById(R.id.carpark_availability_1);
            price_calculator = itemView.findViewById(R.id.est_price_1);
            cardView = itemView.findViewById(R.id.cardviewLayout);
            longitude = itemView.findViewById(R.id.longitude);
            latitude = itemView.findViewById(R.id.latitude);
            arrowDropDown = itemView.findViewById(R.id.fragment_carpark_timing_arrow_down);

            arrowDropDown.setOnClickListener(v -> {

            });


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    double lat =  Double.parseDouble((String) latitude.getText());
                    double lon = Double.parseDouble((String) longitude.getText());
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    intent.putExtra("username", username);
                    Toast.makeText(v.getContext(), "CLICKED" + location.getText() + " long: " + longitude.getText() + " lat :" + latitude.getText(), Toast.LENGTH_SHORT).show();
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public ParkingCardViewAdapter(ArrayList<ParkingCardView> parkingCardViewArrayList, String username) {
        this.parkingCardViewArrayList = parkingCardViewArrayList;
        this.username = username;
    }

    @NonNull
    @NotNull
    @Override
    public ParkingCardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_parking_scrollview_cardview, parent, false);

        return new ParkingCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ParkingCardViewAdapter.ParkingCardViewHolder holder, int position) {
        ParkingCardView currentItem = parkingCardViewArrayList.get(position);
        if (currentItem.isRedColour()) {
            //make red colour
            holder.carpark_availability.setTextColor(Color.RED);
            holder.carpark_availability.setText(currentItem.getCarpark_availability());
        } else {
            holder.carpark_availability.setTextColor(Color.BLACK);
            holder.carpark_availability.setText(currentItem.getCarpark_availability());
        }
        holder.location.setText(currentItem.getLocation());
        holder.price_calculator.setText(currentItem.getPrice_calculator());
        double lon = currentItem.getLongitude();
        double lat = currentItem.getLatitude();
        holder.longitude.setText("" + lon);
        holder.latitude.setText("" + lat);
    }

    @Override
    public int getItemCount() {
        return parkingCardViewArrayList.size();
    }


}
