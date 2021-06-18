package com.example.myway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ParkingCardViewAdapter extends RecyclerView.Adapter<ParkingCardViewAdapter.ParkingCardViewHolder> {

    private ArrayList<ParkingCardView> parkingCardViewArrayList;

    public static class ParkingCardViewHolder extends RecyclerView.ViewHolder {
        private TextView location;
        private TextView carpark_availability;
        private TextView price_calculator;

        public ParkingCardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.destination_1);
            carpark_availability = itemView.findViewById(R.id.carpark_availability_1);
            price_calculator = itemView.findViewById(R.id.est_price_1);
        }
    }

    public ParkingCardViewAdapter(ArrayList<ParkingCardView> parkingCardViewArrayList) {
        this.parkingCardViewArrayList = parkingCardViewArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public ParkingCardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_parking_scrollview_cardview, parent, false);
        ParkingCardViewHolder parkingCardViewHolder = new ParkingCardViewHolder(v);
        return parkingCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ParkingCardViewAdapter.ParkingCardViewHolder holder, int position) {
        ParkingCardView currentItem = parkingCardViewArrayList.get(position);
        holder.location.setText(currentItem.getLocation());
        holder.carpark_availability.setText(currentItem.getCarpark_availability());
        holder.price_calculator.setText(currentItem.getPrice_calculator());
    }

    @Override
    public int getItemCount() {
        return parkingCardViewArrayList.size();
    }
}
