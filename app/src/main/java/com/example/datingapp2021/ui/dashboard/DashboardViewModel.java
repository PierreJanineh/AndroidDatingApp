package com.example.datingapp2021.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.ui.Result;

import java.util.List;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<List<UserDistance>> favouriteUsers;
    private MutableLiveData<List<UserDistance>> newUsers;
    private MutableLiveData<List<UserDistance>> nearbyUsers;
    private MutableLiveData<List<SmallUser>> roomsUsers;
    private MutableLiveData<Drawable> image;
    private final DashboardRepository repository;
    public boolean nearbyIsNull = true;
    public boolean newIsNull = true;

    public DashboardViewModel() {
        favouriteUsers = new MutableLiveData<>();
        nearbyUsers = new MutableLiveData<>();
        newUsers = new MutableLiveData<>();
        roomsUsers = new MutableLiveData<>();
        image = new MutableLiveData<>();
        this.repository = new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler());
    }

    public DashboardViewModel(DashboardRepository repository) {
        favouriteUsers = new MutableLiveData<>();
        nearbyUsers = new MutableLiveData<>();
        newUsers = new MutableLiveData<>();
        roomsUsers = new MutableLiveData<>();
        image = new MutableLiveData<>();
        this.repository = repository;
    }

    public void getUsersOfRoomsForUser(int uid){
        repository.getUsersOfRoomsForUser(uid, new Callback<List<SmallUser>>() {
            @Override
            public void onComplete(Result<List<SmallUser>> result) {
                if (result instanceof Result.Success) {
                    roomsUsers.setValue(((Result.Success<List<SmallUser>>) result).data);
                }else {
                    roomsUsers.setValue(null);
                    roomsUsers.postValue(null);
                }
            }
        });
    }


    public void getFavouriteUsers(int uid){
        repository.getFavouriteUsers(uid, new Callback<List<UserDistance>>() {
            @Override
            public void onComplete(Result<List<UserDistance>> result) {
                if (result instanceof Result.Success) {
                    favouriteUsers.setValue(((Result.Success<List<UserDistance>>) result).data);
                }else {
                    favouriteUsers.setValue(null);
                    favouriteUsers.postValue(null);
                    //TODO show error in UI
                }
            }
        });
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

    public LiveData<List<SmallUser>> getUsersOfRoomsForUserList(int uid){
        getUsersOfRoomsForUser(uid);
        return roomsUsers;
    }

    public LiveData<List<UserDistance>> getFavouriteUsersList(int uid){
        getFavouriteUsers(uid);
        return favouriteUsers;
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