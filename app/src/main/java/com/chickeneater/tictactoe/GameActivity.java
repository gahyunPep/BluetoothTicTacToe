package com.chickeneater.tictactoe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chickeneater.tictactoe.game.GameBoard;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class GameActivity extends AppCompatActivity {
    public static final String GAME_MODE = "game_mode";
    public static final int MULTIPLAYER = 1;
    public static final int SINGLEPLAYER = 2;

    public static final String IS_HOST = "is_host";

    private GameViewModel gameViewModel;

    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

    private Button player1MoveIndicator;
    private Button player2MoveIndicator;

    private TextView player1Score;
    private TextView player2Score;

    private int gameMode;
    private boolean isHost;

    public static void startMultiPlayerPlayerGame(Context packageContext, boolean asHost) {
        Intent intent = new Intent(packageContext, GameActivity.class);
        intent.putExtra(GameActivity.GAME_MODE, GameActivity.MULTIPLAYER);
        intent.putExtra(GameActivity.IS_HOST, asHost);
        packageContext.startActivity(intent);
    }

    public static void startSinglePlayerPlayerGame(Context packageContext) {
        Intent intent = new Intent(packageContext, GameActivity.class);
        intent.putExtra(GameActivity.GAME_MODE, GameActivity.SINGLEPLAYER);
        packageContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameMode = getIntent().getIntExtra(GAME_MODE, SINGLEPLAYER);
        isHost = getIntent().getBooleanExtra(IS_HOST, true);
        ViewModelProvider.Factory factory = new GameViewModel.Factory(gameMode, isHost);
        gameViewModel = ViewModelProviders.of(this, factory).get(GameViewModel.class);
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

        gameViewModel.getWinnerState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer winner) {
                onPlayerWon(winner);
            }
        });

        gameViewModel.getPlayer1Score().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                player1Score.setText(String.valueOf(integer));
            }
        });

        gameViewModel.getPlayer2Score().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                player2Score.setText(String.valueOf(integer));
            }
        });
    }

    public void onPlayerWon(int winner) {

        switch (winner) {
            case GameBoard.CROSS:
                winnerDialog("Player 1 Won");
                break;

            case GameBoard.NOUGHT:
                winnerDialog("Player 2 Won");
                break;
            case GameViewModel.DRAW:
                winnerDialog("It is a draw");
                break;
        }
    }

    private void onCellClicked(int x, int y) {
        gameViewModel.makeMove(x, y);
    }

    private void displayBoard(List<List<Integer>> lists) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                switch (lists.get(x).get(y)) {
                    case GameBoard.EMPTY:
                        gridImageViews[x][y].setImageDrawable(null);
                        break;
                    case GameBoard.CROSS:
                        gridImageViews[x][y].setImageResource(R.drawable.ic_x);
                        break;
                    case GameBoard.NOUGHT:
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
                        gameViewModel.startGame(gameMode, isHost);
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
