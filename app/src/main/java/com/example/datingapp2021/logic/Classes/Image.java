package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.util.ArrayList;

public class Image {
    public static final String UID = "uid";
    public static final String USER_UID = "userUid";
    public static final String IMG_URL = "imgUrl";
    private int uid, userUid;
    private String imgUrl;

    public Image(int uid, int userUid, String imgUrl) {
        this.uid = uid;
        this.userUid = userUid;
        this.imgUrl = imgUrl;
    }

    public Image(JsonObject jsonObject) {
        this.uid = jsonObject.get(UID).getAsInt();
        this.userUid = jsonObject.get(USER_UID).getAsInt();
        this.imgUrl = jsonObject.get(IMG_URL).getAsString();
    }

    public static ArrayList<Image> readImages(InputStream inputStream){
        String jsonArray = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(jsonArray).getAsJsonArray();
        ArrayList<Image> images = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            Image image = new Image(obj);
            images.add(image);
        }
        return images;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUserUid() {
        return userUid;
    }

    public void setUserUid(int userUid) {
        this.userUid = userUid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
