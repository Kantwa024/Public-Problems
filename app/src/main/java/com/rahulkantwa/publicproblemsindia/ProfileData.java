package com.rahulkantwa.publicproblemsindia;

public class ProfileData {
    public String profileurl;
    private String name;

    public ProfileData(String profileurl, String name) {
        this.profileurl = profileurl;
        this.name = name;
    }

    public ProfileData()
    {

    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

