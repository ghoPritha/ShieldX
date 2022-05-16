package com.example.shieldx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBName="users.db";

    public DBHelper(@Nullable Context context) {
        super(context, DBName, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase shieldXDB) {
        shieldXDB.execSQL("create Table USERS (User_ID INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT NOT NULL , lastname TEXT NOT NULL , phone_number TEXT NOT NULL UNIQUE, " +
                "email_id TEXT NOT NULL UNIQUE, password TEXT NOT NULL )");
//        shieldXDB.execSQL("create Table FOLLOWERS (Follower_ID INTEGER PRIMARY KEY AUTOINCREMENT, follower_firstname TEXT NOT NULL, follower_lastname TEXT NOT NULL , " +
//                "follower_phone_number TEXT NOT NULL UNIQUE, follower_email_id TEXT NOT NULL UNIQUE, follower_about TEXT, FOREIGN KEY (User_ID) REFERENCES USERS(User_ID))");
        shieldXDB.execSQL("create Table ACTIVITY_LOG (Acty_ID INTEGER PRIMARY KEY AUTOINCREMENT, currentLocation TEXT NOT NULL, destination TEXT NOT NULL, " +
                "time_taken default (STRFTIME('%H:%M:%f')), start_acty default (STRFTIME('%Y-%m-%d %H:%M:%f'))," +
                "end_acty default (STRFTIME('%Y-%m-%d %H:%M:%f')),acty_status BOOLEAN default '0', " +
                "FOREIGN KEY (User_ID) REFERENCES USERS(User_ID) )");
//                ", FOREIGN KEY (Follower_ID) REFERENCES FOLLOWERS(Follower_ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase shieldXDB, int oldVersion, int newVersion) {
//        shieldXDB.execSQL("drop Table if exists users");
        shieldXDB.execSQL("drop Table if exists USERS");
        shieldXDB.execSQL("drop Table if exists FOLLOWERS");
        shieldXDB.execSQL("drop Table if exists ACTIVITY_LOG");
        onCreate(shieldXDB);
    }
    public boolean insertData(String firstname,String lastname, String phoneNumber, String emailID, String password){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("firstname",firstname);
        contentValues.put("lastname",lastname);
        contentValues.put("phone_number",phoneNumber);
        contentValues.put("email_id",emailID);
        contentValues.put("password",password);

        long result = shieldXDB.insert("USERS",null,contentValues);
        if(result==-1) return false;
        else return true;
    }
    public boolean checkDataOnLogin(String email, String password){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        Cursor cursor = shieldXDB.rawQuery("Select * from USERS where email_id=? and password=?", new String[] {email, password});
        if (cursor.moveToFirst()) //cursor.getCount()>0)
            return true;
        else
            return false;
    }
    public boolean checkDataOnSignUp(String phone,String email){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        Cursor cursor = shieldXDB.rawQuery("Select * from USERS where phone_number=? and email_id=?", new String[] {phone,email});
        if (cursor.moveToFirst())
            return true;
        else
            return false;
    }
//    public boolean insertFollowers(String follower_firstname,String follower_lastname, String follower_phone_number, String follower_email_id, String follower_about, Integer user_ID){
//        SQLiteDatabase shieldXDB = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("follower_firstname",follower_firstname);
//        contentValues.put("follower_lastname",follower_lastname);
//        contentValues.put("follower_phone_number",follower_phone_number);
//        contentValues.put("follower_email_id",follower_email_id);
//        contentValues.put("follower_about",follower_about);
//        contentValues.put("User_ID",user_ID);
//
//        long result = shieldXDB.insert("FOLLOWERS",null,contentValues);
//        if(result==-1) return false;
//        else return true;
//    }

    public boolean insertActivity(String current_Location,String destination, String time_taken, String acty_startTime, String acty_endTime, boolean acty_status, Integer user_ID){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("currentLocation",current_Location);
        contentValues.put("destination",destination);
        contentValues.put("time_taken",time_taken);
        contentValues.put("start_acty",acty_startTime);
        contentValues.put("end_acty",acty_endTime);
        contentValues.put("acty_status",acty_status);
        contentValues.put("User_ID",user_ID);

        long result = shieldXDB.insert("ACTIVITY_LOG",null,contentValues);
        if(result==-1) return false;
        else return true;
    }
}
