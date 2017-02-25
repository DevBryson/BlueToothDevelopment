package com.basil.bluetoothdevelopment.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.basil.bluetoothdevelopment.MainActivity;
import com.basil.bluetoothdevelopment.R;
import com.basil.bluetoothdevelopment.utils.PreferenceUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 每次App启动时首先加载的Activity，判断是否展现引导页面还是启动页面
 * Created by Basil on 2017/2/18.
 */
public class ApplicationActivity extends AutoLayoutActivity {

    @BindView(R.id.application_bg)
    ImageView applicationBg;
    /**
     * 定义三个切换动画
     */
    private Animation mFadeIn;

    private Animation mFadeOut;

    private Animation mFadeInScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DoAction();
    }

    /**
     * 建立监听事件
     */
    private void setAnimListener() {
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                applicationBg.startAnimation(mFadeInScale);
            }
        });

        mFadeInScale.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                applicationBg.startAnimation(mFadeOut);
            }
        });

        mFadeOut.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(ApplicationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {

            }
        });
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.application_fade_in);
        mFadeIn.setDuration(500);
        mFadeInScale = AnimationUtils.loadAnimation(this,
                R.anim.application_fade_in_scale);
        mFadeInScale.setDuration(2000);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.application_fade_out);
        mFadeOut.setDuration(500);
        applicationBg.setAnimation(mFadeIn);
    }

    /**
     * 随机选择背景图片
     */
    private void RandomApplicationBg() {
//        int index = new Random().nextInt(2);
//        if (index == 1) {
//            applicationBg.setImageResource(R.mipmap.entrance1);
//        } else {
//            applicationBg.setImageResource(R.mipmap.entrance2);
//        }
        applicationBg.setImageResource(R.mipmap.entrance2);
    }

    private void DoAction() {
        //读取Preference文件，识别isFirst标志，默认读到的boolean值是true
        boolean isFirst = PreferenceUtils.readBoolean(this, "Bluetooth", "isFirst", true);      //判断是否为第一次启动
        if (isFirst == true) {
            Welcome();      //打开引导界面
        } else {
            ComingApp();    //打开闪屏
        }
    }

    /**
     * 进入欢迎界面
     */
    private void Welcome() {
        Intent intent = new Intent();
        intent.setClass(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 直接进入app
     */
    private void ComingApp() {
        setContentView(R.layout.activity_application);
        ButterKnife.bind(this);
        RandomApplicationBg();
        initAnim();
        setAnimListener();
    }


}
