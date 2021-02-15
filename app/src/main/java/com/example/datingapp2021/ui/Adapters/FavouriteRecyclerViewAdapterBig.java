package com.example.datingapp2021.ui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.logic.Classes.OtherUser;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeUser;
import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.DB.SocketServer;

import java.util.List;

public class FavouriteRecyclerViewAdapterBig extends RecyclerView.Adapter<FavouriteRecyclerViewAdapterBig.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<WholeUser> wholeUsers;
    private Context mContext;

    public FavouriteRecyclerViewAdapterBig(Context context, List<WholeUser> wholeUsers) {
        this.mContext = context;
        this.wholeUsers = wholeUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_big, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        SmallUser currentSmallUser = SocketServer.getCurrentUser();
        if(position == 0) {
            holder.cardView.setRadius(17);
            holder.cardView.setBackground(null);
            holder.userName.setText(currentSmallUser.getUsername());
            holder.distance.setText("0");
        }else{
            holder.cardView.setBackgroundResource(R.drawable.ic_launcher_background);
            holder.userName.setText(wholeUsers.get(position).getUsername());
//            holder.distance.setText(otherUser.getDistance()+"");
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: ");
                Intent intent;
                /*if(position == 0){
                    intent = new Intent(mContext, OwnProfileActivity.class);
                    mContext.startActivity(intent);
                }else {
                    intent = new Intent(mContext, ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", users.get(position).getUid()+"");
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return wholeUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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