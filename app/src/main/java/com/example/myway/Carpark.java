package com.example.myway;

// Arraylist fields all correspond in index.

import android.util.Log;

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
    private String parkingSystem;
    private double distanceApart; // Distance from current location
    private Integer availableLots;

    public boolean isCentralCarpark() {
        return centralCarpark;
    }

    private boolean centralCarpark;
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
            for (int i=0; i<HDBCentral.length; i++) {
                if(carParkNo.equals(HDBCentral[i])) {
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

//        @Override
//        public String calculate(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
//            Log.d("Error >>>>", "Carpark type not specified");
//            return "";
//        }

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
        public String newCalc(String date, String currentDay, int currentTime, int finalTime) {
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

        @Override
        public String newCalc(String date, String currentDay, int currentTime, int finalTime) {
            PublicHolidays phChecker = new PublicHolidays();
            String nextDay = null;
            if(finalTime <= currentTime) {
                int index = 0;
                for (int i=0; i<dayArray.length; i++) {
                    if (dayArray[i].equals(currentDay)) {
                        index = i+1;
                    }
                }
                nextDay = dayArray[index % 7];
            }
            double cost = 0.0;
            if (currentDay.equals("Sunday") || phChecker.isItPH(date)) {
                // day of entry is sunday or ph
                if (nextDay != null) {
                    // in and out on different days
                    // 1. newCalc(currentTime till 2230)
                    // 2. newCalc(0700 till finalTime)
                    // 1 && 2 positive -> enter before 2230 and leave after 0700 next day +$5
                    // 1 positive 2 negative -> enter before 2230 and leave before 0700 next day -> if num hours btwn 2230 and finalTime > 4.5 +$5 else normal
                    // 1 negative 2 positive -> enter after 2230 and leave after 0700 next day -> if num hours btwn 2230 and finalTime > 4.5 + $5 else normal
                    // 1 && 2 negative -> enter after 2230 and leave before 0700 the next day -> if num hours btwn 2230 and finalTime > 4.5 + $5 else normal
                    // need get nextDay's date by calculation
                } else {
                    // in and out on same day
                    if (currentTime >= 700 && finalTime <= 2230) {
                        // in and out within free parking range
                        return "free parking";
                    } else {
                        // 1. in before 0700 (x), out before 2230
                        // 2. in before 0700 (x), out after 2230 (x)
                        // 3. in after 0700, out after 2230 (x)
                        // above 3 conditions only affect cp with night parking
                        if (this.getNightParking().equals("YES")) {
                            // there is night parking
                            if (currentTime < 0700) {
                                if (finalTime < 2230) {
                                    // 1. in before 0700 (x), out before 2230
                                    int minutesBefore0700 = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(0700));
                                } else {
                                    // 2. in before 0700 (x), out after 2230 (x)
                                    int minutesBefore0700 = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(0700));
                                    int minutesAfter2230 = calculateTimeDifference(String.valueOf(2230), String.valueOf(finalTime));
                                }
                            } else {
                                // 3. in after 0700, out after 2230 (x)
                                int minutesAfter2230 = calculateTimeDifference(String.valueOf(2230), String.valueOf(finalTime));

                            }
                        } else {
                            // there is no night parking
                            int minutesApart = calculateTimeDifference(String.valueOf(currentTime), String.valueOf(finalTime));
                            if (this.getParkingSystem().equals("ELECTRONIC PARKING")) {
                               if (checkNonGrace(this.getCarParkNo())) {
                                   // NO NIGHT + ELECTRONIC + NO GRACE
                                   cost = minutesApart * (0.60 / 30);
                               } else {
                                   // NO NIGHT + ELECTRONIC + GRACE
                                   cost = (minutesApart - 10) * (0.60 / 30);
                               }
                            } else {
                                // no need check grace here as intervals are in 15mins and with grace also 5min -> 30min
                                // NO NIGHT + COUPON
                                int remainder = minutesApart % 30;
                                if (remainder == 0) {
                                    // duration is divisible by 30mins
                                    cost = (minutesApart / 30) * 0.60;
                                } else {
                                    // duration is non divisible by 30mins
                                    cost = ((minutesApart / 30) + 1) * 0.60;
                                }
                            }
                        }
                    }
                }
            } else {
                // day of entry is non sunday or ph
                if (nextDay != null) {
                    // in and out on different days
                    // 1. newCalc(currentTime till 2230)
                    // 2. newCalc(0700 till finalTime)
                    // 1 && 2 positive -> enter before 2230 and leave after 0700 next day +$5
                    // 1 positive 2 negative -> enter before 2230 and leave before 0700 next day -> if num hours btwn 2230 and finalTime > 4.5 +$5 else normal
                    // 1 negative 2 positive -> enter after 2230 and leave after 0700 next day -> if num hours btwn 2230 and finalTime > 4.5 + $5 else normal
                    // 1 && 2 negative -> enter after 2230 and leave before 0700 the next day -> if num hours btwn 2230 and finalTime > 4.5 + $5 else normal
                    // need get nextDay's date by calculation
                } else {
                    // in and out on same day
                    if (this.isCentralCarpark()  ) {
                        // it is a central carpark
                    }
                }
            }

            return "";
        }

//        @Override
//        public String calculate(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
//            PublicHolidays ph = new PublicHolidays();
//            String nextDay = null;
//            if (finalTime <= currentTime) {
//                int index = 0;
//                for (int i = 0; i < dayArray.length; i++) {
//                    if (dayArray[i].equals(currentDay)) {
//                        index = i;
//                    }
//                }
//                nextDay = dayArray[(index + 1) % 7];
//            }
//
//            if (currentDay.equals("Sunday") || ph.isItPH(date)) {
//                if (nextDay != null) {
//
//                } else {
//                    if (currentTime >= 700 && finalTime <= 2230) {
//                        return "free parking";
//                    } else {
//                        int before0700 = 0;
//                        int after2230 = 0; // This will be spillover timing to next day night parking (e.g. 2230 - 0000 has 1h 30 mins of night parking fee included in next day 0000 - xxxx hour)
//                        if (currentTime <= 700) before0700 = calculateTimeDifference(String.valueOf(700), String.valueOf(currentTime));
//                        if (finalTime >= 2230) after2230 = calculateTimeDifference(String.valueOf(2230), String.valueOf(finalTime));
//
//                        // The timing we charge for before 0700 if we base on coupon 30 mins charge
//                        double before0700coupon = (before0700 % 30 != 0) ? (before0700 / 30.0) + 1.0 : before0700 / 30.0;
//
//                        // The price we charge for before 0700 if we base on coupon 30 mins charge
//                        before0700coupon = this.getNightParking().equals("YES") ? (before0700coupon * 0.60 > 5 ? 5.0 : before0700coupon * 0.60) : before0700coupon * 0.60;
//
//                        double minuteRate = 0.60 / 30;
//                        final double flatRateGracePeriod = ((before0700 - 10) * minuteRate) + after2230 * minuteRate;
//                        double totalCharged = this.getParkingSystem().equals("ELECTRONIC PARKING") ? (!checkNonGrace(this.getCarParkNo())
//                                                                                                        ? (this.getNightParking().equals("YES")
//                                                                                                        // Grace period + Electronic + Night parking
//                                                                                                            ? (((before0700 - 10) * minuteRate) > 5 ? 5.0 + after2230 * minuteRate : flatRateGracePeriod)
//                                                                                                        // Grace period + Electronic + No Night Parking
//                                                                                                            : flatRateGracePeriod)
//                                                                                                        : this.getNightParking().equals("YES")
//                                                                                                        // No grace period + Electronic + night parking
//                                                                                                            ? ((before0700 * minuteRate) > 5 ? 5.0 + after2230 * minuteRate : (before0700 + after2230) * minuteRate)
//                                                                                                        // No grace period + Electronic + no night parking
//                                                                                                            : (before0700 + after2230) * minuteRate)
//                                                                                                   : (after2230 % 30) != 0
//                                                                                                        ? (((after2230 / 30.0) + 1.0) * 0.60) + before0700coupon
//                                                                                                        : ((after2230 / 30.0) * 0.60) + before0700coupon;
//
////                         0100 to 2300 0100 to 0700 -> $5. 2230 - 2300 --> $5 + $0.60 = $5.60
//                        return String.format("%.2f", totalCharged);
//                    }
//                }
//            }
//            return null;
//        }

        private boolean checkNonGrace(String cpNo) {
            for (int i = 0; i < nonGracePeriodCarParks.length; i++) {
                if (nonGracePeriodCarParks[i].equals(cpNo)) {
                    return true;
                }
            }
            return false;
        }

        // maybe need to check for electronic or coupon parking as well as special rates such as 0.6/1.2
        // assumption here is $0.60/30mins and electronic parking

//        public String calculateHDB(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
//            double cost = (numHours * 1.2) + ((numMinutes / 30.0) * 0.6);
//            PublicHolidays phChecker = new PublicHolidays();
//            Log.d("Check park system", "Electronic or coupon " + this.getParkingSystem());
//            if (this.getParkingSystem().equals("ELECTRONIC PARKING")) {
//                if (shortTermParking.equals("7AM-10.30PM")) {
//                    if (freeParking.equals("NO")) {
//                        return String.format("est. $%.2f", cost);
//                    } else {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 700 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    }
//                } else if (shortTermParking.equals("7AM-7PM")) {
//                    if (freeParking.equals("NO")) {
//                        return String.format("est. $%.2f", cost);
//                    } else {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 700 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    }
//                } else if (shortTermParking.equals("NO")) {
//                    if (freeParking.equals("NO") && nightParking.equals("YES")) {
//                        if ((currentTime >= 2230 || currentTime <= 700) && finalTime <= 700 && cost > 5.0) {
//                            cost = 5.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    } else if (freeParking.equals("NO") && nightParking.equals("NO")) {
//                        return String.format("est. $%.2f", cost);
//                    } else if (freeParking.equals("SUN & PH 7AM-10.30PM") && nightParking.equals("YES")) {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 700 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        } else if ((currentTime >= 2230 || currentTime <= 700) && finalTime <= 700 && cost > 5.0) {
//                            cost = 5.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    } else {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 700 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    }
//                } else {
//                    if (freeParking.equals("NO")) {
//                        if ((currentTime >= 2230 || currentTime <= 700) && finalTime <= 700 && cost > 5.0) {
//                            cost = 5.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    } else if (freeParking.equals("SUN & PH 1PM-10.30PM")) {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 1300 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        } else if ((currentTime >= 2230 || currentTime <= 700) && finalTime <= 700 && cost > 5.0) {
//                            cost = 5.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    } else {
//                        if ((currentDay.equals("Sunday") || phChecker.isItPH(date)) && (currentTime >= 700 && finalTime <= 2230)) {
//                            cost = 0.0;
//                        } else if ((currentTime >= 2230 || currentTime <= 700) && finalTime <= 700 && cost > 5.0) {
//                            cost = 5.0;
//                        }
//                        return String.format("est. $%.2f", cost);
//                    }
//                }
//            } else {
//                return "COUPON PARKING"; // need to round off.
//            }
//        }
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

//        @Override
//        public String calculate(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
//            Log.d("Error >>>>", "Carpark type not specified");
//            return "";
//        }

        @Override
        public String newCalc(String date, String currentDay, int currentTime, int finalTime) {
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

    public String toString() {
        return "" + carParkNo;
    }

    // given startTime and endTime in HHMM, returns minutes apart.
    protected static int calculateTimeDifference(String startTime, String endTime) {
        // 0530 - 0700 (END > START)
        if (startTime.length() <= 3) startTime = "0" + startTime;
        if (endTime.length() <= 3) endTime = "0" + endTime;

        // 05 AND 30
        Integer startHour = Integer.parseInt(startTime.substring(0, 1));
        Integer startMin = Integer.parseInt(startTime.substring(2));

        // 07 AND 00
        Integer endHour = Integer.parseInt(endTime.substring(0, 1));
        Integer endMin = Integer.parseInt(endTime.substring(2));

        Integer differenceInTime = 0;

        if (endMin < startMin) {
            differenceInTime = (endHour - startHour - 1) * 60 + (startMin - endMin);
        }  else {
            differenceInTime = (endHour - startHour) * 60 + (endMin - startMin);
        }

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

//    public String calculate(String date, String currentDay, int currentTime, int numHours, int numMinutes, int finalTime) {
//        Log.d("Error >>>>", "Carpark type not specified");
//        return "";
//    }

    public String newCalc(String date, String currentDay, int currentTime, int finalTime) {
        return "";
    }
}
