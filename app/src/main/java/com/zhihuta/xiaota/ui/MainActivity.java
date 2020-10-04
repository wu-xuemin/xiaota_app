package com.zhihuta.xiaota.ui;
//package com.cbt.tabusingfragment;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zhihuta.xiaota.FrdFragment;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.SettingFragment;
import com.zhihuta.xiaota.WeixinFragment;

public class MainActivity extends FragmentActivity implements OnClickListener {
    //声明四个Tab的布局文件
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;
    private LinearLayout mTabSetting;

    //声明四个Tab的ImageButton
    private ImageButton mWeixinImg;
    private ImageButton mFrdImg;
    private ImageButton mAddressImg;
    private ImageButton mSettingImg;

    //声明四个Tab分别对应的Fragment
    private Fragment mFragWeinxin;
    private Fragment mFragFrd;
    private Fragment mFragAddress;
    private Fragment mFragSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(0);//默认选中第一个Tab
    }

    private void initEvents() {
        //初始化四个Tab的点击事件
        mTabWeixin.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);
    }

    private void initViews() {
        //初始化四个Tab的布局文件
        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabSetting = (LinearLayout) findViewById(R.id.id_tab_setting);

        //初始化四个ImageButton
        mWeixinImg = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mFrdImg = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mAddressImg = (ImageButton) findViewById(R.id.id_tab_address_img);
        mSettingImg = (ImageButton) findViewById(R.id.id_tab_setting_img);

        Button addDianxianQinCeButton = (Button) findViewById(R.id.button5);///
        addDianxianQinCeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个Intent(当前Activity, SecondActivity)=====显示Intent
                Intent intent = new Intent(MainActivity.this, AddDxQingCeActivity.class);

                //启动Intent
                startActivity(intent);
            }
        });
        Button showDianxianQinCeButton = (Button) findViewById(R.id.button6);///
        showDianxianQinCeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个Intent(当前Activity, SecondActivity)=====显示Intent
                Intent intent = new Intent(MainActivity.this, ShowDxQingCeActivity.class);

                //启动Intent
                startActivity(intent);
            }
        });

        Button addTietaLujingMoxingButton = (Button) findViewById(R.id.button7);///
        addTietaLujingMoxingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个Intent(当前Activity, SecondActivity)=====显示Intent
                Intent intent = new Intent(MainActivity.this, AddTietaLujingMoxingActivity.class);

                //启动Intent
                startActivity(intent);
            }
        });

    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将四个ImageButton置为灰色
        resetImgs();
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                selectTab(0);//当点击Tab就选中该的Tab
                break;
            case R.id.id_tab_frd:
                selectTab(1);
                break;
            case R.id.id_tab_address:
                selectTab(2);
                break;
            case R.id.id_tab_setting:
                selectTab(3);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);

        Button addDianxianQinCeButton = (Button) findViewById(R.id.button5);///
        Button showDianxianQinCeButton = (Button) findViewById(R.id.button6);///
        Button addLujinMoxingButton = (Button) findViewById(R.id.button4);///
        Button showLujingMoxingButton = (Button) findViewById(R.id.button7);///
        Button calculateDianXianLengthButton = (Button) findViewById(R.id.button8);///
        Button showDianXianLengthButton = (Button) findViewById(R.id.button9);///
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //设置微信的ImageButton为绿色
                mWeixinImg.setImageResource(R.mipmap.tab_weixin_pressed);
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (mFragWeinxin == null) {
                    mFragWeinxin = new WeixinFragment();
                    transaction.add(R.id.id_content, mFragWeinxin);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFragWeinxin);
                }
                addDianxianQinCeButton.setVisibility(View.VISIBLE);
                showDianxianQinCeButton.setVisibility(View.VISIBLE);
                addLujinMoxingButton.setVisibility(View.GONE);
                showLujingMoxingButton.setVisibility(View.GONE);
                calculateDianXianLengthButton.setVisibility(View.GONE);
                showDianXianLengthButton.setVisibility(View.GONE);

                break;
            case 1:
                mFrdImg.setImageResource(R.mipmap.tab_find_frd_pressed);
                if (mFragFrd == null) {
                    mFragFrd = new FrdFragment();
                    transaction.add(R.id.id_content, mFragFrd);
                } else {
                    transaction.show(mFragFrd);
                }
                addDianxianQinCeButton.setVisibility(View.GONE);
                showDianxianQinCeButton.setVisibility(View.GONE);
                addLujinMoxingButton.setVisibility(View.VISIBLE);
                showLujingMoxingButton.setVisibility(View.VISIBLE);
                calculateDianXianLengthButton.setVisibility(View.GONE);
                showDianXianLengthButton.setVisibility(View.GONE);
                break;
            case 2:
                mAddressImg.setImageResource(R.mipmap.tab_address_pressed);
                if (mFragAddress == null) {
                    mFragAddress = new AddressFragment();
                    transaction.add(R.id.id_content, mFragAddress);
                } else {
                    transaction.show(mFragAddress);
                }
                addDianxianQinCeButton.setVisibility(View.GONE);
                showDianxianQinCeButton.setVisibility(View.GONE);
                addLujinMoxingButton.setVisibility(View.GONE);
                showLujingMoxingButton.setVisibility(View.GONE);
                calculateDianXianLengthButton.setVisibility(View.VISIBLE);
                showDianXianLengthButton.setVisibility(View.VISIBLE);

                break;
            case 3:
                mSettingImg.setImageResource(R.mipmap.tab_settings_pressed);
                if (mFragSetting == null) {
                    mFragSetting = new SettingFragment();
                    transaction.add(R.id.id_content, mFragSetting);
                } else {
                    transaction.show(mFragSetting);
                }

                addDianxianQinCeButton.setVisibility(View.GONE);
                showDianxianQinCeButton.setVisibility(View.GONE);
                addLujinMoxingButton.setVisibility(View.GONE);
                showLujingMoxingButton.setVisibility(View.GONE);
                calculateDianXianLengthButton.setVisibility(View.GONE);
                showDianXianLengthButton.setVisibility(View.GONE);
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mFragWeinxin != null) {
            transaction.hide(mFragWeinxin);
        }
        if (mFragFrd != null) {
            transaction.hide(mFragFrd);
        }
        if (mFragAddress != null) {
            transaction.hide(mFragAddress);
        }
        if (mFragSetting != null) {
            transaction.hide(mFragSetting);
        }
    }

    //将四个ImageButton置为灰色
    private void resetImgs() {
        mWeixinImg.setImageResource(R.mipmap.tab_weixin_normal);
        mFrdImg.setImageResource(R.mipmap.tab_find_frd_normal);
        mAddressImg.setImageResource(R.mipmap.tab_address_normal);
        mSettingImg.setImageResource(R.mipmap.tab_settings_normal);
    }
}


//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
///**
// * Created by littlecurl 2018/6/24
// */
//
///**
// * 此类 implements View.OnClickListener 之后，
// * 就可以把onClick事件写到onCreate()方法之外
// * 这样，onCreate()方法中的代码就不会显得很冗余
// */
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initView();
//    }
//
//    private void initView() {
//        // 初始化控件对象
//        Button mBtMainLogout = findViewById(R.id.bt_main_logout);
//        // 绑定点击监听器
//        mBtMainLogout.setOnClickListener(this);
//    }
//
//    public void onClick(View view) {
//        if (view.getId() == R.id.bt_main_logout) {
//            Intent intent = new Intent(this, loginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//}
