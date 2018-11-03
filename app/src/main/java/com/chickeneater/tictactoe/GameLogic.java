package com.chickeneater.tictactoe;

class GameLogic {
    String cellsArr[][] = new String[3][3];

    public void setX(int x, int y){
        if(isEmpty(x,y)){
            cellsArr[x][y]="X";
        }
    }

    public void setY(int x, int y){
        if(isEmpty(x,y)){
            cellsArr[x][y]="Y";
        }
    }

    public boolean isEmpty(int x, int y){
        if(cellsArr[x][y]==null){
            return true;
        }
        else{
            return false;
        }
    }

}
