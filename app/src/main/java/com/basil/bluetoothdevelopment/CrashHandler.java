package com.basil.bluetoothdevelopment;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录软件Crash信息
 * Created by Basil on 2017/2/18.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    //系统默认的异常处理器
    private Thread.UncaughtExceptionHandler defaultCrashHandler;

    private static final String TAG = "CrashHandler";
    private Context mContext;
    private static CrashHandler crashHandler = new CrashHandler();

    //私有化构造函数
    private CrashHandler() {
    }

    //获取实例
    public static CrashHandler getInstance() {
        return crashHandler;
    }

    public void init(Context context) {
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置系统的默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            //导出异常信息到SD卡中
            if(!Debug.isDebuggerConnected()) {
                dumpExceptionToSDCard(throwable);
            }

            //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决bug
            uploadExceptionToServer();
        }catch(IOException e) {
            e.printStackTrace();
        }
        //throwable.printStackTrace();
        //如果系统提供默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if(defaultCrashHandler != null){
            defaultCrashHandler.uncaughtException(thread, throwable);
        }else {
            //自己处理
            try {
                //延迟2秒杀进程
                Thread.sleep(2000);
                Toast.makeText(mContext, "程序出错了~", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //记录异常信息到本地文本中
    private void dumpExceptionToSDCard(Throwable throwable) throws IOException {
        //如果SD卡非正常挂载，则用Log输出异常信息
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "SD卡未挂载或无法使用，暂时无法把Crash信息写入SD卡");
            return;
        }
        File dir = new File(Config.CRASH_FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long currentTime = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime));
        //建立记录Crash信息的文本
        File file = new File(Config.CRASH_FILE_PATH + time + Config.CRASH_FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //记录时间
            printWriter.println(time);
            //记录手机信息
            dumpPhoneInfo(printWriter);

            printWriter.println();
            throwable.printStackTrace(printWriter);
            printWriter.close();
            Log.e(TAG,"记录Crash信息成功");
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            Log.e(TAG, "记录Crash信息失败");
        }
    }

    //记录手机信息
    private void dumpPhoneInfo(PrintWriter printWriter) {
        //安卓系统版本号
        printWriter.print("OS Version:");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);

        //手机硬件制造商
        printWriter.print("Vendor:");
        printWriter.println(Build.MANUFACTURER);

        //系统定制商
        printWriter.print("Brand:");
        printWriter.println(Build.BRAND);

        //手机型号
        printWriter.print("Model:");
        printWriter.println(Build.MODEL);

        //CPU架构
        printWriter.print("CPU ABI:");
        printWriter.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer(){
        //将异常信息发送到服务器
    }

}
