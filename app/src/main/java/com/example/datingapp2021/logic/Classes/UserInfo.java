package com.example.datingapp2021.logic.Classes;

import com.example.datingapp2021.logic.DB.SocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.datingapp2021.logic.Classes.WholeUser.INFO;
import static java.util.Calendar.DAY_OF_MONTH;

public class UserInfo {

    /*
    UserInfo class in Server contains:
                  1.  uid            int
                  2.  about          String
                  3.  wight          GeoPoint
                  4.  height         String
                  5.  birthDate      ArrayList<Integer>
                  6.  relationship   ArrayList<Room>
                  7.  religion       UserInfo
                  8.  orientation    Orientation(Enum)
                  9.  ethnicity      Ethnicity(Enum)
                  10. stds           STD[](Enums)
                  11. role           Role(Enum)
                  12. disabilities   Disability[](Enums)
                  13. notInDB        boolean
     */


    public static final String ABOUT = "about";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String BIRTH_DATE = "birthDate";
    public static final String RELATIONSHIP = "relationship";
    public static final String RELIGION = "religion";
    public static final String ORIENTATION = "orientation";
    public static final String ETHNICITY = "ethnicity";
    public static final String REFERENCE = "reference";
    public static final String STDS = "stDs";
    public static final String ROLE = "role";
    public static final String DISABILITIES = "disabilities";
    public static final String UID = "uid";
    public static final int YEAR = 100;
    public static final int MONTH = 200;
    public static final int DAY = 300;
    private int uid;
    private String about;
    private int weight, height;
    private Date birthDate;
    private Relationship relationship;
    private Religion religion;
    private Orientation orientation;
    private Ethnicity ethnicity;
    private Reference reference;
    private ArrayList<STD> stDs;
    private Role role;
    private ArrayList<Disability> disabilities;

    /**
     * Declared whether the Object contains info from DB or data needs to be requested.
     */
    public boolean isUploaded = false;

    public UserInfo(int uid) {
        this.uid = uid;
        this.isUploaded = false;
    }

    /**
     * Creates a UserInfo object from JsonObject.
     * @param jsonObject
     * Userinfo jsonObject.
     * JsonObject{}
     */
    public UserInfo(JsonObject jsonObject) throws ParseException {
        JsonObject object = jsonObject.getAsJsonObject(INFO);
        this.about = object.get(ABOUT).getAsString();
        this.weight = object.get(WEIGHT).getAsInt();
        this.height = object.get(HEIGHT).getAsInt();
//        this.birthDate = java.sql.Date.valueOf(object.get(BIRTH_DATE).getAsString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        this.birthDate = simpleDateFormat.parse(object.get(BIRTH_DATE).getAsString());
        this.relationship = Relationship.valueOf(object.get(RELATIONSHIP).getAsString());
        this.religion = Religion.valueOf(object.get(RELIGION).getAsString());
        this.orientation = Orientation.valueOf(object.get(ORIENTATION).getAsString());
        this.ethnicity = Ethnicity.valueOf(object.get(ETHNICITY).getAsString());
        this.reference = Reference.valueOf(object.get(REFERENCE).getAsString());
        JsonArray jsonArray;
        String[] arr;

        if (!object.get(STDS).isJsonNull()){
            jsonArray = object.get(STDS).getAsJsonArray();
            arr = new String[jsonArray.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = jsonArray.get(i).getAsString();
            }
            this.stDs = STD.getArrayOfEnumsFrom(arr);
        }else {
            this.stDs = new ArrayList<>();
        }

        this.role = Role.valueOf(object.get(ROLE).getAsString());
        if (!object.get(DISABILITIES).isJsonNull()){
            jsonArray = object.get(DISABILITIES).getAsJsonArray();
            arr = new String[jsonArray.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = jsonArray.get(i).getAsString();
            }
            this.disabilities = Disability.getArrayOfEnumsFrom(arr);
        }else {
            this.disabilities = new ArrayList<>(0);
        }
    }

