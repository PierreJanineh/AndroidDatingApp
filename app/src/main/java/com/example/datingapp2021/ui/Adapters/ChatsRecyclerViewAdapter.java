package com.example.datingapp2021.ui.Adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.DB.SocketServer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

	private Room room;
	private List<Message> messagesList;
	private SharedPreferences sharedPreferences;
	private Boolean isFromMe;
	private int pos;
	
	public ChatsRecyclerViewAdapter(Room room, List<Message> messagesList, SharedPreferences sharedPreferences) {
		this.room = room;
		setList(messagesList);
		this.sharedPreferences = sharedPreferences;
	}

	public void setList(List<Message> messages){
		if (messages == null || messages.size() == 0){
			this.messagesList = new ArrayList<>();
			return;
		}
		sortMessages(messages);
		this.messagesList = messages;
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		isFromMe = !messagesList.get(pos).isItToMe(SocketServer.getCurrentUserUID(sharedPreferences));
		if(isFromMe){
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_chat, viewGroup, false);
			return new MyChatViewHolder(view);
		}else {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat, viewGroup, false);
			return new ViewHolder(view);
		}
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder recyclerViewHolder, final int i) {
//		pos = i+1;
		ViewHolder viewHolder;
		MyChatViewHolder myChatViewHolder;
		if(isFromMe){
			myChatViewHolder = (MyChatViewHolder) recyclerViewHolder;
			myChatViewHolder.content.setText( messagesList.get(i).getContent() );
			myChatViewHolder.time.setText(messagesList.get(i).getTimestamp().toString());

			final View view = (View) myChatViewHolder.content.getParent();
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(view.getContext(), "Long pressed on "+ messagesList.get(i).getContent(), Toast.LENGTH_LONG).show();
					return true;
				}
			});
		}else{
			viewHolder = (ViewHolder) recyclerViewHolder;
			viewHolder.content.setText( messagesList.get(i).getContent() );
			viewHolder.time.setText(messagesList.get(i).getTimestamp().toString());
			
			final View view = (View) viewHolder.content.getParent();
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(view.getContext(), "Long pressed on "+ messagesList.get(i).getContent(), Toast.LENGTH_LONG).show();
					return true;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return messagesList.size();
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		
		TextView content, time;
		
		public ViewHolder(View itemView) {
			super(itemView);
			content = itemView.findViewById(R.id.txtChat);
			time = itemView.findViewById(R.id.txtTime);
		}
	}
	public static class MyChatViewHolder extends RecyclerView.ViewHolder {
		
		TextView content, time;
		
		public MyChatViewHolder(View itemView) {
			super(itemView);
			content = itemView.findViewById(R.id.txtChat);
			time = itemView.findViewById(R.id.txtTime);
		}
		
	}

	public void sortMessages(List<Message> arr){
		boolean isSorted = false;
		int upTo = arr.size() - 1;
		while (!isSorted){
			isSorted = true;
			for (int i = 0; i < upTo; i++) {
				if(arr.get(i).getTimestamp().after(arr.get(i+1).getTimestamp())){
					Message temp = arr.get(i);
					arr.set(i, arr.get(i+1));
					arr.set(i+1, temp);
					isSorted = false;
				}
			}
			upTo--;
		}
	}
	
}
