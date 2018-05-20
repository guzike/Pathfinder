package com.android.example.pathfinder.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.example.pathfinder.AppExecutors;
import com.android.example.pathfinder.R;
import com.android.example.pathfinder.activity.TrackViewModel;
import com.android.example.pathfinder.activity.TrackViewModelFactory;
import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;

public class TrackDialogFragment extends DialogFragment {

    private static final String TAG = TrackDialogFragment.class.getSimpleName();

    private static final String KEY = "trackId";

    private String mTrackId = "";
    private AppDatabase mDb;
    private Context mContext;
    private Button mOkButton;
    private TextInputEditText mNameInput;
    private TextInputEditText mColorInput;
    private TextView mStartDate;
    private TextView mEndDate;
    private CheckBox mDisplayCheckbox;

    public static TrackDialogFragment newInstance(String trackId) {
        TrackDialogFragment f = new TrackDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(KEY, trackId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrackId = getArguments().getString(KEY);
        }
        mDb = AppDatabase.getInstance(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.track_dialog, container, false);

        mOkButton = v.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(button -> {
            updateTrackDetails();
            TrackDialogFragment.this.dismiss();
        });

        mNameInput = v.findViewById(R.id.name_input);
        mColorInput = v.findViewById(R.id.color_input);
        mStartDate = v.findViewById(R.id.start);
        mEndDate = v.findViewById(R.id.end);
        mDisplayCheckbox = v.findViewById(R.id.display);

        TrackViewModelFactory factory = new TrackViewModelFactory(mDb, mTrackId);
        final TrackViewModel viewModel = ViewModelProviders.of(this, factory).get(TrackViewModel.class);
        viewModel.getTrack().observe(this, track -> {
            viewModel.getTrack().removeObservers(this);
            updateUi(track);
        });

        return v;
    }

    /**
     * Update the UI with new data from the database.
     * @param track the new track data.
     */
    private void updateUi(@Nullable TrackEntry track) {
        Log.d(TAG, "updateUi of Dialog");
        if (track == null) {
            return;
        }
        mNameInput.setText(track.getName());
        mColorInput.setText(String.valueOf(track.getColor()));
        mStartDate.setText(track.getStartDate().toString());
        mEndDate.setText(track.getEndDate().toString());
        mDisplayCheckbox.setChecked(track.isDisplayed());
    }

    /**
     * Update information about the track in the database.
     */
    private void updateTrackDetails() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDb.trackDao().updateTrackDetails(
                    mTrackId,
                    mNameInput.getText().toString(),
                    Integer.valueOf(mColorInput.getText().toString()),
                    mDisplayCheckbox.isChecked());
        });
    }
}
