package com.example.myway;

// Arraylist fields all correspond in index.

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Carpark {

    private String carParkNo; // URA and HDB different codes
    private String address; // URA and HDB different addresses
    private double xCoord; // Set to 0.0 if doesn't exist
    private double yCoord; // Set to 0.0 if doesn't exist
    private double SVY21xCoord; // Easting
    private double SVY21yCoord; // Northing
    private LatLonCoordinate parkingLatLon;
    private SVY21Coordinate parkingSVY21;
    private String parkingSystem; // B for electronic, C for coupon
    private double distanceApart; // Distance from current location
    private Integer availableLots;

    public Carpark(String carParkNo, String address, double SVY21xCoord, double SVY21yCoord, String parkingSystem) {
        this.carParkNo = carParkNo.replace("\"", "");
        this.address = address;
        this.SVY21xCoord = SVY21xCoord;
        this.SVY21yCoord = SVY21yCoord;
        this.parkingSystem = parkingSystem;
        parkingSVY21 = new SVY21Coordinate(SVY21xCoord, SVY21yCoord);
        parkingLatLon = parkingSVY21.asLatLon();
        xCoord = parkingLatLon.getLongitude();
        yCoord = parkingLatLon.getLatitude();
    }

    public static class URA extends Carpark {

        private ArrayList<String> weekdayMin = new ArrayList<>();
        private ArrayList<String> weekdayRate = new ArrayList<>();
        private ArrayList<String> startTime = new ArrayList<>();
        private ArrayList<String> endTime = new ArrayList<>();
        private ArrayList<String> satdayMin = new ArrayList<>();
        private ArrayList<String> satdayRate = new ArrayList<>();
        private ArrayList<String> sunPHMin = new ArrayList<>();
        private ArrayList<String> sunPHRate = new ArrayList<>();
        private ArrayList<String> parkCapacity = new ArrayList<>();
        private ArrayList<String> vehCat = new ArrayList<>();
        private String remarks;

        public URA(String cPN, String a, double svyX, double svyY, String pS) {
            super(cPN, a, svyX, svyY, pS);
        }

        public String getWeekdayMin(int index) {
            return this.weekdayMin.get(index);
        }

        public void setWeekdayMin(String weekdayMin) {
            this.weekdayMin.add(weekdayMin);
        }

        public String getWeekdayRate(int index) {
            return this.weekdayRate.get(index);
        }

        public void setWeekdayRate(String weekdayRate) {
            this.weekdayRate.add(weekdayRate);
        }

        public String getStartTime(int index) {
            return this.startTime.get(index);
        }

        public void setStartTime(String startTime) {
            this.startTime.add(startTime);
        }

        public String getEndTime(int index) {
            return this.endTime.get(index);
        }

        public void setEndTime(String endTime) {
            this.endTime.add(endTime);
        }

        public String getSatdayMin(int index) {
            return this.satdayMin.get(index);
        }

        public void setSatdayMin(String satdayMin) {
            this.satdayMin.add(satdayMin);
        }

        public String getSatdayRate(int index) {
            return this.satdayRate.get(index);
        }

        public void setSatdayRate(String satdayRate) {
            this.satdayRate.add(satdayRate);
        }

        public String getSunPHMin(int index) {
            return this.sunPHMin.get(index);
        }

        public void setSunPHMin(String sunPHMin) {
            this.sunPHMin.add(sunPHMin);
        }

        public String getSunPHRate(int index) {
            return this.sunPHRate.get(index);
        }

        public void setSunPHRate(String sunPHRate) {
            this.sunPHRate.add(sunPHRate);
        }

        public String getParkCapacity(int index) {
            return this.parkCapacity.get(index);
        }

        public void setParkCapacity(String parkCapacity) {
            this.parkCapacity.add(parkCapacity);
        }

        public String getVehCat(int index) {
            return this.vehCat.get(index);
        }

        public void setVehCat(String vehCat) {
            this.vehCat.add(vehCat);
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        @Override
        public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
            if (obj instanceof Carpark.URA) {
                Carpark.URA compareObj = (Carpark.URA) obj;
                return compareObj.getCarParkNo().equals(this.getCarParkNo());
            } else {
                return false;
            }
        }
    }

    public static class HDB extends Carpark {
        private String carParkType; // Multi-Storey, Basement etc.
        private String shortTermParking; // Timeframe, e.g. Whole Day
        private String freeParking; // Yes, No, or Timeframe
        private String nightParking; // Yes / No
        private String carParkDecks; // Number of decks
        private String gantryHeight; // Height of gantry
        private String carParkBasement; // Y / N

        public HDB(String cPN, String a, double svyX, double svyY, String pS) {
            super(cPN, a, svyX, svyY, pS);
        }

        public String getCarParkBasement() {
            return carParkBasement;
        }

        public void setCarParkBasement(String carParkBasement) {
            this.carParkBasement = carParkBasement;
        }

        public String getGantryHeight() {
            return gantryHeight;
        }

        public void setGantryHeight(String gantryHeight) {
            this.gantryHeight = gantryHeight;
        }

        public String getCarParkDecks() {
            return carParkDecks;
        }

        public void setCarParkDecks(String carParkDecks) {
            this.carParkDecks = carParkDecks;
        }

        public String getNightParking() {
            return nightParking;
        }

        public void setNightParking(String nightParking) {
            this.nightParking = nightParking;
        }

        public String getFreeParking() {
            return freeParking;
        }

        public void setFreeParking(String freeParking) {
            this.freeParking = freeParking;
        }

        public String getShortTermParking() {
            return shortTermParking;
        }

        public void setShortTermParking(String shortTermParking) {
            this.shortTermParking = shortTermParking;
        }

        public String getCarParkType() {
            return carParkType;
        }

        public void setCarParkType(String carParkType) {
            this.carParkType = carParkType;
        }

        @Override
        public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
            if (obj instanceof Carpark.HDB) {
                Carpark.HDB compareObj = (Carpark.HDB) obj;
                return compareObj.getCarParkNo().equals(this.getCarParkNo());
            } else {
                return false;
            }
        }

        // maybe need to check for electronic or coupon parking as well as special rates such as 0.6/1.2
        // assumption here is $0.60/30mins and electronic parking
        // PH is not checked as well -> i think got api
        public String calculateHDB(String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
            double cost = 0.0;
            if (shortTermParking.equals("7AM-10.30PM")) {
                if (freeParking.equals("NO")) {
                    cost = (numHours * 1.2) + ((numMinutes / 30.0) * 0.6);
                    return String.format("est. $%.2f", cost);
                } else { // sun and ph 7am-10.30pm
                    return shortTermParking + " " + freeParking + " " + nightParking;
                }
            } else if (shortTermParking.equals("7AM-7PM")) {
                if (freeParking.equals("NO")) {
                    cost = (numHours * 1.2) + ((numMinutes / 30.0) * 0.6);
                    return String.format("est. $%.2f", cost);
                } else {// sun and ph 7am-10.30pm
                    return shortTermParking + " " + freeParking + " " + nightParking;
                }
            } else if (shortTermParking.equals("NO")) {
                if (freeParking.equals("NO") && nightParking.equals("YES")) {
                    return shortTermParking + " " + freeParking + " " + nightParking;
                } else if (freeParking.equals("NO") && nightParking.equals("NO")) {
                    return shortTermParking + " " + freeParking + " " + nightParking;
                } else if (freeParking.equals("SUN & PH 7AM-10.30PM") && nightParking.equals("YES")) {
                    return shortTermParking + " " + freeParking + " " + nightParking;
                } else { // free parking sun and ph 7am-10.30pm && no night parking
                    return shortTermParking + " " + freeParking + " " + nightParking;
                }
            } else { // whole day short term parking
                if (freeParking.equals("NO")) {
                    return shortTermParking + " " + freeParking + " " + nightParking;
                } else if (freeParking.equals("SUN & PH 1PM-10.30PM")) {
                    return shortTermParking + " " + freeParking + " " + nightParking;
                } else { // free parking sun and ph 7am-10.30pm
                    return shortTermParking + " " + freeParking + " " + nightParking;
                }
            }
        }
    }

    public static class LTA extends Carpark {

        public LTA(String cPN, String a, double svyX, double svyY, String pS) {
            super(cPN, a, svyX, svyY, pS); // pS in this case would be the C - Car, Y - Motor, H - Heavy Vehicles
        }

        @Override
        public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
            if (obj instanceof Carpark.LTA) {
                Carpark.LTA compareObj = (Carpark.LTA) obj;
                return compareObj.getCarParkNo().equals(this.getCarParkNo());
            } else {
                return false;
            }
        }
    }

    public String getParkingSystem() {
        return parkingSystem;
    }

    public Integer getAvailableLots() {
        return availableLots;
    }

    public void setAvailableLots(Integer availableLots) {
        this.availableLots = availableLots;
    }

    public double getSVY21xCoord() {
        return SVY21xCoord;
    }

    public double getSVY21yCoord() { return SVY21yCoord; }

    public double getyCoord() {
        return yCoord;
    }


    public double getxCoord() {
        return xCoord;
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

    public String toString() {
        return "" + carParkNo;
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
}
