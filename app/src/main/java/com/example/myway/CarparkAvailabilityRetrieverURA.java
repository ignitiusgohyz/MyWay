package com.example.myway;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CarparkAvailabilityRetrieverURA {
    public static JSONObject fetchCarparkAvailability(String accessKey, String accessToken) {

        FutureTask<String> task = new FutureTask<>(() -> {
            String link = "https://www.ura.gov.sg/uraDataService/invokeUraDS?service=Car_Park_Availability";
            BufferedReader reader = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("AccessKey", accessKey);
                connection.setRequestProperty("Token", accessToken);
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        Executor executor = Executors.newFixedThreadPool(1);
        executor.execute(task);
        String response;

        try {
            response = (String) task.get();
            return new JSONObject(Objects.requireNonNull(response));
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
