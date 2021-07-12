package com.example.myway;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ParkingCardViewAdapter extends RecyclerView.Adapter<ParkingCardViewAdapter.ParkingCardViewHolder> {

    private static ArrayList<ParkingCardView> parkingCardViewArrayList;
    private static ArrayList<ParkingCardViewHolder> viewHolders = new ArrayList<>();
    private static String username;
    private static Context context;

    public static class ParkingCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView location;
        private final TextView carpark_availability;
        private final TextView price_calculator;
        private final CardView cardView;
        private final TextView longitude;
        private final TextView latitude;
        private final ImageButton arrowDropDown;
        private final Dialog mDialog;
        private final TextView parkDuration;
        private final ImageButton threeDots;
        private final ImageButton arrowRight;
        private final TextView viewRates;

        @SuppressLint("SetTextI18n")
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
            threeDots = itemView.findViewById(R.id.ic_ellipsis);
            arrowRight = itemView.findViewById(R.id.fragment_carpark_rates_arrow_right);
            viewRates = itemView.findViewById(R.id.view_rates_1);

            viewHolders.add(this);

            parkDuration.setOnClickListener(v -> arrowDropDown.callOnClick());

            arrowDropDown.setOnClickListener(v -> {
                mDialog.setContentView(R.layout.number_picker_dialog);
                NumberPicker hourPicker = mDialog.findViewById(R.id.hourPicker);
                NumberPicker minutePicker = mDialog.findViewById(R.id.minutePicker);
                hourPicker.setMaxValue(23);
                minutePicker.setMinValue(1);
                minutePicker.setMaxValue(4);
                minutePicker.setDisplayedValues(new String[] {"0", "15", "30", "45"});
                hourPicker.setWrapSelectorWheel(false);
                minutePicker.setWrapSelectorWheel(false);
                mDialog.show();
                Button confirmTime = mDialog.findViewById(R.id.confirm_button);
                confirmTime.setOnClickListener(v1 -> {
                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
                    String date = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
                    Log.d("CheckDate", "Date" + date);
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

                    Log.d("ARRAYLIST SIZE>>>>>>>>>>>>>>>>",parkingCardViewArrayList.size() + "");
                    //pass in current time, parkingduration, carpark information
                    String duration = currentTime + " - " + finalTime;
                    for (int i = 0; i < 16; i++) {
                        ParkingCardView parkingCardView = parkingCardViewArrayList.get(i);
                        String price = calculatePrice(date, currentDay, ((currentHour*100) + currentMinute), numHours, numMinutes, ((finalHour*100) + finalMinute), parkingCardView);
                        Log.d("PRICE PRICE PRICE INTO CP>>>>>>>>>>>>>", "" + price);
                        parkingCardView.getCurrentCP().setPrice(price.equals(" no est.") ? 99999 : Double.parseDouble(price));
                        parkingCardView.getCurrentCP().setDuration(duration);
                        parkingCardView.setPrice_calculator(price);
                        parkingCardView.setDuration(duration);
                        if (i < viewHolders.size()) {
                            Log.d("Updates view on selection", "This one");
                            ParkingCardViewHolder parkingCardViewHolder = viewHolders.get(i);
                            parkingCardViewHolder.setPrice(price, duration);
                        }
                    }

                    Parking.selectedTiming();
                    mDialog.dismiss();
                });
            });

            threeDots.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), threeDots);
                popupMenu.getMenuInflater().inflate(R.menu.three_dots_cardview, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Set Parking Alarm")) {
//                        Intent intent = new Intent(v.getContext(), ParkingAlarmFragment.class);
//                        v.getContext().startActivity(intent);
                        Toast.makeText(v.getContext(), "PARKING ALARM SELECTED", Toast.LENGTH_SHORT).show();
                    } else {
                        informationTransition(v);
                    }
                    popupMenu.dismiss();
                    return true;
                });
                popupMenu.show();
            });

            viewRates.setOnClickListener(v -> arrowRight.performClick());

            arrowRight.setOnClickListener(this::informationTransition);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                double lat =  Double.parseDouble((String) latitude.getText());
                double lon = Double.parseDouble((String) longitude.getText());
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lon);
                intent.putExtra("username", username);
                intent.putExtra("location", location.getText());
                v.getContext().startActivity(intent);
            });
        }

        private void setPrice(String price, String duration) {
            price_calculator.setText(price.equals( " no est.") ? price : " est. $" + price);
            parkDuration.setText(duration);
        }

        private void informationTransition(View v) {
            ParkingCardView current = parkingCardViewArrayList.get((int) itemView.getTag());
            Carpark currentCP = current.getCurrentCP();
            if (currentCP instanceof Carpark.HDB) {
                Intent intent = new Intent(v.getContext(), CarparkInformationActivityHDB.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", currentCP.getAddress());
                bundle.putString("carpark_type", ((Carpark.HDB) currentCP).getCarParkType());
                bundle.putString("parking_system", currentCP.getParkingSystem());
                bundle.putString("gantry_height", ((Carpark.HDB) currentCP).getGantryHeight());
                bundle.putString("free_parking", ((Carpark.HDB) currentCP).getFreeParking());
                bundle.putString("night_parking", ((Carpark.HDB) currentCP).getNightParking());
                bundle.putString("rates", currentCP.isCentralCarpark() ? "$0.60 / 30 minutes non-peak\n$1.20 / 30 minutes peak (7am - 5pm, MON - SAT)"
                        : "$0.60 / 30 minutes");
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            } else if (currentCP instanceof Carpark.URA) {
                Intent intent = new Intent(v.getContext(), CarparkInformationActivityURA.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", currentCP.getAddress());
                bundle.putString("parking_system", currentCP.getParkingSystem());
                String placeholder = ((Carpark.URA) currentCP).getRemarks();
                bundle.putString("remarks", placeholder == null || placeholder.equals("") ? "No Remarks" : placeholder);
                bundle.putString("rates", ((Carpark.URA) currentCP).getFormattedRates());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Sorry, this carpark has no information available!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String calculatePrice(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime, ParkingCardView currentCardView) {
        Carpark currentCP = currentCardView.getCurrentCP();
        if (currentCP instanceof Carpark.HDB) {
            return currentCP.calculateRates(date, currentDay, currentTime, finalTime, false);
        } else {
            return " no est.";
        }
//        if (currentCP instanceof Carpark.HDB) {
//            //return ((Carpark.HDB) currentCP).calculate(date, currentDay, currentTime, numHours, numMinutes, finalTime);
//            return currentCP.calculateRates(date, currentDay, currentTime, finalTime, false);
//        } else if (currentCP instanceof Carpark.LTA) {
//            //return ((Carpark.LTA) currentCP).calculate(date, currentDay, currentTime, numHours, numMinutes, finalTime);
//            return currentCP.calculateRates(date, currentDay, currentTime, finalTime, false);
//        } else if (currentCP instanceof Carpark.URA) {
//            //return ((Carpark.URA) currentCP).calculate(date, currentDay, currentTime, numHours, numMinutes, finalTime);
//            return currentCP.calculateRates(date, currentDay, currentTime, finalTime, false);
//        } else {
//            return "info unavailable";
//        }
    }
    public ParkingCardViewAdapter(ArrayList<ParkingCardView> parkingCardViewArrayList, String username, Context context) {
        ParkingCardViewAdapter.parkingCardViewArrayList = parkingCardViewArrayList;
        ParkingCardViewAdapter.username = username;
        this.context = context;
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
        } else {
            holder.carpark_availability.setTextColor(Color.BLACK);
        }
        holder.carpark_availability.setText(currentItem.getCarpark_availability());
        holder.location.setText(currentItem.getLocation());
        String price = currentItem.getPrice_calculator();
        if (price != null) holder.price_calculator.setText(price.equals(" no est.") ? price : " est. $" + price);
        String duration = currentItem.getDuration();
        holder.parkDuration.setText(duration);
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