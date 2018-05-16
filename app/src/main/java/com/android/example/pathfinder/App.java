package com.android.example.pathfinder;

import android.app.Application;

import com.android.example.pathfinder.db.AppDatabase;

public class App extends Application {

    private static AppDatabase mDb;

    public static AppDatabase getDb(){
        return mDb;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = AppDatabase.getInstance(this);
    }
}
