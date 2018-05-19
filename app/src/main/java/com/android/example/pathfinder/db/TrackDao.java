package com.android.example.pathfinder.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface TrackDao {

    @Query("SELECT * FROM track ORDER BY id DESC")
    LiveData<List<TrackEntry>> getAllTracks();

    @Query("SELECT * FROM track WHERE inProgress = 1 OR displayed = 1")
    LiveData<List<TrackEntry>> getTracksToDisplay();

    @Query("SELECT * FROM track WHERE inProgress = 1 ORDER BY id DESC LIMIT 1")
    LiveData<TrackEntry> getTrackInProgress();

    @Query("UPDATE track SET track = :track, endDate = :endDate WHERE trackId = :trackId")
    void updateTrack(String trackId, String track, Date endDate);

    @Query("DELETE FROM track WHERE trackId = :trackId")
    void deleteTrack(String trackId);

    @Query("UPDATE track SET inProgress = 0 WHERE inProgress = 1")
    void resetProgressState();

    @Insert
    void insertTrack(TrackEntry trackEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrack(TrackEntry trackEntry);

    @Delete
    void deleteTrack(TrackEntry trackEntry);
}
