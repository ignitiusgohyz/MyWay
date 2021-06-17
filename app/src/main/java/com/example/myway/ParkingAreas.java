package com.example.myway;

public class ParkingAreas {

    private String carParkNo;
    private String address;
//    private double xCoord;
//    private double yCoord;
//    tried Double.parseDouble but keep getting numberformatexception. passed as String first
    private String xCoord;
    private String yCoord;
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

    public ParkingAreas() {
    }

    public void setCarParkNo(String carParkNo) {
        this.carParkNo = carParkNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setXCoord(String xCoord) {
        this.xCoord = xCoord;
    }

    public void setYCoord(String yCoord) {
        this.yCoord = yCoord;
//        parkingLatLon = new LatLonCoordinate(Double.parseDouble(this.xCoord), Double.parseDouble(this.yCoord));
//        parkingSVY21 = parkingLatLon.asSVY21();
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
        this.carParkBasement = carParkBasement;
    }

    public String toString() {
        return xCoord + " " + yCoord;
    }
}
