package com.basil.bluetoothdevelopment.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basil.bluetoothdevelopment.Config;
import com.basil.bluetoothdevelopment.R;
import com.basil.bluetoothdevelopment.utils.ToastUtils;
import com.houxya.bthelper.BtHelper;
import com.houxya.bthelper.receiver.BroadcastType;
import com.houxya.bthelper.receiver.MessageReceiver;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 数据页面的Activity
 */
public class DataActivity extends AppCompatActivity{

    @BindView(R.id.btn_time)
    Button btn_time;
    @BindView(R.id.btn_acceleration)
    Button btn_acceleration;
    @BindView(R.id.btn_angular_velocity)
    Button btn_angular_velocity;
    @BindView(R.id.btn_angular)
    Button btn_angular;
    @BindView(R.id.tvDataName1)
    TextView tvDataName1;
    @BindView(R.id.tvNum1)
    TextView tvNum1;
    @BindView(R.id.tvDataName2)
    TextView tvDataName2;
    @BindView(R.id.tvNum2)
    TextView tvNum2;
    @BindView(R.id.tvDataName3)
    TextView tvDataName3;
    @BindView(R.id.tvNum3)
    TextView tvNum3;

    private float[] data;       //拿到的传感器数据
    public static int iCurrentGroup = 0;
    private Date mDate;
    private SimpleDateFormat formatter;
    private BtHelper btHelper;

    private MessageReceiver messageReceiver = new MessageReceiver() {
        @Override
        protected void OnReceiveMessage(Bundle dataBundle) {
            data = dataBundle.getFloatArray("Data");       //获取到数据
            //Log.e("测试", String.valueOf(data));

            switch (iCurrentGroup) {
                case 0:
                    mDate = new Date(System.currentTimeMillis());       //获取当前时间

                    tvDataName1.setText("时间");
                    tvDataName2.setText("");
                    tvDataName3.setText("");

                    tvNum1.setText(formatter.format(mDate));
                    tvNum2.setText("");
                    tvNum3.setText("");
                    break;
                case 1:
                    tvNum1.setText(String.format("% 10.2fg", data[0]));
                    tvNum2.setText(String.format("% 10.2fg", data[1]));
                    tvNum3.setText(String.format("% 10.2fg", data[2]));
                    break;
                case 2:
                    tvNum1.setText(String.format("% 10.2f°/s", data[3]));
                    tvNum2.setText(String.format("% 10.2f°/s", data[4]));
                    tvNum3.setText(String.format("% 10.2f°/s", data[5]));
                    break;
                case 3:
                    tvNum1.setText(String.format("% 10.2f°", data[6]));
                    tvNum2.setText(String.format("% 10.2f°", data[7]));
                    tvNum3.setText(String.format("% 10.2f°", data[8]));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏颜色
        StatusBarUtil.setColor(this, getResources().getColor(R.color.themeColor), 0);
        ButterKnife.bind(this);

        //创建一个格式转换器
        formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");

        //注册收消息的广播
        registerReceiver(messageReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE));

        setMTextColor(0);           //设置按钮颜色

        btHelper = BtHelper.getDefault();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.toolbar_open:
                if(null == btHelper.readRunnable) {
                    ToastUtils.showToast(this, "当前并没有连接设备，请连接后再使用记录功能");
                    return true;        //结束当前的item点击事件
                }
                btHelper.setRecord(true);
                ToastUtils.showToast(this, "数据记录功能已开始");
                break;
            case R.id.toolbar_close:
                if(null == btHelper.readRunnable) {
                    ToastUtils.showToast(this, "当前并没有连接设备，请连接后再使用记录功能");
                    return true;        //结束当前的item点击事件
                }

                showReadStateResult(btHelper.getSaveState());
                break;
        }
        return true;
    }

    public void showReadStateResult(int state) {
        switch (state) {
            case 1:
            case 2:
                btHelper.setRecord(false);
                ToastUtils.showToast(this, "数据记录功能已关闭");

                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("数据已经记录至手机根目录：/mnt/sdcard/Record.txt\n是否打开已保存的文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                File myFile=new File("/mnt/sdcard/Record.txt");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(FileProvider.getUriForFile(getBaseContext(), Config.AUTHORITIES, myFile));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);     //申请权限
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case -1:
                ToastUtils.showToast(this, "数据记录功能当前并没有打开，无需关闭");

                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("数据记录功能当前并没有打开,是否打开上一次已保存的文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                File myFile=new File("/mnt/sdcard/Record.txt");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(FileProvider.getUriForFile(getBaseContext(), Config.AUTHORITIES, myFile));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);     //申请权限
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            default:
                ToastUtils.showToast(this, "数据记录功能出现异常，请稍后再试");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(messageReceiver);
        data = null;
        iCurrentGroup = 0;
        mDate = null;
        formatter = null;
        //先停止记录再清空对象引用
        btHelper.setRecord(false);
        btHelper = null;
    }

    @OnClick({R.id.btn_time, R.id.btn_acceleration, R.id.btn_angular_velocity, R.id.btn_angular})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_time:
                iCurrentGroup = 0;

                tvDataName1.setText("时间：");
                tvDataName2.setText("");
                tvDataName3.setText("");

                break;
            case R.id.btn_acceleration:
                iCurrentGroup = 1;

                initText();     //初始化TextView
                break;
            case R.id.btn_angular_velocity:
                iCurrentGroup = 2;

                initText();     //初始化TextView
                break;
            case R.id.btn_angular:
                iCurrentGroup = 3;

                initText();     //初始化TextView
                break;
        }
        setMTextColor(iCurrentGroup);       //改变按钮的颜色
    }

    /**
     * 初始化TextView文字
     */
    public void initText() {
        tvDataName1.setText("X轴：");
        tvNum1.setText("0");
        tvDataName2.setText("Y轴：");
        tvNum2.setText("0");
        tvDataName3.setText("Z轴：");
        tvNum3.setText("0");
    }

    /**
     * 设置按钮文字颜色
     */
    public void setMTextColor(int iCurrentGroup) {

        btn_time.setTextColor(Color.GRAY);
        btn_acceleration.setTextColor(Color.GRAY);
        btn_angular_velocity.setTextColor(Color.GRAY);
        btn_angular.setTextColor(Color.GRAY);

        switch (iCurrentGroup) {
            case 0:
                btn_time.setTextColor(Color.BLACK);
                break;
            case 1:
                btn_acceleration.setTextColor(Color.BLACK);
                break;
            case 2:
                btn_angular_velocity.setTextColor(Color.BLACK);
                break;
            case 3:
                btn_angular.setTextColor(Color.BLACK);
                break;
        }
    }

    @OnClick({R.id.btn_mouse, R.id.btn_mouse1})
    public void onClickMode(View view) {

        switch (view.getId()) {
            case R.id.btn_mouse:
                if(null != btHelper.readRunnable) {
                    Intent intent = new Intent(this, MouseActivity.class);
                    intent.putExtra("mode", Config.FREE_MODE);
                    startActivity(intent);
                    return;
                }
                break;
            case R.id.btn_mouse1:
                if(null != btHelper.readRunnable) {
                    Intent intent = new Intent(this, MouseActivity.class);
                    intent.putExtra("mode", Config.CLICK_MODE);
                    startActivity(intent);
                    return;
                }
                break;
        }
        ToastUtils.showToast(getBaseContext(), "先连接蓝牙设备，才能打开鼠标模式");

        //没连接就打开搜索页面给用户进行连接
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivity(intent);
        finish();
    }
}
