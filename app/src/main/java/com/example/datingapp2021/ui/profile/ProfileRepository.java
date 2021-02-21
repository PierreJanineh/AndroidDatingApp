package com.example.datingapp2021.ui.profile;

import android.content.SharedPreferences;
import android.os.Handler;

import com.example.datingapp2021.logic.Classes.Image;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.Classes.UserInfo;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Result;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class ProfileRepository {
    private final Executor executor;
    private final Handler handler;

    public ProfileRepository(Executor executor, Handler handler){
        this.executor = executor;
        this.handler = handler;
    }

    private void notifyImagesResult(final Result<ArrayList<Image>> result, final Callback<ArrayList<Image>> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    private void notifyCurrentUserResult(final Result<WholeCurrentUser> result, final Callback<WholeCurrentUser> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
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

    public void getImages(int uid, Callback<ArrayList<Image>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyImagesResult(makeSynchronousGetImages(uid), callback);
                }catch (Exception e){
                    Result<ArrayList<Image>> errorResult = new Result.Error<>(e);
                    notifyImagesResult(errorResult, callback);
                }
            }
        });
    }

    public void editCurrentUser(SharedPreferences sharedPreferences, UserInfo userInfo, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyBoolResult(makeSynchronousEditCurrentUser(sharedPreferences, userInfo), callback);
                }catch (Exception e){
                    Result<Boolean> errorResult = new Result.Error<>(e);
                    notifyBoolResult(errorResult, callback);
                }
            }
        });
    }

    public void getCurrentUser(SharedPreferences sharedPreferences, Callback<WholeCurrentUser> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyCurrentUserResult(makeSynchronousGetCurrentUser(sharedPreferences), callback);
                }catch (Exception e){
                    Result<WholeCurrentUser> errorResult = new Result.Error<>(e);
                    notifyCurrentUserResult(errorResult, callback);
                }
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

    public void removeFromFavourites(SharedPreferences sharedPreferences, int uid, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    notifyBoolResult(makeSynchronousRemoveFromFavourites(sharedPreferences, uid), callback);
                } catch (Exception e){
                    Result<Boolean> errorResult = new Result.Error<>(e);
                    notifyBoolResult(errorResult, callback);
                }
            }
        });
    }

    public Result<ArrayList<Image>> makeSynchronousGetImages(int uid) {
        ArrayList<Image> result = SocketServer.getImages(uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<Boolean> makeSynchronousEditCurrentUser(SharedPreferences sharedPreferences, UserInfo userInfo) {
        Boolean result = SocketServer.updateUserInfo(SocketServer.getCurrentUserUID(sharedPreferences), userInfo);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<WholeCurrentUser> makeSynchronousGetCurrentUser(SharedPreferences sharedPreferences) {
        WholeCurrentUser result = SocketServer.getCurrentUser(SocketServer.getCurrentUserUID(sharedPreferences));
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<UserDistance> makeSynchronousGetUserInfo(SharedPreferences sharedPreferences, int uid) {
        UserDistance result = SocketServer.getWholeUserDistance(SocketServer.getCurrentUserUID(sharedPreferences), uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<Boolean> makeSynchronousAddToFavourites(SharedPreferences sharedPreferences, int uid) {
        return new Result.Success<>(SocketServer.addFavouriteUser(SocketServer.getCurrentUserUID(sharedPreferences), uid));
    }

    public Result<Boolean> makeSynchronousRemoveFromFavourites(SharedPreferences sharedPreferences, int uid) {
        return new Result.Success<>(SocketServer.removeFavouriteUser(SocketServer.getCurrentUserUID(sharedPreferences), uid));
    }
}