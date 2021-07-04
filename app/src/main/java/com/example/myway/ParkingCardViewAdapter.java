package com.example.myway;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ParkingCardViewAdapter extends RecyclerView.Adapter<ParkingCardViewAdapter.ParkingCardViewHolder> {

    private static ArrayList<ParkingCardView> parkingCardViewArrayList;
    private static String username;


    public static class ParkingCardViewHolder extends RecyclerView.ViewHolder {
        private TextView location;
        private TextView carpark_availability;
        private TextView price_calculator;
        private CardView cardView;
        private TextView longitude;
        private TextView latitude;
        private ImageButton arrowDropDown;
        private Dialog mDialog;
        private TextView parkDuration;

        public ParkingCardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.destination_1);
            carpark_availability = itemView.findViewById(R.id.carpark_availability_1);
            price_calculator = itemView.findViewById(R.id.est_price_1);
            cardView = itemView.findViewById(R.id.cardviewLayout);
            longitude = itemView.findViewById(R.id.longitude);
            latitude = itemView.findViewById(R.id.latitude);
            arrowDropDown = itemView.findViewById(R.id.fragment_carpark_timing_arrow_down);
            parkDuration = itemView.findViewById(R.id.timeslot_1);
            mDialog = new Dialog(itemView.getContext());

            arrowDropDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDialog.setContentView(R.layout.number_picker_dialog);
                    NumberPicker hourPicker = (NumberPicker) mDialog.findViewById(R.id.hourPicker);
                    NumberPicker minutePicker = (NumberPicker) mDialog.findViewById(R.id.minutePicker);
                    hourPicker.setMaxValue(23);
//                    minutePicker.setMaxValue(45);
                    minutePicker.setMinValue(1);
                    minutePicker.setMaxValue(4);
                    minutePicker.setDisplayedValues(new String[] {"0", "15", "30", "45"});
//                    minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                        @Override
//                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                            picker.setValue((newVal < oldVal)? oldVal-15 : oldVal+15);
//                        }
//                    });
                    hourPicker.setWrapSelectorWheel(false);
                    minutePicker.setWrapSelectorWheel(false);
                    mDialog.show();
                    Button confirmTime = mDialog.findViewById(R.id.confirm_button);
                    confirmTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
                            String finalTime = "";
//                            Log.d("CheckDay", "Day" + currentDay);
                            String[] timeArray = currentTime.split(":");
                            int currentHour = Integer.parseInt(timeArray[0]);
                            int currentMinute = Integer.parseInt(timeArray[1]);
                            int numHours = hourPicker.getValue();
                            int numMinutes = Integer.parseInt(minutePicker.getDisplayedValues()[minutePicker.getValue()-1]);
                            int finalHour = currentHour + numHours;
//                            Log.d("HOURPICKER", "hour value: " + finalHour);
                            int finalMinute = currentMinute + numMinutes;
//                            Log.d("HOURPICKER", "minute value: " + finalMinute);
                            if(finalMinute >= 60) {
                                finalMinute %= 60;
                                finalHour++;
                            }
                            if(finalHour >= 24) {
                                finalHour %= 24;
                            }
                            String finalHourString = String.valueOf(finalHour);
                            String finalMinuteString = String.valueOf(finalMinute);
                            if(finalMinute < 10) {
                                finalMinuteString = "0" + finalMinuteString;
                            }
                            if(finalHour < 10) {
                                finalHourString = "0" + finalHourString;
                            }
                            finalTime += finalHourString + ":" + finalMinuteString;
                            //pass in current time, parkingduration, carpark information
                            //pricecalculator method
                            parkDuration.setText(currentTime + " - " + finalTime);
//                            Log.d("PCV SIZE", "SIZE: " + parkingCardViewArrayList.size());
//                            Log.d("PCV POSITION", "INDEX: " + itemView.getTag());
                            Log.d("Calculator", "Day" + currentDay);
                            Log.d("Calculator", "currentTime" + ((currentHour*100) + currentMinute));
                            Log.d("Calculator", "finalTime" + ((finalHour*100) + finalMinute));
                            price_calculator.setText(calculatePrice(currentDay, ((currentHour*100) + currentMinute), numHours, numMinutes, ((finalHour*100) + finalMinute), parkingCardViewArrayList.get((int) itemView.getTag())));
                            mDialog.dismiss();
                        }
                    });
                }
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

    public static String calculatePrice(String currentDay, int currentTime, int numHours, int numMinutes, int finalTime, ParkingCardView currentCardView) {
        Carpark currentCP = currentCardView.getCurrentCP();
        if (currentCP instanceof Carpark.HDB) {
            return ((Carpark.HDB) currentCP).calculateHDB(currentDay, currentTime, numHours, numMinutes, finalTime);
        } else if (currentCP instanceof Carpark.LTA) {
            return ((Carpark.LTA) currentCP).calculateLTA(currentDay, currentTime, numHours, numMinutes, finalTime);
        } else if (currentCP instanceof Carpark.URA) {
            return ((Carpark.URA) currentCP).calculateURA(currentDay, currentTime, numHours, numMinutes, finalTime);
        } else {
            return "info unavailable";
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
        holder.cardView.setTag(position);
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
