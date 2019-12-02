package com.techespo.tsafe.Model;

import java.io.Serializable;

public class Trip implements Serializable {
    String userid;
    String startaddress;
    String endaddress;
    String startlat;
    String startlong;
    String endlat;
    String endlong;
    String contactone;
    String contacttwo;
    int tripstatus;
    String tripstarttime;
    String vehicleno;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    String tripId;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public void setStartaddress(String startaddress) {
        this.startaddress = startaddress;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public void setEndaddress(String endaddress) {
        this.endaddress = endaddress;
    }

    public String getStartlat() {
        return startlat;
    }

    public void setStartlat(String startlat) {
        this.startlat = startlat;
    }

    public String getStartlong() {
        return startlong;
    }

    public void setStartlong(String startlong) {
        this.startlong = startlong;
    }

    public String getEndlat() {
        return endlat;
    }

    public void setEndlat(String endlat) {
        this.endlat = endlat;
    }

    public String getEndlong() {
        return endlong;
    }

    public void setEndlong(String endlong) {
        this.endlong = endlong;
    }

    public String getContactone() {
        return contactone;
    }

    public void setContactone(String contactone) {
        this.contactone = contactone;
    }

    public String getContacttwo() {
        return contacttwo;
    }

    public void setContacttwo(String contacttwo) {
        this.contacttwo = contacttwo;
    }

    public int getTripstatus() {
        return tripstatus;
    }

    public void setTripstatus(int tripstatus) {
        this.tripstatus = tripstatus;
    }

    public String getTripstarttime() {
        return tripstarttime;
    }

    public void setTripstarttime(String tripstarttime) {
        this.tripstarttime = tripstarttime;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    String vehicletype;
}
