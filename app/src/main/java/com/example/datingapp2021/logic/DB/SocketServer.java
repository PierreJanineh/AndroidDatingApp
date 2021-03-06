package com.example.datingapp2021.logic.DB;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.datingapp2021.logic.Classes.GeoPoint;
import com.example.datingapp2021.logic.Classes.Image;
import com.example.datingapp2021.logic.Classes.Message;
import com.example.datingapp2021.logic.Classes.Room;
import com.example.datingapp2021.logic.Classes.SmallUser;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
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
import java.util.List;

public class SocketServer {
    public static final String HOST = "192.168.1.217";
    public static final int PORT = 3000;

    /*MESSAGE*/
    public static final int SEND_MESSAGE = 101;
    public static final int GET_MESSAGES = 102;
    public static final int GET_MESSAGE = 103;
    /*USER*/
    public static final int GET_USER_FROM_UID = 120;
    public static final int GET_SMALL_USER = 121;
    public static final int GET_CURRENT_USER = 122;
    public static final int GET_NEARBY_USERS = 123;
    public static final int GET_NEW_USERS = 124;
    public static final int GET_USERINFO = 125;
    public static final int GET_USERDISTANCE = 126;
    public static final int GET_FAVS = 127;
    public static final int GET_IMAGES = 128;
    public static final int ADD_USER = 129;
    public static final int ADD_FAV = 130;
    public static final int REM_FAV = 131;
    public static final int UPDATE_USERINFO = 132;
    public static final int UPDATE_USER_FIELDS = 133;
    /*GEO_POINT*/
    public static final int UPDATE_LOCATION = 150;
    /*USER_INFO*/
    public static final int GET_ALL_ROOMS = 160;
    public static final int GET_USERS_OF_ROOMS_FOR_USER = 161;
    public static final int PROFILE_VIEW = 162;

    /*SERVER_CODES*/
    public static final int OKAY = 200;
    public static final int READY = 300;
    public static final int FAILURE = 500;

    /*SHARED_PREFERENCES*/
    public static final String SP_USERS = "users";
    public static final String SP_UID = "uid";
    public static final int DONE = 1000;

    private static WholeCurrentUser wholeCurrentUser;

    public static String readStringFromInptStrm(InputStream inputStream) {
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

    public static int getCurrentUserUID(SharedPreferences sharedPreferences) {
//        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        int uid = sharedPreferences.getInt(SP_UID, 0);
        //if user is not in Shared preference, create one with uid = 3
        if (uid == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SP_UID, 3);
            editor.apply();
        }
        return 3;
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

    /**
     * Send a message by sending a Room object containing last message.
     * @param roomJson
     * Room JsonObject.
     * @return
     * Updated Room object.
     */
    public static Room sendMessage(String roomJson){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(SEND_MESSAGE);
            byte[] messageBytes = roomJson.getBytes();
            outputStream.write(messageBytes.length);
            outputStream.write(messageBytes);
            return new Room(inputStream);
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
     * Get user Profile images from DB.
     * @param uid
     * User uid int.
     * @return
     * ArrayList of Image.
     */
    public static ArrayList<Image> getImages(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            socket = new Socket(HOST, 3000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_IMAGES);
            outputStream.write(uid);

            return Image.readImages(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
     * Get nearby Users as UserDistance.
     * @param uid
     * int current uid to get distance from.
     * @return
     * List of UserDistance.
     */
    public static List<UserDistance> getNearbyUsers(int uid){


        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            socket = new Socket(HOST, 3000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_NEARBY_USERS);
            outputStream.write(uid);

            return UserDistance.readUsers(inputStream);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
    public static boolean removeFavouriteUser(int currentUser, int favUser){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(REM_FAV);
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
    public static boolean updateUserInfo(int uid, UserInfo userInfo){
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

            int status = inputStream.read();
            return status == OKAY;
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
     * Gets 25 new users in DB, less if there are less users in DB. This function communicates with the DB through Socket.
     * @return
     * List of 25 UserDistance
     */
    public static List<UserDistance> getNewUsers(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_NEW_USERS);
            outputStream.write(uid);

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

    public static WholeCurrentUser getCurrentUser(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_CURRENT_USER);
            outputStream.write(uid);

            return new WholeCurrentUser(inputStream);
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

    public static List<SmallUser> getAllUsersOfRoomsForUser(int uid){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(GET_USERS_OF_ROOMS_FOR_USER);
            outputStream.write(uid);

            return SmallUser.getUsers(inputStream);
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

    public static void profileView(int uid, int otherUID){
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            socket = new Socket(HOST, PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(PROFILE_VIEW);
            outputStream.write(uid);
            outputStream.write(otherUID);

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
    }


}
