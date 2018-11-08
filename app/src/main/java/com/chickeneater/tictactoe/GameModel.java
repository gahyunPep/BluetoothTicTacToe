package com.chickeneater.tictactoe;

import java.util.Arrays;

class GameModel {
    public static final int EMPTY = 0;
    public static final int NOUGHT = 1;
    public static final int CROSS = 2;
    public static final int DRAW = EMPTY;

    private int cellsArr[][] = new int[3][3];
    private int winner = EMPTY;
    private int activePlayer = CROSS;

    public GameModel() {
        for (int[] row : cellsArr) {
            Arrays.fill(row, EMPTY);
        }
    }

    public void makeMove(int x, int y) {
        if (activePlayer == CROSS) {
            setCross(x, y);
            activePlayer = NOUGHT;
        } else {
            setNought(x, y);
            activePlayer = CROSS;
        }

        winner = assignWinner(x, y);
    }

    public boolean isCurrentPlayerCross() {
        return activePlayer == CROSS;
    }

    private void setNought(int x, int y) {
        if (isCellEmpty(x, y)) {
            cellsArr[x][y] = NOUGHT;
        }
    }

    private void setCross(int x, int y) {
        if (isCellEmpty(x, y)) {
            cellsArr[x][y] = CROSS;
        }
    }

    public boolean isCellEmpty(int x, int y) {
        return cellsArr[x][y] == EMPTY;
    }

    public int get(int x, int y) {
        return cellsArr[x][y];
    }

    // method for checking winner public
    private int assignWinner(int x, int y) {
        if (x == y || (x == 0 && y == 2) || (x == 2 && y == 0)) {
            int mainDiagonal = checkMainDiagonal();
            if (mainDiagonal != EMPTY) {
                return mainDiagonal;
            }

            int subDiagonal = checkSubDiagonal();
            if (subDiagonal != EMPTY) {
                return mainDiagonal;
            }
        }

        int rowWinner = checkRowsForWin(x);
        if (rowWinner != EMPTY) {
            return rowWinner;
        }

        int columnsWinner = checkColsForWin(y);
        if (columnsWinner != EMPTY) {
            return columnsWinner;
        }

        return EMPTY;
    }

    public int getWinner() {
        return winner;
    }

    public boolean isEnded() {
        return winner != EMPTY || isDraw();
    }

    private boolean isDraw() {
        //check if the board is full
        for (int[] rows : cellsArr) {
            for (int cell : rows) {
                if (cell == EMPTY) return false;
            }
        }
        //check if it's Draw!
        return true;
    }

    /*Sub Methods for assignWinner() methods*/
    //check if the same value
    private int checkIfSameVal(int a, int b, int c) {
        if (a != EMPTY && a == b && b == c) {
            return a;
        } else {
            return EMPTY;
        }
    }

    //check rows for win
    private int checkRowsForWin(int x) {
        return checkIfSameVal(cellsArr[x][0], cellsArr[x][1], cellsArr[x][2]);
    }

    //check cols for win
    private int checkColsForWin(int y) {
        return checkIfSameVal(cellsArr[0][y], cellsArr[1][y], cellsArr[2][y]);
    }

    private int checkMainDiagonal() {
        return checkIfSameVal(cellsArr[0][0], cellsArr[1][1], cellsArr[2][2]);
    }

    private int checkSubDiagonal() {
        return checkIfSameVal(cellsArr[0][2], cellsArr[1][1], cellsArr[2][0]);
    }
}
