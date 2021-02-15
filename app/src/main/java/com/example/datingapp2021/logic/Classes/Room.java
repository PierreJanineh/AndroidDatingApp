package com.example.datingapp2021.logic.Classes;

import android.util.Log;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {

    public static final String ROOM_UID = "roomUid";
    public static final String SEEN_BY = "seenBy";
    public static final String MESSAGES = "messages";
    public static final String RECIPIENTS = "recipients";
    private int roomUid;
    private ArrayList<Integer> seenBy;
    private ArrayList<Integer> messages;
    private ArrayList<Integer> recipients;
    private Message lastMessage;

    public Room(int roomUid, ArrayList<Integer> seenBy, ArrayList<Integer> messages, ArrayList<Integer> recipients, Message lastMessage) {
        this.roomUid = roomUid;
        this.seenBy = seenBy;
        this.messages = messages;
        this.recipients = recipients;
        this.lastMessage = lastMessage;
    }

    public Room(JsonObject jsonObject){
        this.roomUid = jsonObject.get(ROOM_UID).getAsInt();
        ArrayList<Integer> arr = new ArrayList<>();
        JsonArray jArr;
        if (jsonObject.has(SEEN_BY) && !jsonObject.getAsJsonArray(SEEN_BY).isJsonNull()) {
            jArr = jsonObject.get(SEEN_BY).getAsJsonArray();
            for (JsonElement element : jArr) {
                arr.add(element.getAsInt());
            }
            this.seenBy = arr;
        }else {
            this.seenBy = new ArrayList<>();
        }
        if (jsonObject.has(MESSAGES) && !jsonObject.getAsJsonArray(MESSAGES).isJsonNull()) {
            jArr = jsonObject.get(MESSAGES).getAsJsonArray();
            for (JsonElement element : jArr) {
                arr.add(element.getAsInt());
            }
            this.messages = arr;
        }else {
            this.messages = new ArrayList<>();
        }
        if (jsonObject.has(RECIPIENTS) && !jsonObject.getAsJsonArray(RECIPIENTS).isJsonNull()) {
            jArr = jsonObject.get(RECIPIENTS).getAsJsonArray();
            for (JsonElement element : jArr) {
                arr.add(element.getAsInt());
            }
            this.recipients = arr;
        }else {
            this.recipients = new ArrayList<>();
        }
        if (messages != null && !messages.isEmpty()) {
            this.lastMessage = SocketServer.getMessage(messages.get(messages.size()-1));
        }
    }

    public Room(InputStream inputStream) throws IOException {
        int jsonLength = inputStream.read();
        if (jsonLength == -1)
            throw new IOException("json hasn't been sent");
        byte[] jsonBytes = new byte[jsonLength];
        int actuallyRead = inputStream.read(jsonBytes);
        if (actuallyRead != jsonLength)
            throw new IOException("");
        Room jsonRoom = getRoomFromJson(new String(jsonBytes));
        this.roomUid = jsonRoom.getRoomUid();
        this.seenBy = jsonRoom.getSeenBy();
        this.messages = jsonRoom.getMessages();
        this.recipients = jsonRoom.getRecipients();
        this.lastMessage = jsonRoom.getLastMessage();
    }

    public static List<Room> readRooms(InputStream inputStream) throws IOException{
        String s = null;
        try {
            s = SocketServer.readStringFromInputStream(inputStream);
        } catch (Exception e) {
            Log.d("readusers", e.getClass().getName());
        }

//        int jsonLength = inputStream.read();
//        if (jsonLength == -1)
//            throw new IOException("json hasn't been sent");
//        byte[] jsonBytes = new byte[jsonLength];
//        int actuallyRead = inputStream.read(jsonBytes);
//        if (actuallyRead != jsonLength)
//            throw new IOException("");
        return Arrays.asList(getArrayOfRoomsFromJson(s));
    }

    public static Room[] getArrayOfRoomsFromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(json).getAsJsonArray();
        Room[] rooms = new Room[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            Room room = new Room(obj);
            rooms[i] = room;
        }
        return rooms;
    }

    /**
     * Get Room object from Json String.
     * @param json
     * Json String.
     * @return
     * Room Object.
     */
    public static Room getRoomFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        return gson.fromJson(json, Room.class);
    }

    /**
     * Get an Array of booleans to detect which last message of which room is last sent from the user or to them.
     * @param rooms
     * Array of Rooms to search.
     * @param wholeUser
     * Current User.
     * @return
     * Array of booleans.
     */
    public boolean[] isLastMessageToMe(Room[] rooms, WholeUser wholeUser) {
        boolean[] messages = new boolean[rooms.length-1];
        for (int i = 0; i <= rooms.length; i++) {
            messages[i] = rooms[i].getLastMessage().isItToMe(wholeUser.getUid());
        }
        return messages;
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
     * String: json object of Class Room.
     */
    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        return builder.create().toJson(this);
    }

    public int getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(int roomUid) {
        this.roomUid = roomUid;
    }

    public ArrayList<Integer> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(ArrayList<Integer> seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<Integer> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Integer> messages) {
        this.messages = messages;
    }

    public ArrayList<Integer> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<Integer> recipients) {
        this.recipients = recipients;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
