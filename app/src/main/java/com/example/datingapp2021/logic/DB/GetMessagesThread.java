package com.example.datingapp2021.logic.DB;

import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.logic.Classes.WholeUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.example.datingapp2021.logic.DB.SocketServer.GET_MESSAGES;
import static com.example.datingapp2021.logic.DB.SocketServer.HOST;
import static com.example.datingapp2021.logic.DB.SocketServer.PORT;

public class GetMessagesThread extends Thread {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean go;
    private List<Message> messages;
    private NewMessageListener listener;
    private WholeUser wholeUser;

    public GetMessagesThread(List<Message> messages, NewMessageListener listener, WholeUser wholeUser) {
        this.messages = messages;
        go = true;
        this.listener = listener;
        this.wholeUser = wholeUser;
    }

    @Override
    public void run() {
        try{
            while (go) {
                socket = new Socket(HOST, PORT);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                //action:
                outputStream.write(GET_MESSAGES);

                //authenticate:
//                user.write(outputStream);

                //sending to the server from which message number we need to pull messages from
                byte[] fromBytes = new byte[4];
                ByteBuffer.wrap(fromBytes).putInt(messages.size());
                outputStream.write(fromBytes);

                Message message;
                while (true){
                    try {
                        message = new Message(inputStream);
                        messages.add(message);
                        if (listener != null) {
                            listener.onNewMessage(message);
                        }
                    }catch (Exception ex){
                        break;
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public interface NewMessageListener{
        void onNewMessage(Message message);
    }
}