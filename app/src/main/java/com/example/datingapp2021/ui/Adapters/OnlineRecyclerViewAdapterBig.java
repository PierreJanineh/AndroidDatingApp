package com.example.datingapp2021.ui.Adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;
import com.example.datingapp2021.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;

public class OnlineRecyclerViewAdapterBig extends RecyclerView.Adapter<OnlineRecyclerViewAdapterBig.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<UserDistance> users;
    private Fragment fragment;
    private SharedPreferences sharedPreferences;

    public OnlineRecyclerViewAdapterBig(Fragment fragment, List<UserDistance> users, SharedPreferences sharedPreferences) {
        this.fragment = fragment;
        this.users = users;
        this.sharedPreferences = sharedPreferences;
    }

    public void setList(List<UserDistance> users){
        this.users = users;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_big, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
        SmallUser currentSmallUser = SocketServer.getCurrentUser(sharedPreferences);
        if(position == 0) {
            holder.cardView.setRadius(17);
	        holder.cardView.setBackground(null);
            holder.userName.setText(currentSmallUser.getUsername());
            holder.distance.setText("0");
        }else{


            DashboardViewModel viewModel = new DashboardViewModel(new DashboardRepository(Executors.newSingleThreadExecutor(), new Handler()));
            viewModel.getImageDrawableFromURL(users.get(position).getSmallUser().getImg_url()).observe(fragment, new Observer<Drawable>() {
                @Override
                public void onChanged(Drawable drawable) {
                    holder.cardView.setBackground(drawable);
                }
            });

//            holder.cardView.setBackgroundResource(R.drawable.ic_launcher_background);
            holder.userName.setText(users.get(position).getSmallUser().getUsername());
            holder.distance.setText(users.get(position).getDistanceInKM()+"");
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: ");
                Intent intent;
//                if(position == 0){
//                    intent = new Intent(mContext, OwnProfileActivity.class);
//                    mContext.startActivity(intent);
//                }else {
                    intent = new Intent(fragment.getContext(), ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uid", users.get(position).getSmallUser().getUid());
                    intent.putExtras(bundle);
                    fragment.startActivity(intent);
//                }
            }
        });
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull @NotNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull ViewHolder holder) {
        System.out.println("view attached to window: "+holder.userName.getText());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull ViewHolder holder) {
        System.out.println("view attached to window: "+holder.userName.getText());
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        TextView distance;
        ConstraintLayout parentLayout;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.txtUser);
            distance = itemView.findViewById(R.id.txtDistance);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
