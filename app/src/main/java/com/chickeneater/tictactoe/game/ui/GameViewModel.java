package com.chickeneater.tictactoe.game.ui;

import com.chickeneater.tictactoe.core.ui.Event;
import com.chickeneater.tictactoe.game.logic.Game;
import com.chickeneater.tictactoe.game.logic.GameBoard;
import com.chickeneater.tictactoe.game.logic.HotScreenGame;
import com.chickeneater.tictactoe.game.logic.MultiPlayerGame;
import com.chickeneater.tictactoe.game.logic.OnGameEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModel extends ViewModel implements OnGameEventListener {
    private Game mStrategy;
    private MutableLiveData<List<List<Integer>>> cellsListoflists = new MutableLiveData<>();
    //Use Event<Integer>
    private MutableLiveData<Boolean> locationPermissionDenied = new MutableLiveData<>();
    private MutableLiveData<Event<Integer>> winnerState = new MutableLiveData<>();
    private MutableLiveData<Integer> player1Score = new MutableLiveData<>();
    private MutableLiveData<Integer> player2Score = new MutableLiveData<>();
    private MutableLiveData<Boolean> displayWaitingProgressBar = new MutableLiveData<>();
    private MutableLiveData<String> opponentName = new MutableLiveData<>();
    private int playerOneWin = 0, playerTwoWin = 0;


    public GameViewModel(int gameMode, boolean isHost) {
        startGame(gameMode, isHost);
        player1Score.setValue(playerOneWin);
        player2Score.setValue(playerTwoWin);
        locationPermissionDenied.setValue(false);
    }

    @Override
    protected void onCleared() {
        if (mStrategy instanceof MultiPlayerGame) {
            ((MultiPlayerGame) mStrategy).clean();
        }
    }

    public void setLocationPermissionDenied(boolean denied) {
        locationPermissionDenied.setValue(denied);
    }

    public LiveData<Boolean> getLocationPermissionDenied() {
        return locationPermissionDenied;
    }

    public LiveData<Integer> getPlayer1Score() {
        return player1Score;
    }

    public LiveData<Integer> getPlayer2Score() {
        return player2Score;
    }

    public LiveData<List<List<Integer>>> getCellsListoflists() {
        return cellsListoflists;
    }

    public LiveData<Event<Integer>> getWinnerState() {
        return winnerState;
    }

    public LiveData<Boolean> getDisplayWaitingProgressBar() {
        return displayWaitingProgressBar;
    }

    public LiveData<String> getOpponentName() {
        return opponentName;
    }

    @Override
    public void onGameStarted(@Nullable String playerName) {
        if (playerName != null) {
            opponentName.setValue(playerName);
        }
        displayWaitingProgressBar.setValue(false);
    }

    @Override
    public void onMoveMade() {
        List<List<Integer>> updatedBoard = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            updatedBoard.add(new ArrayList<Integer>());
            for (int y = 0; y < 3; y++) {
                updatedBoard.get(x).add(mStrategy.get(x, y));
            }
        }

        cellsListoflists.setValue(updatedBoard);
    }

    @Override
    public void onPlayerWon(int winner) {

        switch (winner) {
            case GameBoard.CROSS:
                player1Score.setValue(++playerOneWin);
                break;
            case GameBoard.NOUGHT:
                player2Score.setValue(++playerTwoWin);
                break;
        }
        winnerState.setValue(new Event<>(winner));

    }

    @Override
    public void onDraw() {
        winnerState.setValue(new Event<>(GameBoard.DRAW));
    }

    public void makeMove(int x, int y) {
        if (mStrategy.canMakeMove()) {
            mStrategy.makeMove(x, y);
        }
    }

    public boolean isCurrentPlayerCross() {
        return mStrategy.isCurrentPlayerCross();
    }

    public void startGame(int gameMode, boolean isHost) {

        if (gameMode == GameActivity.SINGLEPLAYER) {
            displayWaitingProgressBar.setValue(false);
            mStrategy = new HotScreenGame("Player 2", this);
        } else if (gameMode == GameActivity.MULTIPLAYER) {
            displayWaitingProgressBar.setValue(true);
            mStrategy = new MultiPlayerGame(this, isHost);
        }
        List<List<Integer>> initialBoard = new ArrayList<>();

        for (int x = 0; x < 3; x++) {
            initialBoard.add(new ArrayList<Integer>());
            for (int y = 0; y < 3; y++) {
                initialBoard.get(x).add(0);
            }
        }

        cellsListoflists.setValue(initialBoard);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final int mGameMode;
        private final boolean mIsHost;

        public Factory(int gameMode, boolean isHost) {
            mGameMode = gameMode;
            mIsHost = isHost;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new GameViewModel(mGameMode, mIsHost);
        }
    }

}
