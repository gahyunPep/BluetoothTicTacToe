package com.chickeneater.tictactoe;

import java.util.Arrays;

class GameLogic {
    public static final int EMPTY = 0;
    public static final int NOUGHT = 1;
    public static final int CROSS = 2;
    public static final int DRAW = EMPTY;

    private int cellsArr[][] = new int[3][3];

    private int activePlayer;

    public GameLogic() {
        startNewGame();
    }

    public void makeMove(int x, int y) {
        if (activePlayer == CROSS) {
            setCross(x, y);
            activePlayer = NOUGHT;
        } else {
            setNought(x, y);
            activePlayer = CROSS;
        }
    }

    public boolean isCurrentPlayerCross() {
        if (activePlayer == CROSS) {
            return true;
        } else {
            return false;
        }
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

    public void startNewGame() {
        for (int[] row : cellsArr) {
            Arrays.fill(row, EMPTY);
        }
        activePlayer = CROSS;
    }

    public boolean isCellEmpty(int x, int y) {
        if (cellsArr[x][y] == EMPTY) {
            return true;
        } else {
            return false;
        }
    }

    public int get(int x, int y) {
        return cellsArr[x][y];
    }

    // method for checking winner public
    public int checkWinner() {
        if (checkDiagonalsForWin() != EMPTY) return checkDiagonalsForWin();
        else if (checkRowsForWin() != EMPTY) return checkRowsForWin();
        else if (checkColsForWin() != EMPTY) return checkColsForWin();
        else
            return EMPTY; // if there is winner then it will return CROSS or NOUGHT, if it's not  it will return Empty.
    }

    public boolean isDraw() {
        //check if the board is full
        boolean isBoardFull = true;
        for (int[] rows : cellsArr) {
            for (int cell : rows) {
                if (cell == 0) isBoardFull = false;
            }
        }
        //check if it's Draw!
        if (isBoardFull == true && checkWinner() == EMPTY) {
            return true;
        } else {
            return false;
        }
    }

    /*Sub Methods for checkWinner() methods*/
    //check if the same value
    public int checkIfSameVal(int a, int b, int c) {
        if (a != 0 && a == b && b == c) {
            return a;
        } else {
            return 0;
        }
    }

    //check rows for win
    public int checkRowsForWin() {
        int matchVal = EMPTY;
        for (int i = 0; i < cellsArr.length; i++) {
            matchVal = checkIfSameVal(cellsArr[i][0], cellsArr[i][1], cellsArr[i][2]);
            if (matchVal != EMPTY)
                return matchVal;
        }
        return matchVal;
    }

    //check cols for win
    public int checkColsForWin() {
        int matchVal = EMPTY;
        for (int i = 0; i < cellsArr[0].length; i++) {
            matchVal = checkIfSameVal(cellsArr[0][i], cellsArr[1][i], cellsArr[2][i]);
            if (matchVal != EMPTY)
                return matchVal;
        }
        return matchVal;
    }

    //check diagonals for win
    public int checkDiagonalsForWin() {
        int matchVal1 = checkIfSameVal(cellsArr[0][0], cellsArr[1][1], cellsArr[2][2]);
        int matchVal2 = checkIfSameVal(cellsArr[0][2], cellsArr[1][1], cellsArr[2][0]);
        if (matchVal1 != EMPTY) {
            return matchVal1;
        } else if (matchVal2 != EMPTY) {
            return matchVal2;
        } else return EMPTY;
    }

    public boolean isEnded() {
        if (checkWinner() != 0 || isDraw()) return true;
        return false;
    }
}
