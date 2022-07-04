package com.example.shieldx.DAO;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

public class ActivityLog implements Serializable {

    LatLng currentLocation, destination, source;
    String userMail, userName, destinationName, sourceName, modeOfTransport, duration, activityDate;
    Long durationInSeconds;
    Boolean journeyCompleted, destinationReached, aborted;
    Time expectedTime;
    List<Follower> followersList;

    public ActivityLog() {
    }

    public ActivityLog(String userMail,LatLng startLocation, LatLng destination, String destinationName){

        this.userMail = userMail;
        this.source = startLocation;
        this.destination = destination;
        this.destinationName = destinationName;
    }
    public ActivityLog(LatLng currentLocation, LatLng destination, LatLng startLocation, String userMail, String userName, String destinationName, String sourceName, String typeOfActivity, Boolean journeyCompleted, Boolean destinationReached, Time expectedTime, List<Follower> followersList) {
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.source = startLocation;
        this.userMail = userMail;
        this.userName = userName;
        this.destinationName = destinationName;
        this.sourceName = sourceName;
        this.modeOfTransport = typeOfActivity;
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

    public LatLng getSource() {
        return source;
    }

    public void setSource(LatLng source) {
        this.source = source;
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

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public Boolean getAborted() {
        return aborted;
    }

    public void setAborted(Boolean aborted) {
        this.aborted = aborted;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }
}
