package com.example.android.firebasemessaging;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        RadioButton message = findViewById(R.id.radio_message);
        RadioButton favourite = findViewById(R.id.radio_favourite);
        if(getApplicationContext().getSharedPreferences
                ("settings", MODE_PRIVATE).getBoolean("VIEW_FAVOURITE", false)){
            favourite.setChecked(true);
            message.setChecked(false);
        }
        else{
            favourite.setChecked(false);
            message.setChecked(true);
        }

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("VIEW_FAVOURITE", false);
                editor.commit();
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("VIEW_FAVOURITE", true);
                editor.commit();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");

    }
}
