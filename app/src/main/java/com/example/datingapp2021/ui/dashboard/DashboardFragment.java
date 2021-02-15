package com.example.datingapp2021.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datingapp2021.databinding.FragmentDashboardBinding;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Adapters.OnlineRecyclerViewAdapterBig;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private List<UserDistance> nearbyList = new ArrayList<>();
    private List<UserDistance> newList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        dashboardViewModel =
//                new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel =
                new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView newRV = binding.newRV;
        final RecyclerView onlineRV = binding.onlineRV;
        final ProgressBar onlinePB = binding.onlinePB;
        final SwipeRefreshLayout refreshLayout = binding.refreshLO;

        OnlineRecyclerViewAdapterBig newUsersAdapter = new OnlineRecyclerViewAdapterBig(this, nearbyList, getActivity().getSharedPreferences(SocketServer.SP_USERS, Context.MODE_PRIVATE));
        OnlineRecyclerViewAdapterBig nearbyUsersAdapter = new OnlineRecyclerViewAdapterBig(this, newList, getActivity().getSharedPreferences(SocketServer.SP_USERS, Context.MODE_PRIVATE));
        LinearLayoutManager newUsersManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager nearbyUsersManager = new GridLayoutManager(getContext(), 3);
        newRV.setAdapter(newUsersAdapter);
        newRV.setLayoutManager(newUsersManager);

        onlineRV.setAdapter(nearbyUsersAdapter);
        onlineRV.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                System.out.println("item changed");
                super.onItemRangeChanged(positionStart, itemCount);
            }
        });
        onlineRV.setLayoutManager(nearbyUsersManager);

        getNewUsers(newUsersAdapter);

        getNearbyUsers(onlinePB, nearbyUsersAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onlinePB.setVisibility(View.VISIBLE);
                getNewUsers(newUsersAdapter);
                getNearbyUsers(onlinePB, nearbyUsersAdapter);
            }
        });

        return root;
    }

    private void getNewUsers(OnlineRecyclerViewAdapterBig newUsersAdapter) {
        dashboardViewModel.getNewList().observe(getViewLifecycleOwner(), new Observer<List<UserDistance>>() {
            @Override
            public void onChanged(@Nullable List<UserDistance> list) {
                if (!dashboardViewModel.newIsNull && list != null && list.size() > 0){
                    Log.d("onChangedBabe", "onChanged: if1");
                    if (newList.size() > 0){
                        newList.clear();
                    }
                    newList.addAll(list);
                    newUsersAdapter.setList(newList);
                    newUsersAdapter.notifyDataSetChanged();
                }else {
                    Log.d("onChangedBabe", "onChanged: else1");
                    Snackbar.make(getContext(), getView(), "No new users at all baby", 10000).show();
                }
            }
        });
    }

    private void getNearbyUsers(ProgressBar onlinePB, OnlineRecyclerViewAdapterBig nearbyUsersAdapter) {
        dashboardViewModel.getNearbyList().observe(getViewLifecycleOwner(), new Observer<List<UserDistance>>() {
            @Override
            public void onChanged(@Nullable List<UserDistance> list) {
                if (!dashboardViewModel.nearbyIsNull && list != null && list.size() > 0){
                    Log.d("onChangedBabe", "onChanged: if");
                    if (nearbyList.size() > 0) {
                        nearbyList.clear();
                    }
                    nearbyList.addAll(list);
                    nearbyUsersAdapter.setList(nearbyList);
                    nearbyUsersAdapter.notifyDataSetChanged();
                    onlinePB.setVisibility(View.GONE);
                }else {
                    Log.d("onChangedBabe", "onChanged: else");
                    onlinePB.setVisibility(View.GONE);
                    Snackbar.make(getContext(), getView(), "No nearby users at all baby", 20000).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}