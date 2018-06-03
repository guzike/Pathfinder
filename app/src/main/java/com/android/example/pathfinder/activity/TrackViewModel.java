package com.android.example.pathfinder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.example.pathfinder.AppExecutors;
import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

public class TrackViewModel extends ViewModel {

    private LiveData<TrackEntry> track;
    private AppDatabase mDb;

    TrackViewModel(AppDatabase database, String trackId) {
        this.mDb = database;
        this.track = database.trackDao().getTrackById(trackId);
    }

    public LiveData<TrackEntry> getTrack() {
        return track;
    }

    /**
     * Update information about the track in the database.
     */
    public void updateTrackDetails(String name, int color, boolean isChecked) {
        if (track.getValue() == null) {
            return;
        }
        AppExecutors.getInstance().diskIO().execute(() -> mDb.trackDao().updateTrackDetails(
                track.getValue().getTrackId(),
                name,
                color,
                isChecked));
    }
}
