package com.example.datingapp2021.ui.profile;

import android.content.SharedPreferences;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.ui.Result;

import java.util.List;
import java.util.concurrent.Executors;

public class ProfileViewModel {

    private MutableLiveData<UserDistance> user;
    private MutableLiveData<Boolean> favSuccess;
    private final ProfileRepository repository;
    public boolean userIsNull = true;

    public ProfileViewModel() {
        this.user = new MutableLiveData<>();
        this.favSuccess = new MutableLiveData<>();
        this.repository = new ProfileRepository(Executors.newSingleThreadExecutor(), new Handler());
    }

    public ProfileViewModel(ProfileRepository repository) {
        this.user = new MutableLiveData<>();
        this.favSuccess = new MutableLiveData<>();
        this.repository = repository;
    }

    public void getUserDistance(SharedPreferences sharedPreferences, int otherUID) {
        repository.getUserInfo(sharedPreferences, otherUID, new Callback<UserDistance>() {
            @Override
            public void onComplete(Result<UserDistance> result) {
                if (result instanceof Result.Success) {
                    userIsNull = false;
                    user.setValue(((Result.Success<UserDistance>) result).data);
                }else if (result instanceof Result.SuccessNULL) {
                    userIsNull = true;
                    user.setValue(null);
                    user.postValue(null);
                }else {
                    userIsNull = true;
                    user.setValue(null);
                    user.postValue(null);
                    //TODO show error in UI
                }
            }
        });
    }

    public void addToFavs(SharedPreferences sharedPreferences, int uid){
        repository.addToFavourites(sharedPreferences, uid, new Callback<Boolean>() {
            @Override
            public void onComplete(Result<Boolean> result) {
                favSuccess.setValue(((Result.Success<Boolean>) result).data);
            }
        });
    }

    public LiveData<Boolean> addToFavsAndGetBool(SharedPreferences sharedPreferences, int uid){
        addToFavs(sharedPreferences, uid);
        return favSuccess;
    }

    public LiveData<UserDistance> getUserDistanceObject(SharedPreferences sharedPreferences, int uid) {
        getUserDistance(sharedPreferences, uid);
        return user;
    }
}

interface Callback<T>{
    void onComplete(Result<T> result);
}