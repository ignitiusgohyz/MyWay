package com.example.myway;

public class ParkingCardView {
    private String location;
    private String carpark_availability;
    private String price_calculator;

    public ParkingCardView(String location, String carpark_availability, String price_calculator) {
        this.location = location;
        this.carpark_availability = carpark_availability;
        this.price_calculator = price_calculator;
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
}
