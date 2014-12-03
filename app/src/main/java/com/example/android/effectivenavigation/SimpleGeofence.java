package com.example.android.effectivenavigation;

import com.google.android.gms.location.Geofence;

/**
 * Created by vinaykola on 11/25/14.
 */
public class SimpleGeofence {
    //Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;

    public SimpleGeofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition ) {
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    public String getId(){
        return mId;
    }
    public double getLatitude(){
        return mLatitude;
    }
    public double getLongitude(){
        return mLongitude;
    }
    public float getRadius(){
        return mRadius;
    }
    public long getExpirationDuration(){
        return mExpirationDuration;
    }
    public int getTransitionType(){
        return mTransitionType;
    }

    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(mExpirationDuration)
                .setNotificationResponsiveness(5*1000)
                .build();
    }
}
