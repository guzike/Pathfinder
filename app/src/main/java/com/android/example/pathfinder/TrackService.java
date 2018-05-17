package com.android.example.pathfinder;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
    private static final String TAG = TrackService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 111;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0f; // TODO: set it to 1 when the app is done
    private static final String TRACK_DEFAULT_NAME = "Track name";

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
        if (mIsStarted) {
            stopService();
        } else {
            mIsStarted = true;
            insertTrack();
            startLocationUpdates();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void insertTrack() {
        AppExecutors.getInstance().diskIO().execute(() ->
                mDb.trackDao().insertTrack(new TrackEntry(TRACK_DEFAULT_NAME, mTrackId, mEncodedTrack, new Date(), new Date())));
    }

    private void updateTrack() {
        AppExecutors.getInstance().diskIO().execute(() ->
                mDb.trackDao().updateTrack(mTrackId, mEncodedTrack, new Date()));
    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(LOCATION_INTERVAL)
                .setFastestInterval(LOCATION_INTERVAL)
                .setSmallestDisplacement(LOCATION_DISTANCE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

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

    private void stopService() {
        stopSelf();
    }

}
