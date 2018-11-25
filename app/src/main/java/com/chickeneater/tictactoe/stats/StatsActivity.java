package com.chickeneater.tictactoe.stats;

import android.os.Bundle;
import android.widget.TextView;

import com.chickeneater.tictactoe.R;

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


        Stats stats = new Stats(this);
        winTxt.setText(String.valueOf(stats.getWin()));
        lossTxt.setText(String.valueOf(stats.getLoss()));
        drawTxt.setText(String.valueOf(stats.getDraw()));

    }
}
