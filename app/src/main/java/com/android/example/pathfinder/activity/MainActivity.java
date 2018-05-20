package com.android.example.pathfinder.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.example.pathfinder.R;
import com.android.example.pathfinder.db.TrackEntry;
import com.android.example.pathfinder.service.TrackService;
import com.android.example.pathfinder.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;
    private boolean mIsIdleSate = false;
    private GoogleMap mMap;
    private PermissionUtils.PermissionDeniedDialog mPermissionDeniedDialog;
    private PermissionUtils.RationaleDialog mRationaleDialog;
    private FloatingActionButton mRecFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecFab = findViewById(R.id.rec_fab);
        mRecFab.setBackgroundColor(Color.WHITE);
        mRecFab.setOnClickListener(view -> {
            showSnackBar(mIsIdleSate);
            Intent intent = new Intent(this, TrackService.class);
            intent.setAction(TrackService.ACTION_TOGGLE);
            startService(intent);
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTrackService();
    }

    @Override
    protected void onPause() {
        dismissPermissionDialogs();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_tracks_list) {
            Intent intent = new Intent(MainActivity.this, TrackListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        setupViewModel();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Checks the state of TrackService in case it was killed.
     */
    private void checkTrackService() {
        Intent intent = new Intent(this, TrackService.class);
        intent.setAction(TrackService.ACTION_CHECK_STATE);
        startService(intent);
    }

    /**
     * Dismiss all permission dialogs.
     */
    private void dismissPermissionDialogs() {
        if (mPermissionDeniedDialog != null) {
            mPermissionDeniedDialog.dismiss();
            mPermissionDeniedDialog = null;
        }
        if (mRationaleDialog != null) {
            mRationaleDialog.dismiss();
            mRationaleDialog = null;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        mPermissionDeniedDialog = PermissionUtils.PermissionDeniedDialog.newInstance();
        mPermissionDeniedDialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            mRationaleDialog = PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Setup view model for the MainActivity.
     */
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTracksToDisplay().observe(this, this::drawTracks);
        viewModel.getTrackInProgress().observe(this, this::updateUi);
    }

    /**
     * Draw provided tracks on the map.
     * @param trackEntries the tracks to be drawn.
     */
    private void drawTracks(List<TrackEntry> trackEntries) {
        Log.d(TAG, "drawTracks");
        mMap.clear();
        for (TrackEntry track : trackEntries) {
            drawTrack(track);
        }
    }

    /**
     * Draw provided track on the map.
     * @param track the track to be drawn.
     */
    private void drawTrack(TrackEntry track) {
        List<LatLng> decodedPath = PolyUtil.decode(track.getTrack());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(track.getColor()));
    }

    /**
     * Update current state depending on existing of a track in "recording" state.
     * @param trackEntry the track in "recording" state.
     */
    private void updateUi(TrackEntry trackEntry) {
        Log.d(TAG, "updateUi");
        mIsIdleSate = trackEntry == null;
        updateFab(mIsIdleSate);
    }

    /**
     * Update floating action button state.
     * @param isIdleState the new state.
     */
    private void updateFab(boolean isIdleState) {
        mRecFab.setImageResource(isIdleState ? R.drawable.ic_circle : R.drawable.ic_square);
    }

    /**
     * Show Snackbar to indicate recording status changes.
     * @param isIdleState the current state.
     */
    private void showSnackBar(boolean isIdleState) {
        int snackBarText = isIdleState ? R.string.snack_bar_recording_started : R.string.snack_bar_recording_stopped;
        Snackbar.make(mRecFab, getText(snackBarText), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
