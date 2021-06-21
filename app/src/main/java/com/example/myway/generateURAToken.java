package com.example.myway;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class generateURAToken {

    public static String getToken(String accessKey) {

        // Generate token
        FutureTask<String> task = new FutureTask<>(() -> {
            String link = "https://www.ura.gov.sg/uraDataService/insertNewToken.action";
            BufferedReader reader;
            HttpURLConnection connection;
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("AccessKey", accessKey);
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            connection.disconnect();
            reader.close();
            return buffer.toString();
        });

        Executor executor = Executors.newFixedThreadPool(1);
        executor.execute(task);
        String JSONresponse;
        String tokenResponse = null;
        JSONObject tokenJson;

        try {
            JSONresponse = (String) task.get();
            tokenJson = new JSONObject(JSONresponse);
            if (tokenJson.has("Result")) {
                tokenResponse = tokenJson.getString("Result");
            }
            return tokenResponse;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
