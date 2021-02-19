package com.example.datingapp2021.ui.favourites;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datingapp2021.databinding.FragmentFavouritesBinding;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.Adapters.NearbyUsersRecyclerViewAdapter;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static com.example.datingapp2021.logic.DB.SocketServer.SP_USERS;

public class FavouritesFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentFavouritesBinding binding;

    private int uid;
    private List<UserDistance> users = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));
        uid = SocketServer.getCurrentUserFrom(getActivity().getSharedPreferences(SP_USERS, Context.MODE_PRIVATE));

        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.noFavsLbl;
        final ProgressBar progressBar = binding.progress;
        final RecyclerView recyclerView = binding.recyclerView;
        final SwipeRefreshLayout swipeLayout = binding.swipeLayout;

        NearbyUsersRecyclerViewAdapter nearbyUsersRecyclerViewAdapter = new NearbyUsersRecyclerViewAdapter(false, this, users);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setAdapter(nearbyUsersRecyclerViewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        dashboardViewModel.getFavouriteUsersList(uid).observe(getViewLifecycleOwner(), new Observer<List<UserDistance>>() {
            @Override
            public void onChanged(List<UserDistance> userDistances) {
                if (userDistances != null && userDistances.size() > 0){
                    users = userDistances;
                    nearbyUsersRecyclerViewAdapter.setList(users);
                    textView.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getContext(), "No Favourite users found.", Toast.LENGTH_SHORT).show();
                    textView.setVisibility(View.VISIBLE);
                    nearbyUsersRecyclerViewAdapter.setList(new ArrayList<>());
                }
                progressBar.setVisibility(View.GONE);
                swipeLayout.setRefreshing(false);
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dashboardViewModel.getFavouriteUsers(uid);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}