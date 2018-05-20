package com.android.example.pathfinder.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.List;

public class TrackListViewModel extends AndroidViewModel {

    private static final String TAG = TrackListViewModel.class.getSimpleName();

    private LiveData<List<TrackEntry>> tracks;

    public TrackListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "getTracks");
        tracks = database.trackDao().getAllTracks();
    }

    public LiveData<List<TrackEntry>> getAllTracks() {
        return tracks;
    }
}
