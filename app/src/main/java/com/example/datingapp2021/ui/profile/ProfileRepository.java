package com.example.datingapp2021.ui.profile;

import android.content.SharedPreferences;
import android.os.Handler;
import android.telecom.Call;

import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Result;

import java.util.List;
import java.util.concurrent.Executor;

public class ProfileRepository {
    private final Executor executor;
    private final Handler handler;

    public ProfileRepository(Executor executor, Handler handler){
        this.executor = executor;
        this.handler = handler;
    }

    private void notifyResult(final Result<UserDistance> result, final Callback<UserDistance> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    private void notifyBoolResult(final Result<Boolean> result, final Callback<Boolean> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    public void getUserInfo(SharedPreferences sharedPreferences, int uid, Callback<UserDistance> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyResult(makeSynchronousGetUserInfo(sharedPreferences, uid), callback);
                }catch (Exception e){
                    Result<UserDistance> errorResult = new Result.Error<>(e);
                    notifyResult(errorResult, callback);
                }
            }
        });
    }

    public void addToFavourites(SharedPreferences sharedPreferences, int uid, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    notifyBoolResult(makeSynchronousAddToFavourites(sharedPreferences, uid), callback);
                } catch (Exception e){
                    Result<Boolean> errorResult = new Result.Error<>(e);
                    notifyBoolResult(errorResult, callback);
                }
            }
        });
    }

    public Result<UserDistance> makeSynchronousGetUserInfo(SharedPreferences sharedPreferences, int uid) {
        UserDistance result = SocketServer.getWholeUserDistance(SocketServer.getCurrentUser(sharedPreferences).getUid(), uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<Boolean> makeSynchronousAddToFavourites(SharedPreferences sharedPreferences, int uid) {
        return new Result.Success<>(SocketServer.addFavouriteUser(SocketServer.getCurrentUser(sharedPreferences).getUid(), uid));
    }
}