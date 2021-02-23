package com.example.datingapp2021.ui.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.GetMessagesThread;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.logic.Service.MainService;
import com.example.datingapp2021.ui.Adapters.ChatsRecyclerViewAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.datingapp2021.logic.DB.SocketServer.SP_USERS;

public class ChatActivity extends AppCompatActivity {

    private int uid, otherUid;
    private Room room = null;
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatsRecyclerViewAdapter chatsRecyclerViewAdapter;

    private SmallUser to, from;

    public MainService service;
    public boolean bound;
    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainService.MainBinder binder = (MainService.MainBinder) iBinder;
            service = binder.getService();
            bound = true;

            service.currentUser.observe(ChatActivity.this, new Observer<WholeCurrentUser>() {
                @Override
                public void onChanged(WholeCurrentUser wholeCurrentUser) {
                    if (wholeCurrentUser != null){
                        if (getRoom(wholeCurrentUser)){
                            service.startGettingRoomMessages(room.getUid(), new GetMessagesThread.MessagesListener() {
                                @Override
                                public void onNewMessage(List<Message> messages) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatsRecyclerViewAdapter.setList(messages);
                                            recyclerView.smoothScrollToPosition(messages.size()-1);
                                        }
                                    });
                                }
                            });
                        }else {
                            //Room is null
                        }
                    }
                }
            });

            service.room.observe(ChatActivity.this, new Observer<Room>() {
                @Override
                public void onChanged(Room room) {
                    ChatActivity.this.room = room;
                }
            });

            service.user.observe(ChatActivity.this, new Observer<SmallUser>() {
                @Override
                public void onChanged(SmallUser smallUser) {
                    if (smallUser.getUid() == uid)
                        ChatActivity.this.from = smallUser;
                    else
                        ChatActivity.this.to = smallUser;
                }
            });

//            service.roomMessages.observe(ChatActivity.this, new Observer<List<Message>>() {
//                @Override
//                public void onChanged(List<Message> messages) {
//
//                }
//            });

            service.getCurrentUser(uid);
            service.getUser(otherUid);
            service.getUser(uid);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        otherUid = bundle.getInt("uid");
        uid = SocketServer.getCurrentUserUID(getSharedPreferences(SP_USERS, MODE_PRIVATE));

        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        recyclerView = findViewById(R.id.list_of_messages);
        SharedPreferences sharedPreferences = getSharedPreferences(SP_USERS, MODE_PRIVATE);
        chatsRecyclerViewAdapter = new ChatsRecyclerViewAdapter(room, messages, sharedPreferences);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(chatsRecyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * Go in 2 loops over user's rooms, and room's recipients, to check if there's a room between me an the other user. If positive, assign it to this.room
     * @param wholeCurrentUser
     * Current user to check chat rooms for.
     * @return
     * true if got the room, false if no room exists.
     */
    private boolean getRoom(WholeCurrentUser wholeCurrentUser) {
        ArrayList<Room> rooms = wholeCurrentUser.getChatRooms();
        if (rooms == null || rooms.size() == 0) {
            return false;
        }
        for (Room room : rooms) {
            ArrayList<Integer> recpnts = room.getRecipients();
            for (Integer rec : recpnts) {
                if (rec == otherUid) {
                    ChatActivity.this.room = room;
                    return true;
                }
            }
        }
        return false;
    }

    public void sendMsg(View view){

        EditText inptMsg = (EditText) findViewById(R.id.inptMessage);
        String content = inptMsg.getText().toString();
        Message message = new Message(content, new Timestamp(new Date().getTime()), to, from);

        if(this.room == null){
            ArrayList<Integer> recipients = new ArrayList<>();
            recipients.add(uid);
            recipients.add(otherUid);
        /*
        SeenBy is sent as null, have not been seen yet.
        messages is sent as null, uid has not been initialized yet, initializes in DB.
        */
            room = new Room(null, null, recipients, message);
        }else {
            room.setLastMessage(message);
        }
        service.sendMessage(room);
        inptMsg.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound){
            unbindService(serviceConnection);
            bound = false;
        }
    }
}
