package com.chickeneater.tictactoe.game;

public interface OnGameEventListener {
    void onGameStarted(String playerName);

    void onMoveMade(); //call this after each move

    void onPlayerWon(int winner); //call this after someone won and who's the winner

    void onDraw();
}
