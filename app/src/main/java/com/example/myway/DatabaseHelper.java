package com.example.myway;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String COLUMN_USER_EMAIL = "USER_EMAIL";
    public static final String COLUMN_USER_LAST_NAME = "USER_LAST_NAME";
    public static final String COLUMN_USER_USERNAME = "USER_USERNAME";
    public static final String COLUMN_USER_PASSWORD = "USER_PASSWORD";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + USER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_FIRST_NAME + " TEXT," +
                " " + COLUMN_USER_LAST_NAME + " TEXT, " + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_USER_USERNAME + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public boolean addOne(UserModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_FIRST_NAME, userModel.getFirst_name());
        cv.put(COLUMN_USER_LAST_NAME, userModel.getLast_name());
        cv.put(COLUMN_USER_EMAIL, userModel.getEmail());
        cv.put(COLUMN_USER_USERNAME, userModel.getUsername());
        cv.put(COLUMN_USER_PASSWORD, userModel.getPassword());

        long insert = db.insert(USER_TABLE, null,cv);
        closeDB();
        return insert != -1; // -1 means failed to insert
    }

    public boolean usernameExists(String username) {
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_USERNAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{username});
        int count = cursor.getCount();
        cursor.close();
        closeDB();
        return count > 0;
    }

    public boolean emailExists(String email) {
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{email});
        int count = cursor.getCount();
        cursor.close();
        closeDB();
        return count > 0;
    }

    private void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean verifyPassword(String username, String password) {
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_USERNAME + " = ? AND "
                + COLUMN_USER_PASSWORD + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[]{username, password});
        int count = cursor.getCount();
        cursor.close();
        closeDB();
        return count > 0;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + USER_TABLE;
        String queryStringNext = "DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + USER_TABLE+ "'";
        db.execSQL(queryString);
        db.execSQL(queryStringNext);
        db.execSQL("VACUUM");
        closeDB();
    }
}
