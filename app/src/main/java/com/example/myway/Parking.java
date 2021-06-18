package com.example.myway;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.geojson.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Parking extends AppCompatActivity {

    private double destinationLng;
    private double destinationLat;
    private String destination;
    private TextView destination_display;
    private LatLonCoordinate destinationLatLon;
    private SVY21Coordinate destinationSVY21;
    private List<ParkingCardView> pcvArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking);
        Bundle bundle = getIntent().getExtras();
        destinationLat = bundle.getDouble("destinationLat");
        destinationLng = bundle.getDouble("destinationLng");

        destination = "Destination:\n" + bundle.getString("destination");
        destination_display = findViewById(R.id.fragment_parking_destination_text);
        destination_display.setText(destination);

        destinationLatLon = new LatLonCoordinate(destinationLat,destinationLng);
        destinationSVY21 = destinationLatLon.asSVY21();
        try {
            readHDBParkingData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // COMPARATOR FOR DISTANCE, IMPLEMENT FOR PRICE AND THE REST
        Collections.sort(parkingAreasList, (o1, o2) -> {
            if (o1.getDistanceApart() < o2.getDistanceApart()) {
                return -1;
            } else if(o1.getDistanceApart() > o2.getDistanceApart()) {
                return 1;
            } else {
                return 0;
            }
        });

        Log.d("MyActivity", "print: " + parkingAreasList.toString());
        getTopSixteenParkings();
    }

    private List<ParkingAreas> parkingAreasList = new ArrayList<>();
    private void readHDBParkingData() throws IOException {
        InputStream hdbparking = getResources().openRawResource(R.raw.hdbparking);
        BufferedReader reader = new BufferedReader(new InputStreamReader(hdbparking, Charset.forName("UTF-8")));

        String line = "";
        try {
            reader.readLine();
            int i = 1;
            while ((line = reader.readLine()) != null) {
//                Log.d("MyActivity", "Line: " + line);
                String[] tokens = line.split("\",\"");
                //should we replace , with /n if its displaying of these information
                //useful if we accessing these information
                ParkingAreas parkingArea = new ParkingAreas();
                parkingArea.setCarParkNo(tokens[0]);
                parkingArea.setAddress(tokens[1]);
                parkingArea.setXCoord(Double.parseDouble(tokens[2].replace("\"", "")));
                parkingArea.setYCoord(Double.parseDouble(tokens[3].replace("\"", "")));
                parkingArea.setCarParkType(tokens[4]);
                parkingArea.setParkingSystem(tokens[5]);
                parkingArea.setShortTermParking(tokens[6]);
                parkingArea.setFreeParking(tokens[7]);
                parkingArea.setNightParking(tokens[8]);
                parkingArea.setCarParkDecks(tokens[9]);
                parkingArea.setGantryHeight(tokens[10]);
                parkingArea.setCarParkBasement(tokens[11]);
//                double distance1 = parkingArea.getParkingLatLon().getLatitude() - destinationLatLon.getLatitude();
//                double distance2 = parkingArea.getParkingLatLon().getLongitude() - destinationLatLon.getLongitude();
                double distance1 = parkingArea.getParkingSVY21().getNorthing() - destinationSVY21.getEasting();
                double distance2 = parkingArea.getParkingSVY21().getEasting() - destinationSVY21.getNorthing();
                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                parkingArea.setDistanceApart(Math.abs(distanceApart));
//                Log.i("MyActivity", "LatLonCoordinates: " + parkingArea.getParkingLatLon());
//                Log.i("MyActivity", "SVY21Coordinates: " + parkingArea.getParkingSVY21());
//                Log.i("MyActivity", "parkingNorthing: " + parkingArea.getParkingSVY21().getNorthing());
//                Log.i("MyActivity", "destinationNorthing: " + destinationSVY21.getNorthing());
//                Log.i("MyActivity", "parkEasting: " + parkingArea.getParkingSVY21().getEasting());
//                Log.i("MyActivity", "destinationEasting: " + destinationSVY21.getEasting());
                Log.i("MyActivity", "Address: " + parkingArea.getAddress() + ": " + distanceApart);
//                Log.d("MyActivity", "Counter: " + i);
                parkingAreasList.add(parkingArea);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line , e);
            e.printStackTrace();
        }
    }

    private void getTopSixteenParkings() {
        List<ParkingAreas> topSixteenParkings = parkingAreasList.subList(0, 16);
        Log.d("MyActivity", "toptenparking" + topSixteenParkings);
        for(int i=0; i<16; i++) {
            Log.d("MyActivity", "Address: " + topSixteenParkings.get(i).getAddress());
            String currentAddress = topSixteenParkings.get(i).getAddress();
            pcvArrayList.add(new ParkingCardView(currentAddress,
                    "200", "price is this"));
        }
        recyclerView = findViewById(R.id.fragment_parking_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ParkingCardViewAdapter((ArrayList<ParkingCardView>) pcvArrayList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
