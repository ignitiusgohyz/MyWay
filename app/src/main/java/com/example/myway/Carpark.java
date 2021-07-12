package com.example.myway;

// Arraylist fields all correspond in index.

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("LogNotTimber")
public class Carpark {

    private final String carParkNo; // URA and HDB different codes
    private final String address; // URA and HDB different addresses
    private final double xCoord; // Set to 0.0 if doesn't exist
    private final double yCoord; // Set to 0.0 if doesn't exist
    private final double SVY21xCoord; // Easting
    private final double SVY21yCoord; // Northing
    private final LatLonCoordinate parkingLatLon;
    private final SVY21Coordinate parkingSVY21;
    private final String parkingSystem;
    private double distanceApart; // Distance from current location
    private Integer availableLots;
    private double price;
    private String duration;
    protected static final String freeParkingCost = "0.00";
    protected final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    public boolean isCentralCarpark() {
        return centralCarpark;
    }

    private final boolean centralCarpark;
    private static final String[] dayArray = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public Carpark(String carParkNo, String address, double SVY21xCoord, double SVY21yCoord, String parkingSystem) {
        this.carParkNo = carParkNo.replace("\"", "");
        this.centralCarpark = centralCheck(carParkNo);
        this.address = address;
        this.SVY21xCoord = SVY21xCoord;
        this.SVY21yCoord = SVY21yCoord;
        this.parkingSystem = parkingSystem;
        parkingSVY21 = new SVY21Coordinate(SVY21xCoord, SVY21yCoord);
        parkingLatLon = parkingSVY21.asLatLon();
        xCoord = parkingLatLon.getLongitude();
        yCoord = parkingLatLon.getLatitude();
    }

