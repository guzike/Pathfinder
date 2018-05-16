package com.android.example.pathfinder.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "track")
public class TrackEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String trackId;

    @Ignore
    public TrackEntry(String name, String trackId) {
        this.name = name;
        this.trackId = trackId;
    }

    public TrackEntry(int id, String name, String trackId) {
        this.id = id;
        this.name = name;
        this.trackId = trackId;
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

}
