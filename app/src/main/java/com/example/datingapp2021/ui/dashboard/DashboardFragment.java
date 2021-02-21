package com.example.datingapp2021.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.example.datingapp2021.ui.Adapters.NearbyUsersRecyclerViewAdapter;
import com.example.datingapp2021.ui.Adapters.NewUsersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private ProgressBar onlinePB;
    private SwipeRefreshLayout refreshLayout;

    private List<UserDistance> nearbyList = new ArrayList<>();
    private List<UserDistance> newList = new ArrayList<>();

    private int uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView newRV = binding.newRV;
        final RecyclerView onlineRV = binding.onlineRV;
        onlinePB = binding.onlinePB;
        refreshLayout = binding.refreshLO;

        NewUsersRecyclerViewAdapter newUsersAdapter = new NewUsersRecyclerViewAdapter( this, nearbyList);
        NearbyUsersRecyclerViewAdapter nearbyUsersAdapter = new NearbyUsersRecyclerViewAdapter(true, this, newList);
        LinearLayoutManager newUsersManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager nearbyUsersManager = new GridLayoutManager(getContext(), 3);
        newRV.setAdapter(newUsersAdapter);
        newRV.setLayoutManager(newUsersManager);

        onlineRV.setAdapter(nearbyUsersAdapter);
        onlineRV.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
            }
        });
        onlineRV.setLayoutManager(nearbyUsersManager);

        uid = SocketServer.getCurrentUserUID(getActivity().getSharedPreferences(SocketServer.SP_USERS, Context.MODE_PRIVATE));
        getNewUsers(newUsersAdapter);

        getNearbyUsers(nearbyUsersAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onlinePB.setVisibility(View.VISIBLE);
                dashboardViewModel.getNewUsers(uid);
                dashboardViewModel.getNearbyUsers(uid);
            }
        });

        return root;
    }

    private void getNewUsers(NewUsersRecyclerViewAdapter newUsersAdapter) {
        dashboardViewModel.getNewList(uid).observe(getViewLifecycleOwner(), new Observer<List<UserDistance>>() {
            @Override
            public void onChanged(@Nullable List<UserDistance> list) {
                if (!dashboardViewModel.newIsNull && list != null && list.size() > 0){
                    if (newList.size() > 0){
                        newList.clear();
                    }
                    newList.addAll(list);
                    newUsersAdapter.setList(newList);
                }else {
                    newUsersAdapter.setList(new ArrayList<>());
                    Toast.makeText(getContext(), "No new users at all baby", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getNearbyUsers(NearbyUsersRecyclerViewAdapter nearbyUsersAdapter) {
        dashboardViewModel.getNearbyList(uid).observe(getViewLifecycleOwner(), new Observer<List<UserDistance>>() {
            @Override
            public void onChanged(@Nullable List<UserDistance> list) {
                if (!dashboardViewModel.nearbyIsNull && list != null && list.size() > 0){
                    if (nearbyList.size() > 0) {
                        nearbyList.clear();
                    }
                    nearbyList.addAll(list);
                    nearbyUsersAdapter.setList(nearbyList);
                    onlinePB.setVisibility(View.INVISIBLE);
                    refreshLayout.setRefreshing(false);
                }else {
                    nearbyUsersAdapter.setList(new ArrayList<>());
                    onlinePB.setVisibility(View.INVISIBLE);
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No nearby users at all baby", Toast.LENGTH_SHORT).show();
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