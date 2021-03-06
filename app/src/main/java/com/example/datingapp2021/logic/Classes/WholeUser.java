package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.datingapp2021.logic.Classes.GeoPoint.GEO_POINT;

public class WholeUser {

    /*
    User class in Server contains:
                  1. uid            int
                  2. username       String
                  3. geoPoint       GeoPoint
                  4. img_url        String
                  5. favs           ArrayList<Integer>
                  6. chatrooms      ArrayList<Room>
                  7. info           UserInfo
     */


    public static final int ADD = 1;
    public static final int REMOVE = 2;
    public static final String UID = "uid";
    public static final String USERNAME = "username";
    public static final String IMG_URL = "img_url";
    public static final String INFO = "info";
    public static final String FAVS = "favs";
    public static final String USER = "user";

    private int uid;
    private String username;
    private GeoPoint geoPoint;
    private String img_url;
    private UserInfo info;

    public WholeUser(int uid, String username, GeoPoint geoPoint, String img_url, UserInfo info) {
        this.uid = uid;
        this.username = username;
        this.geoPoint = geoPoint;
        this.img_url = img_url;
        this.info = info;
    }

    /**
     * Creates a WholeUser object from JsonObject.
     * @param jsonObject
     * WholeUser jsonObject.
     */
    public WholeUser(JsonObject jsonObject) throws ParseException {
        JsonObject userObject = jsonObject.getAsJsonObject(USER);
        this.uid = userObject.get(WholeUser.UID).getAsInt();
        this.username = userObject.get(WholeUser.USERNAME).getAsString();
        JsonObject geoPointObject = userObject.getAsJsonObject(GEO_POINT);
        this.geoPoint = new GeoPoint(geoPointObject);
        this.img_url = userObject.get(IMG_URL).isJsonNull() ? null : userObject.get(IMG_URL).getAsString();
        this.info = new UserInfo(userObject);
    }

    /**
     * Gets a user from InputStream by reading string and creating a new User
     * @param inputStream
     * InputStream object from the Socket
     * @throws IOException
     * throws IOException if reading from InputStream fails.
     * @throws ParseException
     * throws IOParseException if parsing Json failed.
     */
    public WholeUser(InputStream inputStream) throws IOException, ParseException {
        String s = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(s).getAsJsonObject();
        this.uid = object.get(WholeUser.UID).getAsInt();
        this.username = object.get(WholeUser.USERNAME).getAsString();
        this.geoPoint = new GeoPoint(object);
        this.img_url = object.get(IMG_URL).isJsonNull() ? null : object.get(IMG_URL).getAsString();
        this.info = new UserInfo(object);
    }

    /**
     * Get other Users From list of rooms.
     * @param roomsList
     * List of Rooms.
     * @param currentWholeUser
     * The User currently asking for other Users in Rooms.
     * @return
     * List of Users.
     */
    public static List<SmallUser> getUsersFromRooms(List<Room> roomsList, WholeUser currentWholeUser){

        List<SmallUser> wholeUsers = new ArrayList<>();

        for (int i = 0; i <= roomsList.size(); i ++) {
            SmallUser from = roomsList.get(i).getLastMessage().getFrom(),
                    to = roomsList.get(i).getLastMessage().getTo();

            if (roomsList.get(i).getLastMessage().getFrom().getUid() == currentWholeUser.getUid() /*todo*/) {
                wholeUsers.add(to);
            }else {
                wholeUsers.add(from);
            }
        }
        return wholeUsers;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }
}