    private boolean centralCheck(String carParkNo) {
        String[] HDBCentral = new String[] {"ACB", "BBB", "BRB1", "CY", "DUXM", "HLM", "KAB", "KAM", "KAS", "PRM", "SLS", "SR1", "SR2", "TPM", "UCS", "WCB"};
        if (this instanceof Carpark.HDB) {
            for (String s : HDBCentral) {
                if (carParkNo.equals(s)) {
                    return true;
                }
            }
            return false;
        } else if (this instanceof Carpark.URA) {
            return false;
        } else {
            return false;
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public static class URA extends Carpark {

        private final ArrayList<String> weekdayMin = new ArrayList<>();
        private final ArrayList<String> weekdayRate = new ArrayList<>();
        private final ArrayList<String> startTime = new ArrayList<>();
        private final ArrayList<String> endTime = new ArrayList<>();
        private final ArrayList<String> satdayMin = new ArrayList<>();
        private final ArrayList<String> satdayRate = new ArrayList<>();
        private final ArrayList<String> sunPHMin = new ArrayList<>();
        private final ArrayList<String> sunPHRate = new ArrayList<>();
        private final ArrayList<String> parkCapacity = new ArrayList<>();
        private final ArrayList<String> vehCat = new ArrayList<>();
        private String remarks;

        public URA(String cPN, String a, double svyX, double svyY, String pS) {
            super(cPN, a, svyX, svyY, pS);
        }

        public String getFormattedRates() {
            StringBuilder ret = new StringBuilder();
            int i = 0;
            for (String vehicle : vehCat) {
                if (vehicle.equals("Car")) {
                    String weekdayMinimum = weekdayMin.get(i);
                    String weekdayRates = weekdayRate.get(i);
                    String saturdayMinimum = satdayMin.get(i);
                    String saturdayRate = satdayRate.get(i);
                    String sundayMinimum = sunPHMin.get(i);
                    String sundayRate = sunPHRate.get(i);
                    String effectiveStart = startTime.get(i);
                    String effectiveEnd = endTime.get(i);
                    ret.append("Weekday: ").append(weekdayRates).append(" / ").append(weekdayMinimum).append("\n");
                    ret.append("Saturday: ").append(saturdayRate).append(" / ").append(saturdayMinimum).append("\n");
                    ret.append("Sunday / PH: ").append(sundayRate).append(" / ").append(sundayMinimum).append("\n");
                    ret.append("Time Period: ").append(effectiveStart).append(" - ").append(effectiveEnd).append("\n\n");
                    i++;
                }
            }
            return ret.toString();
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

        @Override
        public String calculateRates(String date, String currentDay, int currentTime, int finalTime, boolean second) {
            return "";
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
        private static final String[] nonGracePeriodCarParks = new String[] { "HG55", "HG97", "HG47"};

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

        @SuppressLint("LogNotTimber")
        @Override
        public String calculateRates(String date, String currentDay, int currentTime, int finalTime, boolean second) {
            PublicHolidays phChecker = new PublicHolidays();
            String nextDay = null;
            if (finalTime <= currentTime && !second) {
                int index = 0;
                for (int i=0; i<dayArray.length; i++) {
                    if (dayArray[i].equals(currentDay)) {
                        index = i+1;
                    }
                }
                nextDay = dayArray[index % 7];
            }

            double cost;

            if (currentDay.equals("Sunday") || phChecker.isItPH(date)) {
                // day of entry is sunday or ph
                if (nextDay != null) {
                    double firstDayCost = Double.parseDouble(calculateRates(date, currentDay, currentTime, 2230, true));
                    Log.d("FIRSTDAY SUN PH>>>>>>", Double.toString(firstDayCost));
                    Log.d("SECONDDAY SUN PH>>>>>>", calculateRates(findNextDate(), nextDay, 700, finalTime, true));
                    double secondDayCost = Double.parseDouble(calculateRates(findNextDate(), nextDay, 700, finalTime, true));
                    int inBetweenMinutes;
                    if (secondDayCost == 0) {
                        inBetweenMinutes = calculateTimeDifference(String.valueOf(2230), String.valueOf(finalTime + 2400));
                        secondDayCost = calculateNightParking(this, inBetweenMinutes, false);
                    }

                    return decimalFormat.format(firstDayCost + secondDayCost);
                } else {
                    // in and out on same day
                    if (after0700_before2230(currentTime, finalTime)) {
                        // Free parking since in after 7am and out before 10.30pm
                        Log.d("Free Parking >>>", "FREE");
                        return Carpark.freeParkingCost;
                    } else {

                        int minutesBefore = 0;
                        int minutesAfter = 0;

                        if (bothBefore0700(currentTime, finalTime) || bothAfter2230(currentTime, finalTime)) {
                            minutesBefore = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                        } else if (before0700_before2230(currentTime, finalTime)) {
                            minutesBefore = calculateTimeDifference(String.valueOf(currentTime), "0700");
                        } else if (before0700_after2230(currentTime, finalTime)) {
                            minutesBefore = calculateTimeDifference(String.valueOf(currentTime), "0700");
                            minutesAfter = calculateTimeDifference("2230", String.valueOf(finalTime));
                        } else {
                            minutesAfter = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                        }

                        Log.d("Minutes Before ---->", Integer.toString(minutesBefore));
                        Log.d("Minutes After ---->", Integer.toString(minutesAfter));

                        if (hasNightParking(this)) {
                            // There is night parking
                            if (this.hasNoGracePeriod() || !isElectronicParking(this)) {
                                cost = calculateNightParking(this, minutesBefore, false)
                                        + calculateNightParking(this, minutesAfter, false);
                            } else {
                                cost = calculateNightParking(this, minutesBefore, true)
                                        + calculateNightParking(this, minutesAfter, false);
                            }
                        } else {
                            // There is NO night parking
                            if (this.hasNoGracePeriod() || isElectronicParking(this)) {
                                cost = calculateNormalParking(this, minutesBefore, false)
                                        + calculateNormalParking(this, minutesAfter, false);
                            } else {
                                cost = calculateNormalParking(this, minutesBefore, true)
                                        + calculateNormalParking(this, minutesAfter, false);
                            }
                        }

                        Log.d("Single Day Cost ------>", Double.toString(cost));
                    }
                }
            } else {
                // day of entry is non sunday or ph
                if (nextDay != null) {
                    double firstDayCost = Double.parseDouble(calculateRates(date, currentDay, currentTime, 2230, true));
                    Log.d("FIRSTDAY WEEKDAY>>>>>>", Double.toString(firstDayCost));
                    double secondDayCost = Double.parseDouble(calculateRates(findNextDate(), nextDay, 700, finalTime, true));
                    Log.d("SECONDDAY WEEKDAY>>>>>>", Double.toString(secondDayCost));
                    int inBetweenMinutes;
                    if (secondDayCost == 0) {
                        inBetweenMinutes = calculateTimeDifference(String.valueOf(2230), String.valueOf(finalTime + 2400));
                        secondDayCost = calculateNightParking(this, inBetweenMinutes, false);
                    }

                    return decimalFormat.format(firstDayCost + secondDayCost);
                } else {
                    // in and out on same day
                    int minutesDay = 0;
                    int minutesNight = 0;
                    int minutesPeak = 0;

                    Log.d("Current & Final Time>>>", currentTime + " AND " + finalTime);

                    if (endBefore0700(currentTime, finalTime)) {
                        return Carpark.freeParkingCost;
                    }

                    if (bothBefore0700(currentTime, finalTime)) {
                        minutesNight = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                    } else if (within1700AND2230(currentTime, finalTime)) {
                        minutesDay = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                    } else if (before0700_before1700(currentTime, finalTime)) {
                        minutesNight = calculateTimeDifference(String.valueOf(currentTime), "0700");
                        minutesPeak = calculateTimeDifference("0700", String.valueOf(finalTime));
                    } else if (bothBefore1700(currentTime, finalTime)) {
                        minutesPeak = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                    } else if (before1700_before2230(currentTime, finalTime)) {
                        minutesPeak = calculateTimeDifference(String.valueOf(currentTime), "1700");
                        minutesDay = calculateTimeDifference("1700", String.valueOf(finalTime));
                    } else if (before0700_after2230(currentTime, finalTime)) {
                        minutesDay = calculateTimeDifference("1700", "2230") + calculateTimeDifference("2230", String.valueOf(finalTime));
                        minutesNight = calculateTimeDifference(String.valueOf(currentTime), "0700");
                        minutesPeak = calculateTimeDifference("0700", "1700");
                    } else if (before1700_after2230(currentTime, finalTime)) {
                        minutesPeak = calculateTimeDifference(String.valueOf(currentTime), "1700");
                        minutesDay = calculateTimeDifference("1700", String.valueOf(finalTime));
                    }

                    Log.d("Minutes Day ---->", Integer.toString(minutesDay));
                    Log.d("Minutes Night ---->", Integer.toString(minutesNight));
                    Log.d("Minutes Peak ---->", Integer.toString(minutesPeak));

                    if (this.hasNoGracePeriod() || !isElectronicParking(this)) {
                        cost = calculateNightParking(this, minutesNight, false)
                                + calculatePeakParking(this, minutesPeak, false)
                                + calculateNormalParking(this, minutesDay, false);
                    } else {
                        cost = calculateNightParking(this, minutesNight, true)
                                + calculatePeakParking(this, minutesPeak, false)
                                + calculateNormalParking(this, minutesDay, false);
                    }
                }
            }

            return decimalFormat.format(cost);
        }

        private boolean hasNoGracePeriod() {
            String cpNo = this.getCarParkNo();
            for (String nonGracePeriodCarPark : nonGracePeriodCarParks) {
                if (nonGracePeriodCarPark.equals(cpNo)) {
                    return true;
                }
            }
            return false;
        }

        private double calculateNightParking(HDB hdb, int minutes, boolean grace) {
            if (minutes == 0) {
                return 0.0;
            }

            if (hdb.getParkingSystem().equals("ELECTRONIC PARKING")) {
                double cost;
                if (grace) {
                    cost = (minutes - 10) * 0.60 / 30.0;
                } else {
                    cost = minutes * 0.60 / 30.0;
                }
                return Math.min(cost, 5.0);
            } else { // Coupon Parking
                int minutesCharged = (minutes % 30) == 0 ? (minutes / 30) : (minutes / 30) + 1;
                double cost = minutesCharged * 0.60;
                return Math.min(cost, 5.0);
            }
        }

        private double calculateNormalParking(HDB hdb, int minutes, boolean grace) {
            if (minutes == 0) {
                return 0.0;
            }

            double rate = 0.60;

            if (hdb.getParkingSystem().equals("ELECTRONIC PARKING")) {
                double cost;
                if (grace) {
                    cost = (minutes - 10) * rate / 30.0;
                } else {
                    cost = minutes * rate / 30.0;
                }
                return cost;
            } else { // Coupon Parking
                int minutesCharged = (minutes % 30) == 0 ? (minutes / 30) : (minutes / 30) + 1;
                return minutesCharged * rate;
            }
        }

        private double calculatePeakParking(HDB hdb, int minutes, boolean grace) {
            if (minutes == 0) {
                return 0.0;
            }
            double rate = 0.60;
            if (hdb.isCentralCarpark()) {
                rate = 1.20;
            }
            if (hdb.getParkingSystem().equals("ELECTRONIC PARKING")) {
                double cost;
                if (grace) {
                    cost = (minutes - 10) * rate / 30.0;
                } else {
                    cost = minutes * rate / 30.0;
                }
                return cost;
            } else { // Coupon Parking
                int minutesCharged = (minutes % 30) == 0 ? (minutes / 30) : (minutes / 30) + 1;
                return minutesCharged * rate;
            }
        }
    }

    protected boolean endBefore0700(int start, int end) {
        return start == 700 && end < 700;
    }

    protected boolean before1700_after2230(int start, int end) {
        return start <= 1700 && end > 2230;
    }

    protected boolean within1700AND2230(int start, int end) {
        return 1700 <= start && 1700 <= end && 2230 >= start && 2230 >= end;
    }

    protected boolean before1700_before2230(int start, int end) {
        return start < 1700 && end <= 2230;
    }

    protected boolean isElectronicParking(HDB hdb) {
        return hdb.getParkingSystem().equals("ELECTRONIC PARKING");
    }

    protected boolean before0700_before1700(int start, int end) {
        return start < 700 && end <= 1700;
    }

    protected boolean bothBefore0700(int start, int end) {
        return start < 700 && end < 700;
    }

    protected boolean bothBefore1700(int start, int end) {
        return start <= 1700 && end <= 1700;
    }

    protected boolean bothAfter2230(int start, int end) {
        return start > 2230 && end > 2230;
    }

    protected boolean after0700_before2230(int start, int end) {
        return start >= 700 && end <= 2230;
    }

    protected boolean after0700_after2230(int start, int end) {
        return start >= 700 && end > 2230;
    }

    protected boolean before0700_before2230(int start, int end) {
        return start < 700 && end <= 2230;
    }

    protected boolean before0700_after2230(int start, int end) {
        return start < 700 && end > 2230;
    }

    protected boolean hasNightParking(HDB hdb) {
        return hdb.getNightParking().equals("YES");
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

        @Override
        public String calculateRates(String date, String currentDay, int currentTime, int finalTime, boolean second) {
            return "";
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

    public @NotNull String toString() {
        return "" + carParkNo;
    }

    // given startTime and endTime in HHMM, returns minutes apart.
    protected static int calculateTimeDifference(String startTime, String endTime) {
        // 0530 - 0700 (END > START)
        // 2300 - 2230 (START > END)
        if (startTime.length() <= 3) startTime = "0" + startTime;
        if (endTime.length() <= 3) endTime = "0" + endTime;

        Log.d("STARTTIME>>>>>>>>>>>>>>>>", startTime);

        // 05 AND 30
        // 23 AND 00
        Integer startHour = Integer.parseInt(startTime.substring(0, 2)); // 2230    2230    2258
        int startMin = Integer.parseInt(startTime.substring(2)); //     2313    2429    2401

        // 07 AND 00
        // 22 AND 30
        Integer endHour = Integer.parseInt(endTime.substring(0, 2)); //
        int endMin = Integer.parseInt(endTime.substring(2)); //

        Log.d("TIMING CHECK>>>>>>>>>>", startHour.toString() + startMin + " " + endHour.toString() + endMin);

        int differenceInTime;

        if (endMin < startMin) {
            differenceInTime = (endHour - startHour - 1) * 60 + (60 - startMin + endMin);
        }  else {
            differenceInTime = (endHour - startHour) * 60 + (endMin - startMin);
        }
        Log.d("DIFFERENCE IN TIME IN METHOD >>>>", Integer.toString(differenceInTime));

        return differenceInTime;
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

    public String calculateRates(String date, String currentDay, int currentTime, int finalTime, boolean second) {
        return "";
    }

    public String findNextDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        long nextDayMilliSeconds = currentDate.getTime() + (24 * 60 * 60 * 1000);
        return dateFormat.format(new Date(nextDayMilliSeconds));
    }
}