    public UserInfo(int uid, String about, int weight, int height, Date birthDate, Relationship relationship, Religion religion, Orientation orientation, Ethnicity ethnicity, Reference reference, ArrayList<STD> stDs, Role role, ArrayList<Disability> disabilities) {
        this.uid = uid;
        this.about = about;
        this.weight = weight;
        this.height = height;
        this.birthDate = birthDate;
        this.relationship = relationship;
        this.religion = religion;
        this.orientation = orientation;
        this.ethnicity = ethnicity;
        this.reference = reference;
        this.stDs = stDs;
        this.role = role;
        this.disabilities = disabilities;
        this.isUploaded = true;
    }

    public UserInfo(InputStream inputStream) throws IOException, ParseException {
        String s = SocketServer.readStringFromInptStrm(inputStream);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(s).getAsJsonObject();
        UserInfo userInfo = new UserInfo(object);
        this.about = userInfo.getAbout();
        this.weight = userInfo.getWeight();
        this.height = userInfo.getHeight();
        this.birthDate = userInfo.getBirthDate();
        this.relationship = userInfo.getRelationship();
        this.religion = userInfo.getReligion();
        this.orientation = userInfo.getOrientation();
        this.ethnicity = userInfo.getEthnicity();
        this.reference = userInfo.getReference();
        this.stDs = userInfo.getStDs();
        this.role = userInfo.getRole();
        this.disabilities = userInfo.getDisabilities();
        this.isUploaded = true;
    }

    /**
     * Get UserInfo object from Json String.
     * @param json
     * Json String.
     * @return
     * UserInfo Object.
     */
    private static UserInfo getUserInfoFromJson(String json){
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        return gson.fromJson(json, UserInfo.class);
    }

    /**
     * Get age of user by years.
     * @return
     * int of years of age.
     */
    public int getAge(){

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(new Date().getTime());
        int todaysYear = today.get(Calendar.YEAR);
        int todaysMonth = today.get(Calendar.MONTH);
        int todaysDay = today.get(DAY_OF_MONTH);
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(birthDate.getTime());
        int birthYear = birth.get(Calendar.YEAR);
        int birthMonth = birth.get(Calendar.MONTH);
        int birthDay = birth.get(DAY_OF_MONTH);

        int age = (todaysYear-birthYear)-1;
        if (todaysMonth > birthMonth){
            age++;
        } else if (todaysMonth == birthMonth){
            if (todaysDay >= birthDay){
                age++;
            }
        }

        return age;
    }

