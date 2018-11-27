package com.chickeneater.tictactoe;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chickeneater.tictactoe.game.ui.GameActivity;
import com.chickeneater.tictactoe.lobby.LobbyActivity;
import com.chickeneater.tictactoe.stats.Stats;
import com.chickeneater.tictactoe.stats.StatsActivity;
import com.google.android.material.textfield.TextInputEditText;

public class HomeScreenActivity extends AppCompatActivity {

    Button hostButton;
    Button statsButton;
    Button joinButton;
    Button deviceMultiplayerButton;
    TextInputEditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        hostButton = findViewById(R.id.hostBluetoothButton);
        statsButton = findViewById(R.id.statsButton);
        nameInput = findViewById(R.id.inputName);
        joinButton = findViewById(R.id.joinBluetoothButton);
        deviceMultiplayerButton = findViewById(R.id.deviceMultiplayerButton);
        final Stats stats = new Stats(this);
        nameInput.setText(stats.getName());

        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stats.setName(String.valueOf(nameInput.getText()));
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
                stats.setName(String.valueOf(nameInput.getText()));
                GameActivity.startSinglePlayerPlayerGame(v.getContext());
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stats.setName(String.valueOf(nameInput.getText()));
                startActivity(new Intent(HomeScreenActivity.this, LobbyActivity.class));
            }
        });
    }

}
