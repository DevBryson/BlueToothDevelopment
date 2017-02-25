package com.basil.bluetoothdevelopment.mvp.presenter;

import android.bluetooth.BluetoothDevice;

import com.basil.bluetoothdevelopment.callback.GetPaireedCallback;
import com.basil.bluetoothdevelopment.callback.SearchBTCallback;
import com.basil.bluetoothdevelopment.mvp.model.DeviceListModel;
import com.basil.bluetoothdevelopment.mvp.view.DeviceListView;

import java.util.Set;

/**
 * Created by Basil on 2017/2/20.
 */

public class DeviceListPresenter extends BasePresenter<DeviceListView> {

    private DeviceListView mView;
    private DeviceListModel mModel;

    public DeviceListPresenter() {
        this.mModel = new DeviceListModel();
    }

    public void initView() {
        this.mView = getView();
    }

    public void initHelper() {

        mModel.initHelper();
    }

    /**
     * 获取已经配对过的蓝牙设备列表
     */
    public void getPairedDevices() {
        mModel.getPairedDevices(new GetPaireedCallback() {
            @Override
            public void onSuccess(Set<BluetoothDevice> bluetoothDevices) {
                mView.showPairedDevices(bluetoothDevices);
            }

            @Override
            public void onFailed() {
                mView.showNoPairedDevices();
            }
        });
    }

    /**
     * 搜索蓝牙设备
     */
    public void searchBTDevices() {
        mModel.searchBTDevices(new SearchBTCallback() {
            @Override
            public void onSearchSuccess(String deviceInfo) {
                mView.showSearchDevice(deviceInfo);
            }

            @Override
            public void onSearchFailed() {
                mView.showSearchFailed();
            }

            @Override
            public void onSearchCompleted() {
                mView.showSearchComplete();
            }
        });
    }
}
