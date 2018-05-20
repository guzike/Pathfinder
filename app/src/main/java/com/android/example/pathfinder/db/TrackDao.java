package com.android.example.pathfinder.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface TrackDao {

    /**
     * Select all tracks in descending order.
     *
     * @return the list of all tracks.
     */
    @Query("SELECT * FROM track ORDER BY id DESC")
    LiveData<List<TrackEntry>> getAllTracks();

    /**
     * Select all tracks which are supposed to be displayed on the map.
     *
     * @return the list of all tracks to be drawn.
     */
    @Query("SELECT * FROM track WHERE inProgress = 1 OR displayed = 1")
    LiveData<List<TrackEntry>> getTracksToDisplay();

    /**
     * Select track by its trackId.
     *
     * @param trackId the trackId of the track.
     * @return the track with provided trackId.
     */
    @Query("SELECT * FROM track WHERE trackId = :trackId")
    LiveData<TrackEntry> getTrackById(String trackId);

    /**
     * Select track which is currently in progress state.
     *
     * @return the track in progress.
     */
    @Query("SELECT * FROM track WHERE inProgress = 1 ORDER BY id DESC LIMIT 1")
    LiveData<TrackEntry> getTrackInProgress();

    /**
     * Update track with new track data and end time.
     *
     * @param trackId the trackId of the track to be updated.
     * @param track   the new track data.
     * @param endDate the new end date.
     */
    @Query("UPDATE track SET track = :track, endDate = :endDate WHERE trackId = :trackId")
    void updateTrack(String trackId, String track, Date endDate);

    /**
     * Update track details.
     *
     * @param trackId   the trackId of the track to be updated.
     * @param name      the new name of the track.
     * @param color     the new color of the track.
     * @param displayed the new displayed state of the track.
     */
    @Query("UPDATE track SET name = :name, color = :color, displayed = :displayed WHERE trackId = :trackId")
    void updateTrackDetails(String trackId, String name, int color, boolean displayed);

    /**
     * Update states of all the tracks to idle.
     */
    @Query("UPDATE track SET inProgress = 0 WHERE inProgress = 1")
    void resetProgressState();

    /**
     * Delete the track from the database.
     *
     * @param trackId the trackId of the track to be deleted.
     */
    @Query("DELETE FROM track WHERE trackId = :trackId")
    void deleteTrack(String trackId);

    /**
     * Insert the new track to the database.
     *
     * @param trackEntry the track to be inserted.
     */
    @Insert
    void insertTrack(TrackEntry trackEntry);

}
