package com.chickeneater.tictactoe.core.android;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.chickeneater.tictactoe.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by romanlee on 11/20/18.
 * To the power of Love
 */
public class LocationAndDiscoverabilityUtils {
    private static final int DISCOVERABILITY_TIME = 20;
    private static final int PERMISSION_REQUEST_LOCATION = 303;

    public static void becameDiscoverable(Activity activity) {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        activity.startActivity(discoverableIntent);
    }

    public static boolean requestLocationPermissionIfNeed(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isLocationPermissionGranted(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //TODO Rokanank doesn't like passing activity to requestLocationPermissionIfNeed
    public static AlertDialog locationPermissionRejectedDialog(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.location_reject_title))
                .setMessage(activity.getString(R.string.location_reject_message))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermissionIfNeed(activity);
                    }
                })
                .setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public static void dismissSafely(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
