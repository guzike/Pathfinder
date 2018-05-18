package com.android.example.pathfinder.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {TrackEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public abstract TrackDao trackDao();

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "Tracks.db")
                        .build();
            }
            return sInstance;
        }
    }

}
