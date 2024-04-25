package br.ucsal.avaliacaon1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutCredits extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Button btnBack = (Button) findViewById(R.id.goBackButton);
        btnBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.goBackButton) {
            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);
        }
    }
}
