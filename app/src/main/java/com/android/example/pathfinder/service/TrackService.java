package com.android.example.pathfinder.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.example.pathfinder.App;
import com.android.example.pathfinder.AppExecutors;
import com.android.example.pathfinder.R;
import com.android.example.pathfinder.activity.MainActivity;
import com.android.example.pathfinder.db.AppDatabase;
import com.android.example.pathfinder.db.TrackEntry;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.Date;
import java.util.List;

public class TrackService extends Service {
    public static final String ACTION_CHECK_STATE = "ACTION_CHECK_STATE";
    public static final String ACTION_TOGGLE = "ACTION_TOGGLE";
    private static final String TAG = TrackService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 111;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0f; // TODO: set it to 1 when the app is done
    private static final String TRACK_DEFAULT_NAME = "Track name";
    private static final int TRACK_DEFAULT_COLOR = Color.BLACK;
    private String mTrackId = null;
    private String mEncodedTrack = "";

    private boolean mIsStarted = false;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private AppDatabase mDb;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mDb = AppDatabase.getInstance(this);
        mTrackId = String.valueOf(System.currentTimeMillis());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                List<LatLng> pointsList = PolyUtil.decode(mEncodedTrack);
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, location.toString());
                    pointsList.add(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                mEncodedTrack = PolyUtil.encode(pointsList);
                updateTrack();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        String action = intent.getAction();
        if (action == null || action.isEmpty()) {
            stopService();
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case ACTION_CHECK_STATE:
                if (!mIsStarted) {
                    stopService();
                }
                return super.onStartCommand(intent, flags, startId);
            case ACTION_TOGGLE:
                if (mIsStarted) {
                    stopService();
                    return super.onStartCommand(intent, flags, startId);
                } else {
                    mIsStarted = true;
                    insertTrack();
                    startLocationUpdates();
                }
                break;
            default:
                stopService();
                return super.onStartCommand(intent, flags, startId);
        }


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, App.CHANNEL_ID)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.ticker_text))
                        .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Insert new track in the database.
     */
    private void insertTrack() {
        AppExecutors.getInstance().diskIO().execute(() ->
                mDb.trackDao().insertTrack(new TrackEntry(
                        TRACK_DEFAULT_NAME,
                        mTrackId,
                        mEncodedTrack,
                        new Date(),
                        new Date(),
                        false,
                        true,
                        TRACK_DEFAULT_COLOR)));
    }

    /**
     * Update current track with new track data and end date.
     */
    private void updateTrack() {
        AppExecutors.getInstance().diskIO().execute(() ->
                mDb.trackDao().updateTrack(mTrackId, mEncodedTrack, new Date()));
    }

    /**
     * Stop all pending recordings.
     */
    private void stopRecordings() {
        AppExecutors.getInstance().diskIO().execute(() -> mDb.trackDao().resetProgressState());
    }

    /**
     * Delete current track from the database.
     */
    private void deleteTrack() {
        AppExecutors.getInstance().diskIO().execute(() -> mDb.trackDao().deleteTrack(mTrackId));
    }

    /**
     * Create new location request to location services.
     * @return the new location request.
     */
    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(LOCATION_INTERVAL)
                .setFastestInterval(LOCATION_INTERVAL)
                .setSmallestDisplacement(LOCATION_DISTANCE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Start location updates if permissions were granted.
     */
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            stopService();
            return;
        }

        mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                null /* Looper */);
    }

    /**
     * Stop current service saving data properly.
     */
    private void stopService() {
        if ("".equals(mEncodedTrack)) {
            deleteTrack();
        }
        stopRecordings();
        stopSelf();
    }

}
