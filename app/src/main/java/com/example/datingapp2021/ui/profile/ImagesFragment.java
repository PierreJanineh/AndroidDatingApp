package com.example.datingapp2021.ui.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.datingapp2021.R;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;

import java.util.concurrent.Executors;

public class ImagesFragment extends Fragment {

    private ImageView imageView;
    private TextView textView;
    private String imgUrl;

    private DashboardViewModel viewModel;

    public ImagesFragment(String url) {
        this.imgUrl = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);
        imageView = view.findViewById(R.id.imgView);
        textView = view.findViewById(R.id.txtView);
        assert getArguments() != null;
        String pageNum = getArguments().getString("Message");
        int img = getArguments().getInt("Images");
//        imageView.setImageDrawable(drawable);
        textView.setText(pageNum);

        viewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));
        viewModel.getImageDrawableFromURL(imgUrl).observe(getViewLifecycleOwner(), new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                System.out.println("got image");
                ImagesFragment.this.imageView.setImageDrawable(drawable);
            }
        });
        return view;
    }

    public void setImage(String url){
        viewModel.getImageFromURL(url);
    }
}
