package com.example.myway;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Parking extends AppCompatActivity {

    private List<ParkingCardView> pcvArrayList = new ArrayList<>();
    private ImageButton filterButton;
    ArrayList<ArrayList<String>> HDBList;
    List<Carpark> topSixteenParkings;
    ArrayList<Carpark> masterList;
    ArrayList<ArrayList<String>> URAList;

    private static final String accessKey = "dc82311d-b99a-412e-9f12-6f607b758479";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking);
        Bundle bundle = getIntent().getExtras();
        double destinationLat = bundle.getDouble("destinationLat");
        double destinationLng = bundle.getDouble("destinationLng");
        String accessToken = bundle.getString("token");

        String destination = "Destination:\n" + bundle.getString("destination");
        TextView destination_display = findViewById(R.id.fragment_parking_destination_text);
        destination_display.setText(destination);
        filterButton = findViewById(R.id.parking_filter_button);

        filterButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Parking.this, filterButton);
            popupMenu.getMenuInflater().inflate(R.menu.filter_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                filter((String) item.getTitle());
                return true;
            });
            popupMenu.show();
        });

        CompletableFuture<ArrayList<Carpark>> futureURA = CompletableFuture.supplyAsync(generateURADetails::getURAList);
        CompletableFuture<ArrayList<Carpark>> futureHDB = CompletableFuture.supplyAsync(generateHDBDetails::getList);
        ArrayList<Carpark> HDB = futureURA.join();
        ArrayList<Carpark> URA = futureHDB.join();

//        Log.d("Check URA", "URA: " + URA);
        URA.addAll(HDB);

        HDB.clear();
        masterList = URA;
