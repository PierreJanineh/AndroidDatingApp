package com.example.datingapp2021.ui.messages;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.databinding.FragmentMessagesBinding;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.logic.Service.MainService;
import com.example.datingapp2021.ui.Adapters.MessagesRecyclerViewAdapter;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_PRIVATE;
import static com.example.datingapp2021.logic.DB.SocketServer.SP_USERS;

public class MessagesFragment extends Fragment {

    private DashboardViewModel viewModel;
    private FragmentMessagesBinding binding;

    private MessagesRecyclerViewAdapter messagesRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView textView;
    private ProgressBar progressBar;

    private int uid;
    private MutableLiveData<Boolean> noRooms = new MutableLiveData<>(true);
    private List<Room> rooms = null;
    private List<SmallUser> withUsers = null;

    public MainService service;
    public boolean bound;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = setAllViewsVariables(inflater, container);
        bindServiceAndGetViewModel();

        initRecyclerView();

        return root;
    }

    /**
     * Get rooms from User, and assign the value to rooms property.
     * @param wholeCurrentUser
     * Current user to get rooms for.
     * @return
     * true if has rooms, false if doesn't.
     */
    private boolean getRooms(WholeCurrentUser wholeCurrentUser) {
        ArrayList<Room> rooms = wholeCurrentUser.getChatRooms();
        if (rooms != null && rooms.size() > 0) {
            this.rooms = rooms;
            return true;
        }
        return false;
    }

    private void bindServiceAndGetViewModel(){
        viewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));
        uid = SocketServer.getCurrentUserUID(getActivity().getSharedPreferences(SP_USERS, MODE_PRIVATE));

        viewModel.getUsersOfRoomsForUserList(uid).observe(getViewLifecycleOwner(), new Observer<List<SmallUser>>() {
            @Override
            public void onChanged(List<SmallUser> users) {
                MessagesFragment.this.withUsers = users;
                if (messagesRecyclerViewAdapter != null){
                    messagesRecyclerViewAdapter.setUsersList(users);
                    messagesRecyclerViewAdapter.setRoomsList(rooms);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        this.noRooms.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (noRooms.getValue()){
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else {
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MainService.MainBinder binder = (MainService.MainBinder) iBinder;
                service = binder.getService();
                bound = true;

                service.currentUser.observe(getViewLifecycleOwner(), new Observer<WholeCurrentUser>() {
                    @Override
                    public void onChanged(WholeCurrentUser wholeCurrentUser) {
                        if (wholeCurrentUser != null){
                            if (getRooms(wholeCurrentUser)){
                                viewModel.getUsersOfRoomsForUser(uid);
                                noRooms.postValue(false);
                            }else {
                                noRooms.postValue(true);
                            }
                        }
                    }
                });
                service.getCurrentUser(uid);

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bound = false;
            }
        };

        Intent intent = new Intent(getContext(), MainService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private View setAllViewsVariables(LayoutInflater inflater, ViewGroup container){
        binding = FragmentMessagesBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerView;
        textView = binding.noChatLbl;
        progressBar = binding.progress;

        progressBar.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    private void initRecyclerView(){
        messagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(rooms, withUsers, this);
        recyclerView.setAdapter(messagesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound){
            getActivity().unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (bound){
            getActivity().unbindService(serviceConnection);
            bound = false;
        }
    }
}