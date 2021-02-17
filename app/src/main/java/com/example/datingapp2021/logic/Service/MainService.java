package com.example.datingapp2021.logic.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Result;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.security.auth.callback.Callback;

public class MainService extends Service {
    private final Handler handler = new Handler();
    private final MainBinder binder = new MainBinder();
    public final MutableLiveData<WholeCurrentUser> currentUser = new MutableLiveData<>();

    private Object result;

    public MainService() {
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
                    System.out.println("running service getCurrentUser()");
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
        Executors.newSingleThreadExecutor().execute(new Runnable() {
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

    public interface ServiceCallback<T>{
        T run();
        void onComplete(T result);
    }
}
