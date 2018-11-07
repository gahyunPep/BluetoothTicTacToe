package com.chickeneater.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private GameLogic mLogic = new GameLogic();
    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridImageViews[i][j] = findViewById(gridImageIds[i][j]);
            }
        }

        drawBoard();
        /*
        //When Cross player clicked
        mLogic.setCross(x, y);

        //When Nought player clicked
        mLogic.setNought(x, y);


        //You need to update UI of a button after that, so it will correspond to mLogic.get();
         */
    }

    private void drawBoard() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                switch (mLogic.get(x, y)) {
                    case GameLogic.EMPTY:
                        gridImageViews[x][y].setImageDrawable(null);
                        break;
                    case GameLogic.CROSS:
                        gridImageViews[x][y].setImageResource(R.drawable.ic_x);
                        break;
                    case GameLogic.NOUGHT:
                        gridImageViews[x][y].setImageResource(R.drawable.ic_o);
                        break;
                }
            }
        }
    }
}
