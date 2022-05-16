package com.example.shieldx;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
        shieldXDB.execSQL("create Table FOLLOWERS (Follower_ID INTEGER PRIMARY KEY AUTOINCREMENT, follower_name TEXT NOT NULL , User_ID INTEGER NOT NULL, " +
                "follower_phone_number TEXT NOT NULL UNIQUE, follower_email_id TEXT NOT NULL, follower_about TEXT, FOREIGN KEY (User_ID) REFERENCES USERS(User_ID))");
        shieldXDB.execSQL("create Table ACTIVITY_LOG (Acty_ID INTEGER PRIMARY KEY AUTOINCREMENT, destination TEXT NOT NULL, " +
                "time_taken default (STRFTIME('%H:%M:%f')), start_acty default (STRFTIME('%Y-%m-%d %H:%M:%f'))," +
                "end_acty default (STRFTIME('%Y-%m-%d %H:%M:%f')),acty_status BOOLEAN default '0', " +
                "FOREIGN KEY (User_ID) REFERENCES USERS(User_ID) , FOREIGN KEY (Follower_ID) REFERENCES FOLLOWERS(Follower_ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase shieldXDB, int oldVersion, int newVersion) {
//        shieldXDB.execSQL("drop Table if exists users");
        shieldXDB.execSQL("drop Table if exists USERS");
//        shieldXDB.execSQL("drop Table if exists FOLLOWERS");
//        shieldXDB.execSQL("drop Table if exists ACTIVITY");
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
    public Cursor checkDataOnLogin(String email, String password){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        Cursor cursor = shieldXDB.rawQuery("Select * from USERS where email_id=? and password=?", new String[] {email, password});
        if (cursor.moveToFirst()) //cursor.getCount()>0)
            return cursor;
        else
            return null;
    }

    public boolean checkDataOnSignUp(String phone,String email){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        Cursor cursor = shieldXDB.rawQuery("Select * from USERS where phone_number=? and email_id=?", new String[] {phone,email});
        if (cursor.moveToFirst())
            return true;
        else
            return false;
    }

    public Cursor fetchData(String phone) {
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        Cursor cursor = shieldXDB.rawQuery("Select * from USERS where phone_number=?", new String[] {phone});
        return cursor;
    }

    public boolean insertDataInFollowers(Integer userId, String firstname, String phoneNumber, String emailID){
        SQLiteDatabase shieldXDB = this.getWritableDatabase();
        if(tableExists(shieldXDB, "FOLLOWERS")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("User_ID", userId);
            contentValues.put("follower_name", firstname);
            contentValues.put("follower_phone_number", phoneNumber);
            contentValues.put("follower_email_id", emailID);

            long result = shieldXDB.insert("FOLLOWERS", null, contentValues);
            if (result == -1) return false;
            else return true;
        }
        else{
            shieldXDB.execSQL("create Table FOLLOWERS (Follower_ID INTEGER PRIMARY KEY AUTOINCREMENT, follower_name TEXT NOT NULL , User_ID INTEGER NOT NULL, " +
                    "follower_phone_number TEXT NOT NULL UNIQUE, follower_email_id TEXT NOT NULL, follower_about TEXT, FOREIGN KEY (User_ID) REFERENCES USERS(User_ID))");
            ContentValues contentValues = new ContentValues();
            contentValues.put("User_ID", userId);
            contentValues.put("follower_firstname", firstname);
            contentValues.put("follower_phone_number", phoneNumber);
            contentValues.put("follower_email_id", emailID);

            long result = shieldXDB.insert("FOLLOWERS", null, contentValues);
            if (result == -1) return false;
            else return true;
        }
    }

    boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
                new String[] {"table", tableName}
        );
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
