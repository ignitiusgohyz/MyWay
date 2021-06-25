package com.example.myway;

import android.util.Log;

public class ParkingCardView {
    private String location;
    private String carpark_availability;
    private String price_calculator;
    private double latitude;
    private double longitude;
    private double distanceFromCurrent;
    private boolean redColour;

    public ParkingCardView(String location, String carpark_availability, String price_calculator, double distanceFromCurrent, double longitude, double latitude) {
        this.location = location;
        this.carpark_availability = carpark_availability;
        this.price_calculator = price_calculator;
        this.distanceFromCurrent = distanceFromCurrent;
        this.latitude = latitude;
        this.longitude = longitude;

        if (carpark_availability.equals("info unavailable")) {
            redColour = true;
        } else {
            String[] temp = carpark_availability.split("/");
            String availableString = temp[0];
            Log.d("Checking percentage", "Available: " + availableString);
            String[] temp2 = temp[1].split(" lots available");
            String totalString = temp2[0];
            Log.d("Checking percentage", "Total: " + totalString);
            double availableInt = Double.parseDouble(availableString);
            double totalInt = Double.parseDouble(totalString);
            Log.d("Checking percentage", "Percentage: " + availableInt / totalInt);
            if (availableInt / totalInt <= 0.1) {
                Log.d("Checking percentage", "IF -> red: " + availableInt / totalInt);
                redColour = true;
            } else {
                Log.d("Checking percentage", "ELSE -> not red: " + availableInt / totalInt);
                redColour = false;
            }
        }
    }

    public String getCarpark_availability() {
        return carpark_availability;
    }

    public String getPrice_calculator() {
        return price_calculator;
    }

    public String getLocation() {
        return location;
    }

    public boolean isRedColour() {
        return redColour;
    }

    public double getDistanceFromCurrent() { return distanceFromCurrent; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }
}
