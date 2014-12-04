package com.example.android.effectivenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohit on 11/30/2014.
 */
public class StorySectionFragment extends Fragment {

    final static String ARG_POSITION = "position";
    int mCurrentPosition = 0;
    Map<Integer, PlotPointNode> storyLine = new HashMap<Integer, PlotPointNode>();

    LaunchpadSectionFragment.ChangeViewFromFragment mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LaunchpadSectionFragment.ChangeViewFromFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_section_story_view, container, false);
        // Demonstration of a collection-browsing activity.
        rootView.findViewById(R.id.demo_next_part_story)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                        //startActivity(intent);
                        //mCurrentPosition += 1;
                        updateArticleView(mCurrentPosition);
                    }
                });
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        //if (savedInstanceState != null) {
        //    mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        //}

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getStoryLine();
    }

    public void getStoryLine() {
        //TO DO : If the story line doesnt exist, then its better to just get out. For that we could call the read function here.
        // If the file name doesn't exist, better to get out of the fragment and go to the main fragment.
        //Get the value for the next part.
        //Read the XML from the raw folder, and then add the points to the story.
        InputStream plotLocations = this.getActivity().getResources().openRawResource(R.raw.plotlocations);
        InputStream storyLines = this.getActivity().getResources().openRawResource(R.raw.plotstory);
        XMLParser parserLocations = new XMLParser();
        XMLParser parserStory = new XMLParser();

        //Convert the input stream into a string and then get the dom elements
        String locations = parserLocations.convertStreamToString(plotLocations);
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
            Log.d("Story Section Fragment", "Plot Id :" + plotId + "Lat:" + Lat + "Lng :" + Lng);
            Log.d("Story Section Fragment", "Story Objective :" + objective);
            PlotPointNode node = new PlotPointNode(Lat, Lng, objective, category, location);
            this.storyLine.put(i,node);
        }


        parserLocations.writeXmlToFile(locations, this.getActivity(), "currentStoryBlah.xml");
    }

    @Override
    public void onResume() {
        super.onResume();

        //Retrieving the saved state of the story. This is where we will take it off from.
        //SharedPreferences settings =  this.getActivity().getPreferences(Context.MODE_PRIVATE);
        //this.mCurrentPosition = settings.getInt("currentNodeStory", -1);
        /*XMLParser parserLocations = new XMLParser();
        InputStream stream = parserLocations.readXmlFromFile(this.getActivity(), "currentStoryBlah.xml");

        //Convert the input stream into a string and then get the dom elements
        String locations = parserLocations.convertStreamToString(stream);
        Document doc = parserLocations.getDomElement(locations);

        //Now, Convert all the plot points into a hashmap based on the ids.
        NodeList nl = doc.getElementsByTagName(this.getActivity().getString(R.string.plot_point));

        for (int i = 0; i < nl.getLength(); i++) {
            Element Location = (Element) nl.item(i);

            String plotId = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_id));
            String Lat = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_latitutde));
            String Lng = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_longitude));
            String location = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_location));
            String category = parserLocations.getValue(Location, this.getActivity().getString(R.string.plot_Category));
            Log.d("ON RESUME Story Section Fragment", "Plot Id :" + plotId + "Lat:" + Lat + "Lng :" + Lng);
            //PlotPointNode node = new PlotPointNode(Lat, Lng, objective, category, location);
            //this.storyLine.put(i,node);
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();

        //Saving the current Progress in the story. The story would be picked up from here the next time.
        SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentNodeStory", mCurrentPosition);
        String msg = "Story Position in onStop:" + String.valueOf(mCurrentPosition);
        Log.d("Story Section Fragment", msg);
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.


        //Retrieving the saved state of the story. This is where we will take it off from.
        SharedPreferences settings =  this.getActivity().getPreferences(Context.MODE_PRIVATE);
        this.mCurrentPosition = settings.getInt("currentNodeStory", -1);
        String msg = "Story Position in Onstart:" + String.valueOf(mCurrentPosition);
        Log.d("Story Section Fragment", msg);
        updateArticleView(mCurrentPosition);
    }

    public void updateArticleView(int position) {
        Log.d("Story size as of you:", String.valueOf(storyLine.size()));
        if (mCurrentPosition >= storyLine.size()-1) {
            mCurrentPosition = -1;
            Log.d("Story Section Fragment", String.valueOf(mCurrentPosition));
            mListener.changeView(0);
        } else {
            Log.d("Story Position in updateArticleView", String.valueOf(mCurrentPosition));
            TextView article = (TextView) getActivity().findViewById(R.id.article);
            mCurrentPosition = mCurrentPosition + 1;
            PlotPointNode node = this.storyLine.get(mCurrentPosition);
            article.setText(node.getObjective());
            //mCurrentPosition = position+1;
            String msg = "Story position after updating the screen:" + String.valueOf(mCurrentPosition);
            Log.d("Story Section Fragment", msg);
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        //outState.putInt(ARG_POSITION, mCurrentPosition);
    }*/
}

