package com.example.datingapp2021.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.ui.Result;

import java.util.List;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<List<UserDistance>> newUsers;
    private MutableLiveData<List<UserDistance>> nearbyUsers;
    private MutableLiveData<Drawable> image;
    private final DashboardRepository repository;
    public boolean nearbyIsNull = true;
    public boolean newIsNull = true;

    public DashboardViewModel() {
        nearbyUsers = new MutableLiveData<>();
        newUsers = new MutableLiveData<>();
        image = new MutableLiveData<>();
        this.repository = new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler());
    }

    public DashboardViewModel(DashboardRepository repository) {
        nearbyUsers = new MutableLiveData<>();
        newUsers = new MutableLiveData<>();
        image = new MutableLiveData<>();
        this.repository = repository;
    }

    public void getNearbyUsers(int uid){
        repository.getNearbyUsers(uid, new Callback<List<UserDistance>>() {
            @Override
            public void onComplete(Result<List<UserDistance>> result) {
                if (result instanceof Result.Success) {
                    nearbyIsNull = false;
                    nearbyUsers.setValue(((Result.Success<List<UserDistance>>) result).data);
                }else if (result instanceof Result.SuccessNULL) {
                    nearbyIsNull = true;
                    nearbyUsers.setValue(null);
                    nearbyUsers.postValue(null);
                }else {
                    nearbyIsNull = true;
                    nearbyUsers.setValue(null);
                    nearbyUsers.postValue(null);
                    //TODO show error in UI
                }
            }
        });
    }

    public void getNewUsers(int uid) {
        repository.getNewUsers(uid, new Callback<List<UserDistance>>() {
            @Override
            public void onComplete(Result<List<UserDistance>> result) {
                if (result instanceof Result.Success) {
                    newIsNull = false;
                    newUsers.setValue(((Result.Success<List<UserDistance>>) result).data);
                }else if (result instanceof Result.SuccessNULL) {
                    newIsNull = true;
                    newUsers.setValue(null);
                    newUsers.postValue(null);
                }else {
                    newIsNull = true;
                    newUsers.setValue(null);
                    newUsers.postValue(null);
                    //TODO show error in UI
                }
            }
        });
    }

    public void getImageFromURL(String url){
        repository.getImageFromURL(url, new Callback<Drawable>() {
            @Override
            public void onComplete(Result<Drawable> result) {
                if (result instanceof Result.Success){
                    image.setValue(((Result.Success<Drawable>) result).data);
                } else {
                    image.setValue(null);
                    image.postValue(null);
                }
            }
        });
    }

    public LiveData<Drawable> getImageDrawableFromURL(String url){
        getImageFromURL(url);
        return image;
    }

    public LiveData<List<UserDistance>> getNewList(int uid) {
        getNewUsers(uid);
        return newUsers;
    }

    public LiveData<List<UserDistance>> getNearbyList(int uid) {
        getNearbyUsers(uid);
        return nearbyUsers;
    }
}

interface Callback<T> {
    void onComplete(Result<T> result);
}