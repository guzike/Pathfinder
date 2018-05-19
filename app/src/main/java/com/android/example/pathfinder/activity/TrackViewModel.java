package com.android.example.pathfinder.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

public class TrackViewModel extends ViewModel {

    private LiveData<TrackEntry> track;

    public TrackViewModel(AppDatabase database, String trackId) {
        this.track = database.trackDao().getTrackById(trackId);
    }

    public LiveData<TrackEntry> getTrack() {
        return track;
    }
}
