package com.android.example.pathfinder.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.pathfinder.R;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TrackViewHolder> {

    private List<TrackEntry> tracksList;

    public TracksAdapter(List<TrackEntry> tracksList) {
        this.tracksList = tracksList;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_list_item, parent, false);

        return new TrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        TrackEntry track = tracksList.get(position);
        holder.name.setText(track.getName());
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public void setTracks(List<TrackEntry> list){
        this.tracksList = list;
        notifyDataSetChanged();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        TrackViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.track_name);
        }
    }
}
