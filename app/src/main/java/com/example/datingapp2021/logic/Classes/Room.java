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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {

    /*
    Room class in Server contains:
                  1. uid            int
                  2. seenBy         int[]
                  3. messages       int[]
                  4. recipients     int[]
                  5. lastMessage    Message
     */

    public static final String UID = "uid";
    public static final String SEEN_BY = "seenBy";
    public static final String MESSAGES = "messages";
    public static final String RECIPIENTS = "recipients";
    public static final String LAST_MESSAGE = "lastMessage";
    private int uid;
    private ArrayList<Integer> seenBy;
    private ArrayList<Integer> messages;
    private ArrayList<Integer> recipients;
    private Message lastMessage;

    public Room(int uid, ArrayList<Integer> seenBy, ArrayList<Integer> messages, ArrayList<Integer> recipients, Message lastMessage) {
        this.uid = uid;
        this.seenBy = seenBy;
        this.messages = messages;
        this.recipients = recipients;
        this.lastMessage = lastMessage;
    }

    /**
     * Creates a Room object from JsonObject by every parameter id.
     * @param jsonObject
     * JsonObject of Room.
     */
    public Room(JsonObject jsonObject){
        this.uid = jsonObject.get(UID).getAsInt();
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
            this.lastMessage = new Message(jsonObject.get(LAST_MESSAGE).getAsJsonObject());
        }
    }

    /**
     * Creates a Room object from JsonObject String by parsing String to Json.
     * @param jsonObject
     * JsonObject String.
     */
    public Room(String jsonObject) {
        JsonParser parser = new JsonParser();
        Room room = new Room(parser.parse(jsonObject).getAsJsonObject());
        this.uid = room.getUid();
        this.seenBy = room.getSeenBy();
        this.messages = room.getMessages();
        this.recipients = room.getRecipients();
        this.lastMessage = room.getLastMessage();
    }

    /**
     * Creates a Room object by reading InputStream Json String.
     * @param inputStream
     * InputStream from Socket.
     * @throws IOException
     * throws IOException is reading from InputStream fails.
     */
    public Room(InputStream inputStream) throws IOException {
        Room jsonRoom = new Room(SocketServer.readStringFromInptStrm(inputStream));
        this.uid = jsonRoom.getUid();
        this.seenBy = jsonRoom.getSeenBy();
        this.messages = jsonRoom.getMessages();
        this.recipients = jsonRoom.getRecipients();
        this.lastMessage = jsonRoom.getLastMessage();
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static List<Room> readRooms(InputStream inputStream) throws IOException{
        return Arrays.asList(getArrayOfRoomsFromJson(SocketServer.readStringFromInptStrm(inputStream)));
    }

    /**
     * Gets Room[] from JsonArray String.
     * @param json
     * JsonArray as String that contains room objects.
     * @return
     * Array of Rooms (Room[]).
     */
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
