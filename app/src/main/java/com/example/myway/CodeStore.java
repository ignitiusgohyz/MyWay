package com.example.myway;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//TODO
// 1. Add in number in registration fields ??.
// 2. Lookout for areas to improve code efficiency to prevent frame skips.
// 3. Reload map on return from settings after permanent denial of permissions.
// 4. Fix database columns (remove firstname and lastname as not used anymore but can add phone number?).
// 5. Add popup inflater for user agreement and policy
// 6. Add in parking system.
// 7. Add functionality to buttons on top fragment for main activity.
// 8. Add functionality to buttons on top fragment for parking activity.
// 9. Retrieve JSON code and feed into our RecycleView.
// 10. Implement algo to read price and calculate based on price.
// 11. Add functionality to the parking list.
// 12. Convert postal code to address? How?
// 13. SHIFT HDB READINGS INTO ANOTHER FILE FOR CONCURRENT PROGRAMMING.


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
//
//    <!--    <EditText-->
//<!--        android:id="@+id/username"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:autofillHints="firstName"-->
//<!--        android:ems="10"-->
//<!--        android:hint="@string/login_hint_username"-->
//<!--        android:inputType="textPersonName"-->
//<!--        app:layout_constraintBottom_toBottomOf="parent"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintHorizontal_bias="0.497"-->
//<!--        app:layout_constraintStart_toStartOf="parent"-->
//<!--        app:layout_constraintTop_toBottomOf="@+id/image"-->
//<!--        app:layout_constraintVertical_bias="0.183" />-->
//
//<!--    <EditText-->
//<!--        android:id="@+id/password"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:layout_marginTop="20dp"-->
//<!--        android:autofillHints="lastName"-->
//<!--        android:ems="10"-->
//<!--        android:hint="@string/login_hint_password"-->
//<!--        android:inputType="textPassword"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintStart_toStartOf="parent"-->
//<!--        app:layout_constraintTop_toBottomOf="@+id/username" />-->
//
//<!--    <Button-->
//<!--        android:id="@+id/login"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:layout_marginTop="44dp"-->
//<!--        android:background="#E91E63"-->
//<!--        android:backgroundTint="#E91E63"-->
//<!--        android:backgroundTintMode="add"-->
//<!--        android:text="@string/Login"-->
//<!--        android:textAppearance="@style/TextAppearance.AppCompat.Body1"-->
//<!--        app:iconPadding="8dp"-->
//<!--        app:iconTint="#808080"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintHorizontal_bias="0.328"-->
//<!--        app:layout_constraintStart_toStartOf="parent"-->
//<!--        app:layout_constraintTop_toBottomOf="@+id/password"-->
//<!--        app:rippleColor="@color/teal_200" />-->
//
//<!--    <Button-->
//<!--        android:id="@+id/register"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:layout_marginTop="44dp"-->
//<!--        android:background="#E91E63"-->
//<!--        android:backgroundTint="#E91E63"-->
//<!--        android:backgroundTintMode="add"-->
//<!--        android:text="@string/Register"-->
//<!--        android:textAppearance="@style/TextAppearance.AppCompat.Body1"-->
//<!--        app:iconPadding="8dp"-->
//<!--        app:iconTint="#808080"-->
//<!--        android:layout_marginEnd="90dp"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintHorizontal_bias="0.687"-->
//<!--        app:layout_constraintStart_toEndOf="@+id/login"-->
//<!--        app:layout_constraintTop_toBottomOf="@+id/password"-->
//<!--        app:rippleColor="@color/teal_200" />-->
//
//<!--    <ImageView-->
//<!--        android:id="@+id/image"-->
//<!--        android:layout_width="199dp"-->
//<!--        android:layout_height="136dp"-->
//<!--        android:contentDescription="@string/app_name"-->
//<!--        android:src="@mipmap/ic_myway_logo"-->
//<!--        android:layout_marginTop="130dp"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintStart_toStartOf="parent"-->
//<!--        app:layout_constraintTop_toTopOf="parent" />-->
//
//<!--    <ImageButton-->
//<!--        android:id="@+id/visibility_button"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:background="#0000"-->
//<!--        android:contentDescription="@string/non_visible"-->
//<!--        android:src="@drawable/ic_password_not_visible"-->
//<!--        app:layout_constraintBottom_toBottomOf="@id/password"-->
//<!--        app:layout_constraintHorizontal_bias="0.333"-->
//<!--        app:layout_constraintLeft_toRightOf="@+id/password"-->
//<!--        app:layout_constraintRight_toRightOf="parent"-->
//<!--        app:layout_constraintTop_toTopOf="@id/password"-->
//<!--        app:layout_constraintVertical_bias="0.476">-->
//
//<!--    </ImageButton>-->
//
//<!--    <Button-->
//<!--        android:id="@+id/tempclearbutton"-->
//<!--        android:layout_width="wrap_content"-->
//<!--        android:layout_height="wrap_content"-->
//<!--        android:text="Temporary Clear\nDatabase Button"-->
//<!--        app:layout_constraintBottom_toTopOf="@+id/image"-->
//<!--        app:layout_constraintEnd_toEndOf="parent"-->
//<!--        app:layout_constraintStart_toStartOf="parent"-->
//<!--        app:layout_constraintTop_toTopOf="parent" />-->

//                        first_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
}
