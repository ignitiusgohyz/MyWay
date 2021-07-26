package com.example.myway;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    private static Parking parkingContext;
    private static Context context;

    public static void setParam(Parking c) {
        parkingContext = c;
    }

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
        private long durationChosen = Long.MAX_VALUE;
        private final Dialog parkingAlarmChoice;

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
            parkingAlarmChoice = new Dialog(itemView.getContext());
            parkingAlarmChoice.setContentView(R.layout.parking_alarm_choice);

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
                    String finalTime = "";
                    String[] timeArray = currentTime.split(":");
                    int currentHour = Integer.parseInt(timeArray[0]);
                    int currentMinute = Integer.parseInt(timeArray[1]);
                    int numHours = hourPicker.getValue();
                    int numMinutes = Integer.parseInt(minutePicker.getDisplayedValues()[minutePicker.getValue()-1]);
                    int finalHour = currentHour + numHours;
                    int finalMinute = currentMinute + numMinutes;
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

                    durationChosen = numHours * 60 + numMinutes;

                    //pass in current time, parkingduration, carpark information
                    String duration = currentTime + " - " + finalTime;
                    for (int i = 0; i < 16; i++) {
                        ParkingCardView parkingCardView = parkingCardViewArrayList.get(i);
                        String price = calculatePrice(date, currentDay, ((currentHour*100) + currentMinute), numHours, numMinutes, ((finalHour*100) + finalMinute), parkingCardView);
                        parkingCardView.getCurrentCP().setPrice(price.equals(" no est.") ? 99999 : Double.parseDouble(price));
                        parkingCardView.getCurrentCP().setDuration(duration);
                        parkingCardView.setPrice_calculator(price);
                        parkingCardView.setDuration(duration);
                        parkingCardView.setDurationStored(durationChosen * 60000);
                        if (i < viewHolders.size()) {
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
//                        if (parkDuration.getText().equals("choose your duration") || durationChosen == Long.MAX_VALUE) {
                            if (parkDuration.getText().equals("choose your duration")) {
                            Toast.makeText(v.getContext(), "Please select a parking duration", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO set parking alarm based on duration
                            parkingAlarmChoice.show();
                            Button now = parkingAlarmChoice.findViewById(R.id.setAlarmNow);
                            Button reach = parkingAlarmChoice.findViewById(R.id.setAlarmAfterNavigation);
                            String tempString = parkDuration.getText().toString();
                            String[] splitTime = tempString.split(" - ");
                            String[] left = splitTime[0].split(":");
                            String leftStart = left[0].charAt(0) == '0' ? left[0].substring(1) : left[0];
                            String rightStart = left[1];
                            leftStart = leftStart + rightStart;

                            String[] right = splitTime[1].split(":");
                            String leftEnd = right[0].charAt(0) == '0' ? right[0].substring(1) : right[0];
                            String rightEnd = right[1];
                            leftEnd = leftEnd + rightEnd;

                            long millisInput = calculateTimeDifference(leftStart, leftEnd) * 60000;

                            now.setOnClickListener(v12 -> {
                                if (parkingContext.hasAlarmSet()) {
                                    Toast.makeText(v12.getContext(), "Please cancel previous alarm", Toast.LENGTH_SHORT).show();
                                } else {
                                    parkingContext.setAlarm(millisInput);
                                    Toast.makeText(v12.getContext(), "Parking Alarm Set", Toast.LENGTH_SHORT).show();
                                    parkingAlarmChoice.dismiss();
                                }
                            });

                            reach.setOnClickListener(v13 -> {
                                if (parkingContext.hasAlarmSet()) {
                                    Toast.makeText(v13.getContext(), "Please cancel previous alarm", Toast.LENGTH_SHORT).show();
                                } else {
                                    parkingContext.updateDelayedAlarm();
                                    MapboxNavigationActivity.setDelayedAlarm(millisInput);
                                    Toast.makeText(v13.getContext(), "Parking Alarm Set upon Arrival at Destination", Toast.LENGTH_SHORT).show();
                                    parkingAlarmChoice.dismiss();
                                }
                            });
                        }
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
                String[] parsedStrings = location.getText().toString().split(" ");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < parsedStrings.length - 1; i++) {
                    stringBuilder.append(parsedStrings[i]).append(" ");
                }
                intent.putExtra("location", stringBuilder.toString());
                v.getContext().startActivity(intent);
            });
        }

        protected static int calculateTimeDifference(String startTime, String endTime) {
            // 0530 - 0700 (END > START)
            // 2300 - 2230 (START > END)
            if (startTime.length() <= 3) startTime = "0" + startTime;
            if (endTime.length() <= 3) endTime = "0" + endTime;

            // 05 AND 30
            // 23 AND 00
            Integer startHour = Integer.parseInt(startTime.substring(0, 2)); // 2230    2230    2258
            int startMin = Integer.parseInt(startTime.substring(2)); //     2313    2429    2401

            // 07 AND 00
            // 22 AND 30
            Integer endHour = Integer.parseInt(endTime.substring(0, 2)); //
            int endMin = Integer.parseInt(endTime.substring(2)); //

            int differenceInTime;

            if (endMin < startMin) {
                differenceInTime = (endHour - startHour - 1) * 60 + (60 - startMin + endMin);
            }  else {
                differenceInTime = (endHour - startHour) * 60 + (endMin - startMin);
            }

            return differenceInTime;
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
        if (currentCP instanceof Carpark.LTA) {
            return " no est.";
        } else {
            return currentCP.calculateRates(date, currentDay, currentTime, finalTime, false);
        }
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

    @SuppressLint("SetTextI18n")
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
        holder.location.setText(currentItem.getLocation() + " (" + currentItem.getDistanceFromCurrent() + "m)");
        String price = currentItem.getPrice_calculator();
        String duration = currentItem.getDuration();
        holder.parkDuration.setText(duration);
        holder.price_calculator.setText(price.equals(" no est.") ? price : " est. $" + price);
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