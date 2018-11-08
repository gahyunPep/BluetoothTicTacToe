package com.chickeneater.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GameLogic mGame = new GameLogic();
    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

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
                        makeMovement(x, y);
                    }
                });
            }
        }

        drawBoard();
    }

    private void makeMovement(int x, int y) {
        if (!mGame.isCellEmpty(x, y)) {
            return;
        }

        mGame.makeMove(x, y);

        if (mGame.isEnded()) {
            showEndGameResult();
        }

        moveIndicatorChange();
        drawBoard();
    }

    private void showEndGameResult() {
        int winner = mGame.checkWinner();
        String toastMessage = "It is draw";
        switch (winner) {
            case GameLogic.CROSS:
                toastMessage = "Player 2 Won";
                playerTwoWin++;
                player2Score.setText(String.valueOf(playerTwoWin));
                break;

            case GameLogic.NOUGHT:
                toastMessage = "Player 1 Won";
                playerOneWin++;
                player1Score.setText(String.valueOf(playerOneWin));
                break;

            case GameLogic.DRAW:
                //TODO show Draw
                break;
        }
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        mGame.startNewGame();
    }

    private void drawBoard() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                switch (mGame.get(x, y)) {
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
        if (mGame.isCurrentPlayerCross()) {
            player1MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player2MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            player2MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player1MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

}