    /**
     * Get Year or Month or Day in String from birthdate.
     * @param yearMonthDay
     * 100-YEAR | 200-MONTH | 300-DAY
     * @return
     * String Day or Month or Year
     */
    public int getBirthYearMonthDay(int yearMonthDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthDate);
        switch (yearMonthDay){
            case YEAR:
                return calendar.get(Calendar.YEAR);
            case MONTH:
                return calendar.get(Calendar.MONTH)+1;
            case DAY:
                return calendar.get(DAY_OF_MONTH);
        }
        return 0;
    }

    /**
     * Write Json object to OutputStream.
     * @param outputStream
     * OutputStream to write to.
     */
    public void write(OutputStream outputStream) throws IOException {

        byte[] bytes = this.toString().getBytes();
        outputStream.write(bytes.length);
        outputStream.write(bytes);
    }

    /**
     * Override toString function to create a json object.
     * @return
     * String: json object of Class UserInfo.
     */
    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        return builder.create().toJson(this);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Religion getReligion() {
        return religion;
    }

    public void setReligion(Religion religion) {
        this.religion = religion;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public ArrayList<STD> getStDs() {
        return stDs;
    }

    public void setStDs(ArrayList<STD> stDs) {
        this.stDs = stDs;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ArrayList<Disability> getDisabilities() {
        return disabilities;
    }

    public void setDisabilities(ArrayList<Disability> disabilities) {
        this.disabilities = disabilities;
    }

    public enum Relationship {
        NOT_DEFINED,
        IN_RELATIONSHIP_SOLO,
        IN_RELATIONSHIP_COUPLE,
        IN_AN_OPEN_RELATIONSHIP,
        SINGLE,
        DIVORCED,
        WIDOWED,
        ITS_COPMLICATED;

        static public int getValOf(Relationship relationship){
            switch (relationship){
                case IN_RELATIONSHIP_SOLO:
                    return 1;
                case IN_RELATIONSHIP_COUPLE:
                    return 2;
                case IN_AN_OPEN_RELATIONSHIP:
                    return 3;
                case SINGLE:
                    return 4;
                case DIVORCED:
                    return 5;
                case WIDOWED:
                    return 6;
                case ITS_COPMLICATED:
                    return 7;
                default:
                    return 0;
            }
        }
        static public Relationship getEnumValOf(int code){
            switch (code){
                case 1:
                    return IN_RELATIONSHIP_SOLO;
                case 2:
                    return IN_RELATIONSHIP_COUPLE;
                case 3:
                    return IN_AN_OPEN_RELATIONSHIP;
                case 4:
                    return SINGLE;
                case 5:
                    return DIVORCED;
                case 6:
                    return WIDOWED;
                case 7:
                    return ITS_COPMLICATED;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum Religion {
        NOT_DEFINED,
        CHRISTIAN,
        MUSLIM,
        JEW,
        ATHEIST;

        static public int getValOf(Religion enumObj){
            switch (enumObj){
                case CHRISTIAN:
                    return 1;
                case MUSLIM:
                    return 2;
                case JEW:
                    return 3;
                case ATHEIST:
                    return 4;
                default:
                    return 0;
            }
        }
        static public Religion getEnumValOf(int code){
            switch (code){
                case 1:
                    return CHRISTIAN;
                case 2:
                    return MUSLIM;
                case 3:
                    return JEW;
                case 4:
                    return ATHEIST;
                default:
                    return NOT_DEFINED;
            }
        }

    }

    public enum Orientation {
        NOT_DEFINED,
        STRAIGHT,
        GAY,
        BISEXUAL,
        TRANSEXUAL,
        TRANSGENDER,
        PANSEXUAL;

        static public int getValOf(Orientation enumObj){
            switch (enumObj){
                case STRAIGHT:
                    return 1;
                case GAY:
                    return 2;
                case BISEXUAL:
                    return 3;
                case TRANSEXUAL:
                    return 4;
                case TRANSGENDER:
                    return 5;
                case PANSEXUAL:
                    return 6;
                default:
                    return 0;
            }
        }
        static public Orientation getEnumValOf(int code){
            switch (code){
                case 1:
                    return STRAIGHT;
                case 2:
                    return GAY;
                case 3:
                    return BISEXUAL;
                case 4:
                    return TRANSEXUAL;
                case 5:
                    return TRANSGENDER;
                case 6:
                    return PANSEXUAL;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum Ethnicity {
        NOT_DEFINED,
        MIDDLE_EASTERN,
        NATIVE_AMERICAN,
        AFRICAN_AMERICAN,
        EUROPEAN,
        LATINO;

        static public int getValOf(Ethnicity enumObj){
            switch (enumObj){
                case MIDDLE_EASTERN:
                    return 1;
                case NATIVE_AMERICAN:
                    return 2;
                case AFRICAN_AMERICAN:
                    return 3;
                case EUROPEAN:
                    return 4;
                case LATINO:
                    return 5;
                default:
                    return 0;
            }
        }
        static public Ethnicity getEnumValOf(int code){
            switch (code){
                case 1:
                    return MIDDLE_EASTERN;
                case 2:
                    return NATIVE_AMERICAN;
                case 3:
                    return AFRICAN_AMERICAN;
                case 4:
                    return EUROPEAN;
                case 5:
                    return LATINO;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum Reference {
        NOT_DEFINED,
        HE,
        SHE,
        HE_SHE,
        THEY,
        OTHER;

        static public int getValOf(Reference enumObj){
            switch (enumObj){
                case HE:
                    return 1;
                case SHE:
                    return 2;
                case HE_SHE:
                    return 3;
                case THEY:
                    return 4;
                case OTHER:
                    return 5;
                default:
                    return 0;
            }
        }
        static public Reference getEnumValOf(int code){
            switch (code){
                case 1:
                    return HE;
                case 2:
                    return SHE;
                case 3:
                    return HE_SHE;
                case 4:
                    return THEY;
                case 5:
                    return OTHER;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum STD {
        NOT_DEFINED,
        NO_STDS,
        HIV_POS,
        HIV_NEG;

        static public STD[] getEnumsFrom(int[] codes) {
            STD[] stds = new STD[codes.length];
            for (int i = 0; i < codes.length; i++) {
                stds[i] = getEnumValOf(codes[i]);
            }
            return stds;
        }

        static public ArrayList<STD> getArrayOfEnumsFrom(int[] codes) {
            ArrayList<STD> stds = new ArrayList<>();
            for (int code : codes) {
                stds.add(getEnumValOf(code));
            }
            return stds;
        }

        static public ArrayList<STD> getArrayOfEnumsFrom(String[] values) {
            ArrayList<STD> stds = new ArrayList<>();
            for (String val : values) {
                stds.add(valueOf(val));
            }
            return stds;
        }

        static public ArrayList<Integer> getArrayOfIntsFrom(STD[] values) {
            ArrayList<Integer> stds = new ArrayList<>();
            for (STD val : values) {
                stds.add(getValOf(val));
            }
            return stds;
        }

        static public int getValOf(STD enumObj){
            switch (enumObj){
                case NO_STDS:
                    return 1;
                case HIV_POS:
                    return 2;
                case HIV_NEG:
                    return 3;
                default:
                    return 0;
            }
        }
        static public STD getEnumValOf(int code){
            switch (code){
                case 1:
                    return NO_STDS;
                case 2:
                    return HIV_POS;
                case 3:
                    return HIV_NEG;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum Role {
        NOT_DEFINED,
        TOP,
        BOTTOM,
        VERSATILE,
        VERSATILE_TOP,
        VERSATILE_BOTTOM;

        static public int getValOf(Role enumObj){
            switch (enumObj){
                case TOP:
                    return 1;
                case BOTTOM:
                    return 2;
                case VERSATILE:
                    return 3;
                case VERSATILE_TOP:
                    return 4;
                case VERSATILE_BOTTOM:
                    return 5;
                default:
                    return 0;
            }
        }
        static public Role getEnumValOf(int code){
            switch (code){
                case 1:
                    return TOP;
                case 2:
                    return BOTTOM;
                case 3:
                    return VERSATILE;
                case 4:
                    return VERSATILE_TOP;
                case 5:
                    return VERSATILE_BOTTOM;
                default:
                    return NOT_DEFINED;
            }
        }
    }

    public enum Disability {
        NOT_DEFINED, BLIND;

        static public Disability[] getEnumsFrom(int[] codes) {
            Disability[] disabilities = new Disability[codes.length];
            for (int i = 0; i < codes.length; i++) {
                disabilities[i] = getEnumValOf(codes[i]);
            }
            return disabilities;
        }

        static public ArrayList<Disability> getArrayOfEnumsFrom(int[] codes) {
            ArrayList<Disability> disabilities = new ArrayList<>();
            for (int code : codes) {
                disabilities.add(getEnumValOf(code));
            }
            return disabilities;
        }

        static public ArrayList<Integer> getArrayOfIntsFrom(Disability[] values) {
            ArrayList<Integer> stds = new ArrayList<>();
            for (Disability val : values) {
                stds.add(getValOf(val));
            }
            return stds;
        }

        static public ArrayList<Disability> getArrayOfEnumsFrom(String[] values) {
            ArrayList<Disability> disabilities = new ArrayList<>();
            for (String val : values) {
                disabilities.add(valueOf(val));
            }
            return disabilities;
        }

        static public int getValOf(Disability enumObj){
            switch (enumObj){
                case BLIND:
                    return 1;
                default:
                    return 0;
            }
        }
        static public Disability getEnumValOf(int code){
            switch (code){
                case 1:
                    return BLIND;
                default:
                    return NOT_DEFINED;
            }
        }
    }

}
