package com.example.datingapp2021.logic.Classes;

import android.util.Log;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static com.example.datingapp2021.logic.Classes.WholeUser.IMG_URL;
import static com.example.datingapp2021.logic.DB.SocketServer.READY;

public class UserDistance {

    /*
    UserDistance class in Server contains:
                  1. user           User
                  2. distance       float
     */


    public static final String DISTANCE = "distance";
    private WholeUser wholeUser;
    private SmallUser smallUser;
    private float distance;
    private boolean isWhole;

    public UserDistance(WholeUser wholeUser, float distance) {
        this.wholeUser = wholeUser;
        this.smallUser = null;
        this.distance = distance;
        this.isWhole = true;
    }

    public UserDistance(SmallUser smallUser, float distance) {
        this.wholeUser = null;
        this.smallUser = smallUser;
        this.distance = distance;
        this.isWhole = false;
    }

    public UserDistance(JsonObject jsonObject, boolean isWhole) throws ParseException {
        if (isWhole){
            this.wholeUser = new WholeUser(jsonObject);
            this.smallUser = null;
        }else {
            this.wholeUser = null;
            this.smallUser = new SmallUser(jsonObject);
        }
        this.isWhole = isWhole;
        this.distance = jsonObject.get(DISTANCE).getAsFloat();
    }

    public UserDistance(InputStream inputStream, boolean isWhole){
        try{
            String s = SocketServer.readStringFromInputStream(inputStream);
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(s).getAsJsonObject();
            if (isWhole) {
                this.wholeUser = new WholeUser(object);
                this.smallUser = null;
            } else {
                this.wholeUser = null;
                this.smallUser = new SmallUser(object);
            }
            this.isWhole = isWhole;
            this.distance = object.get(DISTANCE).getAsFloat();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<UserDistance> readUsers(InputStream inputStream) throws ParseException {
        Log.d("system.err", "readUsers: ");
        String s = null;
        try {
            s = SocketServer.readStringFromInputStream(inputStream);
        } catch (Exception e) {
            Log.d("readusers", e.getClass().getName());
        }
        return Arrays.asList(getArrayOfUserDistancesFromJsonString(s));
    }

    public static UserDistance[] getArrayOfUserDistancesFromJsonString(String s) throws ParseException {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(s).getAsJsonArray();
        UserDistance[] users = new UserDistance[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            UserDistance userDistance = new UserDistance(obj, false);
            users[i] = userDistance;
        }
        return users;
    }

    public double getDistanceInKM(){
        return distance * 0.62137119;
    }

    /**
     * Get UserDistance object from Json String.
     * @param json
     * Json String.
     * @return
     * UserDistance Object.
     */
    public static UserDistance getUserDistanceFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        return gson.fromJson(json, UserDistance.class);
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
     * String: json object of Class UserDistance.
     */
    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        return builder.create().toJson(this);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public WholeUser getWholeUser() {
        return wholeUser;
    }

    public void setWholeUser(WholeUser wholeUser) {
        this.wholeUser = wholeUser;
    }

    public SmallUser getSmallUser() {
        return smallUser;
    }

    public void setSmallUser(SmallUser smallUser) {
        this.smallUser = smallUser;
    }

    public boolean isWhole() {
        return isWhole;
    }
}
