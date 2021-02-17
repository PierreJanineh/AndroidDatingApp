package com.example.datingapp2021.ui.profile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.logic.Service.MainService;
import com.example.datingapp2021.ui.Adapters.ViewPagerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;

import java.util.concurrent.Executors;

import static com.example.datingapp2021.logic.DB.SocketServer.SP_UID;
import static com.example.datingapp2021.logic.DB.SocketServer.SP_USERS;

public class ProfileActivity extends AppCompatActivity implements LifecycleOwner {

    public static final String TAG = "ProfileManager";
    public static final String COMMA = ", ";

    private ProfileViewModel profileViewModel;

    /*Views defined in XML file (activity_profile)*/
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private LinearLayout drawerLayout;
    private FloatingActionButton infoBtn, chatBtn, favBtn;
    private TextView txtNameAge,
            txtAbout,
            txtRole,
            txtHeight,
            txtWeight,
            txtEthnicity,
            txtRelationship,
            txtReference,
            txtOrientation,
            txtReligion;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    /*Service Connection*/
    private MainService service;
    private boolean bound;

    private int uid;//from SharedPreferences
    private int otherUid;//from intent bundle extras
    private final MutableLiveData<WholeCurrentUser> currentWholeUser = new MutableLiveData<>();//from service
    private final MutableLiveData<Boolean> isFav = new MutableLiveData<>();//from view model

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        onStartActivity();
        getImagesListForPager();
        configAndAttachSlidr();
        setAllViewVariables();
        setBottomSheetBehavior();
        profileViewModel();
    }

    private void onStartActivity() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MainService.MainBinder binder = (MainService.MainBinder) iBinder;
                service = binder.getService();
                bound = true;
                if (service != null) {
                    currentWholeUser.observe(ProfileActivity.this, new Observer<WholeCurrentUser>() {
                        @Override
                        public void onChanged(WholeCurrentUser wholeCurrentUser) {
                            checkIfUidIsFavOrMine();
                        }
                    });
                    service.currentUser.observe(ProfileActivity.this, new Observer<WholeCurrentUser>() {
                        @Override
                        public void onChanged(WholeCurrentUser wholeCurrentUser) {
                            currentWholeUser.setValue(wholeCurrentUser);
                        }
                    });
                    service.getCurrentUser(uid);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bound = false;
            }
        };

        Bundle bundle = getIntent().getExtras();
        otherUid = bundle.getInt("uid");

        SharedPreferences sharedPreferences = getSharedPreferences(SP_USERS, MODE_PRIVATE);
        uid = SocketServer.getCurrentUserFrom(sharedPreferences);

        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    /**
     * Configure Slidr options and attach to Activity to support slide to return.
     */
    private void configAndAttachSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
                .sensitivity(9999999)
                .velocityThreshold(0)
                .distanceThreshold(0.50f)
                .edge(true)
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        Log.d(TAG, "onSlideStateChanged: started. state: " + state);

                    }

                    @Override
                    public void onSlideChange(float percent) {
                        Log.d(TAG, "onSlideChange: started. percents: " + percent);

                    }

                    @Override
                    public void onSlideOpened() {
                        Log.d(TAG, "onSlideOpened: started.");

                    }

                    @Override
                    public boolean onSlideClosed() {
                        Log.d(TAG, "onSlideClosed: started.");

                        return false;
                    }
                })
                .build();
        Slidr.attach(this, config);
    }

    /**
     * Sets BottomSheetBehavior for info view.
     */
    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(drawerLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        bottomSheetBehavior.setHalfExpandedRatio(0.12f);
        bottomSheetBehavior.setFitToContents(true);
    }

    /**
     * Sets all view variables from XML file.
     */
    private void setAllViewVariables() {
        favBtn = findViewById(R.id.btnFav);
        chatBtn = findViewById(R.id.btnChat);
        infoBtn = findViewById(R.id.btnInfo);

        drawerLayout = findViewById(R.id.drawer);

        txtNameAge = findViewById(R.id.nameAge);
        txtAbout = findViewById(R.id.aboutP);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        txtRelationship = findViewById(R.id.txtRelationship);
        txtReligion = findViewById(R.id.txtReligion);
        txtOrientation = findViewById(R.id.txtOrientation);
        txtRole = findViewById(R.id.txtRole);
        txtEthnicity = findViewById(R.id.txtEthnicity);
        txtReference = findViewById(R.id.txtReference);
    }

    /**
     * Creates and defines profileViewModel variable.
     * And defines a listener for onChange() situation for getUserDistanceObject() method.
     */
    private void profileViewModel() {
        profileViewModel = new ProfileViewModel(new ProfileRepository(Executors.newSingleThreadExecutor(), new Handler()));
        profileViewModel.getUserDistanceObject(getSharedPreferences(SP_USERS, MODE_PRIVATE), otherUid).observe(this, new Observer<UserDistance>() {
            @Override
            public void onChanged(UserDistance userDistance) {
                if (userDistance != null) {
                    String ageName = userDistance.getWholeUser().getUsername() + COMMA + userDistance.getWholeUser().getInfo().getAge();
                    txtNameAge.setText(ageName);
                    txtAbout.setText(userDistance.getWholeUser().getInfo().getAbout());
                    txtHeight.setText(userDistance.getWholeUser().getInfo().getHeight() + "");
                    txtWeight.setText(userDistance.getWholeUser().getInfo().getWeight() + "");
                    txtRelationship.setText(userDistance.getWholeUser().getInfo().getRelationship().name());
                    txtReligion.setText(userDistance.getWholeUser().getInfo().getReligion().name());
                    txtOrientation.setText(userDistance.getWholeUser().getInfo().getOrientation().name());
                    txtRole.setText(userDistance.getWholeUser().getInfo().getRole().name());
                    txtEthnicity.setText(userDistance.getWholeUser().getInfo().getEthnicity().name());
                    txtReference.setText(userDistance.getWholeUser().getInfo().getReference().name());
                }
            }
        });
    }

    /**
     * Checks if the Activity's bundle extras uid equals to the current user's uid, else if the uid is in current user's favs list it assigns favBtn state as selected.
     */
    private void checkIfUidIsFavOrMine() {
        if (currentWholeUser.getValue() != null) {
            if (currentWholeUser.getValue().getUid() == otherUid) {
                favBtn.setVisibility(View.GONE);
                isFav.setValue(false);
            } else if (currentWholeUser.getValue().getFavs().contains(otherUid)) {
                favBtn.setSelected(true);
                isFav.setValue(true);
            }else {
                isFav.setValue(false);
            }
        }
    }

    private void getImagesListForPager() {

        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {
        Log.d(TAG, "initViewPagerAdapter: started.");

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
    }


    public void showHide(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
    }

    public void addToFavs(View view) {
        isFav.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                favBtn.setSelected(aBoolean);
            }
        });
        if (isFav.getValue() != null) {
            System.out.println("other uid = " + otherUid);
            if (!isFav.getValue()) {
                profileViewModel.addToFavsAndGetBool(getSharedPreferences(SP_USERS, MODE_PRIVATE), otherUid).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isFav.setValue(aBoolean);
                            }
                        });
                    }
                });
            } else {
                profileViewModel.removeFromFavsAndGetBool(getSharedPreferences(SP_USERS, MODE_PRIVATE), otherUid).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isFav.setValue(!aBoolean);
                            }
                        });
                    }
                });
            }
        }
    }


//    public void openChat(View view) {
//        Intent intent = new Intent(this, ChatActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("other_user", myCurrentUser);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }

}