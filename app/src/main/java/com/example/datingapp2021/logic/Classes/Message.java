package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

public class Message {

    public static final String UID = "uid";
    public static final String CONTENT = "content";
    public static final String TO = "to";
    public static final String FORM = "form";
    public static final String TIMESTAMP = "timestamp";
    private int uid;
    private SmallUser to;
    private SmallUser from;
    private String content;
    private Timestamp timestamp;
    private boolean isItFromMe;

    public Message(int uid, String content, Timestamp timestamp, SmallUser to, SmallUser from) {
        this.uid = uid;
        this.content = content;
        this.timestamp = timestamp;
        this.to = to;
        this.from = from;
    }

    public Message(JsonObject jsonObject){
        this.uid = jsonObject.get(UID).getAsInt();
        this.content = jsonObject.get(CONTENT).getAsString();
        this.to = SocketServer.getSmallUser(jsonObject.get(TO).getAsInt());
        this.from = SocketServer.getSmallUser(jsonObject.get(FORM).getAsInt());
        this.timestamp = Timestamp.valueOf(jsonObject.get(TIMESTAMP).getAsString());
    }

    public Message(InputStream inputStream) throws IOException{
        int jsonLength = inputStream.read();
        if (jsonLength == -1)
            throw new IOException("json hasn't been sent");
        byte[] jsonBytes = new byte[jsonLength];
        int actuallyRead = inputStream.read(jsonBytes);
        if (actuallyRead != jsonLength)
            throw new IOException("");
        Message jsonMessage = getMessageFromJson(new String(jsonBytes));
        this.uid = jsonMessage.getUid();
        this.content = jsonMessage.getContent();
        this.timestamp = jsonMessage.getTimestamp();
        this.to = jsonMessage.getTo();
        this.from = jsonMessage.getFrom();
    }

    /**
     * Get Message object from Json String.
     * @param json
     * Json String.
     * @return
     * Message Object.
     */
    public static Message getMessageFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        return gson.fromJson(json, Message.class);
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
     * String: json object of Class Message.
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SmallUser getTo() {
        return to;
    }

    public void setTo(SmallUser to) {
        this.to = to;
    }

    public SmallUser getFrom() {
        return from;
    }

    public void setFrom(SmallUser from) {
        this.from = from;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isItToMe(int meUserUID) {
        return setIsItToMe(meUserUID);
    }

    private boolean setIsItToMe(int uid) {
        if (to.getUid() == uid){
            isItFromMe = true;
            return true;
        }else{
            isItFromMe = false;
            return false;
        }
    }
}
