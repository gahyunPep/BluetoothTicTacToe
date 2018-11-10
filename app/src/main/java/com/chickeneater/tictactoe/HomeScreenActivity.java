package com.chickeneater.tictactoe;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreenActivity extends AppCompatActivity {

    Button startButton;
    Button statsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        startButton = findViewById(R.id.startButton);
        statsButton = findViewById(R.id.statsButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = v.getContext().getSharedPreferences("Player", Context.MODE_PRIVATE);

                startActivity(new Intent(HomeScreenActivity.this, LobbyActivity.class));

            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, StatsActivity.class));
            }
        });
    }
}
