package com.basil.bluetoothdevelopment.mvp.view;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by Basil on 2017/2/20.
 */

public interface DeviceListView {

    void showPairedDevices(Set<BluetoothDevice> bluetoothDevices);
    void showNoPairedDevices();

    void showSearchDevice(String deviceInfo);
    void showSearchComplete();
    void showSearchFailed();
}
