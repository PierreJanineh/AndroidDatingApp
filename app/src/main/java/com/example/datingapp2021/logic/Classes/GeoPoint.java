package com.example.datingapp2021.logic.Classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GeoPoint {


    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String GEO_POINT = "geoPoint";

    private float lat, lng;

    public GeoPoint(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public GeoPoint(JsonObject jsonObject){
        this.lat = jsonObject.get(LAT).getAsFloat();
        this.lng = jsonObject.get(LNG).getAsFloat();
    }

    public GeoPoint(InputStream inputStream) throws IOException{
        int jsonLength = inputStream.read();
        if (jsonLength == -1)
            throw new IOException("json hasn't been sent");
        byte[] jsonBytes = new byte[jsonLength];
        int actuallyRead = inputStream.read(jsonBytes);
        if (actuallyRead != jsonLength)
            throw new IOException("");
        GeoPoint jsonGeoPoint = getGeoPointFromJson(new String(jsonBytes));
        this.lat = jsonGeoPoint.getLat();
        this.lng = jsonGeoPoint.getLng();
    }

    /**
     * Get GeoPoint object from Json String.
     * @param json
     * Json String.
     * @return
     * GeoPoint Object.
     */
    public static GeoPoint getGeoPointFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        return gson.fromJson(json, GeoPoint.class);
    }

    /**
     * Write Json object to OutputStream.
     * @param outputStream
     * OutputStream to write to.
     */
    public void write(OutputStream outputStream) throws IOException {

        byte[] bytes = this.toString().getBytes();
        outputStream.write(bytes.length);
        outputStream.write(bytes);
    }

    /**
     * Override toString function to create a json object.
     * @return
     * String: json object of Class User.
     */
    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        return builder.create().toJson(this);
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }


}
