package com.chickeneater.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private GameLogic mLogic = new GameLogic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        //Sample code to check cell state and change cell image.
        switch (mLogic.get(x, y)) {
            case GameLogic.EMPTY:
                break;

            case GameLogic.CROSS:
                break;

            case GameLogic.NOUGHT:
                break;
        }
        */
    }
}