//        Log.d("Check masterlist", "Masterlist: " + masterList);
        masterList.sort((o1, o2) -> Double.compare(o1.getDistanceApart(), o2.getDistanceApart()));
        getTopSixteenParkings();
    }

    private void filter(String type) {
        if (type.equals("Distance")) {
            topSixteenParkings.sort((o1, o2) -> Double.compare(o1.getDistanceApart(), o2.getDistanceApart()));
            fillPCVArrayList();
        } else if (type.equals("Price")) {
            fillPCVArrayList();
        } else {
            // Availability
            topSixteenParkings.sort((o1, o2) -> Integer.compare(o1.getAvailableLots(), o2.getAvailableLots()));
            fillPCVArrayList();
        }
        inflateRecycler();
    }


    // Retrieves HDB Carpark Availability
    private ArrayList<ArrayList<String>> parseHDBAPI(JSONObject JSONresponse) {
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


    private ArrayList<ArrayList<String>> parseURAAPI(JSONObject JSONresponse) {
        ArrayList<ArrayList<String>> masterArrayList = new ArrayList<>();
        ArrayList<String> availableLots = new ArrayList<>();
        ArrayList<String> typeOfLots = new ArrayList<>();
        ArrayList<String> carparkNum = new ArrayList<>();
        try {
            if (JSONresponse.has("Result")) {
                JSONArray jsonItemArray = JSONresponse.getJSONArray("Result");
                Log.d("Check JSONSIZE", "ArraySize: " + jsonItemArray.length());
                for (int i = 0; i < jsonItemArray.length(); i++) {
                    JSONObject obj  = jsonItemArray.getJSONObject(i);
                    String lotsAvailable = obj.getString("lotsAvailable");
                    String carparkNo = obj.getString("carparkNo");
                    String lotType = obj.getString("lotType");
                    availableLots.add(lotsAvailable);
                    typeOfLots.add(lotType);
                    carparkNum.add(carparkNo);
                }
            }
            masterArrayList.add(carparkNum);
            masterArrayList.add(availableLots);
            masterArrayList.add(typeOfLots);
            return masterArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getTopSixteenParkings() {
        topSixteenParkings = masterList.subList(0, 16);
        for (Carpark cp : topSixteenParkings) {
            Log.d("TOP CP>>>>", cp.getAddress() + " " + cp.getOwnedBy());
        }
        fillPCVArrayList();
        inflateRecycler();
    }

    private void fillPCVArrayList() {
        pcvArrayList.clear();
        JSONObject JSONresponse = CarparkAvailabilityRetrieverHDB.fetchCarparkAvailability();
        HDBList = parseHDBAPI(JSONresponse);

        String token = generateURAToken.getToken(accessKey);
        JSONObject URAresponse = CarparkAvailabilityRetrieverURA.fetchCarparkAvailability(accessKey, token);
        URAList = parseURAAPI(URAresponse);

        Log.d("TestHDB", "Read: " + HDBList);
        Log.d("TestHDB", "Read: " + HDBList.get(0).size());
        Log.d("TestURA", "Read: " + URAList);
        Log.d("TestURA", "Read: " + URAList.get(0).size());

        ArrayList<String> HDBCarparkNum = HDBList.get(0);
        ArrayList<String> HDBTotal = HDBList.get(1);
        ArrayList<String> HDBAvailable = HDBList.get(2);
        ArrayList<String> HDBType = HDBList.get(3);

        ArrayList<String> URACarparkNum = URAList.get(0);
        ArrayList<String> URATotal = new ArrayList<>();
        ArrayList<String> URAAvailable = URAList.get(1);
        ArrayList<String> URAType = URAList.get(2);

        for(int i=0; i<URACarparkNum.size(); i++) {
            URATotal.add("capacity");
        }

        URACarparkNum.addAll(HDBCarparkNum);
        URATotal.addAll(HDBTotal);
        URAAvailable.addAll(HDBAvailable);
        URAType.addAll(HDBType);

        ArrayList<String> CarparkNumberFinder = new ArrayList<>(URACarparkNum);
        ArrayList<String> CarparkTotalFinder = new ArrayList<>(URATotal);
        ArrayList<String> CarparkAvailableFinder = new ArrayList<>(URAAvailable);
        ArrayList<String> CarparkTypeFinder = new ArrayList<>(URAType);

        URACarparkNum.clear();
        HDBCarparkNum.clear();
        URAAvailable.clear();
        HDBAvailable.clear();
        URATotal.clear();
        HDBTotal.clear();
        URAType.clear();
        HDBType.clear();

        for(int i=0; i<16; i++) {
//            Log.d("MyActivity", "Address: " + topSixteenParkings.get(i).getAddress() + " " + topSixteenParkings.get(i).getOwnedBy());
            Carpark currentCP = topSixteenParkings.get(i);
            String currentCarparkNo = currentCP.getCarParkNo();
            int index = CarparkNumberFinder.indexOf(currentCarparkNo);
            Log.d("Inside For Loop", "Carpark No: " + currentCarparkNo);
            Log.d("Inside For Loop", "Index: " + index);
            String currentAddress = currentCP.getAddress();
            Log.d("Inside For Loop", "Address: " + currentAddress);
            double distance = currentCP.getDistanceApart();

            if (index == -1) {
                currentCP.setAvailableLots(-1);
                pcvArrayList.add(new ParkingCardView(currentAddress,
                        "info unavailable", "price is this",
                        distance));
            } else {
                if (currentCP.getOwnedBy().equals("URA")) {
                    String available = CarparkAvailableFinder.get(index);
                    currentCP.setAvailableLots(Integer.parseInt(available));
                    String total = currentCP.getParkCapacity().get(0);
//            Log.d("Carpark No: ", currentCarparkNo);
                    pcvArrayList.add(new ParkingCardView(currentAddress,
                            available + "/" + total + " lots available"
                            , "price is this", distance));
                } else {
                    String available = CarparkAvailableFinder.get(index);
                    currentCP.setAvailableLots(Integer.parseInt(available));
                    String total = CarparkTotalFinder.get(index);
//            Log.d("Carpark No: ", currentCarparkNo);
                    pcvArrayList.add(new ParkingCardView(currentAddress,
                            available + "/" + total + " lots available"
                            , "price is this", distance));
                }
            }
        }
    }


    private void inflateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.fragment_parking_recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new ParkingCardViewAdapter((ArrayList<ParkingCardView>) pcvArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

//    private void fillPCVArrayList(boolean overload) {
//        pcvArrayList.clear();
//        JSONObject JSONresponse = CarparkAvailabilityRetrieverHDB.fetchCarparkAvailability();
//        HDBList = parseHDBAPI(JSONresponse);
//
//        JSONObject URAresponse = CarparkAvailabilityRetrieverURA.fetchCarparkAvailability(accessKey);
//        URAList = parseURAAPI(URAresponse);
//
//        Log.d("TestHDB", "Read: " + HDBList);
//        Log.d("TestHDB", "Read: " + HDBList.get(0).size());
//        Log.d("TestURA", "Read: " + URAList);
//        Log.d("TestURA", "Read: " + URAList.get(0).size());
//
//        ArrayList<String> HDBCarparkNum = HDBList.get(0);
//        ArrayList<String> HDBTotal = HDBList.get(1);
//        ArrayList<String> HDBAvailable = HDBList.get(2);
//        ArrayList<String> HDBType = HDBList.get(3);
//        ArrayList<String> URACarparkNum = URAList.get(0);
//        ArrayList<String> URATotal = new ArrayList<>();
//        ArrayList<String> URAAvailable = URAList.get(1);
//        ArrayList<String> URAType = URAList.get(2);
//
//        for(int i=0; i<URACarparkNum.size(); i++) {
//            URATotal.add("capacity");
//        }
//
//        URACarparkNum.addAll(HDBCarparkNum);
//        URATotal.addAll(HDBTotal);
//        URAAvailable.addAll(HDBAvailable);
//        URAType.addAll(HDBType);
//
//        ArrayList<String> CarparkNumberFinder = new ArrayList<>(URACarparkNum);
//        ArrayList<String> CarparkTotalFinder = new ArrayList<>(URATotal);
//        ArrayList<String> CarparkAvailableFinder = new ArrayList<>(URAAvailable);
//        ArrayList<String> CarparkTypeFinder = new ArrayList<>(URAType);
//
//        URACarparkNum.clear();
//        HDBCarparkNum.clear();
//        URAAvailable.clear();
//        HDBAvailable.clear();
//        URATotal.clear();
//        HDBTotal.clear();
//        URAType.clear();
//        HDBType.clear();
//
//        for(int i=0; i<16; i++) {
//            Carpark currentCp = topSixteenParkings.get(i);
//            String curentCarparkNo = currentCp.getCarParkNo();
//            int index = CarparkNumberFinder.indexOf(curentCarparkNo);
//            if (index == -1) {
//                currentCp.setAvailableLots(-1);
//            } else {
//                String available = CarparkAvailableFinder.get(index);
//                currentCp.setAvailableLots(Integer.parseInt(available));
//            }
//        }
//        topSixteenParkings.sort((o1,o2) -> Integer.compare(o2.getAvailableLots(), o1.getAvailableLots()));
//        for(int i=0; i<16; i++) {
////            Log.d("MyActivity", "Address: " + topSixteenParkings.get(i).getAddress() + " " + topSixteenParkings.get(i).getOwnedBy());
//            Carpark currentCP = topSixteenParkings.get(i);
//            String currentCarparkNo = currentCP.getCarParkNo();
//            int index = CarparkNumberFinder.indexOf(currentCarparkNo);
//            Log.d("Inside For Loop", "Carpark No: " + currentCarparkNo);
//            Log.d("Inside For Loop", "Index: " + index);
//            String currentAddress = currentCP.getAddress();
//            Log.d("Inside For Loop", "Address: " + currentAddress);
//            double distance = currentCP.getDistanceApart();
//
//            if (index == -1) {
//                pcvArrayList.add(new ParkingCardView(currentAddress,
//                        "info unavailable", "price is this",
//                        distance));
//            } else {
//                if (currentCP.getOwnedBy().equals("URA")) {
//                    String available = CarparkAvailableFinder.get(index);
//                    String total = currentCP.getParkCapacity().get(0);
////            Log.d("Carpark No: ", currentCarparkNo);
//                    pcvArrayList.add(new ParkingCardView(currentAddress,
//                            available + "/" + total + " lots available"
//                            , "price is this", distance));
//                } else {
//                    String available = CarparkAvailableFinder.get(index);
//                    String total = CarparkTotalFinder.get(index);
////            Log.d("Carpark No: ", currentCarparkNo);
//                    pcvArrayList.add(new ParkingCardView(currentAddress,
//                            available + "/" + total + " lots available"
//                            , "price is this", distance));
//                }
//            }
//        }
//    }

}
