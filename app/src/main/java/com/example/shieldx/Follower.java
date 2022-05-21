package com.example.shieldx;

public class Follower {


    int Follower_ID, User_ID;
    String Follower_Name, Follower_Number, Follower_Email, Follower_About;

    public Follower() {
    }

    public Follower(int user_ID, String follower_Name, String follower_Number, String follower_Email, String follower_About) {
        User_ID = user_ID;
        Follower_Name = follower_Name;
        Follower_Number = follower_Number;
        Follower_Email = follower_Email;
        Follower_About = follower_About;
    }

    public int getFollower_ID() {
        return Follower_ID;
    }

    public void setFollower_ID(int follower_ID) {
        this.Follower_ID = follower_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        this.User_ID = user_ID;
    }

    public String getFollower_Name() {
        return Follower_Name;
    }

    public void setFollower_Name(String follower_Name) {
        this.Follower_Name = follower_Name;
    }

    public String getFollower_Number() {
        return Follower_Number;
    }

    public void setFollower_Number(String follower_Number) {
        this.Follower_Number = follower_Number;
    }

    public String getFollower_Email() {
        return Follower_Email;
    }

    public void setFollower_Email(String follower_Email) {
        this.Follower_Email = follower_Email;
    }

    public String getFollower_About() {
        return Follower_About;
    }

    public void setFollower_About(String follower_About) {
        this.Follower_About = follower_About;
    }

    public String encodedEmail() { return this.Follower_Email.replace(".","%2E").replace("_","%5F").replace("@","%40");}
}
