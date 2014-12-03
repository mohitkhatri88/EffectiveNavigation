package com.example.android.effectivenavigation;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by slohia on 10/27/14.
 */
public class MapFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener,
        LocationListener {

    private LocationClient mLocationClient;
    Location mCurrentLocation;



    private PendingIntent mGeofenceRequestIntent;
    public enum REQUEST_TYPE {ADD}
    private REQUEST_TYPE mRequestType;
    private boolean mGeofenceRequestInProgress;
    private ArrayList<Geofence> mCurrentGeofences;
    private List<Boolean> mGeofencesAdded;

    public static final String ARG_SECTION_NUMBER = "section_number";


    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS  = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = FASTEST_INTERVAL_IN_SECONDS * MILLISECONDS_PER_SECOND;
    private static final long SECONDS_PER_HOUR = 60;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 24;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;

    LocationRequest mLocationRequest;
    boolean mUpdatesRequested;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;


    public static MapView viewMap;

    ArrayList<LatLng> locationList;
    public static int gameState = 0;


    // Check for Google Play Services.
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static MapView getViewMap(){
        return viewMap;
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    // Handle results returned to the FragmentActivity by Google PLay services
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Geofence Detection", "Google Play Services is available");
            return true;
        } else {
            // Some error handling code
            return false;
        }
    }

    // Right now this is hard coded
    private ArrayList<LatLng> fetchLocations() {
        LatLng loc1 = new LatLng(37.377166, -122.086966);
        LatLng loc2 = new LatLng(37.382008, -122.086151);
        LatLng loc3 = new LatLng(37.385461, -122.078265);
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        list.add(loc1);
        list.add(loc2);
        list.add(loc3);

        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
        viewMap = (MapView) rootView.findViewById(R.id.mapview);
        viewMap.onCreate(savedInstanceState);
        viewMap.onResume();
        GoogleMap map = viewMap.getMap();
        MapsInitializer.initialize(this.getActivity());
        mGeofenceRequestInProgress = false;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();

        mLocationClient = new LocationClient(this.getActivity(), this, this);
        mUpdatesRequested = false;
        mGeofenceRequestInProgress = true;


        locationList  = fetchLocations();
        mCurrentGeofences = new ArrayList<Geofence>();
        for (int i = 0; i < locationList.size(); i++){
            LatLng loc = locationList.get(i);
            SimpleGeofence mUIGeofence = new SimpleGeofence(
                    Integer.toString(i),
                    loc.latitude,
                    loc.longitude,
                    Float.valueOf("10"),
                    GEOFENCE_EXPIRATION_TIME,
                    Geofence.GEOFENCE_TRANSITION_ENTER
            );
            mCurrentGeofences.add(mUIGeofence.toGeofence());
        }
        mGeofencesAdded =new ArrayList<Boolean>(Arrays.asList(new Boolean[mCurrentGeofences.size()]));
        Collections.fill(mGeofencesAdded, new Boolean(false));

        viewMap.getMap().setMyLocationEnabled(true);
        return rootView;
    }

    public static void resetMarkers() {
        if (viewMap != null){
            viewMap.getMap().clear();
        }
    }

    // Create a PendingIntent that triggers an IntentService when a transition occurs
    private PendingIntent getTransitionPendingIntent(){
        Intent intent = new Intent(this.getActivity(), ReceiveTransitionsIntentService.class);
        Log.d("DEBUG", "returning intent");
        return PendingIntent.getService(
                this.getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Toast.makeText(this.getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
        mCurrentLocation = mLocationClient.getLastLocation();

        if (MapFragment.gameState >= locationList.size()){
            Toast.makeText(this.getActivity(), "Game OVER", Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "GAME OVER");
            gameState = 0; // We'll do this is in a loop

        } else {
            LatLng destination = locationList.get(gameState);
            viewMap.getMap().addMarker(new MarkerOptions().position(destination).title("Destination"));
            viewMap.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 15));

            if (!mGeofencesAdded.get(gameState)) {

                Log.d("DEBUG", "Need to add geofences");

                // Add Geofences
                mGeofenceRequestIntent = getTransitionPendingIntent();
                mLocationClient.addGeofences(mCurrentGeofences.subList(gameState,gameState+1), mGeofenceRequestIntent, this);
                mGeofencesAdded.set(gameState, true);
            }
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this.getActivity(), "Disconnected", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this.getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) +
                "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
        Log.d("DEBUG", "Location changed");
    }


    @Override
    public void onPause() {
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop(){
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }

        viewMap.getMap().setMyLocationEnabled(true);
    }

    // Provide the implementation if OnAddGeofencesResultListener.onAddGeofencesResult
    @Override
    public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
        if (LocationStatusCodes.SUCCESS == statusCode) {
            Toast.makeText(this.getActivity(), "Geofences added", Toast.LENGTH_SHORT).show();
            Log.d("DEBUG", "added geofences");
        } else {
            Toast.makeText(this.getActivity(), "Geofences failed to add", Toast.LENGTH_SHORT).show();
            Log.d("DEBUG", "couldn't add geofences");
        }
        mGeofenceRequestInProgress = false; // this is probably not set properly
    }
}