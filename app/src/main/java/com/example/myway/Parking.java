package com.example.myway;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.CacheDispatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

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
    List<Carpark> topSixteenParkings;
    ArrayList<Carpark> masterList;

    private static final String accessKey = "dc82311d-b99a-412e-9f12-6f607b758479";
    private static String accessToken;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking);
        Bundle bundle = getIntent().getExtras();
        destinationLat = bundle.getDouble("destinationLat");
        destinationLng = bundle.getDouble("destinationLng");
        accessToken = bundle.getString("token");

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

        destinationLatLon = new LatLonCoordinate(destinationLat, destinationLng);
        destinationSVY21 = destinationLatLon.asSVY21();

        CompletableFuture<ArrayList<Carpark>> futureURA = CompletableFuture.supplyAsync(() -> {
            ArrayList<Carpark> temp = generateURADetails.getURAList();
            return temp;
        });
        CompletableFuture<ArrayList<Carpark>> futureHDB = CompletableFuture.supplyAsync(() -> {
            try {
                ArrayList<Carpark> temp = readHDBParkingData();
                return temp;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        ArrayList<Carpark> HDB = futureHDB.join();
        ArrayList<Carpark> URA = futureURA.join();

        for (Carpark cp : HDB) {
            URA.add(cp);
        }
        HDB.clear();
        masterList = URA;
        Collections.sort(masterList, (o1, o2) -> {
            if (o1.getDistanceApart() < o2.getDistanceApart()) {
                return -1;
            } else if (o1.getDistanceApart() > o2.getDistanceApart()) {
                return 1;
            } else {
                return 0;
            }
        });
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

    // Retrieves URA Carpark Details
    protected ArrayList<Carpark> readURAParkingData() {
        return generateURADetails.getURACarparkDetails(accessToken, accessKey, destinationSVY21);
    }

    // Retrieves HDB Carpark Details
    protected ArrayList<Carpark> readHDBParkingData() throws IOException {

        InputStream hdbparking = getResources().openRawResource(R.raw.hdbparking);
        BufferedReader reader = new BufferedReader(new InputStreamReader(hdbparking, Charset.forName("UTF-8")));
        String line = "";
        ArrayList<Carpark> temp = new ArrayList<>();

        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\",\"");
                Carpark carpark = new Carpark();
                carpark.setCarParkNo(tokens[0]);
                carpark.setAddress(tokens[1]);
                carpark.setXCoord(Double.parseDouble(tokens[2].replace("\"", "")));
                carpark.setYCoord(Double.parseDouble(tokens[3].replace("\"", "")));
                carpark.setCarParkType(tokens[4]);
                carpark.setParkingSystem(tokens[5]);
                carpark.setShortTermParking(tokens[6]);
                carpark.setFreeParking(tokens[7]);
                carpark.setNightParking(tokens[8]);
                carpark.setCarParkDecks(tokens[9]);
                carpark.setGantryHeight(tokens[10]);
                carpark.setCarParkBasement(tokens[11]);
                carpark.setOwnedBy("HDB");
                double distance1 = carpark.getParkingSVY21().getNorthing() - destinationSVY21.getEasting();
                double distance2 = carpark.getParkingSVY21().getEasting() - destinationSVY21.getNorthing();
                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                carpark.setDistanceApart(Math.abs(distanceApart));
                temp.add(carpark);
            }
            return temp;
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line , e);
            e.printStackTrace();
        }
        return temp;
    }

    // Retrieves HDB Carpark Availability
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

    private void getTopSixteenParkings() {
        topSixteenParkings = masterList.subList(0, 16);
        int count = 0;
        for (Carpark cp : masterList) {
            Log.d("CP>>>>", "CP " + count + " " + cp.getOwnedBy() + " " + cp.getDistanceApart());
            count++;
        }
//        Log.d("MyActivity", "top16parking" + topSixteenParkings);
        fillPCVArrayList();
        inflateRecycler();
    }

    private void fillPCVArrayList() {
        pcvArrayList.clear();
        JSONObject JSONresponse = CarparkAvailabilityRetrieverHDB.fetchCarparkAvailability();
        allCarparkDetails = parseAPI(JSONresponse);
        ArrayList<String> CarparkNumberFinder = allCarparkDetails.get(0);
        ArrayList<String> CarparkTotalFinder = allCarparkDetails.get(1);
        ArrayList<String> CarparkAvailableFinder = allCarparkDetails.get(2);
        ArrayList<String> CarparkTypeFinder = allCarparkDetails.get(3);
        for(int i=0; i<16; i++) {
//            Log.d("MyActivity", "Address: " + topSixteenParkings.get(i).getAddress() + " " + topSixteenParkings.get(i).getOwnedBy());
            Carpark currentCP = topSixteenParkings.get(i);
            String currentCarparkNo = currentCP.getCarParkNo();
            int index = CarparkNumberFinder.indexOf(currentCarparkNo);
            String currentAddress = currentCP.getAddress();
            double distance = currentCP.getDistanceApart();
            if (index == -1) {
                pcvArrayList.add(new ParkingCardView(currentAddress,
                        "carpark space unavailable", "price is this",
                        distance));
            } else {
                String available = CarparkAvailableFinder.get(index);
                String total = CarparkTotalFinder.get(index);
//            Log.d("Carpark No: ", currentCarparkNo);
                pcvArrayList.add(new ParkingCardView(currentAddress,
                        available + "/" + total + " lots available"
                        , "price is this", distance));
            }
        }
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
