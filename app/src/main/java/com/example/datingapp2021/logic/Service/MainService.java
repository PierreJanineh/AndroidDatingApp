package com.example.datingapp2021.logic.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.SocketServer;

public class MainService extends Service {
    private MainBinder binder;
    private boolean allowRebind;
    private WholeCurrentUser currentUser;

    /**
     * Gets WholeCurrentUser from DB if currentUser variable in MainService is null.
     * @param uid
     * uid of User
     * @return
     * WholeCurrentUser object.
     */
    public WholeCurrentUser getCurrentUser(int uid){
        if (currentUser == null){
            currentUser = SocketServer.getCurrentUser(uid);
        }
        return currentUser;
    }

    /**
     * Forces get WholeCurrentUser from DB despite null status of variable currentUser in MainService.
     * @param uid
     * uid of User
     * @return
     * WholeCurrentUser object.
     */
    public WholeCurrentUser forceGetCurrentUser(int uid){
        currentUser = SocketServer.getCurrentUser(uid);
        return currentUser;
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
        binder = new MainBinder();
        return binder;
    }

//    @Override
//    public boolean onUnbind(Intent intent) {
//        System.out.println("Service UNBOUND");
//        return allowRebind;
//    }

    @Override
    public void onDestroy() {
        System.out.println("Service DONE");
        super.onDestroy();
    }
}
