package com.chickeneater.tictactoe;

import java.util.Arrays;

class GameLogic {
    public static final int EMPTY = 0;
    public static final int NOUGHT = 1;
    public static final int CROSS = 2;

    private int cellsArr[][] = new int[3][3];

    public GameLogic() {
        for (int[] row : cellsArr) {
            Arrays.fill(row, EMPTY);
        }

        //Test purpose only TODO @rokanank delete after @Nithil will finish UI
//        cellsArr[0][0] = CROSS;
//        cellsArr[1][1] = NOUGHT;
//        cellsArr[0][2] = CROSS;
    }

    public void setNought(int x, int y) {
        if (isEmpty(x, y)) {
            cellsArr[x][y] = NOUGHT;
        }
    }

    public void setCross(int x, int y) {
        if (isEmpty(x, y)) {
            cellsArr[x][y] = CROSS;
        }
    }

    public void setEmpty(int x, int y) {
        cellsArr[x][y] = EMPTY;
    }

    public boolean isEmpty(int x, int y) {
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
    public int getWinner() {
        if (checkDiagonalsForWin() != EMPTY) return checkDiagonalsForWin();
        else if (checkRowsForWin() != EMPTY) return checkRowsForWin();
        else if (checkColsForWin() != EMPTY) return checkColsForWin();
        else
            return EMPTY; // if there is winner then it will return CROSS or NOUGHT, if it's not  it will return Empty.
    }

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

}
