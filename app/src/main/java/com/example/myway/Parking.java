package com.example.myway;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parking extends AppCompatActivity {

    private List<ParkingCardView> pcvArrayList = new ArrayList<>();
    private ImageButton filterButton;
    List<Carpark> topSixteenParkings;
    ArrayList<Carpark> masterList = new ArrayList<>(); // Contains both URA and HDB carparks
    ArrayList<ArrayList<String>> URAList;
    ArrayList<ArrayList<String>> HDBList;
    ArrayList<ArrayList<String>> LTAList;
    String username;

    private static final String accessKey = "dc82311d-b99a-412e-9f12-6f607b758479"; // URA access key, to be changed yearly
    protected final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        String destination = "Destination:\n" + bundle.getString("destination");
        TextView destination_display = findViewById(R.id.fragment_parking_destination_text);
        destination_display.setText(destination);

        // Controls our filter button in our parking menu
        filterButton = findViewById(R.id.parking_filter_button);
        filterButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Parking.this, filterButton);
            popupMenu.getMenuInflater().inflate(R.menu.filter_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                popupMenu.dismiss();
                filter((String) item.getTitle());
                return true;
            });
            popupMenu.show();
        });

        resumption();
    }

    private void resumption() {
        ArrayList<Carpark> HDB = new GenerateCarparkStatic.generateHDB().getList();
        ArrayList<Carpark> URA = new GenerateCarparkStatic.generateURA().getList();
        ArrayList<Carpark> LTA = new GenerateCarparkStatic.generateLTA().getList();
        ArrayList<Carpark> tempMaster = new ArrayList<>();

//        ArrayList<Carpark> tempMaster = new ArrayList<>();
        // Adds HDB & LTA car parks into URA, so from here URA contains all carparks
        tempMaster.addAll(URA);
        tempMaster.addAll(HDB);
        tempMaster.addAll(LTA);

//        Log.d("MASTERLIST SIZE>>>>", masterList.size() +"");
//        Log.d("LTA SIZE>>>>", LTA.size() +"");
//        Log.d("URA SIZE>>>>", URA.size() + "");
//        Log.d("HDB SIZE>>>>", HDB.size() + "");

        // masterList contains all HDB and URA carparks
        masterList = tempMaster;

        // Sorts masterList by distance first
        masterList.sort((o1, o2) -> Double.compare(o1.getDistanceApart(), o2.getDistanceApart()));
        getTopSixteenParkings();
    }

    private static boolean timingSelected = false;
    protected static void selectedTiming() { timingSelected = true; }

    // Logic behind our filter button
    private void filter(String type) {
        if (type.equals("Distance")) {
            // Sorts the car parks by distance
            topSixteenParkings.sort((o1, o2) -> Double.compare(o1.getDistanceApart(), o2.getDistanceApart()));
            fillPCVArrayList();
        } else if (type.equals("Price")) {
            // Sorts the car parks by price
            TextView estimate = findViewById(R.id.est_price_1);
            Log.d("Filtering by price....", "Filtering....");
            if (estimate.getText().equals(" no est.") && !timingSelected) {
                Toast.makeText(Parking.this, "Please select a timing!", Toast.LENGTH_SHORT).show();
            } else {
                topSixteenParkings.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
                Log.d("Carparks sorting now", "Now");
                for (Carpark cp : topSixteenParkings) {
                    Log.d("PRICE>>>", cp.getCarParkNo() + " " + cp.getPrice() + "");
                }
                fillPCVArrayList();
            }
        } else {
            // Sorts the car parks by availability of lots
            topSixteenParkings.sort((o2, o1) -> Integer.compare(o1.getAvailableLots(), o2.getAvailableLots()));
            fillPCVArrayList();
        }
        inflateRecycler();
    }


    // Retrieves HDB Carpark Availability
    private ArrayList<ArrayList<String>> parseHDBAPI(JSONObject JSONresponse) {
        ArrayList<ArrayList<String>> masterArrayList = new ArrayList<>(); // Contains all of the below array lists
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

    // Retrieves LTA Carpark Availability
    private ArrayList<ArrayList<String>> parseLTAApi(JSONObject JSONresponse) {
        ArrayList<ArrayList<String>> masterArrayList = new ArrayList<>();
        ArrayList<String> availableLots = new ArrayList<>();
        ArrayList<String> carparkNum = new ArrayList<>();
        ArrayList<String> carparkType = new ArrayList<>();
        try {
            if (JSONresponse.has("value")) {
                JSONArray jsonArray = JSONresponse.getJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj.getString("Agency").equals("LTA")) {
                        int available = obj.getInt("AvailableLots");
                        String CarparkID = obj.getString("CarParkID");
                        carparkNum.add(CarparkID);
                        availableLots.add(Integer.toString(available));
                        carparkType.add(obj.getString("LotType"));
                    } else {
                        break;
                    }
                }
            }
            masterArrayList.add(carparkNum);
            masterArrayList.add(availableLots);
            masterArrayList.add(carparkType);
            return masterArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieves URA Carpark Availability
    private ArrayList<ArrayList<String>> parseURAAPI(JSONObject JSONresponse) {
        ArrayList<ArrayList<String>> masterArrayList = new ArrayList<>(); // Contains all of the below array lists
        ArrayList<String> availableLots = new ArrayList<>();
        ArrayList<String> typeOfLots = new ArrayList<>();
        ArrayList<String> carparkNum = new ArrayList<>();
        try {
            if (JSONresponse.has("Result")) {
                JSONArray jsonItemArray = JSONresponse.getJSONArray("Result");
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

    // Retrieves the top 16 parking spaces by taking a sublist of our sorted masterlist
    private void getTopSixteenParkings() {
        topSixteenParkings = masterList.subList(0, 16);
        fillPCVArrayList();
        inflateRecycler();
    }

    //
    private void fillPCVArrayList() {
        pcvArrayList.clear();

        JSONObject HDBresponse = CarparkAvailabilityRetriever.fetchCarparkAvailability(null, null, null, "https://api.data.gov.sg/v1/transport/carpark-availability");
        HDBList = parseHDBAPI(HDBresponse);

        String token = generateURAToken.getToken(accessKey);
        JSONObject URAresponse = CarparkAvailabilityRetriever.fetchCarparkAvailability(null, accessKey, token, "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability");
        URAList = parseURAAPI(URAresponse);

        JSONObject LTAreponse = CarparkAvailabilityRetriever.fetchCarparkAvailability("/REKmS31QtqYR2ux49l1OQ==", null, null, "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2");
        LTAList = parseLTAApi(Objects.requireNonNull(LTAreponse));

        // Sets array list for HDB
        ArrayList<String> HDBCarparkNum = HDBList.get(0);
        ArrayList<String> HDBTotal = HDBList.get(1);
        ArrayList<String> HDBAvailable = HDBList.get(2);

        // Sets array list for URA
        ArrayList<String> URACarparkNum = URAList.get(0);
        ArrayList<String> URATotal = new ArrayList<>();
        ArrayList<String> URAAvailable = URAList.get(1);

        for (int i = 0; i < URACarparkNum.size(); i++) {
            URATotal.add("dud capacity"); // Fills up to match original array size
        }

        // Sets array list for LTA
        ArrayList<String> LTACarparkNum = LTAList.get(0);
        ArrayList<String> LTAAvailable = LTAList.get(1);
        ArrayList<String> LTATotal = new ArrayList<>();

        for (int i = 0; i < LTACarparkNum.size(); i++) {
            LTATotal.add("dud");
        }

        URACarparkNum.addAll(HDBCarparkNum);
        URACarparkNum.addAll(LTACarparkNum);

        URATotal.addAll(HDBTotal);
        URATotal.addAll(LTATotal);

        URAAvailable.addAll(HDBAvailable);
        URAAvailable.addAll(LTAAvailable);

        ArrayList<String> CarparkNumberFinder = new ArrayList<>(URACarparkNum);
        ArrayList<String> CarparkTotalFinder = new ArrayList<>(URATotal);
        ArrayList<String> CarparkAvailableFinder = new ArrayList<>(URAAvailable);

        URACarparkNum.clear();
        HDBCarparkNum.clear();
        LTACarparkNum.clear();
        URAAvailable.clear();
        HDBAvailable.clear();
        LTAAvailable.clear();
        URATotal.clear();
        HDBTotal.clear();
        LTATotal.clear();

        for (int i = 0; i < 16; i++) {
            Carpark currentCP = topSixteenParkings.get(i);
            String currentCarparkNo = currentCP.getCarParkNo();
            int index = CarparkNumberFinder.indexOf(currentCarparkNo);
            String currentAddress = currentCP.getAddress();
            double distance = currentCP.getDistanceApart();

//            Log.d("CP DETAILS>>>", currentCarparkNo + " " + currentAddress + " LONG" + currentCP.getxCoord() + " LAT: " + currentCP.getyCoord());

            // thought: -> optimise code by passing carpark as a variable.
            if (index == -1) {
                currentCP.setAvailableLots(-1);
                pcvArrayList.add(new ParkingCardView(currentCP, currentAddress,
                        "info unavailable"
                        , currentCP.getPrice() > 999 || currentCP.getPrice() == 0.0 ? " no est." : decimalFormat.format(currentCP.getPrice()),
                        distance, currentCP.getxCoord(), currentCP.getyCoord(),
                        currentCP.getDuration() == null ? "choose your duration" : currentCP.getDuration()));
            } else {
                String available = CarparkAvailableFinder.get(index);
                currentCP.setAvailableLots(Integer.parseInt(available));
                String total = currentCP instanceof Carpark.URA ? ((Carpark.URA) currentCP).getParkCapacity(0)
                                                                : currentCP instanceof Carpark.HDB
                                                                ? CarparkTotalFinder.get(index)
                                                                : "LTA";
                pcvArrayList.add(new ParkingCardView(currentCP, currentAddress,
                        total.equals("LTA") ? available + " lots available"
                                            : available + "/" + total + " lots available"
                        , currentCP.getPrice() > 999 || currentCP.getPrice() == 0.0 ? " no est." : decimalFormat.format(currentCP.getPrice())
                        , distance, currentCP.getxCoord(), currentCP.getyCoord(),
                        currentCP.getDuration() == null ? "choose your duration" : currentCP.getDuration()));
            }
        }
    }

    // Responsible for the layout and information display of our Parking menu
    private void inflateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.fragment_parking_recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter<ParkingCardViewAdapter.ParkingCardViewHolder> adapter = new ParkingCardViewAdapter((ArrayList<ParkingCardView>) pcvArrayList, username, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
