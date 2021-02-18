package com.example.datingapp2021.ui.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.datingapp2021.logic.Classes.Image;
import com.example.datingapp2021.ui.profile.ImagesFragment;
import com.example.datingapp2021.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProfileImagesViewPagerAdapter extends FragmentStateAdapter {

    private Context context;
    private ArrayList<Image> imgs = new ArrayList<>();

    public ProfileImagesViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /*@Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }*/

    public void setItems(ArrayList<Image> images){
        this.imgs = images;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        ImagesFragment imagesFragment = new ImagesFragment(imgs.get(position).getImgUrl());
        Bundle bundle = new Bundle();
        bundle.putString("Message", "Position is: "+position+1);
        bundle.putInt("Images",imgs.get(position).getUid());
//        position = position+1;
        imagesFragment.setArguments(bundle);
        return imagesFragment;
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    /*@Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }*/
}