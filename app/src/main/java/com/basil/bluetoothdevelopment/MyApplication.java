package com.basil.bluetoothdevelopment;

import android.app.Application;

import com.houxya.bthelper.BtHelper;

/**
 * Created by Basil on 2017/2/18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 通过继承Thread.UncaughtExceptionHandler，记录App发生异常的信息
         */
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //先初始化蓝牙连接库的实例（单例模式）
        BtHelper.init(this);
    }
}
