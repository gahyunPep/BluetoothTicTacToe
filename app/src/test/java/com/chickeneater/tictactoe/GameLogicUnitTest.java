package com.chickeneater.tictactoe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameLogicUnitTest {

    @Test
    public void simpleGameTest() {
        GameModel game = new GameModel();
        game.makeMove(0, 0);
        game.makeMove(0, 1);
        game.makeMove(1, 0);
        game.makeMove(0, 2);
        game.makeMove(2, 0);


        assertTrue(game.isEnded());
    }

    @Test
    public void checkDrawTest() {
        GameModel game = new GameModel();
        game.makeMove(0, 2);
        game.makeMove(0, 1);
        game.makeMove(1, 0);
        game.makeMove(0, 0);
        game.makeMove(1, 1);
        game.makeMove(1, 2);
        game.makeMove(2, 1);
        game.makeMove(0, 1);
        game.makeMove(2, 2);

        assertTrue(game.isEnded());
    }

    @Test
    public void checkCrossIsWinner() {
        GameModel game = new GameModel();
        game.makeMove(0, 0);
        game.makeMove(0, 1);
        game.makeMove(1, 0);
        game.makeMove(0, 2);
        game.makeMove(2, 0);

        assertEquals(GameModel.CROSS, game.getWinner());
    }


}
