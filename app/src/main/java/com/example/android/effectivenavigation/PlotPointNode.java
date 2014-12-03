package com.example.android.effectivenavigation;

/**
 * Created by Mohit on 11/30/2014.
 */
public class PlotPointNode {
    String lat;
    String lng;
    String objective;
    String category;
    String Location;

    public PlotPointNode(String lat, String lng, String objective, String category, String location) {
        this.lat = lat;
        this.lng = lng;
        this.objective = objective;
        this.category = category;
        Location = location;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
