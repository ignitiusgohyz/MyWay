package com.example.myway;

public class ParkingCardView {
    private final String location;
    private final String carpark_availability;
    private String price_calculator;
    private final double latitude;
    private final double longitude;
    private final int distanceFromCurrent;
    private String duration;
    private final boolean redColour;
    private final Carpark currentCP;
    private long durationStored;

    public ParkingCardView(Carpark currentCP, String location, String carpark_availability, String price_calculator, double distanceFromCurrent, double longitude, double latitude, String duration) {
        this.currentCP = currentCP;
        this.location = location;
        this.carpark_availability = carpark_availability;
        this.price_calculator = price_calculator;
        this.distanceFromCurrent = (int) distanceFromCurrent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.duration = duration;

        if (carpark_availability.equals("info unavailable")) {
            redColour = true;
        } else {
            if (carpark_availability.length() == 5) {
                String[] temp = carpark_availability.split("/");
                String availableString = temp[0];
//                Log.d("Checking percentage", "Available: " + availableString);
                String[] temp2 = temp[1].split(" lots available");
                String totalString = temp2[0];
//                Log.d("Checking percentage", "Total: " + totalString);
                double availableInt = Double.parseDouble(availableString);
                double totalInt = Double.parseDouble(totalString);
//                Log.d("Checking percentage", "Percentage: " + availableInt / totalInt);
                //                    Log.d("Checking percentage", "IF -> red: " + availableInt / totalInt);
                //                    Log.d("Checking percentage", "ELSE -> not red: " + availableInt / totalInt);
                redColour = availableInt / totalInt <= 0.1;
            } else {
                redColour = false;
            }
        }
    }



    public Carpark getCurrentCP() {
        return currentCP;
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

    public void setPrice_calculator(String price_calculator) {
        this.price_calculator = price_calculator;
    }

    public boolean isRedColour() {
        return redColour;
    }

    public void setDurationStored(long ms) {
        durationStored = ms;
    }

    public long getDurationStored() {
        return durationStored;
    }

    public int getDistanceFromCurrent() { return distanceFromCurrent; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
