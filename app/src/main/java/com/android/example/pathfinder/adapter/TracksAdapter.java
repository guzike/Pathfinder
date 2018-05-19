package com.android.example.pathfinder.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.pathfinder.R;
import com.android.example.pathfinder.db.TrackEntry;

import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TrackViewHolder> {

    private static final String FRAGMENT_DIALOG_TAG = "FRAGMENT_DIALOG_TAG";

    private AppCompatActivity mActivity;
    private List<TrackEntry> mTracksList;

    public TracksAdapter(AppCompatActivity activity, List<TrackEntry> tracksList) {
        this.mActivity = activity;
        this.mTracksList = tracksList;
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
        TrackEntry track = mTracksList.get(position);
        holder.name.setText(track.getName());
        holder.clickItem.setTag(track.getTrackId());
        holder.clickItem.setOnClickListener(v -> showDialog((String) v.getTag()));
    }

    @Override
    public int getItemCount() {
        return mTracksList.size();
    }

    public void setTracks(List<TrackEntry> list) {
        this.mTracksList = list;
        notifyDataSetChanged();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View clickItem;

        TrackViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.track_name);
            clickItem = view.findViewById(R.id.click_item);
        }
    }

    public static class TrackDialogFragment extends DialogFragment {

        private static final String KEY = "trackId";

        private String mTrackId = "";

        static TrackDialogFragment newInstance(String trackId) {
            TrackDialogFragment f = new TrackDialogFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString(KEY, trackId);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mTrackId = getArguments().getString(KEY);
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.track_dialog, container, false);

//            // Watch for button clicks.
//            Button button = (Button)v.findViewById(R.id.show);
//            button.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    // When button is clicked, call up to owning activity.
//                    ((FragmentDialog)getActivity()).showDialog();
//                }
//            });

            return v;
        }
    }

    private void showDialog(String trackId) {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = TrackDialogFragment.newInstance(trackId);
        newFragment.show(ft, FRAGMENT_DIALOG_TAG);
    }
}
