package com.basil.bluetoothdevelopment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.basil.bluetoothdevelopment.ui.activity.DataActivity;
import com.basil.bluetoothdevelopment.ui.activity.DeviceListActivity;
import com.basil.bluetoothdevelopment.ui.activity.HelpActivity;
import com.basil.bluetoothdevelopment.ui.activity.SettingActivity;
import com.basil.bluetoothdevelopment.utils.ToastUtils;
import com.houxya.bthelper.BtHelper;
import com.houxya.bthelper.i.IConnectionListener;
import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * App主页面的Activity
 */
public class MainActivity extends AppCompatActivity {

    private String connectedAddress;
    private BtHelper btHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        StatusBarUtil.setColor(this,getResources().getColor(R.color.themeColor),0);

        btHelper = BtHelper.getDefault();       //获取蓝牙操作库对象
    }

    @OnClick({R.id.bt_connect, R.id.tv_connect, R.id.bt_help, R.id.tv_help, R.id.bt_data, R.id.tv_data, R.id.bt_setting, R.id.tv_setting})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bt_connect:           //利用case穿透，减少代码量
            case R.id.tv_connect:
                //先启动蓝牙搜索的Activity
                intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, Config.REQUEST_CONNECT_DEVICE);
                break;
            case R.id.bt_help:
            case R.id.tv_help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_data:
            case R.id.tv_data:
                intent = new Intent(this, DataActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_setting:
            case R.id.tv_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 利用startActivityForResult 和 onActivityResult在activity间传递数据
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Config.REQUEST_CONNECT_DEVICE:// 如果请求的是连接蓝牙的请求
                if (resultCode == Activity.RESULT_OK) {
                    //获取要连接的设备的地址
                    connectedAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    btHelper.connectDevice(connectedAddress, new IConnectionListener() {
                        @Override
                        public void OnConnectionStart() {

                        }

                        @Override
                        public void OnConnectionSuccess() {
                            ToastUtils.showToast(getBaseContext(), "连接" + connectedAddress + "成功");
                            //成功后直接打开数据页面
                            Intent intent = new Intent(getBaseContext(), DataActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void OnConnectionFailed(Exception e) {
                            ToastUtils.showToast(getBaseContext(), "连接失败");
                        }
                    });
                }
                break;
        }
    }
}
