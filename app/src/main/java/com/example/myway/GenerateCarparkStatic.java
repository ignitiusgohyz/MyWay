package com.example.myway;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SuppressLint("LogNotTimber")
public abstract class GenerateCarparkStatic {

    protected static ArrayList<Carpark> HDBList;
    protected static ArrayList<Carpark> URAList;
    protected static ArrayList<Carpark> LTAList;

    public GenerateCarparkStatic() {}

    public static class generateHDB extends GenerateCarparkStatic {
        public generateHDB() {}

        @Override
        protected ArrayList<Carpark> readCSV(InputStream inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            ArrayList<Carpark> temp = new ArrayList<>();
            try {
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\",\"");
                    Carpark.HDB carpark = new Carpark.HDB(tokens[0], tokens[1],
                            Double.parseDouble(tokens[2].replace("\"", "")),
                            Double.parseDouble(tokens[3].replace("\"", "")),
                            tokens[5]);
                    carpark.setCarParkType(tokens[4]);
                    carpark.setShortTermParking(tokens[6]);
                    carpark.setFreeParking(tokens[7]);
                    carpark.setNightParking(tokens[8]);
                    carpark.setCarParkDecks(tokens[9]);
                    carpark.setGantryHeight(tokens[10]);
                    carpark.setCarParkBasement(tokens[11]);
                    temp.add(carpark);
                }
                return temp;
            } catch (IOException e) {
                Log.d("HDB", "Error reading data file on line" + line , e);
                e.printStackTrace();
            }
            return temp;
        }

        @Override
        protected void fillCPDistances(LatLonCoordinate destination) {
            if (HDBList != null) {
                for (Carpark carpark : HDBList) {
//                    double distance1 = carpark.getParkingSVY21().getNorthing() - destination.getEasting();
//                    double distance2 = carpark.getParkingSVY21().getEasting() - destination.getNorthing();
//                    double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
//                    carpark.setDistanceApart(Math.abs(distanceApart));
                    double destinationLat = destination.getLatitude();
                    double destinationLng = destination.getLongitude();
                    double carparkLat = carpark.getParkingSVY21().asLatLon().getLatitude();
                    double carparkLng = carpark.getParkingSVY21().asLatLon().getLongitude();
//                    Log.d("Destination Lat Lng>>>>>", destinationLat + " AND " + destinationLng);
//                    Log.d("Carpark Lat Lng>>>>>", carparkLat + " AND " + carparkLng + " : " + carpark.getAddress());
                    carpark.setDistanceApart(distanceInMetres(destinationLat, destinationLng, carparkLat, carparkLng));
                }
            }
        }

        @Override
        protected double distanceInMetres(double destinationLat, double destinationLng, double carparkLat, double carparkLng) {
            double radiusOfEarth = 6378.137;
            double dLat = ((carparkLat * Math.PI) / 180.0) - ((destinationLat * Math.PI) / 180.0);
            double dLon = ((carparkLng * Math.PI) / 180.0) - ((destinationLng * Math.PI) / 180.0);
            double a = (Math.sin(dLat/2.0) * Math.sin(dLat/2.0)) +
                    (Math.cos((destinationLat * Math.PI) / 180.0) * Math.cos((carparkLat * Math.PI) / 180.0) *
                            Math.sin(dLon/2.0) * Math.sin(dLon/2.0));
            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = radiusOfEarth * c;
            return d * 1000.0;
        }

        @Override
        protected ArrayList<Carpark> getList() {
            return HDBList;
        }

        @Override
        protected void setList(ArrayList<Carpark> carparkArrayList) {
            HDBList = carparkArrayList;
        }
    }

    public static class generateURA extends GenerateCarparkStatic {
        public generateURA() {}

        @Override
        protected ArrayList<Carpark> readCSV(InputStream inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            ArrayList<Carpark> temp = new ArrayList<>();
            ArrayList<String> carparkCodes = new ArrayList<>();
            try {
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (carparkCodes.contains(tokens[2])) {
                        int index = carparkCodes.indexOf(tokens[2]);
                        Carpark.URA retrieved = (Carpark.URA) temp.get(index);
                        retrieved.setWeekdayMin(tokens[0]);
                        retrieved.setWeekdayRate(tokens[1]);
                        retrieved.setVehCat(tokens[5]);
                        retrieved.setSatdayMin(tokens[6]);
                        retrieved.setSatdayRate(tokens[7]);
                        retrieved.setSunPHMin(tokens[8]);
                        retrieved.setSunPHRate(tokens[9]);
                        retrieved.setStartTime(tokens[12]);
                        retrieved.setParkCapacity(tokens[13]);
                        retrieved.setEndTime(tokens[14]);
                    } else {

                        String coordinate_string = tokens[10];
                        double y = parseCoordinates(coordinate_string, "y");
                        double x = parseCoordinates(coordinate_string, "x");

                        Carpark.URA carpark = new Carpark.URA(tokens[2], tokens[4], x, y, tokens[3].equals("C") ? "COUPON PARKING" : "ELECTRONIC PARKING");
                        carpark.setWeekdayMin(tokens[0]);
                        carpark.setWeekdayRate(tokens[1]);
                        carpark.setVehCat(tokens[5]);
                        carpark.setSatdayMin(tokens[6]);
                        carpark.setSatdayRate(tokens[7]);
                        carpark.setSunPHMin(tokens[8]);
                        carpark.setSunPHRate(tokens[9]);
                        carpark.setStartTime(tokens[12]);
                        carpark.setParkCapacity(tokens[13]);
                        carpark.setEndTime(tokens[14]);
                        carpark.setRemarks(tokens[15]);

                        temp.add(carpark);
                        carparkCodes.add(tokens[2]);
                    }
                }
                return temp;
            } catch (IOException e) {
                Log.d("URA", "Error reading data file on line" + line , e);
                e.printStackTrace();
            }
            return temp;
        }

        @Override
        protected void fillCPDistances(LatLonCoordinate destination) {
            if (URAList != null) {
                for (Carpark carpark : URAList) {
                    double destinationLat = destination.getLatitude();
                    double destinationLng = destination.getLongitude();
                    double carparkLat = carpark.getParkingSVY21().asLatLon().getLatitude();
                    double carparkLng = carpark.getParkingSVY21().asLatLon().getLongitude();
                    carpark.setDistanceApart(distanceInMetres(destinationLat, destinationLng, carparkLat, carparkLng));
                }
            }
        }

        @Override
        protected ArrayList<Carpark> getList() {
            return URAList;
        }

        @Override
        protected void setList(ArrayList<Carpark> carparkArrayList) {
            URAList = carparkArrayList;
        }

        @Override
        protected double distanceInMetres(double destinationLat, double destinationLng, double carparkLat, double carparkLng) {
            double radiusOfEarth = 6378.137;
            double dLat = ((carparkLat * Math.PI) / 180.0) - ((destinationLat * Math.PI) / 180.0);
            double dLon = ((carparkLng * Math.PI) / 180.0) - ((destinationLng * Math.PI) / 180.0);
            double a = (Math.sin(dLat/2.0) * Math.sin(dLat/2.0)) +
                    (Math.cos((destinationLat * Math.PI) / 180.0) * Math.cos((carparkLat * Math.PI) / 180.0) *
                            Math.sin(dLon/2.0) * Math.sin(dLon/2.0));
            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = radiusOfEarth * c;
            return d * 1000.0;
        }

        private static double parseCoordinates(String coordinateString, String type) {
            if (coordinateString.equals("")) {
                return 0.0;
            }
            StringBuilder flatten = new StringBuilder();
            for (int i = 0; i < coordinateString.length(); i++) {
                if (coordinateString.charAt(i) != '\"') flatten.append(coordinateString.charAt(i));
            }
            StringBuilder x = new StringBuilder();
            StringBuilder y = new StringBuilder();
            boolean split = false;
            for (int i = 0; i < flatten.length(); i++) {
                char cursor = flatten.charAt(i);
                if (cursor == ',') {
                    split = true;
                } else if (split) {
                    y.append(cursor);
                } else {
                    x.append(cursor);
                }
            }
            return type.equals("x") ? Double.parseDouble(x.toString()) : Double.parseDouble(y.toString());
        }
    }

    public static class generateLTA extends GenerateCarparkStatic {
        public generateLTA() {}

        @Override
        protected double distanceInMetres(double destinationLat, double destinationLng, double carparkLat, double carparkLng) {
            double radiusOfEarth = 6378.137;
            double dLat = ((carparkLat * Math.PI) / 180.0) - ((destinationLat * Math.PI) / 180.0);
            double dLon = ((carparkLng * Math.PI) / 180.0) - ((destinationLng * Math.PI) / 180.0);
            double a = (Math.sin(dLat/2.0) * Math.sin(dLat/2.0)) +
                    (Math.cos((destinationLat * Math.PI) / 180.0) * Math.cos((carparkLat * Math.PI) / 180.0) *
                            Math.sin(dLon/2.0) * Math.sin(dLon/2.0));
            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = radiusOfEarth * c;
            return d * 1000.0;
        }

        @Override
        protected ArrayList<Carpark> readCSV(InputStream inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            ArrayList<Carpark> temp = new ArrayList<>();
            try {
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens[6].equals("LTA")) {
                        double y = parseCoordinates(tokens[3], "x");
                        double x = parseCoordinates(tokens[3], "y");
                        SVY21Coordinate svyCoordinate = new LatLonCoordinate(y, x).asSVY21();
                        Carpark.LTA carpark = new Carpark.LTA(tokens[0], tokens[2],
                                svyCoordinate.getEasting(),
                                svyCoordinate.getNorthing(),
                                tokens[5]);
                        carpark.setAvailableLots(Integer.parseInt(tokens[4]));

                        temp.add(carpark);
                    } else {
                        break;
                    }
                }
                return temp;
            } catch (IOException e) {
                Log.wtf("LTA", "Error reading data file on line" + line , e);
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return temp;
        }

        @Override
        protected void fillCPDistances(LatLonCoordinate destination) {
            if (LTAList != null) {
                for (Carpark carpark : LTAList) {
                    double destinationLat = destination.getLatitude();
                    double destinationLng = destination.getLongitude();
                    double carparkLat = carpark.getParkingSVY21().asLatLon().getLatitude();
                    double carparkLng = carpark.getParkingSVY21().asLatLon().getLongitude();
                    carpark.setDistanceApart(distanceInMetres(destinationLat, destinationLng, carparkLat, carparkLng));
                }
            }
        }

        @Override
        protected ArrayList<Carpark> getList() {
            return LTAList;
        }

        @Override
        protected void setList(ArrayList<Carpark> carparkArrayList) {
            LTAList = carparkArrayList;
        }

        private double parseCoordinates(String coordinates, String type) {
            if (coordinates.equals("")) {
                return 0.0;
            }

            StringBuilder x = new StringBuilder();
            StringBuilder y = new StringBuilder();
            boolean split = false;
            for (int i = 0; i < coordinates.length(); i++) {
                char cursor = coordinates.charAt(i);
                if (cursor == ' ') {
                    split = true;
                } else if (split) {
                    y.append(cursor);
                } else {
                    x.append(cursor);
                }
            }
            return type.equals("x") ? Double.parseDouble(x.toString()) : Double.parseDouble(y.toString());
        }
    }

    protected abstract ArrayList<Carpark> readCSV(InputStream inputStream);

    protected abstract void fillCPDistances(LatLonCoordinate destination);

    protected abstract ArrayList<Carpark> getList();

    protected abstract void setList(ArrayList<Carpark> carparkArrayList);

    protected abstract double distanceInMetres(double destinationLat, double destinationLng, double carparkLat, double carparkLng);
}
