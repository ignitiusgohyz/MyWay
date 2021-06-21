package com.example.myway;

// Arraylist fields all correspond in index.

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Carpark {

    private String carParkNo; // URA and HDB different codes
    private String address; // URA and HDB different addresses
    private double xCoord; // Set to 0.0 if doesn't exist
    private double yCoord; // Set to 0.0 if doesn't exist
    private String carParkType; // HDB has types, URA does not and will be filled with "none"
    private String parkingSystem; // B for electronic, C for coupon
    private String shortTermParking; // HDB exclusive
    private String freeParking; // HDB exclusive
    private String nightParking; // HDB exclusive
    private String carParkDecks; // HDB exclusive
    private String gantryHeight; // HDB exclusive
    private String carParkBasement; // HDB exclusive
    private String ownedBy; // HDB  or URA
    private LatLonCoordinate parkingLatLon;
    private SVY21Coordinate parkingSVY21;
    private double distanceApart;

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public void setParkingLatLon(LatLonCoordinate parkingLatLon) {
        this.parkingLatLon = parkingLatLon;
    }

    public void setParkingSVY21(SVY21Coordinate parkingSVY21) {
        this.parkingSVY21 = parkingSVY21;
    }

    public void setWeekdayMin(String weekdayMin) {
        this.weekdayMin.add(weekdayMin);
    }

    public void setWeekdayRate(String weekdayRate) {
        this.weekdayRate.add(weekdayRate);
    }

    public void setStartTime(String startTime) {
        this.startTime.add(startTime);
    }

    public void setEndTime(String endTime) {
        this.endTime.add(endTime);
    }

    public void setSatdayMin(String satdayMin) {
        this.satdayMin.add(satdayMin);
    }

    public void setSatdayRate(String satdayRate) {
        this.satdayRate.add(satdayRate);
    }

    public void setSunPHMin(String sunPHMin) {
        this.sunPHMin.add(sunPHMin);
    }


    public void setSunPHRate(String sunPHRate) {
        this.sunPHRate.add(sunPHRate);
    }

    public void setParkCapacity(int parkCapacity) {
        this.parkCapacity.add(parkCapacity);
    }

    public void setVehCat(String vehCat) {
        this.vehCat.add(vehCat);
    }

    private ArrayList<String> weekdayMin = new ArrayList<>();
    private ArrayList<String> weekdayRate = new ArrayList<>();
    private ArrayList<String> startTime = new ArrayList<>();
    private ArrayList<String> endTime = new ArrayList<>();
    private ArrayList<String> satdayMin = new ArrayList<>();
    private ArrayList<String> satdayRate = new ArrayList<>();
    private ArrayList<String> sunPHMin = new ArrayList<>();
    private ArrayList<String> sunPHRate = new ArrayList<>();
    private ArrayList<Integer> parkCapacity = new ArrayList<>();
    private ArrayList<String> vehCat = new ArrayList<>();
    private String remarks;

    public Carpark() {
    }
    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public String getCarParkNo() { return carParkNo; }

    public String getAddress() {
        return address;
    }

    public void setDistanceApart(double distanceApart) {
        this.distanceApart = distanceApart;
    }

    protected double getDistanceApart() { return this.distanceApart; }

    public LatLonCoordinate getParkingLatLon() {
        return parkingLatLon;
    }

    public SVY21Coordinate getParkingSVY21() {
        return parkingSVY21;
    }

    public void setCarParkNo(String carParkNo) {
        this.carParkNo = carParkNo.substring(1);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setXCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public void setYCoord(double yCoord) {
        this.yCoord = yCoord;
        parkingSVY21 = new SVY21Coordinate(this.xCoord, this.yCoord);
        parkingLatLon = parkingSVY21.asLatLon();
    }

    public void setCarParkType(String carParkType) {
        this.carParkType = carParkType;
    }

    public void setParkingSystem(String parkingSystem) {
        this.parkingSystem = parkingSystem;
    }

    public void setShortTermParking(String shortTermParking) {
        this.shortTermParking = shortTermParking;
    }

    public void setFreeParking(String freeParking) {
        this.freeParking = freeParking;
    }

    public void setNightParking(String nightParking) {
        this.nightParking = nightParking;
    }

    public void setCarParkDecks(String carParkDecks) {
        this.carParkDecks = carParkDecks;
    }

    public void setGantryHeight(String gantryHeight) {
        this.gantryHeight = gantryHeight;
    }

    public void setCarParkBasement(String carParkBasement) {
        this.carParkBasement = carParkBasement.substring(0,carParkBasement.length()-2);
    }

    public String toString() {
        return "" + distanceApart;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj instanceof Carpark) {
            Carpark compareObj = (Carpark) obj;
            return compareObj.getCarParkNo().equals(this.getCarParkNo());
        } else {
            return false;
        }
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getCarParkBasement() {
        return carParkBasement;
    }

    public String getGantryHeight() {
        return gantryHeight;
    }

    public String getCarParkDecks() {
        return carParkDecks;
    }

    public String getNightParking() {
        return nightParking;
    }

    public String getFreeParking() {
        return freeParking;
    }

    public String getShortTermParking() {
        return shortTermParking;
    }

    public String getParkingSystem() {
        return parkingSystem;
    }

    public String getCarParkType() {
        return carParkType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
