package com.chickeneater.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;

public class Stats {
    private SharedPreferences sharedPref;

    public Stats(Context context) {
        sharedPref = context.getSharedPreferences("Player", Context.MODE_PRIVATE);
    }

    public  String getName() {
        return sharedPref.getString("Name", "UnknownPlayer");
    }

    public void updateWin() {
        sharedPref.edit()
                .putInt("Win", getWin() + 1)
                .apply();
    }

    public int getWin() {
        return sharedPref.getInt("Win", 0);
    }

    public void updateLoss(){
        sharedPref.edit()
                .putInt("Loss",getLoss()+1)
                .apply();
    }

    public int getLoss() {
        return sharedPref.getInt("Loss",0);
    }

    public void updateDraw(){
        sharedPref.edit()
                .putInt("Draw",getDraw()+1)
                .apply();
    }

    public int getDraw() {
        return sharedPref.getInt("Draw",0);
    }
}
