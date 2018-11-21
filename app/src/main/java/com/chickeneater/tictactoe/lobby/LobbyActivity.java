package com.chickeneater.tictactoe.lobby;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chickeneater.tictactoe.GameActivity;
import com.chickeneater.tictactoe.R;
import com.chickeneater.tictactoe.core.ui.EventObserver;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.isLocationPermissionGranted;
import static com.chickeneater.tictactoe.core.android.LocationAndDiscoverabilityUtils.requestLocationPermissionIfNeed;


public class LobbyActivity extends AppCompatActivity implements DevicesRecyclerViewAdapter.OnDeviceSelectedListener {
    private DevicesRecyclerViewAdapter mAdapter;
    private LobbyViewModel mViewModel;
    private ProgressBar mScanProgressBar;
    private TextView mScanTxt;
    private TextView mConnectTxt;
    private RecyclerView mDevicesRecyclerView;
    private Toolbar mToolbar;
    private MenuItem mScanButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        mScanProgressBar = findViewById(R.id.rescanProgressbar);
        mScanTxt = findViewById(R.id.scanTextView);
        mConnectTxt = findViewById(R.id.connectTextView);
        setupList();
        setupToolbar();

        if (savedInstanceState == null) {
            requestLocationPermissionIfNeed(this);
        }

        mViewModel = ViewModelProviders.of(this, new LobbyViewModel.Factory(this)).get(LobbyViewModel.class);

        mViewModel.getIsDiscovering().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSearching) {
                if (isSearching) {
                    mScanButton.setEnabled(false);
                    mScanProgressBar.setVisibility(View.VISIBLE);
                    mScanTxt.setVisibility(View.VISIBLE);
                } else {
                    mScanButton.setEnabled(true);
                    mScanProgressBar.setVisibility(View.INVISIBLE);
                    mScanTxt.setVisibility(View.INVISIBLE);
                }
            }
        });

        mViewModel.getDevicesData().observe(this, new Observer<List<DeviceInList>>() {
            @Override
            public void onChanged(List<DeviceInList> deviceInLists) {
                displayDevices(deviceInLists);
            }
        });


        mViewModel.getDeviceConnectedEvent().observe(this, new EventObserver<LobbyViewModel.Device>() {
            @Override
            public void onEventHappened(LobbyViewModel.Device value) {
                mConnectTxt.setVisibility(View.INVISIBLE);
                mScanProgressBar.setVisibility(View.INVISIBLE);
                GameActivity.startMultiPlayerPlayerGame(LobbyActivity.this, value.isHost);
            }
        });

        mViewModel.getDeviceConnectionFailed().observe(this, new EventObserver<Void>() {

            @Override
            public void onEventHappened(Void value) {
                //TODO @Gahuyn show message that connection failed and give user opportunity to click again
            }
        });
    }

    private void setupList() {
        mDevicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        mAdapter = new DevicesRecyclerViewAdapter(new DevicesDifUtils());
        mAdapter.setOnDeviceSelectedListener(this);
        mDevicesRecyclerView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.toolbar_menu);
        mScanButton = mToolbar.getMenu().getItem(0);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_scan) {
                    restartScan();
                    return true;
                }
                return false;
            }
        });
    }

    private void restartScan() {
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
        mScanProgressBar.setVisibility(View.VISIBLE);
        mConnectTxt.setVisibility(View.VISIBLE);
        mDevicesRecyclerView.setVisibility(View.INVISIBLE);
        mViewModel.connectToDevice(device);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isLocationPermissionGranted(requestCode, grantResults)) {
            mViewModel.restartDiscovery(this);
        } else {
            //TODO @Gahuyn change it to the meaningful text and extract resources
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
