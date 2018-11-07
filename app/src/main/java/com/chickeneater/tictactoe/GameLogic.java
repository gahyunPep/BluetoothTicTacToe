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

    public void setEmpty(int x, int y){
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

}
