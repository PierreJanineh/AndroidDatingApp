package com.example.datingapp2021.ui.profile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

public class ProfileActivity extends AppCompatActivity {

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

    private int uid;
    private WholeCurrentUser currentWholeUser;

    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainService.MainBinder binder = (MainService.MainBinder) iBinder;
            service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getImagesListForPager();
        configAndAttachSlidr();
        setAllViewVariables();
        setBottomSheetBehavior();
        profileViewModel();
        checkIfUidIsFavOrMine();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getInt("uid");
        System.out.println("bundle = "+uid);

        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        currentWholeUser = service.getCurrentUser(uid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound){
            unbindService(serviceConnection);
            bound = false;
        }
    }

    /**
     * Configure Slidr options and attach to Activity to support slide to return.
     */
    private void configAndAttachSlidr(){
        SlidrConfig config = new SlidrConfig.Builder()
                .sensitivity(9999999)
                .velocityThreshold(0)
                .distanceThreshold(0.50f)
                .edge(true)
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        Log.d(TAG, "onSlideStateChanged: started. state: "+state);

                    }

                    @Override
                    public void onSlideChange(float percent) {
                        Log.d(TAG, "onSlideChange: started. percents: "+percent);

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
    private void setBottomSheetBehavior(){
        bottomSheetBehavior = BottomSheetBehavior.from(drawerLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        bottomSheetBehavior.setHalfExpandedRatio(0.12f);
        bottomSheetBehavior.setFitToContents(true);
    }

    /**
     * Sets all view variables from XML file.
     */
    private void setAllViewVariables(){
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
    private void profileViewModel(){
        profileViewModel = new ProfileViewModel(new ProfileRepository(Executors.newSingleThreadExecutor(), new Handler()));
        profileViewModel.getUserDistanceObject(getSharedPreferences(SocketServer.SP_USERS, MODE_PRIVATE), uid).observe(this, new Observer<UserDistance>() {
            @Override
            public void onChanged(UserDistance userDistance) {
                if (userDistance != null) {
                    String ageName = userDistance.getWholeUser().getUsername() + COMMA + userDistance.getWholeUser().getInfo().getAge();
                    txtNameAge.setText(ageName);
                    txtAbout.setText(userDistance.getWholeUser().getInfo().getAbout());
                    txtHeight.setText(userDistance.getWholeUser().getInfo().getHeight()+"");
                    txtWeight.setText(userDistance.getWholeUser().getInfo().getWeight()+"");
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
    private void checkIfUidIsFavOrMine(){
        if (currentWholeUser.getUid() == uid){
            favBtn.setVisibility(View.GONE);
        }else if (currentWholeUser.getFavs().contains(uid)){
            favBtn.setSelected(true);
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
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }else{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
    }

//    public void openChat(View view) {
//        Intent intent = new Intent(this, ChatActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("other_user", myCurrentUser);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
    
    public void addToFavs(View view) {
        profileViewModel.addToFavsAndGetBool(getSharedPreferences(SocketServer.SP_USERS, MODE_PRIVATE), uid).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                favBtn.setSelected(true);
            }
        });
    }
//
//    private class SetContentValues extends AsyncTask<Void, Void, User>{
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            openChatBtn.setEnabled(false);
//        }
//
//        @Override
//        protected User doInBackground(Void... voids) {
//            while(myService == null){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return SocketServer.getUserFromUID(uid);
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            currentUser = user;
//            myCurrentUser = new OtherUser(currentUser, user);
//            openChatBtn.setEnabled(true);
//            UserInfo userInfo = user.getInfo();
//            if (userInfo != null) {
//                infoMenu.setVisibility(View.VISIBLE);
//                int userAge = getAgeFromYear(userInfo.getBirthDate().toString());
//                txtNameAge.setText(user.getUsername() + ", " + userAge);
//                txtAbout.setText(String.valueOf(userInfo.getAbout()));
//                txtHeight.setText(String.valueOf(userInfo.getHeight()));
//                txtWeight.setText(String.valueOf(userInfo.getWeight()));
//                txtEthnicity.setText(getResources().getStringArray(R.array.ethnicity)[UserInfo.Ethnicity.getValOf(userInfo.getEthnicity())]);
//                txtReference.setText(getResources().getStringArray(R.array.reference)[UserInfo.Reference.getValOf(userInfo.getReference())]);
//                txtRelationship.setText(getResources().getStringArray(R.array.relationship)[UserInfo.Relationship.getValOf(userInfo.getRelationship())]);
//                txtReligion.setText(getResources().getStringArray(R.array.religion)[UserInfo.Religion.getValOf(userInfo.getReligion())]);
//                txtOrientation.setText(getResources().getStringArray(R.array.orientation)[UserInfo.Orientation.getValOf(userInfo.getOrientation())]);
//                txtRole.setText(getResources().getStringArray(R.array.role)[UserInfo.Role.getValOf(userInfo.getRole())]);
//            }else{
//                txtNameAge.setText(user.getUsername());
//                infoMenu.setVisibility(View.GONE);
//            }
//        }
//
//        private int getAgeFromYear(String birthDate) {
//            return Calendar.getInstance().get(Calendar.YEAR) - (Integer.valueOf(birthDate.split("/")[2]));
//        }
//    }
    
//    public class AddToFavs extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while (myService == null){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            myService.addFavourites(currentUser);
//            return null;
//        }
//    }
}