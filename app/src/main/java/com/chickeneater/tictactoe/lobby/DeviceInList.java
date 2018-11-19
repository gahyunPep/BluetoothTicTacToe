package com.chickeneater.tictactoe.lobby;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by romanlee on 11/10/18.
 * To the power of Love
 * Data structure of Device object.
 */
public class DeviceInList implements Comparable<DeviceInList> {
    private static final String DEFAULT_DEVICE_NAME = "Unknown device";

    @NonNull
    private final String mName;
    @NonNull
    private final String mAddress;
    @NonNull
    private boolean paired;

    public DeviceInList(@Nullable String name, @Nullable String address, boolean connectedBefore) {
        mName = name != null ? name : DEFAULT_DEVICE_NAME;
        mAddress = address != null ? address : "Unknown address";
        this.paired = connectedBefore;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getAddress() {
        return mAddress;
    }

    @NonNull
    public boolean getPaired() {return paired;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceInList)) return false;
        DeviceInList that = (DeviceInList) o;
        return Objects.equals(mName, that.mName) &&
                Objects.equals(mAddress, that.mAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mAddress);
    }

    @Override
    public int compareTo(DeviceInList o) {

        if(paired && !o.paired){
            return 1;
        }

        if(!paired && o.paired) {
            return -1;
        }

        if (DEFAULT_DEVICE_NAME.equals(mName) && DEFAULT_DEVICE_NAME.equals(o.mName)) {
            return 0;
        }

        if (DEFAULT_DEVICE_NAME.equals(mName)) {
            return 1;
        }

        if (DEFAULT_DEVICE_NAME.equals(o.mName)) {
            return -1;
        }
        return mName.compareTo(o.mName);
    }

    public static class FakeDevice extends DeviceInList{
        public FakeDevice() {
            super("FakeName", "Fake address", true);
        }
    }
}
