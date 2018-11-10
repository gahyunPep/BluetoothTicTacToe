package com.chickeneater.tictactoe;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel implements GameModel.OnGameEventListener {

    private GameModel mGame;
    private MutableLiveData<List<List<Integer>>> cellsListoflists = new MutableLiveData<>();
    private int playerOneWin = 0, playerTwoWin = 0;

    public GameViewModel() {
       startGame();
    }

    public LiveData<List<List<Integer>>> getCellsListoflists() {

        return cellsListoflists;
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

    }

    @Override
    public void onDraw() {

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
