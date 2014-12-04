package com.example.android.effectivenavigation;

/**
 * Created by Mohit on 12/3/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that launches other parts of the demo application.
 */
public class LaunchpadSectionFragment extends Fragment {

    // Container Activity must implement this interface
    public interface ChangeViewFromFragment {
        public void changeView(int item);
    }

    ChangeViewFromFragment mListener;

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


        return rootView;
    }

    public void getNewStoryLocations() {
        XMLParser parser = new XMLParser();

        //Contact the URL, and get much awaited Story.
        //parser.getXmlFromUrl()
    }
}
