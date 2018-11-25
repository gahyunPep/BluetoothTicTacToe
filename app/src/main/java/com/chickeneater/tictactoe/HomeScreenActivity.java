package com.chickeneater.tictactoe;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chickeneater.tictactoe.lobby.LobbyActivity;
import com.google.android.material.textfield.TextInputEditText;

public class HomeScreenActivity extends AppCompatActivity {

    Button hostButton;
    Button statsButton;
    Button joinButton;
    Button deviceMultiplayerButton;
    TextInputEditText nameInput;
    public final static String NAME = "Name";
    public final static String WIN = "Win";
    public final static String LOSS = "Loss";
    public final static String DRAW = "Draw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        hostButton = findViewById(R.id.hostBluetoothButton);
        statsButton = findViewById(R.id.statsButton);
        nameInput = findViewById(R.id.inputName);
        joinButton = findViewById(R.id.joinBluetoothButton);
        deviceMultiplayerButton = findViewById(R.id.deviceMultiplayerButton);

        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNameToSharedPreferences(v);
                GameActivity.startMultiPlayerPlayerGame(v.getContext(), true);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, StatsActivity.class));
            }
        });

        deviceMultiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.startSinglePlayerPlayerGame(v.getContext());
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNameToSharedPreferences(v);
                startActivity(new Intent(HomeScreenActivity.this, LobbyActivity.class));
            }
        });
    }

    private void saveNameToSharedPreferences(View v) {
        String name = String.valueOf(nameInput.getText());
        SharedPreferences prefs = v.getContext().getSharedPreferences("Player", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(NAME, name);
        prefsEditor.apply();
    }
}
