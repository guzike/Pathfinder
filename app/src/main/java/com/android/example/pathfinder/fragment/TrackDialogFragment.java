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

import com.android.example.pathfinder.R;
import com.android.example.pathfinder.activity.TrackViewModel;
import com.android.example.pathfinder.activity.TrackViewModelFactory;
import com.android.example.pathfinder.db.TrackEntry;

public class TrackDialogFragment extends DialogFragment {

    private static final String TAG = TrackDialogFragment.class.getSimpleName();

    private static final String KEY = "trackId";

    private static final String NAME_KEY = "NAME_KEY";
    private static final String COLOR_KEY = "COLOR_KEY";
    private static final String START_KEY = "START_KEY";
    private static final String END_KEY = "END_KEY";
    private static final String DISPLAY_KEY = "DISPLAY_KEY";

    private TrackViewModel mViewModel;
    private String mTrackId = "";
    private Context mContext;
    private Button mOkButton;
    private TextInputEditText mNameInput;
    private TextInputEditText mColorInput;
    private TextView mStartDate;
    private TextView mEndDate;
    private CheckBox mDisplayCheckbox;

    public static TrackDialogFragment newInstance(String trackId) {
        TrackDialogFragment f = new TrackDialogFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.track_dialog, container, false);

        mOkButton = v.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(button -> {
            mViewModel.updateTrackDetails(
                    mNameInput.getText().toString(),
                    Integer.valueOf(mColorInput.getText().toString()),
                    mDisplayCheckbox.isChecked());
            TrackDialogFragment.this.dismiss();
        });

        mNameInput = v.findViewById(R.id.name_input);
        mColorInput = v.findViewById(R.id.color_input);
        mStartDate = v.findViewById(R.id.start);
        mEndDate = v.findViewById(R.id.end);
        mDisplayCheckbox = v.findViewById(R.id.display);

        if (savedInstanceState == null) {
            TrackViewModelFactory factory = new TrackViewModelFactory(mContext.getApplicationContext(), mTrackId);
            mViewModel = ViewModelProviders.of(this, factory).get(TrackViewModel.class);
            mViewModel.getTrack().observe(this, track -> {
                mViewModel.getTrack().removeObservers(this);
                updateUi(track);
            });
        } else {
            restoreUi(savedInstanceState);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(NAME_KEY, mNameInput.getText().toString());
        outState.putString(COLOR_KEY, mColorInput.getText().toString());
        outState.putString(START_KEY, mStartDate.getText().toString());
        outState.putString(END_KEY, mEndDate.getText().toString());
        outState.putBoolean(DISPLAY_KEY, mDisplayCheckbox.isChecked());
        super.onSaveInstanceState(outState);
    }

    /**
     * Restore the UI from saved data.
     *
     * @param bundle the track data.
     */
    private void restoreUi(Bundle bundle) {
        Log.d(TAG, "restore of Dialog");
        if (bundle == null) {
            return;
        }
        mNameInput.setText(bundle.getString(NAME_KEY));
        mColorInput.setText(bundle.getString(COLOR_KEY));
        mStartDate.setText(bundle.getString(START_KEY));
        mEndDate.setText(bundle.getString(END_KEY));
        mDisplayCheckbox.setChecked(bundle.getBoolean(DISPLAY_KEY));
    }

    /**
     * Update the UI with new data from the database.
     *
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

}
