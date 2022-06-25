package com.example.shieldx.Util;

import com.example.shieldx.DAO.Follower;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.List;

public class JourneyModel {

    String destinationName, sourceName, modeOfTransport, duration, followers;
    Boolean journeyCompleted, destinationReached, aborted;

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public Boolean getAborted() {
        return aborted;
    }

    public void setAborted(Boolean aborted) {
        this.aborted = aborted;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }
}
