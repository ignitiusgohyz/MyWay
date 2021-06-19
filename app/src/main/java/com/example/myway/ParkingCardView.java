package com.example.myway;

public class ParkingCardView {
    private String location;
    private String carpark_availability;
    private String price_calculator;
    private double distanceFromCurrent;

    public ParkingCardView(String location, String carpark_availability, String price_calculator, double distanceFromCurrent) {
        this.location = location;
        this.carpark_availability = carpark_availability;
        this.price_calculator = price_calculator;
        this.distanceFromCurrent = distanceFromCurrent;
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

    public double getDistanceFromCurrent() { return distanceFromCurrent; }
}
