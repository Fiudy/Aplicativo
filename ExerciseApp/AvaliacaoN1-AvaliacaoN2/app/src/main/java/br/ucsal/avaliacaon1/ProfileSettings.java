package br.ucsal.avaliacaon1;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;

    private int gender;
    private String userFullName;
    private String date;
    private float Weight;
    private float Height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditText userNameField = (EditText) findViewById(R.id.userName);
        EditText birthDate = (EditText) findViewById(R.id.Birthday);
        EditText weightNumber = (EditText) findViewById(R.id.Weight);
        EditText heightNumber = (EditText) findViewById(R.id.Height);
        RadioButton femaleButton = (RadioButton) findViewById(R.id.radioButtonFeminine);
         femaleButton.setOnCheckedChangeListener(this);
        RadioButton maleButton = (RadioButton) findViewById(R.id.radioButtonMasculine);
         maleButton.setOnCheckedChangeListener(this);
        Button saveButton = (Button) findViewById(R.id.saveButton);

        sharedPrefs=getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        userFullName = sharedPrefs.getString("userName", "");
        date = sharedPrefs.getString("Birthday", "");
        Weight = sharedPrefs.getFloat("Weight", 0);
        Height = sharedPrefs.getFloat("Height", 0);
        gender = sharedPrefs.getInt("Gender",0);

        userNameField.setText(userFullName);
        birthDate.setText(date);
        weightNumber.setText(String.valueOf(Weight));
        heightNumber.setText(String.valueOf(Height));
        if (gender == 1){
            femaleButton.setChecked(true);}
        if (gender == 2){
            maleButton.setChecked(true);}

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged (CompoundButton buttonView,  boolean isChecked) {
        if (buttonView.getId()==R.id.radioButtonFeminine && buttonView.isChecked()){
            gender= 1;}
        if (buttonView.getId()==R.id.radioButtonMasculine && buttonView.isChecked())
            gender= 2;
    }

    @Override
    public void onClick(View v) {

        sharedPrefsEditor=sharedPrefs.edit();

        EditText userNameField = (EditText) findViewById(R.id.userName);
        EditText birthDate = (EditText) findViewById(R.id.Birthday);
        EditText weightNumber = (EditText) findViewById(R.id.Weight);
        EditText heightNumber = (EditText) findViewById(R.id.Height);

        userFullName = userNameField.getText().toString();
        date = birthDate.getText().toString();
        Weight = Float.parseFloat(weightNumber.getText().toString());
        Height = Float.parseFloat(heightNumber.getText().toString());

        sharedPrefsEditor.putString("userName", userFullName);
        sharedPrefsEditor.putString("Birthday", date);
        sharedPrefsEditor.putFloat("Weight", Weight);
        sharedPrefsEditor.putFloat("Height", Height);
        sharedPrefsEditor.putInt("Gender", gender);
        sharedPrefsEditor.commit();
    }
}