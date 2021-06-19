package com.example.myway;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mapbox.geojson.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ImageButton filterButton;
    ArrayList<ArrayList<String>> allCarparkDetails;
    List<ParkingAreas> topSixteenParkings;


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
        filterButton = findViewById(R.id.parking_filter_button);

        filterButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Parking.this, filterButton);
            popupMenu.getMenuInflater().inflate(R.menu.filter_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    filter((String) item.getTitle());
                    return true;
                }
            });
            popupMenu.show();
        });

        destinationLatLon = new LatLonCoordinate(destinationLat,destinationLng);
        destinationSVY21 = destinationLatLon.asSVY21();
        try {
            readHDBParkingData();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void filter(String type) {
        if (type.equals("Distance")) {
            Collections.sort(topSixteenParkings, (o1, o2) -> {
                if (o1.getDistanceApart() < o2.getDistanceApart()) {
                    return -1;
                } else if(o1.getDistanceApart() > o2.getDistanceApart()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        } else if (type.equals("Price")) {

        } else {
            // Availability
        }
        fillPCVArrayList();
        inflateRecycler();
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
//                Log.i("MyActivity", "Address: " + parkingArea.getAddress() + ": " + distanceApart);
//                Log.d("MyActivity", "Counter: " + i);
                parkingAreasList.add(parkingArea);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line , e);
            e.printStackTrace();
        }
    }

    private void getTopSixteenParkings() {
        topSixteenParkings = parkingAreasList.subList(0, 16);
//        Log.d("MyActivity", "toptenparking" + topSixteenParkings);
        fillPCVArrayList();
        inflateRecycler();
    }

    private void fillPCVArrayList() {
        pcvArrayList.clear();
        JSONObject JSONresponse = CarparkAvailabilityRetriever.fetchCarparkAvailability();
        allCarparkDetails = parseAPI(JSONresponse);
        ArrayList<String> CarparkNumberFinder = allCarparkDetails.get(0);
        ArrayList<String> CarparkTotalFinder = allCarparkDetails.get(1);
        ArrayList<String> CarparkAvailableFinder = allCarparkDetails.get(2);
        ArrayList<String> CarparkTypeFinder = allCarparkDetails.get(3);
        for(int i=0; i<16; i++) {
//            Log.d("MyActivity", "Address: " + topSixteenParkings.get(i).getAddress());
            ParkingAreas currentCP = topSixteenParkings.get(i);
            String currentCarparkNo = currentCP.getCarParkNo();
            int index = CarparkNumberFinder.indexOf(currentCarparkNo);
            String available = CarparkAvailableFinder.get(index);
            String total = CarparkTotalFinder.get(index);
//            Log.d("Carpark No: ", currentCarparkNo);
            String currentAddress = currentCP.getAddress();
            double distance = currentCP.getDistanceApart();
            pcvArrayList.add(new ParkingCardView(currentAddress,
                    available + "/" + total + " lots available"
                    , "price is this", distance));
        }
    }

    private ArrayList<ArrayList<String>> parseAPI(JSONObject JSONresponse) {
        ArrayList<ArrayList<String>> masterArrayList = new ArrayList<>();
        ArrayList<String> totalLots = new ArrayList<>();
        ArrayList<String> availableLots = new ArrayList<>();
        ArrayList<String> lotType = new ArrayList<>();
        ArrayList<String> carparkNum = new ArrayList<>();
        try {
            if (JSONresponse.has("items")) {
                JSONArray jsonItemArray = JSONresponse.getJSONArray("items");
                for (int i = 0; i < jsonItemArray.length(); i++) {
                    if (jsonItemArray.getJSONObject(i).has("carpark_data")) {
                        JSONArray jsonArray = jsonItemArray.getJSONObject(i).getJSONArray("carpark_data");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject currentObj = jsonArray.getJSONObject(j);
                            if (currentObj.has("carpark_number")) {
                                carparkNum.add(currentObj.getString("carpark_number"));
//                                Log.d("CP: ", currentObj.getString("carpark_number"));
                            }
                            if (currentObj.has("carpark_info")) {
                                JSONArray jsonLotsArray = currentObj.getJSONArray("carpark_info");
                                JSONObject obj = jsonLotsArray.getJSONObject(0);
                                String total_lots = obj.getString("total_lots");
                                String available_lots = obj.getString("lots_available");
                                String lot_type = obj.getString("lot_type");
                                totalLots.add(total_lots);
                                availableLots.add(available_lots);
                                lotType.add(lot_type);
//                                Log.d("DETAILS: ", "total: " + total_lots + " avail: " + available_lots + " type: " + lot_type);
                            }
                        }
                    }
                }
            }
            masterArrayList.add(carparkNum);
            masterArrayList.add(totalLots);
            masterArrayList.add(availableLots);
            masterArrayList.add(lotType);
            return masterArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void inflateRecycler() {
        recyclerView = findViewById(R.id.fragment_parking_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ParkingCardViewAdapter((ArrayList<ParkingCardView>) pcvArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


}
