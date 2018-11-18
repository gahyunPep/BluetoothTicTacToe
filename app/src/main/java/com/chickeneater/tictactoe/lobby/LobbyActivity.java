package com.chickeneater.tictactoe.lobby;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chickeneater.tictactoe.GameActivity;
import com.chickeneater.tictactoe.R;
import com.chickeneater.tictactoe.core.ui.EventObserver;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

//TODO @Gahyun add scan again button in to actionbar call restartScan() function when clicked
public class LobbyActivity extends AppCompatActivity implements DevicesRecyclerViewAdapter.OnDeviceSelectedListener {
    private DevicesRecyclerViewAdapter mAdapter;
    private static final int DISCOVERABILITY_TIME = 20;
    private static final int PERMISSION_REQUEST_LOCATION = 303;
    private LobbyViewModel mViewModel;
    private Button rescanBtn;
    private ProgressBar rescanProgressBar;
    private TextView scanTxt;
    private TextView preTxt;
    private TextView deviceTxt;
    private RecyclerView mDevicesRecyclerView;
    private RecyclerView pDevicesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestLocationPermission(); //TODO @Mr.Lee sometimes bluetooth permission pops up earlier than location permission HELP ME
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        rescanProgressBar = findViewById(R.id.rescanProgressbar);
        scanTxt = findViewById(R.id.scantextView);
        mDevicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        pDevicesRecyclerView = findViewById(R.id.preDevicesRecyclerView);
        preTxt = findViewById(R.id.preDeviceTxtView);
        deviceTxt = findViewById(R.id.newDeviceTxtView);
        mAdapter = new DevicesRecyclerViewAdapter(new DevicesDifUtils());
        mAdapter.setOnDeviceSelectedListener(this);
        mDevicesRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) { //Avoid running it on screen rotation
            becameDiscoverable();
        }
        mViewModel = ViewModelProviders.of(this, new LobbyViewModel.Factory(this)).get(LobbyViewModel.class);

        mViewModel.getIsDiscovering().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSearching) {
                if (isSearching) {
                    rescanBtn.setVisibility(View.INVISIBLE);
                    rescanProgressBar.setVisibility(View.VISIBLE);
                    scanTxt.setVisibility(View.VISIBLE);
                    mDevicesRecyclerView.setVisibility(View.INVISIBLE);
                    pDevicesRecyclerView.setVisibility(View.INVISIBLE);
                    preTxt.setVisibility(View.INVISIBLE);
                    deviceTxt.setVisibility(View.INVISIBLE);

                } else {
                    rescanBtn.setVisibility(View.VISIBLE);
                    rescanProgressBar.setVisibility(View.INVISIBLE);
                    scanTxt.setVisibility(View.INVISIBLE);
                    mDevicesRecyclerView.setVisibility(View.VISIBLE);
                    pDevicesRecyclerView.setVisibility(View.VISIBLE);
                    preTxt.setVisibility(View.VISIBLE);
                    deviceTxt.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewModel.getDevicesData().observe(this, new Observer<List<DeviceInList>>() {
            @Override
            public void onChanged(List<DeviceInList> deviceInLists) {
                displayDevices(deviceInLists);
            }
        });

        rescanBtn = findViewById(R.id.rescanBtn);
        rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartScan();
            }
        });

        mViewModel.getDeviceConnectedEvent().observe(this, new EventObserver<String>() {
            @Override
            public void onEventHappened(String value) {
                Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        mViewModel.getDeviceConnectionFailed().observe(this, new EventObserver<Void>() {

            @Override
            public void onEventHappened(Void value) {
                //TODO @Gahuyn show message that connection failed and give user opportunity to click again
            }
        });
    }

    private void restartScan() {
        becameDiscoverable();
        mViewModel.restartDiscovery(this);
    }

    private void displayDevices(List<DeviceInList> devices) {
        mAdapter.submitList(devices);
        mDevicesRecyclerView.scrollToPosition(0);
    }

    @Override
    public void OnDeviceSelected(DeviceInList device) {
        connectToDevice(device);
    }

    //method clicks and gets phone name and address and return it
    private void connectToDevice(DeviceInList device) {
        //TODO Gahyun show progress bar, and restrict user from clicking again
        mViewModel.connectToDevice(device);
    }


    private void becameDiscoverable() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        startActivity(discoverableIntent);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                   PERMISSION_REQUEST_LOCATION);
        } else {
            Toast.makeText(this, "Already has permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Just got the permission", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
