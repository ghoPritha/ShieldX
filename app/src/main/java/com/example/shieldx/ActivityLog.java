package com.example.shieldx;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.List;

public class ActivityLog {

    LatLng currentLocation, destination, startLocation;
    String userMail, userName, destinationName, sourceName, typeOfActivity;
    Boolean journeyCompleted, destinationReached;
    Time expectedTime;
    List<Follower> followersList;

    public ActivityLog() {
    }

    public ActivityLog(String userMail,LatLng startLocation){
        this.startLocation = startLocation;
        this.userMail = userMail;
    }
    public ActivityLog(LatLng currentLocation, LatLng destination, LatLng startLocation, String userMail, String userName, String destinationName, String sourceName, String typeOfActivity, Boolean journeyCompleted, Boolean destinationReached, Time expectedTime, List<Follower> followersList) {
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.startLocation = startLocation;
        this.userMail = userMail;
        this.userName = userName;
        this.destinationName = destinationName;
        this.sourceName = sourceName;
        this.typeOfActivity = typeOfActivity;
        this.journeyCompleted = journeyCompleted;
        this.destinationReached = destinationReached;
        this.expectedTime = expectedTime;
        this.followersList = followersList;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTypeOfActivity() {
        return typeOfActivity;
    }

    public void setTypeOfActivity(String typeOfActivity) {
        this.typeOfActivity = typeOfActivity;
    }

    public Boolean getJourneyCompleted() {
        return journeyCompleted;
    }

    public void setJourneyCompleted(Boolean journeyCompleted) {
        this.journeyCompleted = journeyCompleted;
    }

    public Boolean getDestinationReached() {
        return destinationReached;
    }

    public void setDestinationReached(Boolean destinationReached) {
        this.destinationReached = destinationReached;
    }

    public Time getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(Time expectedTime) {
        this.expectedTime = expectedTime;
    }

    public List<Follower> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(List<Follower> followersList) {
        this.followersList = followersList;
    }
}
