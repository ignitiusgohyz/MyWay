package com.example.myway;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class generateHDBDetails {

    protected static ArrayList<Carpark> HDBList;

    protected static ArrayList<Carpark> getList() {
        return HDBList;
    }

    public generateHDBDetails() {

    }

    protected static void setHDBList(ArrayList<Carpark> hdbList) {
        HDBList = hdbList;
    }

    protected ArrayList<Carpark> readHDBParkingData(InputStream hdbparking) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(hdbparking, StandardCharsets.UTF_8));
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

//                Log.d("COORDINATES X --> ", carpark.getAddress() + " " + Double.parseDouble(tokens[2].replace("\"", "")) + " Y:" + Double.parseDouble(tokens[3].replace("\"", "")));
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
            Log.wtf("HDB", "Error reading data file on line" + line , e);
            e.printStackTrace();
        }
        return temp;
    }

    protected static void fillCPDistances(SVY21Coordinate destination) {
        if (HDBList != null) {
            for (Carpark carpark : HDBList) {
                double distance1 = carpark.getParkingSVY21().getNorthing() - destination.getEasting();
                double distance2 = carpark.getParkingSVY21().getEasting() - destination.getNorthing();
                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                carpark.setDistanceApart(Math.abs(distanceApart));
            }
        }
    }
}
