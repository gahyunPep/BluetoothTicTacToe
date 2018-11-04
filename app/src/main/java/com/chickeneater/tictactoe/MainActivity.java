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


        /*
        //When Cross player clicked
        mLogic.setCross(x, y);

        //When Nought player clicked
        mLogic.setNought(x, y);


        //You need to update UI of a button after that, so it will correspond to mLogic.get();
         */
    }
}
