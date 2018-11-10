package com.chickeneater.tictactoe.core.data;

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

    public DeviceInList(@Nullable String name, @Nullable String address) {
        mName = name != null ? name : DEFAULT_DEVICE_NAME;
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
}