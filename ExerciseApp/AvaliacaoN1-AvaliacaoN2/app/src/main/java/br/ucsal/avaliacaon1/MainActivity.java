package br.ucsal.avaliacaon1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnProfile =(Button) findViewById(R.id.profileButton);
        Button btnSettings = (Button) findViewById(R.id.settingsButton);
        Button btnAbout = (Button) findViewById(R.id.aboutButton);
        Button btnMonitor =(Button) findViewById(R.id.monitorButton);
        Button btnHistory =(Button) findViewById(R.id.historyButton);
        btnProfile.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnMonitor.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        if (id==R.id.profileButton) {
            Intent i=new Intent(this,ProfileSettings.class);
            startActivity(i);
        }
        if (id==R.id.settingsButton) {
            Intent i=new Intent(this,Settings.class);
            startActivity(i);
        }
        if (id==R.id.aboutButton) {
            Intent i=new Intent(this,AboutCredits.class);
            startActivity(i);
        }
        if (id==R.id.monitorButton) {
            Intent i=new Intent(this, Monitor.class);
            startActivity(i);
        }
        if (id==R.id.historyButton) {
            Intent i=new Intent(this,History.class);
            startActivity(i);

        }
    }
}