package com.example.datingapp2021.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datingapp2021.R;
import com.example.datingapp2021.ui.Adapters.ProfileImagesViewPagerAdapter;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;

public class OwnProfileActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ProfileImagesViewPagerAdapter profileImagesViewPagerAdapter;
    private Intent intent;

    public static final String TAG = "OwnProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_profile);
        getList();
        configAndAttachSlidr();
    }

    private void getList() {
        initViewPagerAdapter();
    }

    private void configAndAttachSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
                .sensitivity(999999999).velocityThreshold(0)
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        Log.d(TAG, "onSlideStateChanged: started.");

                    }

                    @Override
                    public void onSlideChange(float percent) {
                        Log.d(TAG, "onSlideChange: started.");

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

    private void initViewPagerAdapter() {
        Log.d(TAG, "initViewPagerAdapter: started.");

        viewPager = findViewById(R.id.viewPager);
        profileImagesViewPagerAdapter = new ProfileImagesViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(profileImagesViewPagerAdapter);
    }

    public void settings(View view) {
        intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void editProfile(View view) {
        intent = new Intent(this, ProfileEditorActivity.class);
        this.startActivity(intent);
    }
}
