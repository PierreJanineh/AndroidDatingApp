package com.example.datingapp2021.logic.DB;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.datingapp2021.logic.Classes.GeoPoint;
import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeUser;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.Classes.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SocketServer {
    public static final String HOST = "192.168.1.218";
    public static final int PORT = 3000;

    /*MESSAGE*/
    public static final int SEND_MESSAGE = 101;
    public static final int GET_MESSAGES = 102;
    public static final int GET_MESSAGE = 103;
    /*USER*/
    public static final int GET_NEARBY_USERS = 120;
    public static final int ADD_USER = 121;
    public static final int GET_USER_FROM_UID = 122;
    public static final int ADD_FAV = 123;
    public static final int REM_FAV = 124;
    public static final int GET_FAVS = 125;
    public static final int GET_USERINFO = 126;
    public static final int UPDATE_USERINFO = 127;
    public static final int GET_NEW_USERS = 128; //TODO add to server
    public static final int GET_USERDISTANCE = 129;
    public static final int GET_SMALL_USER = 130;
    /*GEO_POINT*/
    public static final int UPDATE_LOCATION = 150;
    /*USER_INFO*/
    public static final int GET_ALL_ROOMS = 160;

    /*SERVER_CODES*/
    public static final int OKAY = 200;
    public static final int READY = 300;
    public static final int FAILURE = 500;

    /*SHARED_PREFERENCES*/
    public static final String SP_USERS = "users";
    public static final String SP_UID = "uid";

    private static SmallUser currentSmallUser;

    public static String readStringFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try{
            int nRead;
            int dataLength = inputStream.read();
            byte[] data = new byte[inputStream.available()];
            while (inputStream.available() != 0) {
                nRead = inputStream.read(data, 0, dataLength);
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static SmallUser getCurrentUser(SharedPreferences sharedPreferences) {
        getCurrentUser();
//        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SP_UID, currentSmallUser.getUid());
        editor.apply();
        Log.d("Socket_getCurrentUser", "got current user");
        return currentSmallUser;
    }

    public static SmallUser getCurrentUser() {
        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<Integer> favs = new ArrayList<>();
        ArrayList<UserInfo.STD> stds = new ArrayList<>();
        ArrayList<UserInfo.Disability> disabilities = new ArrayList<>();
        UserInfo userInfo = new UserInfo(
                3,
                "I am a generated Pierre",
                50,
                170,
                new Date(),
                UserInfo.Relationship.IN_RELATIONSHIP,
                UserInfo.Religion.ATHEIST,
                UserInfo.Orientation.BISEXUAL,
                UserInfo.Ethnicity.MIDDLE_EASTERN,
                UserInfo.Reference.HE,
                stds,
                UserInfo.Role.TOP,
                disabilities);
        favs.add(1);
        currentSmallUser = new SmallUser(
                3,
                "GeneratedPierre",
                new GeoPoint(
                        1,
                        1),
                "");
        return currentSmallUser;
    }

    public static Message getMessage(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_MESSAGE);
            outputStream.write(uid);

            return new Message(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean sendMessage(String messageJson){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(SEND_MESSAGE);
            outputStream.write(SocketServer.getCurrentUser().getUid());

            byte[] messageBytes = messageJson.getBytes();
            outputStream.write(messageBytes.length);
            outputStream.write(messageBytes);
            int response = inputStream.read();
            if(response == OKAY){
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static List<UserDistance> getNearbyUsers(){


        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            socket = new Socket(HOST, 3000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_NEARBY_USERS);
            outputStream.write(SocketServer.getCurrentUser().getUid());

            return UserDistance.readUsers(inputStream);
        } catch (UnknownHostException e) {
            Log.d("makeSynchronous1", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("makeSynchronous2", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("makeSynchronous", "exception: " + e.getClass().getName());
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Add new User to Db. This function communicates with the DB through Socket.
     * @param wholeUser
     * New User object.
     * @return
     * True if succeeded, false if not.
     */
    public static boolean addUser(WholeUser wholeUser){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(ADD_USER);
            wholeUser.write(outputStream);

            int response = inputStream.read();
            if(response == OKAY){
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Get User from UID. This function communicates with the DB through Socket.
     * @param uid
     * User UID
     * @return
     * User Object.
     */
    public static WholeUser getUserFromUID(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_USER_FROM_UID);
            outputStream.write(uid);

            return new WholeUser(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Get UserDistance from current and other users' UID. This function communicates with the DB through Socket.
     * @param myUID
     * Current User's UID
     * @param otherUID
     * Other User's UID
     * @return
     * UserDistance Object.
     */
    public static UserDistance getWholeUserDistance(int myUID, int otherUID){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_USERDISTANCE);
            outputStream.write(myUID);
            outputStream.write(otherUID);

            return new UserDistance(inputStream, true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static SmallUser getSmallUser(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_SMALL_USER);
            outputStream.write(uid);

            return new SmallUser(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Add new Favourite User to Favourites list in Db. This function communicates with the DB through Socket.
     * @param favUser
     * New Favourite User's UID.
     * @param currentUser
     * Current User's UID.
     * @return
     * Boolean if successfully added.
     */
    public static boolean addFavouriteUser(int currentUser, int favUser){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(ADD_FAV);
            outputStream.write(currentUser);
            outputStream.write(favUser);

            int response = inputStream.read();
            return response == OKAY;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Remove Favourite User from Favourites list in Db. This function communicates with the DB through Socket.
     * @param favUser
     * New Favourite User's UID.
     * @param currentUser
     * Current User's UID.
     * @return
     * Boolean if successfully removed.
     */
    public static boolean removeFavouriteUser(int favUser, int currentUser){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(REM_FAV);
            outputStream.write(favUser);
            outputStream.write(currentUser);

            int response = inputStream.read();
            if(response == OKAY){
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Get all favourite Users and assign them to favUsersList variable. This function communicates with the DB through Socket.
     * @param currentUser
     * Current User UID.
     * @return
     * List of favourite Users.
     */
    public static List<UserDistance> getFavouriteUsers(int currentUser){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_FAVS);
            outputStream.write(currentUser);

            return WholeUser.readUsers(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Updates location for user by UID. This function communicates with the DB through Socket.
     * @param uid
     * User UID to update location.
     * @param geoPoint
     * GeoPoint containing lat & lng.
     */
    public static void updateLocationForUser(int uid, GeoPoint geoPoint){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(UPDATE_LOCATION);
            outputStream.write(uid);
            geoPoint.write(outputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get UserInfo by UID. This function communicates with the DB through Socket.
     * @param uid
     * User UID.
     * @return
     * UserInfo object
     */
    public static UserInfo getUserInfoByUID(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_USERINFO);
            outputStream.write(uid);

            return new UserInfo(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Updates UserInfo. This function communicates with the DB through Socket.
     * @param uid
     * User uid.
     * @param userInfo
     * Updated UserInfo Object.
     */
    public static void updateUserInfo(int uid, UserInfo userInfo){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(UPDATE_USERINFO);
            outputStream.write(uid);
            userInfo.write(outputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets 25 new users in DB, less if there are less users in DB. This function communicates with the DB through Socket.
     * @return
     * List of 25 UserDistance
     */
    public static List<UserDistance> getNewUsers(){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_NEW_USERS);
            outputStream.write(SocketServer.getCurrentUser().getUid());

            return UserDistance.readUsers(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Get all Rooms by User UID. This function communicates with the DB through Socket.
     * @param uid
     * User uid to look for Rooms for.
     * @return
     * Array of all rooms.
     */
    public static List<Room> getAllRooms(int uid) {
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_ALL_ROOMS);
            outputStream.write(uid);

            return Room.readRooms(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
