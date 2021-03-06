package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.datingapp2021.logic.Classes.GeoPoint.GEO_POINT;
import static com.example.datingapp2021.logic.Classes.WholeUser.IMG_URL;

public class SmallUser {

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

    private int uid;
    private String username;
    private GeoPoint geoPoint;
    private String img_url;

    public SmallUser(String username, GeoPoint geoPoint, String img_url) {
        this.username = username;
        this.geoPoint = geoPoint;
        this.img_url = img_url;
    }

    public SmallUser(JsonObject jsonObject){
        this.uid = jsonObject.get(WholeUser.UID).getAsInt();
        this.username = jsonObject.get(WholeUser.USERNAME).getAsString();
        //Server returns sometimes lat,lng as objects and some other times as variables in User Object
        this.geoPoint= new GeoPoint(jsonObject.has(GEO_POINT) ? jsonObject.getAsJsonObject(GEO_POINT) : jsonObject);
        this.img_url = jsonObject.get(IMG_URL).isJsonNull() ? null : jsonObject.get(IMG_URL).getAsString();
    }

    public SmallUser(InputStream inputStream) throws IOException {
        String s = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(s).getAsJsonObject();
        SmallUser user = new SmallUser(object);
        this.uid = user.getUid();
        this.username = user.getUsername();
        this.geoPoint = user.getGeoPoint();
        this.img_url = user.getImg_url();
    }

    public static List<SmallUser> getUsers(InputStream inputStream){
        ArrayList<SmallUser> users = new ArrayList<>();

        String s = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(s).getAsJsonArray();
        for (JsonElement element : array) {
            users.add(new SmallUser(element.getAsJsonObject()));
        }
        return users;
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
}
