package com.example.ict652_sulam;

public class Request {
    private String numofpeople, uid, description, reqid, address, longitude, lattitude, time;

    public Request(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Request() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public Request(String reqid, String uid, String numofpeople, String description, String address, String lattitude, String longitude, String time) {
        this.numofpeople = numofpeople;
        this.uid = uid;
        this.description = description;
        this.reqid = reqid;
        this.address = address;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.time = time;
    }

    public String getNumofpeople() {
        return numofpeople;
    }

    public void setNumofpeople(String numofpeople) {
        this.numofpeople = numofpeople;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }
}
