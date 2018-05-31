package com.android.example.pathfinder.activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.example.pathfinder.AppExecutors;
import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.List;

public class TrackListViewModel extends AndroidViewModel {

    private static final String TAG = TrackListViewModel.class.getSimpleName();

    private LiveData<List<TrackEntry>> tracks;
    private AppDatabase mDb;

    public TrackListViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "getTracks");
        mDb = AppDatabase.getInstance(this.getApplication());
        tracks = mDb.trackDao().getAllTracks();
    }

    public LiveData<List<TrackEntry>> getAllTracks() {
        return tracks;
    }

    /**
     * Delete track from the database.
     *
     * @param trackId the trackId of a track to delete.
     */
    public void deleteTrack(String trackId) {
        AppExecutors.getInstance().diskIO().execute(() -> mDb.trackDao().deleteTrack(trackId));
    }
}
