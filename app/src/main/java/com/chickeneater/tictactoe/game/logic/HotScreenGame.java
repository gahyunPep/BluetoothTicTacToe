package com.chickeneater.tictactoe.game.logic;

import androidx.annotation.NonNull;

public class HotScreenGame extends AbstractGame {
    public HotScreenGame(String playerName, @NonNull OnGameEventListener onGameEventListener) {
        super(onGameEventListener);
        onGameEventListener.onGameStarted(playerName);
    }

    @Override
    public boolean canMakeMove() {
        return true;
    }

    @Override
    public void moveMade(int x, int y) {
        mIsCross = !mIsCross;
    }
}
