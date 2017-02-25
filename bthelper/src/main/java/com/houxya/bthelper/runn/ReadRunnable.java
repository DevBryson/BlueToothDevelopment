package com.houxya.bthelper.runn;

import android.os.Bundle;
import android.util.Log;

import com.houxya.bthelper.bean.MyFile;
import com.houxya.bthelper.i.OnReceiveListener;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ReadRunnable implements Runnable {

    private OnReceiveListener mListener;
    private InputStream mInputStream;

    private Queue<Byte> queueBuffer = new LinkedList<Byte>();
    private byte[] packBuffer = new byte[11];
    private float [] fData=new float[31];
    private String strDate,strTime;

    public ReadRunnable(OnReceiveListener listener, InputStream inputStream) {
        mListener = listener;
        mInputStream = inputStream;
    }

//    @Override
//    public void run() {
//        if( null != mInputStream){
//
//            boolean runFlag = true;
//            int n;
//            char[] buffer = new char[1024];
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
//            while (runFlag){
//                try {
//
//                    if(mInputStream.available() <= 0){
//                        continue;
//                    }else {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    n = bufferedReader.read(buffer);
//                    String s = new String(buffer, 0, n);
//                    Log.d("TAG", "receive : "+ s);
//                    mListener.onNewLine(s);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    runFlag = false;
//                    mListener.onConnectionLost();
//                }
//            }
//        }
//    }

    @Override
    public void run() {
        if( null != mInputStream) {

            byte[] tempInputBuffer = new byte[1024];    //缓冲块大小
            int acceptedLen = 0;                        //获取到的长度
            byte sHead;

            long lLastTime = System.currentTimeMillis(); // 获取开始时间
            while (true) {

                try {
                    // 每次对inputBuffer做覆盖处理
                    acceptedLen = mInputStream.read(tempInputBuffer);
                    for (int i = 0; i < acceptedLen; i++) {
                        queueBuffer.add(tempInputBuffer[i]);// 从缓冲区读取到的数据，都存到队列里
                    }

                    while (queueBuffer.size() >= 11) {
                        if ((queueBuffer.poll()) != 0x55) {
                            continue;// peek()返回对首但不删除 poll 移除并返回
                        }
                        sHead = queueBuffer.poll();
                        for (int j = 0; j < 9; j++) {
                            packBuffer[j] = queueBuffer.poll();
                        }
                        switch (sHead) {//
                            case 0x50:
                                int ms = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff));
                                strDate = String.format("20%02d-%02d-%02d", packBuffer[0], packBuffer[1], packBuffer[2]);
                                strTime = String.format(" %02d:%02d:%02d.%03d", packBuffer[3], packBuffer[4], packBuffer[5], ms);
                                RecordData(sHead,strDate + strTime);
                                break;
                            case 0x51:
                                fData[0] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 16;
                                fData[1] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 16;
                                fData[2] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 16 - 1;   //修改了-1
                                fData[17] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
                                RecordData(sHead,String.format("% 10.2f", fData[0])+String.format("% 10.2f", fData[1])+String.format("% 10.2f", fData[2])+" ");
                                break;
                            case 0x52:
                                fData[3] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 2000;
                                fData[4] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 2000;
                                fData[5] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 2000;
                                fData[17] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
                                RecordData(sHead,String.format("% 10.2f", fData[3])+String.format("% 10.2f", fData[4])+String.format("% 10.2f", fData[5])+" ");
                                break;
                            case 0x53:
                                fData[6] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 180;
                                fData[7] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 180;
                                fData[8] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 180;
                                fData[17] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
                                RecordData(sHead,String.format("% 10.2f", fData[6])+String.format("% 10.2f", fData[7])+String.format("% 10.2f", fData[8]));
                                break;
                        }//switch
                    }//while (queueBuffer.size() >= 11)

                    long lTimeNow = System.currentTimeMillis(); // 获取开始时间
                    if (lTimeNow - lLastTime > 80) {
                        lLastTime = lTimeNow;
                        //Message msg = mHandler.obtainMessage(DataMonitor.MESSAGE_READ);
                        Bundle bundle = new Bundle();
                        bundle.putFloatArray("Data", fData);       //只需要传感器的角度等信息

                        //Log.d("TAG", "receive : " + fData);
                        mListener.onNewLine(bundle);
                    }

                } catch (IOException e) {
                    //connectionLost();
                    mListener.onConnectionLost();
                    break;
                }
            }
        }
    }

    MyFile myFile;

    private short IDSave=0;
    private short IDNow;
    private int SaveState=-1;
    private int sDataSave=0;

    public void RecordData(byte ID,String str) throws IOException
    {
        boolean Repeat=false;
        short sData=(short) (0x01<<(ID&0x0f));
        if (((IDNow&sData)==sData)&&(sData<sDataSave)) {IDSave=IDNow;	IDNow=sData;Repeat=true;}
        else IDNow|=sData;
        sDataSave = sData;
        switch (SaveState) {
            case 0:
                myFile.Close();
                SaveState = -1;
                break;
            case 1:
                myFile=new MyFile("/mnt/sdcard/Record.txt");
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss ");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String s="开始时间：" + formatter.format(curDate)+"\r\n" ;
                if ((IDSave&0x02)>0) s+= "  加速度X： 加速度Y： 加速度Z：" ;
                if ((IDSave&0x04)>0) s+="  角速度X： 角速度Y： 角速度Z：";
                if ((IDSave&0x08)>0) s+="    角度X：   角度Y：   角度Z：";
                myFile.Write(s+"\r\n");
                if (Repeat)  {myFile.Write(str);SaveState = 2;}
                break;
            case 2:
                if (Repeat) myFile.Write("  \r\n");
                myFile.Write(str);
                break;
            case -1:
                break;
            default:
                break;
        }
    }

    public void setRecord(boolean record)
    {
        if (record) SaveState = 1;
        else SaveState = 0;
    }

    public int getSaveState() {
        return SaveState;
    }
}
