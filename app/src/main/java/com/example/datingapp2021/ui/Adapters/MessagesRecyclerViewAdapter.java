package com.example.datingapp2021.ui.Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.ui.chat.ChatActivity;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;
import com.example.datingapp2021.ui.messages.MessagesFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private List<Room> rooms;
    private List<SmallUser> withUsers;
    private MessagesFragment fragment;

    public MessagesRecyclerViewAdapter(List<Room> rooms, List<SmallUser> withUsers, MessagesFragment fragment) {
        this.rooms = rooms;
        this.withUsers = withUsers;
        this.fragment = fragment;
    }

    public void setUsersList(List<SmallUser> users){
        this.withUsers = users;
        notifyDataSetChanged();
    }

    public void setRoomsList(List<Room> rooms){
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.nameLbl.setText(withUsers.get(position).getUsername());
        getImageDrawable(holder, position);
        holder.txtMessage.setText(rooms.get(position).getLastMessage().getContent());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + withUsers.get(position).getUsername());

                Intent intent = new Intent(fragment.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("uid", withUsers.get(position).getUid());
                intent.putExtras(bundle);
                fragment.getActivity().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (rooms == null){
            return 0;
        }
        return rooms.size();
    }

    /**
     * Get profile image using http connection in background using view model. And sets cardView.setBackground() on onChange() observer method.
     * @param holder
     * View holder.
     * @param position
     * View position.
     */
    private void getImageDrawable(@NotNull ViewHolder holder, int position) {
        DashboardViewModel viewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));
        viewModel.getImageDrawableFromURL(withUsers.get(position).getImg_url()).observe(fragment, new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                holder.image.setImageDrawable(drawable);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView nameLbl, txtMessage;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nameLbl = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}