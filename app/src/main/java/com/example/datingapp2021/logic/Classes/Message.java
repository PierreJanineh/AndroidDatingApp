package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    /*
    Message class in Server contains:
                1. uid          int
                2. to           User
                3. from         User
                4. content      String
                5. timestamp    java.util.Date.Timestamp
                6. isItMe       boolean
     */

    public static final String UID = "uid";
    public static final String CONTENT = "content";
    public static final String TO = "to";
    public static final String FROM = "from";
    public static final String TIMESTAMP = "timestamp";
    private int uid;
    private SmallUser to;
    private SmallUser from;
    private String content;
    private Timestamp timestamp;
    private boolean isItFromMe;

    public Message(String content, Timestamp timestamp, SmallUser to, SmallUser from) {
        this.content = content;
        this.timestamp = timestamp;
        this.to = to;
        this.from = from;
    }

    public Message(String jsonObject){
        JsonParser parser = new JsonParser();
        Message message = new Message(parser.parse(jsonObject).getAsJsonObject());
        this.uid = message.getUid();
        this.content = message.getContent();
        this.to = message.getTo();
        this.from = message.getFrom();
        this.timestamp = message.getTimestamp();
    }

    public Message(JsonObject jsonObject){
        this.uid = jsonObject.get(UID).getAsInt();
        this.content = jsonObject.get(CONTENT).getAsString();
        this.to = new SmallUser(jsonObject.get(TO).getAsJsonObject());
        this.from = new SmallUser(jsonObject.get(FROM).getAsJsonObject());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy, h:mm:ss aa");
        try {
            this.timestamp = new Timestamp(simpleDateFormat.parse(jsonObject.get(TIMESTAMP).getAsString()).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.timestamp = Timestamp.valueOf(jsonObject.get(TIMESTAMP).getAsString());
    }

    public Message(InputStream inputStream) throws IOException{
        String json = SocketServer.readStringFromInptStrm(inputStream);
        Message jsonMessage = new Message(json);
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
