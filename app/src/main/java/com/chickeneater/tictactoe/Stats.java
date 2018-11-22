package com.chickeneater.tictactoe;

import android.content.SharedPreferences;

public class Stats {

    public static String getName(SharedPreferences sharedPref) {
        return sharedPref.getString("Name", "UnknownPlayer");
    }

    public static void updateWin(SharedPreferences sharedPref) {
        sharedPref.edit()
                .putInt("Win", getWin(sharedPref) + 1)
                .apply();
    }

    public static int getWin(SharedPreferences sharedPref) {
        return sharedPref.getInt("Win", 0);
    }

    public static void updateLoss(SharedPreferences sharedPref){
        sharedPref.edit()
                .putInt("Loss",getLoss(sharedPref)+1)
                .apply();
    }

    public static int getLoss(SharedPreferences sharedPref) {
        return sharedPref.getInt("Loss",0);
    }

    public static void updateDraw(SharedPreferences sharedPref){
        sharedPref.edit()
                .putInt("Draw",getDraw(sharedPref)+1)
                .apply();
    }

    public static int getDraw(SharedPreferences sharedPref) {
        return sharedPref.getInt("Draw",0);
    }
}
