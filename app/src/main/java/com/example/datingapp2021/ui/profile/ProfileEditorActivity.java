package com.example.datingapp2021.ui.profile;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.UserInfo;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.SpecialViews.MultiSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.datingapp2021.logic.DB.SocketServer.SP_UID;

public class ProfileEditorActivity extends AppCompatActivity{
    private String user;
    private int image, height, weight, ethnicity, relationship, religion, orientation, reference, role;
    private ArrayList<UserInfo.STD> stdS;
    private ArrayList<UserInfo.Disability> disabilities;
    private String about;
    private Date birthDate;
    private EditText txtAbout;
    private NumberPicker pkrDay, pkrMonth, pkrYear, pkrHeight, pkrWeight;
    private Spinner spnrEthnicity, spnrReligion, spnrRelationship, spnrOrientation, spnrReference, spnrRole;
    private MultiSpinner spnrDisabilities, spnrSTDs;
    private ArrayAdapter<CharSequence> arrayAdapter;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        txtAbout = findViewById(R.id.about);
        pkrDay = findViewById(R.id.day);
        pkrMonth = findViewById(R.id.month);
        pkrYear = findViewById(R.id.year);
        pkrHeight = findViewById(R.id.height);
        pkrWeight = findViewById(R.id.weight);
        spnrEthnicity = findViewById(R.id.ethnicity);
        spnrOrientation = findViewById(R.id.orientation);
        spnrRelationship = findViewById(R.id.relationship);
        spnrReligion = findViewById(R.id.religion);
        spnrReference = findViewById(R.id.reference);
        spnrSTDs = findViewById(R.id.stds);
        spnrRole = findViewById(R.id.role);
        spnrDisabilities = findViewById(R.id.disabilities);
        progressBar = findViewById(R.id.progressBarInfo);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        new SetValuesTask().execute();

        pkrDay.setMinValue(1);
        pkrDay.setMaxValue(31);
        pkrMonth.setMinValue(1);
        pkrMonth.setMaxValue(12);
        pkrYear.setMinValue(1930);
        pkrYear.setMaxValue(2001);
        pkrYear.setValue(1995);
        pkrHeight.setMinValue(100);
        pkrHeight.setMaxValue(230);
        pkrWeight.setMinValue(40);
        pkrWeight.setMaxValue(200);

	    //Relationship Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.relationship, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrRelationship.setAdapter(arrayAdapter);

	    //Religion Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.religion, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrReligion.setAdapter(arrayAdapter);

	    //Orientation Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.orientation, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrOrientation.setAdapter(arrayAdapter);

        //Ethnicity Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.ethnicity, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrEthnicity.setAdapter(arrayAdapter);

        //Reference Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.reference, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrReference.setAdapter(arrayAdapter);

	    //STDs Spinner
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSTDs.setItems(R.array.stds, android.R.layout.simple_spinner_item, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                //TODO onCancel
            }

            @Override
            public void itemSelected(boolean[] selected) {
                stdS.clear();
                String[] arr = getResources().getStringArray(R.array.stds);
                for (int i = 0; i < arr.length; i++) {
                    if (selected[i]){
                        stdS.add(UserInfo.STD.getEnumValOf(i));
                    }
                }
            }
        });

        //Role Spinner
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.role, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrRole.setAdapter(arrayAdapter);

        //Disabilities Spinner
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDisabilities.setItems(R.array.disability, android.R.layout.simple_spinner_item, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                //TODO onCancel
            }

            @Override
            public void itemSelected(boolean[] selected) {
                disabilities.clear();
                String[] arr = getResources().getStringArray(R.array.disability);
                for (int i = 0; i < arr.length; i++) {
                    if (selected[i]){
                        disabilities.add(UserInfo.Disability.getEnumValOf(i));
                    }
                }
            }
        });

    }


    public void editProfile(View view) throws ParseException {

        SharedPreferences sharedPreferences = getSharedPreferences(SocketServer.SP_USERS, MODE_PRIVATE);
        int uid = sharedPreferences.getInt(SP_UID, 0);

        about = txtAbout.getText().toString();
        height = pkrHeight.getValue();
        weight = pkrWeight.getValue();
        birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(pkrDay.getValue() + "/" + pkrMonth.getValue() + "/" + pkrYear.getValue());
        relationship = spnrRelationship.getSelectedItemPosition();
        religion = spnrReligion.getSelectedItemPosition();
        orientation = spnrOrientation.getSelectedItemPosition();
        role = spnrRole.getSelectedItemPosition();
        ethnicity = spnrEthnicity.getSelectedItemPosition();
        reference = spnrReference.getSelectedItemPosition();

        UserInfo userInfo = new UserInfo(
		        uid,
                about,
                height,
                weight,
                birthDate,
                UserInfo.Relationship.getEnumValOf(relationship),
                UserInfo.Religion.getEnumValOf(religion),
                UserInfo.Orientation.getEnumValOf(orientation),
                UserInfo.Ethnicity.getEnumValOf(ethnicity),
                UserInfo.Reference.getEnumValOf(reference),
                stdS,
                UserInfo.Role.getEnumValOf(role),
                disabilities);
//        new EditProfileTask().execute(userInfo);
    }

//    private class EditProfileTask extends AsyncTask<UserInfo, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(UserInfo... userInfos) {
//            if(userInfos == null || userInfos.length != 1)
//                return null;
//
//            while(myService == null){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            myService.updateInfoFieldInUsersTokens(userInfos[0]);
//
//	        return null;
//        }
//    }
//
//    private class SetValuesTask extends AsyncTask<Void, Void, Map<Object, Object>>{
//
//        @Override
//        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Map<Object, Object> doInBackground(Void... voids) {
//            while(myService == null){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            Map<Object, Object> map = null;
//            if( myService.getCurrentUser().getInfo() != null){
//                map =  myService.getCurrentUser().getInfo().mapOfUserInfo();
//            }
//            return map;
//        }
//
//        @Override
//        protected void onPostExecute(Map<Object, Object> map) {
//
//            if(map != null) {
//                String[] split = ((String) map.get("birthdate")).split("/");
//
//                txtAbout.setText((String) map.get("about"));
//                pkrHeight.setValue((int) map.get("height"));
//                pkrWeight.setValue((int) map.get("weight"));
//
//                pkrDay.setValue(Integer.valueOf(split[0]));
//                pkrMonth.setValue(Integer.valueOf(split[1]));
//                pkrYear.setValue(Integer.valueOf(split[2]));
//
//                spnrEthnicity.setSelection(Integer.valueOf((String) map.get("ethnicity")));
//                spnrRelationship.setSelection(Integer.valueOf((String) map.get("relationship")));
//                spnrReference.setSelection(Integer.valueOf((String) map.get("reference")));
//                spnrReligion.setSelection(Integer.valueOf((String) map.get("religion")));
//                spnrOrientation.setSelection(Integer.valueOf((String) map.get("orientation")));
//                spnrSTDs.setSelection(Integer.valueOf((String) map.get("STDs")));
//                spnrRole.setSelection(Integer.valueOf((String) map.get("role")));
//            }
//            progressBar.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (isBound) {
//            unbindService(serviceConnection);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (isBound) {
//            unbindService(serviceConnection);
//        }
//    }
}
