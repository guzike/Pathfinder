package com.android.example.pathfinder.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.android.example.pathfinder.db.AppDatabase;

public class TrackViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String trackId;

    public TrackViewModelFactory(AppDatabase mDb, String trackId) {
        this.mDb = mDb;
        this.trackId = trackId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new TrackViewModel(mDb, trackId);
    }
}
