package com.zhihuta.xiaota.ui;
//红米8A android版本9 xiaota也没有提示要摄像头权限
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

//public class ZxingScanActivity extends AppCompatActivity {
//public class ZxingScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    public class ZxingScanActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private static final String TAG = "nlgScanQrcodeActivity";
//    private AlertDialog scanQrResultDialog;
    private ZXingView mQRCodeView;
    private ScheduledExecutorService mStopScanTimer;

    private Button mContinueScanBt;
    private Button mFinishScanBt;
    private TextView mDisplayScanResultTv;

    /**
     * 多次扫码的结果 放到String ArrayList里, 每个String 是一个Json， 包含了二维码里的各项信息
     */
//    private List<String> mScanResultList = new ArrayList<>();
    private ArrayList<DistanceData> mScanResultDistanceList = new ArrayList<>();
    private Network mNetwork;


    //从路径界面传给扫描界面的路径信息，在该路径里添加扫描获得的间距
    private LujingData mLujing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_scan);

        mNetwork = Network.Instance(getApplication());
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mDisplayScanResultTv = (TextView) findViewById(R.id.textView_display_scan_result);
        mContinueScanBt = (Button) findViewById(R.id.button_continue_scan);
        mContinueScanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQRCodeView.startCamera();
                mQRCodeView.showScanRect();
                Log.d(TAG, "onStart: startCamera");
                mQRCodeView.startSpot(); ///开启扫描  --要重新开启扫描，否则扫描不出下一个新的二维码
            }
        });

        mFinishScanBt = (Button) findViewById(R.id.button_finish_scan);
        mFinishScanBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击了结束按钮");
                //在扫描页面 已经 保存到了数据库，
//                sendQrMsgBack();
                ZxingScanActivity.this.finish();
            }
            });

        Intent intent = getIntent();
        mLujing = (LujingData) intent.getExtras().getSerializable("mLujing");
    }



    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
        Log.d(TAG, "onStart: startCamera");
        mQRCodeView.startSpot(); ///开启扫描
        //org.apache.commons.lang3.concurrent.BasicThreadFactory
        mStopScanTimer = new ScheduledThreadPoolExecutor(1);
        mStopScanTimer.schedule(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort("扫描失败，切换至手动模式！");
                mQRCodeView.post(new Runnable() {
                    @Override
                    public void run() {
                        mQRCodeView.stopSpot(); /// 关闭扫描
//                        showDialog(null);
                    }
                });
            }
        }, 10, TimeUnit.SECONDS);
    }
    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        mQRCodeView.onDestroy();
        super.onDestroy();
//        if (scanQrResultDialog!=null){
//            scanQrResultDialog.dismiss();
//        }
        if(!mStopScanTimer.isShutdown() ) {
            mStopScanTimer.shutdownNow();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onScanQRCodeSuccess(final String result) {
//        showDialog(result);

        vibrate();
        mDisplayScanResultTv.setText(result);

        //扫码成功，取消之前设置的20秒后的task
        if(!mStopScanTimer.isShutdown() ) {
            mStopScanTimer.shutdownNow();
        }
        // 解析数据，并填入
        Gson gson = new Gson();
        DistanceData distanceData = gson.fromJson(result, DistanceData.class);

        /**
         * 每次扫码成功，都尝试把二维码加入到路径
         */
        LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
        mPostValue.put("qr_id", new Gson().toJson(distanceData.getQr_id()));
        String url = Constant.putLujingDistanceUrl.replace("lujingID", String.valueOf(mLujing.getId()));
        mNetwork.putLujingDistance(url, mPostValue, new PutLujingDistanceHandler());
    }

    @SuppressLint("HandlerLeak")
    class PutLujingDistanceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
                ShowMessage.showToast(ZxingScanActivity.this,"添加间距成功！",ShowMessage.MessageDuring.SHORT);
//                //把扫码新加的各个间距加入间距列表
//                mDistanceList.add(list.get(i));
//                mDistanceAdapter.notifyDataSetChanged();

            }else {

                if( msg.obj != null){
                    if( msg.obj.toString().equals("PATH_QRCODE_EXIST")){
                        ShowMessage.showDialog(ZxingScanActivity.this,"异常！该路径已包含了该二维码" ); //
                    } else {
                        ShowMessage.showDialog(ZxingScanActivity.this,"出错！" ); //
                    }
                }
            }
        }
    }
    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "open camera fail!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}