package com.basil.bluetoothdevelopment.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.basil.bluetoothdevelopment.R;
import com.basil.bluetoothdevelopment.mvp.presenter.DeviceListPresenter;
import com.basil.bluetoothdevelopment.mvp.view.DeviceListView;
import com.basil.bluetoothdevelopment.ui.activity.base.MVPBaseActivity;
import com.basil.bluetoothdevelopment.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索蓝牙设备的Activity
 */
public class DeviceListActivity extends MVPBaseActivity<DeviceListView,DeviceListPresenter>
        implements DeviceListView{

    @BindView(R.id.paired_devices)
    ListView pairedDevices;
    @BindView(R.id.new_devices)
    ListView newDevices;
    @BindView(R.id.button_scan)
    Button buttonScan;

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setColor(this,getResources().getColor(R.color.themeColor),0);

        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.title_activity_setting);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        mPresenter.initView();      //初始化Presenter层的View引用
        mPresenter.initHelper();

        //如果一个activity要返回数据到启动它的那个activity，可以调用setResult()方法
        setResult(Activity.RESULT_CANCELED);

        //初始化一个已经配对过的蓝牙设备adapter和一个新搜索到的蓝牙设备adapter
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);            //已经配对的设备
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);               //新搜索到的设备

        // 给已经配对过的ListView设置对应的adapter并设置点击事件
        pairedDevices.setAdapter(mPairedDevicesArrayAdapter);
        pairedDevices.setOnItemClickListener(mDeviceClickListener);

        // 给已经配对过的ListView设置对应的adapter并设置点击事件
        newDevices.setAdapter(mNewDevicesArrayAdapter);
        newDevices.setOnItemClickListener(mDeviceClickListener);

        mPresenter.getPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 确认蓝牙已经取消搜索
        if (mBtAdapter != null) {
            if(mBtAdapter.isDiscovering()){     //如果还在搜索，直接取消
                mBtAdapter.cancelDiscovery();
            }
        }
        mBtAdapter = null;
        mPairedDevicesArrayAdapter = null;
        mNewDevicesArrayAdapter = null;
    }

    @Override
    protected DeviceListPresenter createPresenter() {
        return new DeviceListPresenter();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.device_list;
    }

    /**
     * 开始搜索蓝牙设备
     */
    private void doDiscovery() {
        if(null != mNewDevicesArrayAdapter && null != mPresenter) {
            mNewDevicesArrayAdapter.clear();        //再次点击搜索按钮时，应该清空新设备的adapter

            mPresenter.searchBTDevices();       //搜索蓝牙设备
        }
    }

    // 一个监听蓝牙设备列表的监听类
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(null == mBtAdapter) {
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if(mBtAdapter.isDiscovering()){
                // 先取消搜索，毕竟现在已经选定设备进行连接
                mBtAdapter.cancelDiscovery();
            }

            // 获取蓝牙设备的地址
            String info = ((TextView) v).getText().toString();
            if (info.equals(getResources().getString(R.string.none_paired)) ||
                    info.equals(getResources().getString(R.string.none_found))) { //当选中的item不是蓝牙设备时，不处理点击事件
                return;
            }
            //后17位就是蓝牙的设备的地址
            String address = info.substring(info.length() - 17);

            // 创建一个Intent并传入地址信息
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // 把承载有要连接的蓝牙设备的地址的Intent作为结果返回
            setResult(Activity.RESULT_OK, intent);
            finish();    //程序自动返回之前的activity
        }
    };

    @OnClick(R.id.button_scan)
    public void onClick() {
        if(null != mNewDevicesArrayAdapter) {
            mNewDevicesArrayAdapter.clear();      //点击搜索时，先清空adapter里面的数据
        }
        doDiscovery();
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public void showPairedDevices(Set<BluetoothDevice> bluetoothDevices) {
        if(null != mPairedDevicesArrayAdapter) {
            for (BluetoothDevice device : bluetoothDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    @Override
    public void showNoPairedDevices() {
        if(null != mPairedDevicesArrayAdapter) {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    public void showSearchDevice(String deviceInfo) {
        if(null != mNewDevicesArrayAdapter) {
            mNewDevicesArrayAdapter.add(deviceInfo);
        }
    }

    @Override
    public void showSearchComplete() {
        //如果此adapter为空，则插入一条提示信息
        if (mNewDevicesArrayAdapter != null && mNewDevicesArrayAdapter.getCount() == 0) {
            String noDevices = getResources().getText(R.string.none_found).toString();
            mNewDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    public void showSearchFailed() {
        if(null != mNewDevicesArrayAdapter) {
            ToastUtils.showToast(this,getString(R.string.search_bt_devices_error));
            String noDevices = getResources().getText(R.string.none_found).toString();
            mNewDevicesArrayAdapter.add(noDevices);
        }
    }
}
