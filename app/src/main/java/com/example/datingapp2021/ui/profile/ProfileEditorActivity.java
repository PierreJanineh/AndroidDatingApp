package com.example.datingapp2021.ui.profile;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.datingapp2021.R;
import com.example.datingapp2021.logic.Classes.UserDistance;
import com.example.datingapp2021.logic.Classes.UserInfo;
import com.example.datingapp2021.logic.Classes.WholeCurrentUser;
import com.example.datingapp2021.logic.DB.SocketServer;
import com.example.datingapp2021.ui.SpecialViews.MultiSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import static com.example.datingapp2021.logic.DB.SocketServer.SP_UID;
import static com.example.datingapp2021.logic.DB.SocketServer.SP_USERS;

public class ProfileEditorActivity extends AppCompatActivity{
    private ProfileViewModel profileViewModel;

    private String user;
    private int image, height, weight, ethnicity, relationship, religion, orientation, reference, role, disability;
    private ArrayList<UserInfo.STD> stdS = new ArrayList<>();
    private ArrayList<UserInfo.Disability> disabilities = new ArrayList<>();
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

        profileViewModel();

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
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSTDs.setItems(R.array.stds, android.R.layout.simple_spinner_item, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                //TODO onCancel
            }

            @Override
            public void itemSelected(boolean[] selected) {
                if (stdS.size() > 0){
                    stdS.clear();
                }
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
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrDisabilities.setItems(R.array.disability, android.R.layout.simple_spinner_item, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                //TODO onCancel
            }

            @Override
            public void itemSelected(boolean[] selected) {
                if (disabilities.size() > 0){
                    disabilities.clear();
                }
                String[] arr = getResources().getStringArray(R.array.disability);
                for (int i = 0; i < arr.length; i++) {
                    if (selected[i]){
                        disabilities.add(UserInfo.Disability.getEnumValOf(i));
                    }
                }
            }
        });

        profileViewModel.userEditSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(ProfileEditorActivity.this, aBoolean ? "Updated profile info" : "update error", Toast.LENGTH_LONG).show();
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
        disability = spnrDisabilities.getSelectedItemPosition();

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

        profileViewModel.editCurrentUser(sharedPreferences, userInfo);
    }


    /**
     * Creates and defines profileViewModel variable.
     * And defines a listener for onChange() situation for getUserDistanceObject() method.
     */
    private void profileViewModel() {
        profileViewModel = new ProfileViewModel(new ProfileRepository(Executors.newSingleThreadExecutor(), new Handler()));
        profileViewModel.getUserCurrentUserObject(getSharedPreferences(SP_USERS, MODE_PRIVATE)).observe(this, new Observer<WholeCurrentUser>() {
            @Override
            public void onChanged(WholeCurrentUser user) {
                if (user != null) {
                    UserInfo info = user.getInfo();
                    txtAbout.setText(info.getAbout());
                    pkrHeight.setValue(info.getHeight());
                    pkrWeight.setValue(info.getWeight());

                    pkrDay.setValue(info.getBirthYearMonthDay(UserInfo.DAY));
                    pkrMonth.setValue(info.getBirthYearMonthDay(UserInfo.MONTH));
                    pkrYear.setValue(info.getBirthYearMonthDay(UserInfo.YEAR));

                    spnrEthnicity.setSelection(UserInfo.Ethnicity.getValOf(info.getEthnicity()));
                    spnrRelationship.setSelection(UserInfo.Relationship.getValOf(info.getRelationship()));
                    spnrReference.setSelection(UserInfo.Reference.getValOf(info.getReference()));
                    spnrReligion.setSelection(UserInfo.Religion.getValOf(info.getReligion()));
                    spnrOrientation.setSelection(UserInfo.Orientation.getValOf(info.getOrientation()));
                    spnrSTDs.setItemsSelected(UserInfo.STD.getArrayOfIntsFrom(info.getStDs().toArray(new UserInfo.STD[info.getStDs().size()])));
                    spnrRole.setSelection(UserInfo.Role.getValOf(info.getRole()));
                    spnrDisabilities.setItemsSelected(UserInfo.Disability.getArrayOfIntsFrom(info.getDisabilities().toArray(new UserInfo.Disability[info.getDisabilities().size()])));
                }
            }
        });
    }
}
