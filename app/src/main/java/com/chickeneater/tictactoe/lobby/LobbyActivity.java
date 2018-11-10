package com.chickeneater.tictactoe.lobby;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.chickeneater.tictactoe.DevicesDifUtils;
import com.chickeneater.tictactoe.DevicesRecyclerViewAdapter;
import com.chickeneater.tictactoe.R;
import com.chickeneater.tictactoe.core.data.DeviceInList;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

//TODO @Gahyun add scan again button in to actionbar call restartScan() function when clicked
public class LobbyActivity extends AppCompatActivity implements DevicesRecyclerViewAdapter.OnDeviceSelectedListener {
    private DevicesRecyclerViewAdapter mAdapter;
    private static final int DISCOVERABILITY_TIME = 20;

    private LobbyViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        RecyclerView devicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        mAdapter = new DevicesRecyclerViewAdapter(new DevicesDifUtils());
        mAdapter.setOnDeviceSelectedListener(this);
        devicesRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState == null) { //Avoid running it on screen rotation
            becameDiscoverable();
        }
        mViewModel = ViewModelProviders.of(this, new LobbyViewModel.Factory(this)).get(LobbyViewModel.class);

        mViewModel.getIsDiscovering().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //TODO show progress indicator visible if true, not visible if false
            }
        });

        mViewModel.getDevicesData().observe(this, new Observer<List<DeviceInList>>() {
            @Override
            public void onChanged(List<DeviceInList> deviceInLists) {
                displayDevices(deviceInLists);
            }
        });
    }

    private void restartScan() {
        becameDiscoverable();
        mViewModel.restartDiscovery(this);
    }

    private void displayDevices(List<DeviceInList> devices) {
        mAdapter.submitList(devices);
    }

    @Override
    public void OnDeviceSelected(DeviceInList device) {
        connectToDevice(device);
    }

    //method clicks and gets phone name and address and return it
    private void connectToDevice(DeviceInList device) {
        mViewModel.connectToDevice(device);
        Toast.makeText(this, device.getName() + "  " + device.getAddress(), Toast.LENGTH_LONG).show();
        //startActivity(new Intent(LobbyActivity.this, MainActivity.class));
    }

    private void becameDiscoverable() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        startActivity(discoverableIntent);
    }
}
