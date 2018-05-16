package com.android.example.pathfinder.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CoordinateDao {

    @Query("SELECT * FROM coordinate WHERE trackId = (:trackId)")
    LiveData<List<CoordinateEntry>> loadCoordinates(String trackId);

    @Insert
    void insertCoordinate(CoordinateEntry coordinateEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrack(CoordinateEntry coordinateEntry);

    @Delete
    void deleteTrack(CoordinateEntry coordinateEntry);
}
