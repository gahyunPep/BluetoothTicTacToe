package com.chickeneater.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {
    TextView winTxt, lossTxt, drawTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        winTxt = findViewById(R.id.textView4);
        lossTxt = findViewById(R.id.textView5);
        drawTxt = findViewById(R.id.textView6);


        SharedPreferences sharedPref = this.getSharedPreferences("Player",Context.MODE_PRIVATE);
        Stats mPlayerStats = new Stats();

        winTxt.setText(String.valueOf(mPlayerStats.getWin(sharedPref)));
        lossTxt.setText(String.valueOf(mPlayerStats.getLoss(sharedPref)));
        drawTxt.setText(String.valueOf(mPlayerStats.getDraw(sharedPref)));

    }
}
