package com.chickeneater.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GameLogic mLogic = new GameLogic();
    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

    private boolean activePlayer = true; // True = Player 1, False = Player 2
    private Button player1MoveIndicator;
    private Button player2MoveIndicator;
    private int playerOneWin = 0, playerTwoWin = 0;
    private TextView player1Score;
    private TextView player2Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player1MoveIndicator = findViewById(R.id.player1moveindicator);
        player2MoveIndicator = findViewById(R.id.player2moveindicator);
        player1Score = findViewById(R.id.player1score);
        player2Score = findViewById(R.id.player2score);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridImageViews[i][j] = findViewById(gridImageIds[i][j]);
                final int x = i;
                final int y = j;
                gridImageViews[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activePlayer == true && mLogic.isEmpty(x, y)) {
                            mLogic.setNought(x, y);
                            activePlayer = false;
                            moveIndicatorChange();
                            drawBoard();
                        } else {
                            mLogic.setCross(x, y);
                            activePlayer = true;
                            moveIndicatorChange();
                            drawBoard();
                        }

                        if (mLogic.getWinner() == GameLogic.NOUGHT) {
                            Toast toastPlayerOne = Toast.makeText(getApplicationContext(),
                                    "Player 1 Won",
                                    Toast.LENGTH_LONG);
                            toastPlayerOne.show();
                            reset();
                            playerOneWin++;
                            player1Score.setText(String.valueOf(playerOneWin));
                        } else if (mLogic.getWinner() == GameLogic.CROSS) {
                            Toast toastPlayerTwo = Toast.makeText(getApplicationContext(),
                                    "Player 2 Won",
                                    Toast.LENGTH_LONG);

                            toastPlayerTwo.show();
                            reset();
                            playerTwoWin++;
                            player2Score.setText(String.valueOf(playerTwoWin));

                        }
                    }
                });
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

    private void moveIndicatorChange() {
        if (activePlayer == true) {
            player1MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player2MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            player2MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player1MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void reset() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                mLogic.setEmpty(x, y);
                drawBoard();
            }
        }
    }
}
