package com.example.shieldx.Util;

import android.content.Context;

public class MySingleton {
    private  static MySingleton instance;
    private Context ctx;

    private MySingleton(Context context) {
        ctx = context;
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MySingleton(context);
        }
        return instance;
    }
}
