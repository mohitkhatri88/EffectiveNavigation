package com.example.android.effectivenavigation;

/**
 * Created by Mohit on 12/3/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;


/**
 * A fragment that launches other parts of the demo application.
 */
public class LaunchpadSectionFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    // CALL BACK CODE TO CHANGE THE FRAGMENT FROM WITHIN THIS FRAGMENT.
    public interface ChangeViewFromFragment {
        public void changeView(int item);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------

    //LOCATION CODE TO GET THE CURRENT LOCATION.
    ChangeViewFromFragment mListener;

    private LocationClient mLocationClient;
    Location mCurrentLocation;
    boolean mUpdatesRequested;
    LocationRequest mLocationRequest;

    // Check for Google Play Services.
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS  = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = FASTEST_INTERVAL_IN_SECONDS * MILLISECONDS_PER_SECOND;
    private static final long SECONDS_PER_HOUR = 60;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 24;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;

    @Override
    public void onConnected(Bundle dataBundle) {
        Toast.makeText(this.getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
        mCurrentLocation = mLocationClient.getLastLocation();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this.getActivity(), "Disconnected", Toast.LENGTH_SHORT ).show();
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
        //viewMap.getMap().setMyLocationEnabled(true);
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ChangeViewFromFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

        // Demonstration of navigating to external activities.
        rootView.findViewById(R.id.demo_external_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //In this case, the user wants to create a new Story. So the point is to get the story from the URl.
                        getNewStoryLocations();
                        mListener.changeView(1);

                    }
                });

        // Demonstration of navigating to external activities.
        rootView.findViewById(R.id.demo_collection_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Person is interested in going to the previous story. Just shift to the story.
                        mListener.changeView(1);
                    }
                });


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new LocationClient(this.getActivity(), this, this);
        return rootView;
    }

    public void getNewStoryLocations() {
        try {
            XMLParser parser = new XMLParser();

            String lat = String.valueOf(mCurrentLocation.getLatitude());
            Log.d("LAUNCH PAD SECTION FRAGMENT, latitude", lat);
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            Log.d("LAUNCH PAD SECTION FRAGMENT, longitude", lng);
            String url = "http://54.174.197.131:8000/api/locations/?" + "lat=" + lat + "&" + "lng"+"=" + lng;
            //String url = "http://54.174.197.131:8000/api/locations/?lat=33.775669&lng=-84.397402";
            //Contact the URL, and get much awaited Story.
            String story = parser.getXmlFromUrl(url);
            this.writeStoryLine(story);
        } catch(Exception e) {
            Log.d("Launchpad Story Locations", e.getMessage());
        }
    }


    public void writeStoryLine(String locations) {
        //TO DO : If the story line doesnt exist, then its better to just get out. For that we could call the read function here.
        // If the file name doesn't exist, better to get out of the fragment and go to the main fragment.
        //Get the value for the next part.
        //Read the XML from the raw folder, and then add the points to the story.
        //InputStream plotLocations = this.getActivity().getResources().openRawResource(R.raw.plotlocations);
        InputStream storyLines = this.getActivity().getResources().openRawResource(R.raw.plotstory);
        XMLParser parserLocations = new XMLParser();
        XMLParser parserStory = new XMLParser();

        //Convert the input stream into a string and then get the dom elements
        //String locations = parserLocations.convertStreamToString(plotLocations);
        Document doc = parserLocations.getDomElement(locations);
        String storyLine = parserStory.convertStreamToString(storyLines);
        Document docStory = parserStory.getDomElement(storyLine);

        //Now, Convert all the plot points into a hashmap based on the ids.
        NodeList nl = doc.getElementsByTagName(this.getActivity().getString(R.string.plot_point));
        NodeList nlStory = docStory.getElementsByTagName(this.getActivity().getString(R.string.plot_point));

        for (int i = 0; i < nl.getLength(); i++) {
            Element Location = (Element) nl.item(i);
            Element Story = (Element) nlStory.item(i);
            String plotId = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_id));
            String Lat = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_latitutde));
            String Lng = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_longitude));
            String location = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_location));
            String category = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_Category));
            String objective = parserLocations.getValue(Story, this.getActivity().getString(R.string.plot_objective));
            Log.d("Launch Pad Section Fragment", "Plot Id :" + plotId + "Lat:" + Lat + "Lng :" + Lng);
            Log.d("Launch Pad Section Fragment", "Story Objective :" + objective);
            Log.d("Launch Pad Section Fragment", "Story Location :" + location);
            PlotPointNode node = new PlotPointNode(Lat, Lng, objective, category, location);
            //this.storyLine.put(i,node);
        }

        parserLocations.writeXmlToFile(locations, this.getActivity(), "currentStoryBlah.xml");
    }
}
