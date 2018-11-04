package com.chickeneater.tictactoe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    private RecyclerView mDevicesRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        mDevicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        mDevicesRecyclerView.setAdapter(mAdapter);

        getDevices();
    }

    private void displayDevices(List<DeviceInList> devices) {
        /*
        TODO display list of devices here inside recyclerView
        Tutorial here https://developer.android.com/guide/topics/ui/layout/recyclerview
        You can skip "Add the support library", and "Add RecyclerView to your layout" part
        */
        //Uncomment this lines when you will create DevicesRecyclerViewAdapter class
        //mAdapter = new DevicesRecyclerViewAdapter(devices);
        //mDevicesRecyclerView.setAdapter(mAdapter);

    }

    //This function will be replaced with BluetoothConnector call. @Gahyun Do not change it
    private void getDevices() {
        List<DeviceInList> retVal = new ArrayList<>();
        retVal.add(new DeviceInList("Phone 1", "12:32:32:43:32"));
        retVal.add(new DeviceInList("Phone 2", "12:32:32:43:32"));
        retVal.add(new DeviceInList("Phone 3", "12:32:32:43:32"));
        retVal.add(new DeviceInList("Phone 4", "12:32:32:43:32"));
        retVal.add(new DeviceInList("Phone 5", "12:32:32:43:32"));
        retVal.add(new DeviceInList("Phone 6", "12:32:32:43:32"));

        displayDevices(retVal);
    }

    //Data structure of Device object.
    class DeviceInList {
        @NonNull
        private final String mName;
        @NonNull
        private final String mAddress;

        public DeviceInList(@Nullable String name, @Nullable String address) {
            mName = name != null ? name : "Unknown device";
            mAddress = address != null ? address : "Unknown address";
        }

        @NonNull
        public String getName() {
            return mName;
        }

        @NonNull
        public String getAddress() {
            return mAddress;
        }
    }
}
