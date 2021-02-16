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

public class UserDistance {

    /*
    UserDistance class in Server contains:
                  1. user           User
                  2. distance       float
     */


    public static final String DISTANCE = "distance";
    private WholeUser wholeUser;
    private SmallUser smallUser;
    /**
     * Distance is calculated in DB and returned by Server.
     */
    private float distance;
    /**
     * A boolean to detect whether the UserDistance User is Whole or Small.
     */
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

    /**
     * Creates a UserDistance object from JsonObject.
     * @param jsonObject
     * UserDistance jsonObject.
     * @param isWhole
     * boolean to detect if the User is Whole or Small.
     */
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

    /**
     * Gets a UserDistance from InputStream by reading JsonObject string and creating a new UserDistance.
     * @param inputStream
     * InputStream object from the Socket
     * @param isWhole
     * boolean to detect if the User is Whole or Small.
     * @throws ParseException
     * throws IOParseException if parsing Json failed.
     */
    public UserDistance(InputStream inputStream, boolean isWhole) throws ParseException {
        String s = SocketServer.readStringFromInptStrm(inputStream);
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
    }

    /**
     * Read users from InputStream by reading String and parsing Json.
     * @param inputStream
     * InputStream object from socket.
     * @return
     * List of UserDistance.
     * @throws IOException
     * throws IOException if reading from InputStream fails.
     * @throws ParseException
     * throws ParseException if parsing Json fails.
     */
    public static List<UserDistance> readUsers(InputStream inputStream) throws ParseException {
        String s = null;
        try {
            s = SocketServer.readStringFromInptStrm(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Arrays.asList(getArrayOfUserDistancesFromJsonString(s));
    }

    /**
     * Gets an Array of UserDistance of SmallUser from JsonObject String by parsing Json.
     * @param jsonObject
     * JsonObject as String.
     * @return
     * UserDistance[]. Array od UserDistance of SmallUser.
     * @throws ParseException
     * throws ParseException if parsing Json fails.
     */
    public static UserDistance[] getArrayOfUserDistancesFromJsonString(String jsonObject) throws ParseException {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(jsonObject).getAsJsonArray();
        UserDistance[] users = new UserDistance[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            UserDistance userDistance = new UserDistance(obj, false);
            users[i] = userDistance;
        }
        return users;
    }

    /**
     * Convert distance to KiloMeters.
     * @return
     * double of KM.
     */
    public double convertDistanceToKM(){
        return distance * 0.62137119;
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
