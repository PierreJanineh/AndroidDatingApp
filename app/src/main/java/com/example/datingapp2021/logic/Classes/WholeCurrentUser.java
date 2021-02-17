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

public class WholeCurrentUser {

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
    public static final String FAVS = "favs";
    public static final String CHAT_ROOMS = "chatRooms";
    public static final String INFO = "info";
    public static final String USER = "user";

    private int uid;
    private String username;
    private GeoPoint geoPoint;
    private String img_url;
    private ArrayList<Integer> favs;
    private ArrayList<Room> chatRooms;
    private UserInfo info;

    public WholeCurrentUser(int uid, String username, GeoPoint geoPoint, String img_url, ArrayList<Integer> favs, ArrayList<Room> chatRooms, UserInfo info) {
        this.uid = uid;
        this.username = username;
        this.geoPoint = geoPoint;
        this.img_url = img_url;
        this.favs = favs;
        this.chatRooms = chatRooms;
        this.info = info;
    }

    /**
     * Creates a WholeUser object from JsonObject.
     * @param jsonObject
     * WholeUser jsonObject.
     */
    public WholeCurrentUser(JsonObject jsonObject) throws ParseException {
        JsonArray jsonArray;

        JsonObject object = jsonObject.getAsJsonObject(USER);
        this.uid = object.get(WholeCurrentUser.UID).getAsInt();
        this.username = object.get(WholeCurrentUser.USERNAME).getAsString();
        this.geoPoint = new GeoPoint(object);
        this.img_url = object.get(IMG_URL).isJsonNull() ? null : object.get(IMG_URL).getAsString();
        ArrayList<Integer> favsArr = new ArrayList<>();
        if (object.get(FAVS).isJsonNull()){
            jsonArray = object.get(FAVS).getAsJsonArray();
            for (JsonElement element : jsonArray)
                favsArr.add(element.getAsInt());
        }

        this.favs = favsArr;
        ArrayList<Room> rooms = null;
        if (!object.get(CHAT_ROOMS).isJsonNull()){
            jsonArray = object.get(CHAT_ROOMS).getAsJsonArray();
            rooms = new ArrayList<>();
            for (JsonElement element : jsonArray)
                rooms.add(new Room(element.getAsJsonObject()));
        }
        this.chatRooms = rooms;
        this.info = new UserInfo(object);
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
    public WholeCurrentUser(InputStream inputStream) throws IOException, ParseException {
        JsonArray jsonArray;
        String s = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(s).getAsJsonObject();
        this.uid = object.get(WholeCurrentUser.UID).getAsInt();
        this.username = object.get(WholeCurrentUser.USERNAME).getAsString();
        this.geoPoint = new GeoPoint(object.getAsJsonObject(GEO_POINT));
        this.img_url = object.get(IMG_URL).isJsonNull() ? null : object.get(IMG_URL).getAsString();
        ArrayList<Integer> favsArr = new ArrayList<>();
        if (object.has(FAVS)){
            jsonArray = object.get(FAVS).getAsJsonArray();
            for (JsonElement element : jsonArray)
                favsArr.add(element.getAsInt());
        }

        this.favs = favsArr;
        ArrayList<Room> rooms = null;
        System.out.println("Object = "+object);
        if (object.has(CHAT_ROOMS)){
            jsonArray = object.get(CHAT_ROOMS).getAsJsonArray();
            rooms = new ArrayList<>();
            for (JsonElement element : jsonArray)
                rooms.add(new Room(element.getAsJsonObject()));
        }
        this.chatRooms = rooms;
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
    public static List<SmallUser> getUsersFromRooms(List<Room> roomsList, WholeCurrentUser currentWholeUser){

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

    public ArrayList<Integer> getFavs() {
        return favs;
    }

    public void setFavs(ArrayList<Integer> favs) {
        this.favs = favs;
    }

    public ArrayList<Room> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(ArrayList<Room> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }
}
