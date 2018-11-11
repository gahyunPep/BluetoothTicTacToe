package com.chickeneater.tictactoe;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel implements GameModel.OnGameEventListener {

    private GameModel mGame;
    public static final int DRAW = 3;
    private MutableLiveData<List<List<Integer>>> cellsListoflists = new MutableLiveData<>();
    private MutableLiveData<Integer> winnerState = new MutableLiveData<>();
    private MutableLiveData<Integer> player1Score = new MutableLiveData<>();
    private MutableLiveData<Integer> player2Score = new MutableLiveData<>();
    private int playerOneWin = 0, playerTwoWin = 0;

    public GameViewModel() {
       startGame();
       player1Score.setValue(playerOneWin);
       player2Score.setValue(playerTwoWin);
    }

    public MutableLiveData<Integer> getPlayer1Score() {
        return player1Score;
    }

    public MutableLiveData<Integer> getPlayer2Score() {
        return player2Score;
    }

    public LiveData<List<List<Integer>>> getCellsListoflists() {
        return cellsListoflists;
    }

    public LiveData<Integer> getWinnerState() {
        return winnerState;
    }

    @Override
    public void onMoveMade() {
        List<List<Integer>> updatedBoard = new ArrayList<>();

        for (int x = 0; x < 3; x++) {
            updatedBoard.add(new ArrayList<Integer>());
            for (int y = 0; y < 3; y++) {
                updatedBoard.get(x).add(mGame.get(x, y));
            }
        }

        cellsListoflists.setValue(updatedBoard);
    }

    @Override
    public void onPlayerWon(int winner) {

        switch (winner){
            case GameModel.CROSS:
                player1Score.setValue(++playerOneWin);
                break;
            case GameModel.NOUGHT:
                player2Score.setValue(++playerTwoWin);
                break;
        }
        winnerState.setValue(winner);

    }

    @Override
    public void onDraw() {
        winnerState.setValue(DRAW);
    }

    public void makeMove(int x, int y) {
        if (!mGame.isCellEmpty(x, y)) {
            return;
        }

        mGame.makeMove(x, y);

    }

    public boolean isCurrentPlayerCross() {
        return mGame.isCurrentPlayerCross();
    }

    public void startGame() {
        mGame = new GameModel();
        mGame.setOnGameEventListener(this);
        List<List<Integer>> initialBoard = new ArrayList<>();

        for (int x = 0; x < 3; x++) {
            initialBoard.add(new ArrayList<Integer>());
            for (int y = 0; y < 3; y++) {
                initialBoard.get(x).add(0);
            }
        }

        cellsListoflists.setValue(initialBoard);
    }
}
