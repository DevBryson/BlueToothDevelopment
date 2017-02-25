package com.basil.bluetoothdevelopment.mvp.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.basil.bluetoothdevelopment.callback.GetPaireedCallback;
import com.basil.bluetoothdevelopment.callback.SearchBTCallback;
import com.houxya.bthelper.BtHelper;
import com.houxya.bthelper.i.OnSearchDeviceListener;

import java.util.List;
import java.util.Set;

/**
 * Created by Basil on 2017/2/20.
 */

public class DeviceListModel {

    private BtHelper btHelper;
    private BluetoothAdapter mBtAdapter;

    private static final String TAG = "DeviceListModel";

    public void initHelper() {
        //获取蓝牙操作库的实例
        btHelper = BtHelper.getDefault();
        // 获取蓝牙句柄
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 获取已经配对过的蓝牙设备列表
     * @param callback
     */
    public void getPairedDevices(GetPaireedCallback callback) {
        if(mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // 获取一个已经配对过的设备的列表
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // 如果有已经配对的设备，那就加入旧配对设备的adapter中
        if (pairedDevices.size() > 0) {         //当有已经配对的设备时
            callback.onSuccess(pairedDevices);
        } else {                                //当无已经配对的设备时
            callback.onFailed();
        }
    }

    /**
     * 搜索蓝牙设备
     * @param callback
     */
    public void searchBTDevices(final SearchBTCallback callback)
    {
        if(null == btHelper) {
            btHelper = BtHelper.getDefault();
        }
        btHelper.searchDevices(new OnSearchDeviceListener() {
            @Override
            public void onStartDiscovery() {            // 在进行搜索前回调
                Log.d(TAG, "onStartDiscovery()");
            }

            @Override
            public void onNewDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "new device: " + device.getName() + " " + device.getAddress());
                //当蓝牙设备已经配对时，跳过它，因为你已经无法与此设备连接
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    callback.onSearchSuccess(device.getName() + "\n" + device.getAddress());
                }
            }

            @Override
            public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {
                Log.d(TAG, "SearchCompleted: bondedList" + bondedList.toString());
                Log.d(TAG, "SearchCompleted: newList" + newList.toString());

                callback.onSearchCompleted();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                callback.onSearchFailed();
            }
        });
    }
}
