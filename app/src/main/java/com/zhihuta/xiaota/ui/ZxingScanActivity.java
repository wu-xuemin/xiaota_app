package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.DistanceData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_scan);

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
                sendQrMsgBack();
                ZxingScanActivity.this.finish();
            }
            });

    }
    public void sendQrMsgBack(){
        Intent intent = getIntent();
        intent.setClass(ZxingScanActivity.this, AddNewLujingActivity.class);
        intent.putExtra("mScanResultDistanceList", (Serializable) mScanResultDistanceList);

//        intent.putExtra("mScanResultDistanceList", mScanResultDistanceList);
        ZxingScanActivity.this.setResult(RESULT_OK, intent);

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
        mScanResultDistanceList.add(distanceData);

        if(mScanResultDistanceList == null){
            ToastUtils.showShort("异常，距离信息为NULL！");
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

//    private void showDialog(final String result) {
//        Log.d(TAG, "result:" + result);
//        if(scanQrResultDialog != null && scanQrResultDialog.isShowing()) {
//            scanQrResultDialog.dismiss();
//            scanQrResultDialog = null;
//        }
//        scanQrResultDialog = new AlertDialog.Builder(ZxingScanActivity.this).create();
//        scanQrResultDialog.setTitle("扫描结果");
//        scanQrResultDialog.setCancelable(false);
//        final EditText et = new EditText(this);
//        if(result != null) {
//            scanQrResultDialog.setMessage("扫描结果： "+result);
//        } else {
//            scanQrResultDialog.setView(et);
//        }
//        scanQrResultDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String qrMessage = "";
//                        if(result != null) {
//                            qrMessage = result;
//                        } else {
//                            qrMessage = et.getText().toString();
//                        }
//                        if("".equals(qrMessage)) {
//                            ToastUtils.showShort("信息为空！");
//                            ZxingScanActivity.this.finish();
//                        } else {
//                            //根据result获取对应taskRecordDetail
//                            Intent intent = getIntent();
//                            intent.putExtra("scanGotMessage", qrMessage);
//                            ZxingScanActivity.this.setResult(RESULT_OK, intent);
//                            if(!mStopScanTimer.isShutdown() ) {
//                                mStopScanTimer.shutdownNow();
//                            }
//                            ZxingScanActivity.this.finish();
//                        }
//                    }
//                });
//        scanQrResultDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"重新扫描",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //重新扫描
//                        mQRCodeView.startSpot();
//                        mStopScanTimer = new ScheduledThreadPoolExecutor(1);
//                        mStopScanTimer.schedule(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showShort("扫描失败，切换至手动模式！");
//                                mQRCodeView.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mQRCodeView.stopSpot();
//                                        showDialog(null);
//                                    }
//                                });
//                            }
//                        }, 10, TimeUnit.SECONDS);
//                    }
//                });
//        // 显示
//        scanQrResultDialog.show();
//        //震动
//        vibrate();
//    }

}