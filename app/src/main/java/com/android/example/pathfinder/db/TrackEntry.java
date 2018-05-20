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
    private boolean displayed;
    private boolean inProgress;
    private int color;

    @Ignore
    public TrackEntry(String name, String trackId, String track, Date startDate, Date endDate, boolean displayed, boolean inProgress, int color) {
        this.name = name;
        this.trackId = trackId;
        this.track = track;
        this.startDate = startDate;
        this.endDate = endDate;
        this.displayed = displayed;
        this.inProgress = inProgress;
        this.color = color;
    }

    public TrackEntry(int id, String name, String trackId, String track, Date startDate, Date endDate, boolean displayed, boolean inProgress, int color) {
        this.id = id;
        this.name = name;
        this.trackId = trackId;
        this.track = track;
        this.startDate = startDate;
        this.endDate = endDate;
        this.displayed = displayed;
        this.inProgress = inProgress;
        this.color = color;
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

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
