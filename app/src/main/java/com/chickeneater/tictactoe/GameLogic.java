package com.chickeneater.tictactoe;

class GameLogic {

    int cellsArr[][] = new int[3][3];


    public void setX(int x, int y){
        if(isEmpty(x,y)){
            cellsArr[x][y]=1;
        }
    }

    public void setY(int x, int y){
        if(isEmpty(x,y)){
            cellsArr[x][y]=2;
        }
    }

    public boolean isEmpty(int x, int y){
        if(cellsArr[x][y] == 0){
            return true;
        }
        else{
            return false;
        }
    }

    // method for checking winner public

}
