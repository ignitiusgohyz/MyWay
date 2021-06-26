package com.example.myway;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class generateLTADetails {

    protected static ArrayList<Carpark> LTAList;

    protected static ArrayList<Carpark> getList() {
        return LTAList;
    }

    public generateLTADetails() {

    }

    protected static void setLTAList(ArrayList<Carpark> ltaList) {
        LTAList = ltaList;
    }

    protected ArrayList<Carpark> readLTAParkingData(InputStream ltaparking) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(ltaparking, StandardCharsets.UTF_8));
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

    protected static void fillCPDistances(SVY21Coordinate destination) {
        if (LTAList != null) {
            for (Carpark carpark : LTAList) {
                double distance1 = carpark.getParkingSVY21().getNorthing() - destination.getEasting();
                double distance2 = carpark.getParkingSVY21().getEasting() - destination.getNorthing();
                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                carpark.setDistanceApart(Math.abs(distanceApart));
            }
        }
    }
}
