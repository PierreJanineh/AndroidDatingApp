package com.example.datingapp2021.logic.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.GetMessagesThread;
import com.example.datingapp2021.logic.DB.SocketServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainService extends Service {
    private final Handler handler = new Handler();
    private final MainBinder binder = new MainBinder();
    public final MutableLiveData<WholeCurrentUser> currentUser = new MutableLiveData<>();
    public final MutableLiveData<Integer> roomUID = new MutableLiveData<>();
    public final MutableLiveData<SmallUser> user = new MutableLiveData<>();
    public List<Message> roomMessages;

    private GetMessagesThread getMessagesThread;

    private Object result;

    public MainService() {
    }

    public void sendMessage(Room room){
        runOnBackground(new ServiceCallback() {
            @Override
            public Object run() {
                return SocketServer.sendMessage(room.toString());
            }

            @Override
            public void onComplete(Object result) {
                roomUID.setValue((int) result);
            }
        });
    }

    /**
     * Gets Room from DB.
     * @param roomUid
     * uid of Room to get Messages from.
     */
    public void startGettingRoomMessages(int roomUid, GetMessagesThread.MessagesListener listener){
        runOnBackground(new ServiceCallback() {
            @Override
            public Object run() {
                getMessagesThread = new GetMessagesThread(roomUid , new GetMessagesThread.MessageListener() {
                    @Override
                    public void onNewMessage(Message message) {
                        if (roomMessages == null) {
                            roomMessages = new ArrayList<>();
                        }
                        roomMessages.add(message);
                        listener.onNewMessage(roomMessages);
                    }
                });
                getMessagesThread.start();
                return null;
            }

            @Override
            public void onComplete(Object result) {

            }
        });
    }

    /**
     * Gets SmallUser from DB.
     * @param uid
     * uid of User
     */
    public void getUser(int uid){
        if (user.getValue() == null){
            runOnBackground(new ServiceCallback() {
                @Override
                public Object run() {
                    return SocketServer.getSmallUser(uid);
                }

                @Override
                public void onComplete(Object result) {
                    user.setValue((SmallUser) result);
                }
            });
        }
    }

    /**
     * Gets WholeCurrentUser from DB if currentUser variable in MainService is null.
     * @param uid
     * uid of User
     */
    public void getCurrentUser(int uid){
        if (currentUser.getValue() == null){
            runOnBackground(new ServiceCallback() {
                @Override
                public Object run() {
                    return SocketServer.getCurrentUser(uid);
                }

                @Override
                public void onComplete(Object result) {
                    currentUser.setValue((WholeCurrentUser) result);
                }
            });
        }
    }

    /**
     * Forces get WholeCurrentUser from DB despite null status of variable currentUser in MainService.
     * @param uid
     * uid of User
     */
    public void forceGetCurrentUser(int uid){
        runOnBackground(new ServiceCallback() {
            @Override
            public Object run() {
                return SocketServer.getCurrentUser(uid);
            }

            @Override
            public void onComplete(Object result) {
                currentUser.setValue((WholeCurrentUser) result);
            }
        });
    }

    private void notifyResult(final Object result, ServiceCallback callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    private void runOnBackground(ServiceCallback callback){
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                result = callback.run();
                notifyResult(result, callback);
            }
        });
    }

    public class MainBinder extends Binder {
        public MainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MainService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Service BOUND");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (getMessagesThread != null){
            getMessagesThread.stopGettingMessages();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("Service DONE");
        super.onDestroy();
    }

    public interface ServiceCallback<T>{
        T run();
        void onComplete(T result);
    }
}
