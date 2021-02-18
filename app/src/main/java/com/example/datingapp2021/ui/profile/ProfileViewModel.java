package com.example.datingapp2021.ui.profile;

import android.content.SharedPreferences;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingapp2021.logic.Classes.Image;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.Classes.UserInfo;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.ui.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProfileViewModel {

    public MutableLiveData<ArrayList<Image>> images;
    public MutableLiveData<WholeCurrentUser> currentUser;
    public MutableLiveData<UserDistance> user;
    public MutableLiveData<Boolean> favSuccess;
    public MutableLiveData<Boolean> remSuccess;
    public MutableLiveData<Boolean> userEditSuccess;
    private final ProfileRepository repository;
    public boolean userIsNull = true;
    public boolean currentUserIsNull = true;

    public ProfileViewModel() {
        this.images = new MutableLiveData<>();
        this.currentUser = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
        this.favSuccess = new MutableLiveData<>();
        this.remSuccess = new MutableLiveData<>();
        this.userEditSuccess = new MutableLiveData<>();
        this.repository = new ProfileRepository(Executors.newSingleThreadExecutor(), new Handler());
    }

    public ProfileViewModel(ProfileRepository repository) {
        this.images = new MutableLiveData<>();
        this.currentUser = new MutableLiveData<>();
        this.user = new MutableLiveData<>();
        this.favSuccess = new MutableLiveData<>();
        this.remSuccess = new MutableLiveData<>();
        this.userEditSuccess = new MutableLiveData<>();
        this.repository = repository;
    }

    public void getImages(int uid) {
        repository.getImages(uid, new Callback<ArrayList<Image>>() {
            @Override
            public void onComplete(Result<ArrayList<Image>> result) {
                images.setValue(((Result.Success<ArrayList<Image>>) result).data);
            }
        });
    }

    public void editCurrentUser(SharedPreferences sharedPreferences, UserInfo userInfo) {
        repository.editCurrentUser(sharedPreferences, userInfo, new Callback<Boolean>() {
            @Override
            public void onComplete(Result<Boolean> result) {
                userEditSuccess.setValue(((Result.Success<Boolean>) result).data);
            }
        });
    }

    public void getCurrentUser(SharedPreferences sharedPreferences) {
        repository.getCurrentUser(sharedPreferences, new Callback<WholeCurrentUser>() {
            @Override
            public void onComplete(Result<WholeCurrentUser> result) {
                if (result instanceof Result.Success) {
                    currentUserIsNull = false;
                    currentUser.setValue(((Result.Success<WholeCurrentUser>) result).data);
                }else if (result instanceof Result.SuccessNULL) {
                    currentUserIsNull = true;
                    currentUser.setValue(null);
                    currentUser.postValue(null);
                }else {
                    currentUserIsNull = true;
                    currentUser.setValue(null);
                    currentUser.postValue(null);
                }
            }
        });
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

    public void removeFromFavs(SharedPreferences sharedPreferences, int otherUID){
        repository.removeFromFavourites(sharedPreferences, otherUID, new Callback<Boolean>() {
            @Override
            public void onComplete(Result<Boolean> result) {
                remSuccess.setValue(((Result.Success<Boolean>) result).data);
            }
        });
    }

    public LiveData<ArrayList<Image>> getImagesArray(int uid) {
        getImages(uid);
        return images;
    }


    public LiveData<Boolean> editCurrentUserAndGetBoolean(SharedPreferences sharedPreferences, UserInfo userInfo) {
        editCurrentUser(sharedPreferences, userInfo);
        return userEditSuccess;
    }

    public LiveData<WholeCurrentUser> getUserCurrentUserObject(SharedPreferences sharedPreferences) {
        getCurrentUser(sharedPreferences);
        return currentUser;
    }

    public LiveData<Boolean> addToFavsAndGetBool(SharedPreferences sharedPreferences, int otherUID){
        addToFavs(sharedPreferences, otherUID);
        return favSuccess;
    }

    public LiveData<Boolean> removeFromFavsAndGetBool(SharedPreferences sharedPreferences, int otherUID){
        removeFromFavs(sharedPreferences, otherUID);
        return remSuccess;
    }

    public LiveData<UserDistance> getUserDistanceObject(SharedPreferences sharedPreferences, int otherUID) {
        getUserDistance(sharedPreferences, otherUID);
        return user;
    }
}

interface Callback<T>{
    void onComplete(Result<T> result);
}