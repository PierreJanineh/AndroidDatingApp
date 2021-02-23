package com.example.datingapp2021.logic.DB;

import androidx.lifecycle.MutableLiveData;

import com.example.datingapp2021.logic.Classes.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.example.datingapp2021.logic.DB.SocketServer.GET_MESSAGES;
import static com.example.datingapp2021.logic.DB.SocketServer.HOST;
import static com.example.datingapp2021.logic.DB.SocketServer.PORT;

public class GetMessagesThread extends Thread {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private int roomUid;
    private boolean go;
    private List<Message> messages = new ArrayList<>();
    private MessageListener listener;

    public GetMessagesThread(int roomUid, MessageListener listener) {
        this.roomUid = roomUid;
        this.go = true;
        this.listener = listener;
    }

    @Override
    public synchronized void start() {
        super.start();
        try{
            while (go) {
                socket = new Socket(HOST, PORT);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                //action:
                outputStream.write(GET_MESSAGES);
                outputStream.write(roomUid);

                //authenticate:
//                user.write(outputStream);

                //sending to the server from which message number we need to pull messages from
                if (messages == null) {
                    outputStream.write(0);
                }else{
                    outputStream.write(messages.size());
                }

                Message message;
                while (true){
                    try {
                        message = new Message(inputStream);
                        messages.add(message);
                        if (listener != null) {
                            listener.onNewMessage(message);
                        }
                    } catch (Exception e){
                        break;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopGettingMessages(){
        go = false;
        interrupt();
    }

    public interface MessageListener {
        void onNewMessage(Message message);
    }

    public interface MessagesListener {
        void onNewMessage(List<Message> messages);
    }
}