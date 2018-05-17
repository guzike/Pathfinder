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

    @Query("SELECT * FROM track")
    LiveData<List<TrackEntry>> loadAllTracks();

    @Query("UPDATE track SET track = :track, endDate = :endDate WHERE trackId = :trackId")
    void updateTrack(String trackId, String track, Date endDate);

    @Insert
    void insertTrack(TrackEntry trackEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrack(TrackEntry trackEntry);

    @Delete
    void deleteTrack(TrackEntry trackEntry);
}
