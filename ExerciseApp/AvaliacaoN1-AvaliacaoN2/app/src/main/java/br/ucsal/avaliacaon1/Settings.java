package br.ucsal.avaliacaon1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    private int exercise;
    private int unity;
    private int orientation;
    private int mapType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        RadioGroup exerciseGroup, unityGroup, mapTypeGroup, orientationGroup;

        exerciseGroup = (RadioGroup) findViewById(R.id.exerciseGroup);
        unityGroup = (RadioGroup) findViewById(R.id.velocityGroup);
        mapTypeGroup = (RadioGroup) findViewById(R.id.mapTypeGroup);
        orientationGroup = (RadioGroup) findViewById(R.id.orientationGroup);

        sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        exercise = sharedPrefs.getInt("Exercise", 0);
        unity = sharedPrefs.getInt("Unity", 0);
        mapType = sharedPrefs.getInt("MapType",0);
        orientation = sharedPrefs.getInt("Orientation",0);

        exerciseGroup.check(exercise);
        unityGroup.check(unity);
        mapTypeGroup.check(mapType);
        orientationGroup.check(orientation);

        exerciseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                exercise = checkedId;
            }
        });

        unityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unity = checkedId;
            }
        });

        mapTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mapType = checkedId;
            }
        });

        orientationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                orientation = checkedId;
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        sharedPrefsEditor=sharedPrefs.edit();
        if (sharedPrefsEditor!=null) {
            sharedPrefsEditor.putInt("Exercise", exercise);
            sharedPrefsEditor.putInt("Unity", unity);
            sharedPrefsEditor.putInt("MapType", mapType);
            sharedPrefsEditor.putInt("Orientation", orientation);
            sharedPrefsEditor.commit();
        }
    }
}