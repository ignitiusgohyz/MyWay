package com.example.myway;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class generateURADetails {

    protected static ArrayList<Carpark> URAList;

    public static void setList(ArrayList<Carpark> list) {
        URAList = list;
    }

    public static ArrayList<Carpark> getURAList() {
        return URAList;
    }

    protected static void fillCPDistances(SVY21Coordinate destination) {
        if (URAList != null) {
            for (Carpark carpark : URAList) {
                double distance1 = carpark.getParkingSVY21().getNorthing() - destination.getEasting();
                double distance2 = carpark.getParkingSVY21().getEasting() - destination.getNorthing();
                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                carpark.setDistanceApart(Math.abs(distanceApart));
            }
        }
    }

    public static ArrayList<Carpark> getURACarparkDetails(InputStream uraParking) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(uraParking, StandardCharsets.UTF_8));
        String line = "";
        ArrayList<Carpark> temp = new ArrayList<>();
        ArrayList<String> carparkCodes = new ArrayList<>();
        try {
            reader.readLine();
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (carparkCodes.contains(tokens[2])) {
                    int index = carparkCodes.indexOf(tokens[2]);
                    Carpark retrieved = temp.get(index);
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
                    Carpark carpark = new Carpark();
                    carpark.setWeekdayMin(tokens[0]);
                    carpark.setWeekdayRate(tokens[1]);
                    carpark.setCarParkNo(tokens[2]);
                    carpark.setParkingSystem(tokens[3].equals("C") ? "COUPON PARKING" : "ELECTRONIC PARKING");
                    carpark.setAddress(tokens[4]);
                    carpark.setVehCat(tokens[5]);
                    carpark.setSatdayMin(tokens[6]);
                    carpark.setSatdayRate(tokens[7]);
                    carpark.setSunPHMin(tokens[8]);
                    carpark.setSunPHRate(tokens[9]);

                    String coordinate_string = tokens[10];
                    double x = parseCoordinates(coordinate_string, "x");
                    double y = parseCoordinates(coordinate_string, "y");
                    carpark.setSVY21xCoord(x);
                    carpark.setSVY21yCoord(y);

                    carpark.setStartTime(tokens[12]);
                    carpark.setParkCapacity(tokens[13]);
                    carpark.setEndTime(tokens[14]);
                    carpark.setRemarks(tokens[15]);
                    carpark.setOwnedBy("URA");
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

