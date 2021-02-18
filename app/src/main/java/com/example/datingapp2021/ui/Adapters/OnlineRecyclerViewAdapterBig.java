package com.example.datingapp2021.ui.Adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.R;
import com.example.datingapp2021.ui.dashboard.DashboardFragment;
import com.example.datingapp2021.ui.dashboard.DashboardRepository;
import com.example.datingapp2021.ui.dashboard.DashboardViewModel;
import com.example.datingapp2021.ui.profile.OwnProfileActivity;
import com.example.datingapp2021.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;

public class OnlineRecyclerViewAdapterBig extends RecyclerView.Adapter<OnlineRecyclerViewAdapterBig.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<UserDistance> users;
    private DashboardFragment fragment;
    private SharedPreferences sharedPreferences;
    private boolean isNearby;


    public OnlineRecyclerViewAdapterBig(boolean isNearby, DashboardFragment fragment, List<UserDistance> users, SharedPreferences sharedPreferences) {
        this.isNearby = isNearby;
        this.fragment = fragment;
        setList(users);
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Set users list and sort it according to distance
     * @param users
     * Users list
     */
    public void setList(List<UserDistance> users){
        if (users != null){
            sort(users);
        }
        this.users = users;
    }

    /**
     * Bubble sort list according to distance variable.
     * @param list
     * Users list
     */
    public static void sort(List<UserDistance> list){
        boolean isSorted = false;
        int upTo = list.size() - 1;
        while (!isSorted){
            isSorted = true;
            for (int i = 0; i < upTo; i++) {
                if(list.get(i).convertDistanceToKM() > list.get(i+1).convertDistanceToKM()){
                    UserDistance temp = list.get(i);
                    list.set(i, list.get(i+1));
                    list.set(i+1, temp);
                    isSorted = false;
                }
            }
            upTo--;
        }
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
        if (isNearby && position == 0){
            holder.cardView.setRadius(17);
            holder.cardView.setBackground(null);
            holder.userName.setText(users.get(position).getSmallUser().getUsername());
            holder.distance.setText("0");
        }else{
            holder.userName.setText(users.get(position).getSmallUser().getUsername());
            holder.distance.setText(users.get(position).convertDistanceToKM()+"");
        }
        getImageDrawable(holder, position);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(position == 0 && isNearby){
                    intent = new Intent(fragment.getContext(), OwnProfileActivity.class);
                }else {
                    intent = new Intent(fragment.getContext(), ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uid", users.get(position).getSmallUser().getUid());
                    intent.putExtras(bundle);
                }
                fragment.startActivity(intent);
            }
        });
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
        viewModel.getImageDrawableFromURL(users.get(position).getSmallUser().getImg_url()).observe(fragment, new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                holder.cardView.setBackground(drawable);
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
