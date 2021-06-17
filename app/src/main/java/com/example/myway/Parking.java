package com.example.myway;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Parking extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking);
        try {
            readHDBParkingData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ParkingAreas> parkingAreasList = new ArrayList<>();
    private void readHDBParkingData() throws IOException {
        InputStream hdbparking = getResources().openRawResource(R.raw.hdbparking);
        BufferedReader reader = new BufferedReader(new InputStreamReader(hdbparking, Charset.forName("UTF-8")));

        String line = "";
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                String[] tokens = line.split(",");
                //should we replace , with /n if its displaying of these information
                //useful if we accessing these information
                ParkingAreas parkingArea = new ParkingAreas();
                parkingArea.setCarParkNo(tokens[0]);
                parkingArea.setAddress(tokens[1]);
                parkingArea.setXCoord(tokens[2]);
                parkingArea.setYCoord(tokens[3]);
                parkingArea.setCarParkType(tokens[4]);
                parkingArea.setParkingSystem(tokens[5]);
                parkingArea.setShortTermParking(tokens[6]);
                parkingArea.setFreeParking(tokens[7]);
                parkingArea.setNightParking(tokens[8]);
                parkingArea.setCarParkDecks(tokens[9]);
                parkingArea.setGantryHeight(tokens[10]);
                parkingArea.setCarParkBasement(tokens[11]);
                parkingAreasList.add(parkingArea);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line , e);
            e.printStackTrace();
        }
    }


}
