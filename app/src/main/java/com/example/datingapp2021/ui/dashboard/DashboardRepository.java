package com.example.datingapp2021.ui.dashboard;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Result;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;

public class DashboardRepository {
    private final Executor executor;
    private final Handler handler;

    public DashboardRepository(Executor executor, Handler handler){
        this.executor = executor;
        this.handler = handler;
    }

    private void notifyResult(final Result<List<UserDistance>> result, final Callback<List<UserDistance>> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    private void notifyImageResult(final Result<Drawable> result, final Callback<Drawable> callback){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }

    public void getFavouriteUsers(int uid, Callback<List<UserDistance>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyResult(makeSynchronousGetFavouriteUsers(uid), callback);
                }catch (Exception e){
                    Result<List<UserDistance>> errorResult = new Result.Error<>(e);
                    notifyResult(errorResult, callback);
                }
            }
        });
    }

    public void getNearbyUsers(int uid, Callback<List<UserDistance>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyResult(makeSynchronousGetNearbyUsers(uid), callback);
                }catch (Exception e){
                    Result<List<UserDistance>> errorResult = new Result.Error<>(e);
                    notifyResult(errorResult, callback);
                }
            }
        });
    }

    public void getNewUsers(int uid, Callback<List<UserDistance>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyResult(makeSynchronousGetNewUsers(uid), callback);
                }catch (Exception e){
                    Result<List<UserDistance>> errorResult = new Result.Error<>(e);
                    notifyResult(errorResult, callback);
                }
            }
        });
    }

    public void getImageFromURL(String urlS, Callback<Drawable> callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyImageResult(makeSynchronousGetImage(urlS), callback);
                } catch (IOException e) {
                    Result<Drawable> result = new Result.Error<>(e);
                    notifyImageResult(result, callback);
                }

            }
        });
    }

    public Result<List<UserDistance>> makeSynchronousGetFavouriteUsers(int uid) {
        List<UserDistance> result = SocketServer.getFavouriteUsers(uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<List<UserDistance>> makeSynchronousGetNearbyUsers(int uid) {
        List<UserDistance> result = SocketServer.getNearbyUsers(uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<List<UserDistance>> makeSynchronousGetNewUsers(int uid) {
        List<UserDistance> result = SocketServer.getNewUsers(uid);
        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }

    public Result<Drawable> makeSynchronousGetImage(String urlS) throws IOException {
        URL url = new URL(urlS);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        BitmapDrawable result = new BitmapDrawable(Resources.getSystem(), bmp);

        if (result == null) {
            return new Result.SuccessNULL<>("null object received");
        }else {
            return new Result.Success<>(result);
        }
    }
}
