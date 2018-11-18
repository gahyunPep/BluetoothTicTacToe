package com.chickeneater.tictactoe.game;

import androidx.annotation.NonNull;

public class HotScreenGame extends AbstractGame {
    public HotScreenGame(@NonNull OnGameEventListener onGameEventListener) {
        super(onGameEventListener);
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
