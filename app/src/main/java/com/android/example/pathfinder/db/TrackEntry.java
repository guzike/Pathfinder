package com.android.example.pathfinder.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "track")
public class TrackEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String trackId;
    private String track;
    private Date startDate;
    private Date endDate;

    @Ignore
    public TrackEntry(String name, String trackId, String track, Date startDate, Date endDate) {
        this.name = name;
        this.trackId = trackId;
        this.track = track;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TrackEntry(int id, String name, String trackId, String track, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.trackId = trackId;
        this.track = track;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
