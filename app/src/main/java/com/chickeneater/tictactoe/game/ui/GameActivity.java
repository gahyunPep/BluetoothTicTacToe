package com.chickeneater.tictactoe.game.ui;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chickeneater.tictactoe.R;
import com.chickeneater.tictactoe.stats.Stats;
import com.chickeneater.tictactoe.core.ui.EventObserver;
import com.chickeneater.tictactoe.game.logic.GameBoard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.becameDiscoverable;
import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.dismissSafely;
import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.isLocationPermissionGranted;
import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.locationPermissionRejectedDialog;
import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.requestLocationPermissionIfNeed;


public class GameActivity extends AppCompatActivity {
    public static final String GAME_MODE = "game_mode";
    public static final int MULTIPLAYER = 1;
    public static final int SINGLEPLAYER = 2;

    public static final String IS_HOST = "is_host";
    public static final String PLAYER_NAME = "player_name";

    private GameViewModel gameViewModel;

    private int[][] gridImageIds = {{R.id.position_0_0, R.id.position_0_1, R.id.position_0_2},
            {R.id.position_1_0, R.id.position_1_1, R.id.position_1_2},
            {R.id.position_2_0, R.id.position_2_1, R.id.position_2_2}};

    private ImageView[][] gridImageViews = new ImageView[3][3];

    private Button player1MoveIndicator;
    private Button player2MoveIndicator;

    private TextView player1Score;
    private TextView player2Score;

    private TextView waitingTextView;
    private ProgressBar waitingProgressBar;

    private TextView player1NameTextView;
    private TextView player2NameTextView;


    private int mGameMode;
    private boolean mIsHost;

    private AlertDialog mLocationPermissionDialog = null;

    public static void startMultiPlayerPlayerGame(Context packageContext, boolean asHost){
        startMultiPlayerPlayerGame(packageContext, asHost, null);
    }

    public static void startMultiPlayerPlayerGame(Context packageContext, boolean asHost, @Nullable String name) {
        Intent intent = new Intent(packageContext, GameActivity.class);
        intent.putExtra(GameActivity.GAME_MODE, GameActivity.MULTIPLAYER);
        intent.putExtra(GameActivity.IS_HOST, asHost);
        intent.putExtra(GameActivity.PLAYER_NAME, name);
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
        mGameMode = getIntent().getIntExtra(GAME_MODE, SINGLEPLAYER);
        mIsHost = getIntent().getBooleanExtra(IS_HOST, false);
        if (mGameMode == MULTIPLAYER && mIsHost) {
            if (savedInstanceState == null && requestLocationPermissionIfNeed(this)) {
                becameDiscoverable(this);
            }
        }

        ViewModelProvider.Factory factory = new GameViewModel.Factory(mGameMode, mIsHost);
        gameViewModel = ViewModelProviders.of(this, factory).get(GameViewModel.class);
        player1MoveIndicator = findViewById(R.id.player1moveindicator);
        player2MoveIndicator = findViewById(R.id.player2moveindicator);
        player1Score = findViewById(R.id.player1score);
        player2Score = findViewById(R.id.player2score);
        waitingTextView = findViewById(R.id.waitingTextView);
        waitingProgressBar = findViewById(R.id.waitingProgressBar);
        player1NameTextView = findViewById(R.id.txtView_Player1);
        player2NameTextView = findViewById(R.id.txtView_Player2);

        Stats stats = new Stats(this);
        String playerName = stats.getName();
        if (mGameMode == MULTIPLAYER) {
            String otherPlayerName = getIntent().getStringExtra(GameActivity.PLAYER_NAME);
            otherPlayerName = otherPlayerName == null ? "" : otherPlayerName;
            if (mIsHost) {
                player1NameTextView.setText(playerName);
                player2NameTextView.setText(otherPlayerName);
            } else {
                player2NameTextView.setText(playerName);
                player1NameTextView.setText(otherPlayerName);
            }
        } else if (mGameMode == SINGLEPLAYER) {
            player1NameTextView.setText(playerName);
        }

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

        gameViewModel.getWinnerState().observe(this, new EventObserver<Integer>() {
            @Override
            public void onEventHappened(Integer winner) {
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

        gameViewModel.getLocationPermissionDenied().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    mLocationPermissionDialog = locationPermissionRejectedDialog(GameActivity.this);
                }
            }
        });

        gameViewModel.getDisplayWaitingProgressBar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    waitingTextView.setVisibility(View.VISIBLE);
                    waitingProgressBar.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            gridImageViews[i][j].setVisibility(View.GONE);
                        }
                    }
                } else {
                    waitingTextView.setVisibility(View.INVISIBLE);
                    waitingProgressBar.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            gridImageViews[i][j].setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        gameViewModel.getOpponentName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (mGameMode == MULTIPLAYER) {
                    if (mIsHost) {
                        player2NameTextView.setText(s);
                    } else {
                        player1NameTextView.setText(s);
                    }
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //noinspection ConstantConditions
        if (gameViewModel.getLocationPermissionDenied().getValue()) {
            dismissSafely(mLocationPermissionDialog);
            mLocationPermissionDialog = locationPermissionRejectedDialog(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissSafely(mLocationPermissionDialog);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isLocationPermissionGranted(requestCode, grantResults)) {
            becameDiscoverable(this);
            gameViewModel.setLocationPermissionDenied(false);
        } else {
            gameViewModel.setLocationPermissionDenied(true);
        }
    }

    public void onPlayerWon(int winner) {

        switch (winner) {
            case GameBoard.CROSS:
                String player1Name = player1NameTextView.getText().toString();
                winnerDialog(getString(R.string.winner_message,  player1Name));
                break;
            case GameBoard.NOUGHT:
                String player2Name = player2NameTextView.getText().toString();
                winnerDialog(getString(R.string.winner_message, player2Name));
                break;
            case GameBoard.DRAW:
                winnerDialog(getString(R.string.game_draw));
                break;
        }
        saveStats(winner);
    }

    private void saveStats(int winner) {
        if (mGameMode != MULTIPLAYER) {
            return;
        }

        Stats stats = new Stats(this);
        switch (winner) {
            case GameBoard.CROSS:
                if (mIsHost) {
                    stats.updateWin();
                } else {
                    stats.updateLoss();
                }
                break;
            case GameBoard.NOUGHT:
                if (mIsHost) {
                    stats.updateLoss();
                } else {
                    stats.updateWin();
                }
                break;
            case GameBoard.DRAW:
                stats.updateDraw();
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
            player1MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player2MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            player2MoveIndicator.setBackgroundColor(getResources().getColor(R.color.colorMoveIndicator));
            player1MoveIndicator.setBackgroundResource(android.R.drawable.btn_default);
        }

    }


    private void winnerDialog(String winnerMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle(getString(R.string.result));

        alertDialogBuilder
                .setMessage(winnerMessage)
                .setCancelable(false);

        if (mGameMode == SINGLEPLAYER) {
            alertDialogBuilder.setPositiveButton(getString(R.string.play_again), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    gameViewModel.startGame(mGameMode, mIsHost);
                }
            });
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GameActivity.this.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
