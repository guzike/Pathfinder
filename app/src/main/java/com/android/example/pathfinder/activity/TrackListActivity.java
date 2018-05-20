package com.android.example.pathfinder.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.android.example.pathfinder.AppExecutors;
import com.android.example.pathfinder.R;
import com.android.example.pathfinder.adapter.TracksAdapter;
import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.Collections;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    private TracksAdapter mTracksAdapter;
    private RecyclerView mRecyclerView;
    private List<TrackEntry> mTracksList;
    private AppDatabase mDb;
    private View mNoTracksView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        mDb = AppDatabase.getInstance(this);

        mTracksList = Collections.emptyList();
        mRecyclerView = findViewById(R.id.track_list);
        mTracksAdapter = new TracksAdapter(this, mTracksList);
        mNoTracksView = findViewById(R.id.no_tracks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTracksAdapter);

        ItemTouchHelper.Callback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (!mTracksList.get(viewHolder.getAdapterPosition()).isInProgress()) {
                    deleteTrack((String) viewHolder.itemView.getTag());
                }
                mTracksAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        setupViewModel();
    }

    private void setupViewModel() {
        TrackListViewModel viewModel = ViewModelProviders.of(this).get(TrackListViewModel.class);
        viewModel.getAllTracks().observe(this, tracks -> {
            mTracksList = tracks;
            mTracksAdapter.setTracks(tracks);
            mNoTracksView.setVisibility(tracks == null || tracks.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * Delete track from the database.
     *
     * @param trackId the trackId of a track to delete.
     */
    private void deleteTrack(String trackId) {
        AppExecutors.getInstance().diskIO().execute(() -> mDb.trackDao().deleteTrack(trackId));
    }

}
