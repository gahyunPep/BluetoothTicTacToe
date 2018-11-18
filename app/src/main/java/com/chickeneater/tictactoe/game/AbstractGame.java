package com.chickeneater.tictactoe.game;

import androidx.annotation.NonNull;

import static com.chickeneater.tictactoe.game.GameBoard.EMPTY;

/**
 * Created by romanlee on 11/17/18.
 * To the power of Love
 */
public abstract class AbstractGame implements Game {
    @NonNull
    protected OnGameEventListener mOnGameEventListener;
    protected boolean mIsCross = true;

    protected GameBoard mBoard = new GameBoard();

    public AbstractGame(@NonNull OnGameEventListener onGameEventListener) {
        mOnGameEventListener = onGameEventListener;
    }

    @Override
    public boolean isCurrentPlayerCross() {
        return mIsCross;
    }

    @Override
    public int get(int x, int y) {
        return mBoard.get(x, y);
    }

    @Override
    public void makeMove(int x, int y) {
        if (!canMakeMove()) {
            return;
        }

        if (mIsCross) {
            mBoard.setCross(x, y);
        } else {
            mBoard.setNought(x, y);
        }
        moveMade(x, y);

        int winner = mBoard.checkForAWinner(x, y);

        mOnGameEventListener.onMoveMade();
        if (winner != EMPTY) {
            mOnGameEventListener.onPlayerWon(winner);
        } else if (mBoard.isDraw()) {
            mOnGameEventListener.onDraw();
        }
    }
}
