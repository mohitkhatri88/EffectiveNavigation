package com.example.android.effectivenavigation;

/**
 * Created by Mohit on 12/3/2014.
 */

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

        // Demonstration of navigating to external activities.
        rootView.findViewById(R.id.demo_external_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create an intent that asks the user to pick a photo, but using
                        // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                        // the application from the device home screen does not return
                        // to the external activity.
                        Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                        externalActivityIntent.setType("image/*");
                        externalActivityIntent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(externalActivityIntent);
                    }
                });

        return rootView;
    }
}
