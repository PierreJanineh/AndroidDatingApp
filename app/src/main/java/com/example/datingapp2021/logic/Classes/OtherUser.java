package com.example.datingapp2021.logic.Classes;

import java.io.Serializable;

public class OtherUser implements Serializable {

    private WholeUser wholeUser, otherWholeUser;
    private Double distance;

    public OtherUser(WholeUser wholeUser, WholeUser otherWholeUser) {
        this.wholeUser = wholeUser;
        this.otherWholeUser = otherWholeUser;
        getDistance();
    }

    public OtherUser(WholeUser wholeUser, WholeUser otherWholeUser, double distance) {
        this.wholeUser = wholeUser;
        this.otherWholeUser = otherWholeUser;
        this.distance = distance;
    }

    public WholeUser getUser() {
        return wholeUser;
    }

    public void setUser(WholeUser wholeUser) {
        this.wholeUser = wholeUser;
    }

    public WholeUser getOtherUser() {
        return otherWholeUser;
    }

    public void setOtherUser(WholeUser otherWholeUser) {
        this.otherWholeUser = otherWholeUser;
    }

    public double getDistance() {
        return getDistance(wholeUser.getGeoPoint(), otherWholeUser.getGeoPoint());
    }

    private double getDistance(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        if (distance != null ){
            return distance;
        }
        int r = 6371; // Radius of the earth in km
        double dLat = deg2rad(geoPoint2.getLat() - geoPoint1.getLat());
        double dLon = deg2rad(geoPoint2.getLng() - geoPoint1.getLng());
        double a = Math.sin(dLat/2) *
                Math.sin(dLat/2) +
                Math.cos(deg2rad(geoPoint1.getLat())) *
                        Math.cos(deg2rad(geoPoint2.getLat())) *
                        Math.sin(dLon/2) *
                        Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        setDistance(r * c);
        return distance;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
