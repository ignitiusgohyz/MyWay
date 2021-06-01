package com.example.myway;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CodeStore {
    // Online DB https connection
    //            ExecutorService executor = Executors.newSingleThreadExecutor();
//            Handler handler = new Handler(Looper.getMainLooper());
//            executor.execute(() -> {
//                StringBuilder result = new StringBuilder();
//                String login_url = "http://192.168.1.24/login.php";
//                try {
//                    String username = createdUsername_string;
//                    String password = createdPassword_string;
//                    URL url = new URL(login_url);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setDoOutput(true);
//                    httpURLConnection.setDoInput(true);
//                    OutputStream outputStream = httpURLConnection.getOutputStream();
//                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                    String post_data = URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+"&"
//                            +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
//                    bufferedWriter.write(post_data);
//                    bufferedWriter.flush();
//                    bufferedWriter.close();
//                    outputStream.close();
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    bufferedReader.close();
//                    inputStream.close();
//                    httpURLConnection.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                handler.post(() -> {
//                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//                    alertDialog.setTitle("Login Status");
//                    alertDialog.setMessage(result);
//                    alertDialog.show();
//                });
//            });

    // Deletes account from sqlite db --> future use in user profile
//    public boolean deleteOne(UserModel userModel) {
        // Deletes a user. If found, delete and return true. Else return false.
//        SQLiteDatabase db = this.getWritableDatabase();
//        String queryString = "DELETE FROM " + USER_TABLE + " WHERE " + COLUMN_ID + " = " + userModel.getId();
//        Cursor cursor = db.rawQuery(queryString, null);
//        if (cursor.moveToFirst()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    // Gets all users from DB
//    public List<UserModel> getAllUsers() {
//        List<UserModel> list = new ArrayList<>();
//
//        String queryString = "SELECT * FROM " + USER_TABLE;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(queryString, null);
//        if (cursor.moveToFirst()) {
//            // loop through cursor (result set) and create new user objects. Put them into return list.
//            do {
//                int userID =cursor.getInt(0);
//                String customerFirstName = cursor.getString(1);
//                String customerLastName = cursor.getString(2);
//                String customerEmail = cursor.getString(3);
//                String customerUsername = cursor.getString(4);
//                String customerPassword = cursor.getString(5);
//
//                UserModel userModel = new UserModel(userID, customerFirstName, customerLastName, customerEmail, customerUsername, customerPassword);
//                list.add(userModel);
//            } while (cursor.moveToNext());
//        } else {
//            // Add nothing to the list
//        }
//        // Always close and remove garbage
//        cursor.close();
//        db.close();
//        return list;
//    }
}
