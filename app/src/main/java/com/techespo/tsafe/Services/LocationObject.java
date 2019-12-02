package com.techespo.tsafe.Services;

public class LocationObject {
    public LocationObject(String userName,String contactOne,String contactTwo,String lat,String lang)
    {
        this.userName=userName;
        this.contactOne=contactOne;
        this.contactTwo=contactTwo;
        this.lat=lat;
        this.lang=lang;
    }

    public String getContactOne() {
        return contactOne;
    }

    public void setContactOne(String contactOne) {
        this.contactOne = contactOne;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;
    String contactOne;

    public String getContactTwo() {
        return contactTwo;
    }

    public void setContactTwo(String contactTwo) {
        this.contactTwo = contactTwo;
    }

    String contactTwo;
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    String lat;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    String lang;
}
