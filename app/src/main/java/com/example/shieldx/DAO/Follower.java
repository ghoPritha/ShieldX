package com.example.shieldx.DAO;

import java.io.Serializable;

public class Follower implements Serializable {


    int followerID;
    String followerName, followerNumber, followerEmail, followerAbout;

    public Follower() {
    }

    public Follower( String follower_Name, String follower_Number, String follower_Email, String follower_About) {
        followerName = follower_Name;
        followerNumber = follower_Number;
        followerEmail = follower_Email;
        followerAbout = follower_About;
    }

    public int getFollowerID() {
        return followerID;
    }

    public void setFollowerID(int followerID) {
        this.followerID = followerID;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    public String getFollowerNumber() {
        return followerNumber;
    }

    public void setFollowerNumber(String followerNumber) {
        this.followerNumber = followerNumber;
    }

    public String getFollowerEmail() {
        return followerEmail;
    }
    public String encodedfollowerEmail() { return this.followerEmail.replace(".","%2E").replace("_","%5F").replace("@","%40");}
    public void setFollowerEmail(String followerEmail) {
        this.followerEmail = followerEmail;
    }

    public String getFollowerAbout() {
        return followerAbout;
    }

    public void setFollowerAbout(String followerAbout) {
        this.followerAbout = followerAbout;
    }

    public String encodedEmail() { return this.followerEmail.replace(".","%2E").replace("_","%5F").replace("@","%40");}
}
