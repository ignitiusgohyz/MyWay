package com.example.myway;


public class ParkingAreas {

    private String carParkNo;
    private String address;
    private double xCoord;
    private double yCoord;
    private String carParkType;
    private String parkingSystem;
    private String shortTermParking;
    private String freeParking;
    private String nightParking;
    private String carParkDecks;
    private String gantryHeight;
    private String carParkBasement;
    private LatLonCoordinate parkingLatLon;
    private SVY21Coordinate parkingSVY21;
    private double distanceApart;

    public ParkingAreas() {
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
}
