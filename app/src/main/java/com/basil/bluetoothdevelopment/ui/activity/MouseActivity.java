package com.basil.bluetoothdevelopment.ui.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.basil.bluetoothdevelopment.Config;
import com.basil.bluetoothdevelopment.R;
import com.houxya.bthelper.receiver.BroadcastType;
import com.houxya.bthelper.receiver.MessageReceiver;

import java.io.OutputStream;

/**
 * 显示鼠标进行操作的页面
 * Created by Basil on 2017/2/18.
 */

public class MouseActivity extends AppCompatActivity {

	private WindowManager mWM;
	private ImageView ivCursor;
	private WindowManager.LayoutParams mParams;
	private int x = 0;
	private int y = 0;

	private float[] data;
	private float mFloat[] = new float[10];
	private int num = 0;

	private int mode;		//鼠标移动的模式

	/**
	 * 通过蓝牙得到数据，进行数据处理
	 */
	private MessageReceiver messageReceiver = new MessageReceiver() {
		@Override
		protected void OnReceiveMessage(Bundle dataBundle) {
			data = dataBundle.getFloatArray("Data");		//实时数据
			switch(mode) {
				case Config.FREE_MODE:
					//当移动时
					if((int)data[6] > 20 | (int)data[6] < -20 |
							(int)data[7] > 20 | (int)data[7] < -20) {
						if((int)data[7] > 0) {		//传感器向上
							mParams.y += -data[7];	//移动的距离就是差值（上方屏幕为负值，但data[7]是正）
						}

						if((int)data[7] < 0) {		//向下
							mParams.y += -data[7];//移动的距离就是差值（下方屏幕为正值，但data[7]是负）
						}

						if((int)data[6] > 20) {		//向右
							mParams.x += data[6];
						}

						if((int)data[6] < -20) {	//向左
							mParams.x += data[6];
						}
						mWM.updateViewLayout(ivCursor, mParams);	//刷新界面
					}
					break;

				case Config.CLICK_MODE:

					//当处于向下的角度时
					if((int)data[7] > 30) {
						int n = num % 10;

						mFloat[n] = data[7];
						float sum = 0;

						//算总和
						for(int i = 0; i < mFloat.length; i++) {
							sum += mFloat[i];
						}

						if((int)sum/mFloat.length > 30 && data[4] > 0) {
							execShellCmd("input tap 180 1400" + "\n");
						}

						mParams.x = 450;
						mParams.y = -100;
						mWM.updateViewLayout(ivCursor, mParams);
						//Log.e("测试", )
					}
					num++;
					break;

				case Config.SWIPE_MODE:

					execShellCmd("input swipe 100 250 200 280" + "\n");
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mode = getIntent().getIntExtra("mode",0);

		//注册广播
		registerReceiver(messageReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE));

		mWM = (WindowManager) this.getBaseContext().getSystemService(
				Context.WINDOW_SERVICE);

		ivCursor = new ImageView(this.getBaseContext());// new一个ImageView控件
		ivCursor.setImageResource(R.drawable.mouse);// 设置ImageView的图片，这个鼠标指针图片我事先已经放到drawable文件夹下面了

		mParams = new WindowManager.LayoutParams();		// 对ivCursor对象的参数描述对象
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;// 宽度自适应
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// 高度自适应
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 设置成不能获取焦点
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 设置成不能触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 设置成界面保持点亮

		mParams.format = PixelFormat.TRANSLUCENT;		//把当前Activity页面设置为透明
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		x += 100;
		y += 100;
		mParams.x = x;// 相对于屏幕原点的x轴距离
		mParams.y = y;// 相对于屏幕原点的y轴距离

		mWM.addView(ivCursor, mParams);

	    this.moveTaskToBack(true);
	}

	private OutputStream os=null;
	private void execShellCmd(String cmd) {

		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			if (os == null) {
				os = Runtime.getRuntime().exec("su").getOutputStream();
			}
			Log.e("测试",cmd);
			Log.e("测试", String.valueOf(cmd.getBytes()));
			os.write(cmd.getBytes());
			os.flush();
		} catch (Throwable t) {
			Log.i("stop", t.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		try
		{
			os.close();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		mWM.removeView(ivCursor);

		unregisterReceiver(messageReceiver);			//接触广播的注册

		super.onDestroy();
	}
}