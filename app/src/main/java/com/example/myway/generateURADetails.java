package com.example.myway;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class generateURADetails {

    protected static ArrayList<Carpark> URAList;

    public static void setList(ArrayList<Carpark> list) {
        URAList = list;
    }

    public static ArrayList<Carpark> getURAList() {
        return URAList;
    }

    public static ArrayList<Carpark> getURACarparkDetails(String accessToken, String accessKey, SVY21Coordinate destinationSVY21) {

        String link = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Details";
        ArrayList<Carpark> arrayList = new ArrayList<>();
        ArrayList<String> carparkCodes = new ArrayList<>();

        FutureTask<JSONObject> task = new FutureTask<>(() -> {
            URL url = new URL(link);
            BufferedReader reader;
//            Log.d("START>>>", "HERE");
            HttpURLConnection httpURLConnection;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("AccessKey", accessKey);
            httpURLConnection.setRequestProperty("Token", accessToken);
            httpURLConnection.connect();
//            Log.d("CONNECTED>>>>>", "HERE");

            InputStream inputStream = httpURLConnection.getInputStream();
//            Log.d("INPUT STREAM>>>>>", "HERE");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder buffer = new StringBuilder();
            String line;
//            Log.d("START OF REQUEST>>>>", "HERE");

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            httpURLConnection.disconnect();
            reader.close();
//            Log.d("END OF REQUEST>>>>", "HERE");
            return new JSONObject(buffer.toString());
        });

        Executor executor = Executors.newFixedThreadPool(1);
        executor.execute(task);

        FutureTask<ArrayList<Carpark>> heavyTask = new FutureTask<>(() -> {
            try {
                JSONObject jsonObject = task.get();
                if (jsonObject.has("Status") && jsonObject.getString("Status").equals("Success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("Result");
                    JSONObject locationObj;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentObj = jsonArray.getJSONObject(i);
                        double x;
                        double y;
                        String cpCode = currentObj.getString("ppCode");
                        if (carparkCodes.contains(cpCode)) {
                            int index = cpCode.indexOf(cpCode);
                            Carpark retrieved = arrayList.get(index);
                            retrieved.setWeekdayMin(currentObj.getString("weekdayMin"));
                            retrieved.setWeekdayRate(currentObj.getString("weekdayRate"));
                            retrieved.setStartTime(currentObj.getString("startTime"));
                            retrieved.setEndTime(currentObj.getString("endTime"));
                            retrieved.setSunPHMin(currentObj.getString("sunPHMin"));
                            retrieved.setSunPHRate(currentObj.getString("sunPHRate"));
                            retrieved.setSatdayMin(currentObj.getString("satdayMin"));
                            retrieved.setSatdayRate(currentObj.getString("satdayRate"));
                            retrieved.setParkCapacity(currentObj.getInt("parkCapacity"));
                            retrieved.setVehCat(currentObj.getString("vehCat"));
                        } else {
                            Carpark carpark = new Carpark();
                            carpark.setCarParkNo(currentObj.getString("ppCode"));
                            carpark.setOwnedBy("URA");
                            carpark.setParkingSystem(currentObj.getString("parkingSystem")); // B - Electronic , C - Coupon
                            carpark.setAddress(currentObj.getString("ppName"));
                            carpark.setWeekdayMin(currentObj.getString("weekdayMin"));
                            carpark.setWeekdayRate(currentObj.getString("weekdayRate"));
                            carpark.setStartTime(currentObj.getString("startTime"));
                            carpark.setEndTime(currentObj.getString("endTime"));
                            carpark.setSunPHMin(currentObj.getString("sunPHMin"));
                            carpark.setSunPHRate(currentObj.getString("sunPHRate"));
                            carpark.setSatdayMin(currentObj.getString("satdayMin"));
                            carpark.setSatdayRate(currentObj.getString("satdayRate"));
                            carpark.setParkCapacity(currentObj.getInt("parkCapacity"));
                            carpark.setVehCat(currentObj.getString("vehCat"));
                            if (currentObj.has("remarks")) {
                                carpark.setRemarks(currentObj.getString("remarks"));
                            }
                            if (currentObj.has("geometries")) {
                                locationObj = currentObj.getJSONArray("geometries").getJSONObject(0);
                                x = parseCoordinates(locationObj, "x");
                                y = parseCoordinates(locationObj, "y");
//                                Log.d("SVY 21 COORDS>>>>", x + " y:" + y);
                                SVY21Coordinate svy21Coordinate = new SVY21Coordinate(x, y);
                                y = svy21Coordinate.asLatLon().getLatitude();
                                x = svy21Coordinate.asLatLon().getLongitude();
//                                Log.d("LATLON COORDS>>>>", x + " y:" + y);
                                carpark.setXCoord(x);
                                carpark.setYCoord(y);
                                double distance1 = svy21Coordinate.getNorthing() - destinationSVY21.getEasting();
                                double distance2 = svy21Coordinate.getEasting() - destinationSVY21.getNorthing();
                                double distanceApart = Math.sqrt(Math.pow(distance1,2) + Math.pow(distance2,2));
                                carpark.setDistanceApart(Math.abs(distanceApart));
                            } else {
                                carpark.setDistanceApart(999999);
                            }
//                            Log.d("Code>>>", currentObj.getString("ppCode"));
//                        Log.d("Address>>>", currentObj.getString("ppName"));
//                        Log.d("Coordinates>>>", "x: " + x + " y: " + y);
                            arrayList.add(carpark);
                            carparkCodes.add(cpCode);
                        }
                    }
//                    Log.d("JSONLENGTH>>>>", jsonArray.length() + "");
//                    Log.d("TOTAL>>>>", arrayList.size() + "");
                    return arrayList;
                }
            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        });

        executor.execute(heavyTask);
        try {
            return heavyTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static double parseCoordinates(JSONObject coordinates, String type) {
        try {
            String coordinateString = coordinates.getString("coordinates");
            StringBuilder x = new StringBuilder();
            StringBuilder y = new StringBuilder();
            boolean split = false;
            for (int i = 0; i < coordinateString.length(); i++) {
                char cursor = coordinateString.charAt(i);
                if (cursor == ',') {
                    split = true;
                } else if (split) {
                    y.append(cursor);
                } else {
                    x.append(cursor);
                }
            }
            return type.equals("x") ? Double.parseDouble(x.toString()) : Double.parseDouble(y.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

