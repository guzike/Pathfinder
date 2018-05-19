package com.android.example.pathfinder.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.example.pathfinder.R;
import com.android.example.pathfinder.adapter.TracksAdapter;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.Collections;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    private TracksAdapter mTracksAdapter;
    private RecyclerView mRecyclerView;
    private List<TrackEntry> mTracksList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        mTracksList = Collections.emptyList();
        mRecyclerView = findViewById(R.id.track_list);
        mTracksAdapter = new TracksAdapter(mTracksList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTracksAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        setupViewModel();
    }

    private void setupViewModel() {
        TrackListViewModel viewModel = ViewModelProviders.of(this).get(TrackListViewModel.class);
        viewModel.getAllTracks().observe(this, tracks -> {
            mTracksAdapter.setTracks(tracks);
        });
    }

}
