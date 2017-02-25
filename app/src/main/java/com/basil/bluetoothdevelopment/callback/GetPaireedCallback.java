package com.basil.bluetoothdevelopment.callback;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * 获取已经配对的列表的Callback
 * Created by Basil on 2017/2/20.
 */

public interface GetPaireedCallback {

    void onSuccess(Set<BluetoothDevice> bluetoothDevices);
    void onFailed();
}
