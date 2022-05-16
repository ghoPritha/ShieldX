package com.example.shieldx;

import com.google.android.material.timepicker.TimeFormat;

import java.sql.Time;

public class Activity {
    Integer activityId, followerId, userId;
    String destination,activityStatus;
    Time timeTaken, startActivity, endActivity;

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Integer followerId) {
        this.followerId = followerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Time getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Time timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Time getStartActivity() {
        return startActivity;
    }

    public void setStartActivity(Time startActivity) {
        this.startActivity = startActivity;
    }

    public Time getEndActivity() {
        return endActivity;
    }

    public void setEndActivity(Time endActivity) {
        this.endActivity = endActivity;
    }
}
