package com.android.example.pathfinder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<TrackEntry>> tracksToDisplay;

    private LiveData<TrackEntry> trackInProgress;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "getTracksToDisplay");
        tracksToDisplay = database.trackDao().getTracksToDisplay();
        trackInProgress = database.trackDao().getTrackInProgress();
    }

    public LiveData<List<TrackEntry>> getTracksToDisplay() {
        return tracksToDisplay;
    }

    public LiveData<TrackEntry> getTrackInProgress() {
        return trackInProgress;
    }
}
