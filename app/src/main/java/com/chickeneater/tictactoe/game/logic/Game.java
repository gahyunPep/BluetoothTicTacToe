package com.chickeneater.tictactoe.game.logic;

import androidx.annotation.Nullable;

/**
 * Created by romanlee on 11/17/18.
 * To the power of Love
 */
public interface Game {
    boolean canMakeMove();
    boolean isCurrentPlayerCross();
    int get(int x, int y);
    void moveMade(int x, int y);
    void makeMove(int x, int y);
}

