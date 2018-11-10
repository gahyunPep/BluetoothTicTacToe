package com.chickeneater.tictactoe;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GameActivity extends AppCompatActivity {
    public static final String DEVICE_ID = "device_id";
    private GameViewModel gameViewModel;
    private int playerOneWin = 0, playerTwoWin = 0;

    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

    private Button player1MoveIndicator;
    private Button player2MoveIndicator;

    private TextView player1Score;
    private TextView player2Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
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
                        onCellClicked(x, y);
                    }
                });
            }
        }

        gameViewModel.getCellsListoflists().observe(this, new Observer<List<List<Integer>>>() {
            @Override
            public void onChanged(List<List<Integer>> board) {
                displayBoard(board);
                moveIndicatorChange();
            }
        });
    }



    public void onPlayerWon(int winner) {
        //displayBoard(lists);
        switch (winner) {
            case GameModel.CROSS:
                playerTwoWin++;
                player1Score.setText(String.valueOf(playerTwoWin));
                winnerDialog("Player 1 Won");
                break;

            case GameModel.NOUGHT:
                playerOneWin++;
                player2Score.setText(String.valueOf(playerOneWin));
                winnerDialog("Player 2 Won");
                break;
        }
    }

    public void onDraw() {
        //displayBoard(lists);
        winnerDialog("It is a draw");
    }


    private void onCellClicked(int x, int y) {
        gameViewModel.makeMove(x, y);
    }

    private void displayBoard(List<List<Integer>> lists) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                switch (lists.get(x).get(y)) {
                    case GameModel.EMPTY:
                        gridImageViews[x][y].setImageDrawable(null);
                        break;
                    case GameModel.CROSS:
                        gridImageViews[x][y].setImageResource(R.drawable.ic_x);
                        break;
                    case GameModel.NOUGHT:
                        gridImageViews[x][y].setImageResource(R.drawable.ic_o);
                        break;
                }
            }
        }
    }

    private void moveIndicatorChange() {
        if (gameViewModel.isCurrentPlayerCross()) {
            player2MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player1MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            player1MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player2MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        }

    }


    private void winnerDialog(String winnerMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Result");

        alertDialogBuilder
                .setMessage(winnerMessage)
                .setCancelable(false)
                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gameViewModel.startGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GameActivity.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}