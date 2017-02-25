package com.basil.bluetoothdevelopment;

import android.os.Environment;

import java.util.UUID;

/**
 * Created by Basil on 2017/2/18.
 */

public class Config {

    //Crash文件存放路径
    public static final String CRASH_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashInfo/";
    //Crash文件后缀
    public static final String CRASH_FILE_NAME_SUFFIX = ".txt";

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //鼠标的移动模式
    public static final int FREE_MODE = 0;
    public static final int CLICK_MODE = 1;
    public static final int SWIPE_MODE = 2;

    //请求Activity返回值的意图
    public static final int REQUEST_CONNECT_DEVICE = 1;

    //7.0后，是不能通过file://路径进行访问文件的，必须通过FileProvider获取权限进行访问
    public static final String AUTHORITIES = "com.basil.bluetoothdevelopment.fileprovider";
}
