package com.zhihuta.xiaota.ui;
//红米8A android版本9 xiaota也没有提示要摄像头权限
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.PathGetObject;
import com.zhihuta.xiaota.bean.response.PathsResponse;
import com.zhihuta.xiaota.bean.response.pathContainsQRResponse;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;
import com.zhihuta.xiaota.util.ShowMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

//public class ZxingScanActivity extends AppCompatActivity {
//public class ZxingScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    public class ZxingScanActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private static final String TAG = "nlgScanQrcodeActivity";
//    private AlertDialog scanQrResultDialog;
    private ZXingView mQRCodeView;
//    private ScheduledExecutorService mStopScanTimer;

    private Button mContinueScanBt;
    private Button mFinishScanBt;
    private TextView mDisplayScanResultTv;

    /**
     * 多次扫码的结果 放到String ArrayList里, 每个String 是一个Json， 包含了二维码里的各项信息
     */
//    private List<String> mScanResultList = new ArrayList<>();
    private Network mNetwork;


    //从路径界面传给扫描界面的路径信息，在该路径里添加扫描获得的间距
    private LujingData mLujing;
    private int mRequestCodeFroPrev; //可能来自主界面，可能来自路径界面
    private ArrayList<DistanceData> mScanResultDistanceList = new ArrayList<>();
    private ArrayList<LujingData> mFilterLujingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_scan);

        //get the request code from starter
        getDataFromPrev();

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
        mDisplayScanResultTv.setText("请对准二维码");
        mContinueScanBt = (Button) findViewById(R.id.button_continue_scan);
        mContinueScanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();

                mDisplayScanResultTv.setText("请对准二维码");
            }
        });

        mFinishScanBt = (Button) findViewById(R.id.button_finish_scan);
        mFinishScanBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击了结束按钮");

                stopScan();

                if (mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING||
                        mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE) {
                    Log.i(TAG, "筛选路径");

                    String qrIDs = null;
                    if(mScanResultDistanceList.size() ==0){
                        //如果没有扫成功，没有任何二维码被累积，则直接返回

                        finish();
                    } else {
                        for (int j = 0; j < mScanResultDistanceList.size(); j++) {
                            if (j == 0) {
                                qrIDs = String.valueOf(mScanResultDistanceList.get(j).getQr_id());
                            } else {
                                qrIDs = qrIDs + "," + String.valueOf(mScanResultDistanceList.get(j).getQr_id());
                            }
                        }

                        HashMap<String, String> getParams = new HashMap<>();
                        getParams.put("qr_ids",qrIDs);
                        getParams.put("project_id",Main.project_id);

                        mNetwork.get(RequestUrlUtility.build(URL.GET_LUJING_LIST), getParams, new FilterPathHandler(getParams),
                                (handler, msg) -> {
                                    handler.sendMessage(msg);
                                });
                    }
                }
                else if (mRequestCodeFroPrev ==Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH)
                {
                    if (mScanResultDistanceList.isEmpty())
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ZxingScanActivity.this);

                        alertDialogBuilder.setTitle("没有选择任何节点，是否需要继续？")
                                .setNegativeButton("结束", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent();

                                        intent.putExtra("mLujing", mLujing);
                                        intent.putExtra("mScanResultDistanceList", mScanResultDistanceList);
                                        setResult(mRequestCodeFroPrev, intent);

                                        finish();
                                    }
                                })
                                .setPositiveButton("继续选择", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                            startScan();
                                    }
                                }).show();
                    }
                    else
                    {
                        for (int i = 0; i < mScanResultDistanceList.size()-1 ; i++) {
                            mScanResultDistanceList.remove(i);
                        }//only choose the last one.

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ZxingScanActivity.this);

                        //choose the last scanned qr
                        alertDialogBuilder.setTitle("确定从节点["
                                + mScanResultDistanceList.get(0).getQr_id()
                                + "]分叉吗？")
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //startScanTimer();
                                        mScanResultDistanceList.clear();
                                        startScan();
                                    }
                                })
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent();

                                        intent.putExtra("mLujing", mLujing);
                                        intent.putExtra("mScanResultDistanceList", mScanResultDistanceList);
                                        setResult(mRequestCodeFroPrev, intent);
                                        finish();
                                    }
                                }).show();
                    }
                }
                else if (mRequestCodeFroPrev ==Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH)
                {
                    final int scanCount = mScanResultDistanceList.size();
                    if (scanCount < 2)
                    {//0,1个码不够
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ZxingScanActivity.this);

                        alertDialogBuilder.setTitle("选择的节点数量小于2个，是否需要继续？")
                                .setNegativeButton("结束", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent();

                                        intent.putExtra("mLujing", mLujing);
                                        intent.putExtra("mScanResultDistanceList", mScanResultDistanceList);
                                        setResult(mRequestCodeFroPrev, intent);

                                        finish();
                                    }
                                })
                                .setPositiveButton("继续选择", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        startScan();
                                    }
                                }).show();
                    }
                    else
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ZxingScanActivity.this);

                        //choose the last scanned qr
                        alertDialogBuilder.setTitle("确定从节点["
                                + mScanResultDistanceList.get(0).getQr_id()
                                +" 至 "
                                + mScanResultDistanceList.get(mScanResultDistanceList.size()-1).getQr_id()
                                + "]复制子路径吗？")
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        mScanResultDistanceList.clear();
                                        startScan();
                                    }
                                })
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent();

                                        intent.putExtra("mLujing", mLujing);
                                        intent.putExtra("mScanResultDistanceList", mScanResultDistanceList);
                                        setResult(mRequestCodeFroPrev, intent);
                                        finish();
                                    }
                                }).show();
                    }
                }
                else if (mRequestCodeFroPrev== Constant.REQUEST_CODE_SCAN_TO_ADD_NEW_QR){
                    /**
                     * 如果是 添加路径，在扫描页面 已经 保存到了数据库，直接关闭
                     */
                    ZxingScanActivity.this.finish();
                }
                else
                {

                }
            }});

    }

    private void getDataFromPrev() {
        Intent intent = getIntent();

        mRequestCodeFroPrev = intent.getIntExtra("requestCode", 0);
        switch (mRequestCodeFroPrev)
        {
            case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE:
            case Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING:
                //如果是从主界面筛选，不需要传路径到本页
                Log.i(TAG, "筛选路径");

                break;
            case Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH:
                mLujing = (LujingData) intent.getExtras().getSerializable("mLujingToPass");

                break;

            case Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH:

                mLujing = (LujingData) intent.getExtras().getSerializable("mLujingToPass");
                break;

            case Constant.REQUEST_CODE_SCAN_TO_ADD_NEW_QR:
                mLujing = (LujingData) intent.getExtras().getSerializable("mLujingToPass");
                break;

            default://?

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        startScan();

        //org.apache.commons.lang3.concurrent.BasicThreadFactory
//        mStopScanTimer = new ScheduledThreadPoolExecutor(1);
//        mStopScanTimer.schedule(new Runnable() {
//            @Override
//            public void run() {
////                ToastUtils.showShort("扫描失败，切换至手动模式！");
//                mQRCodeView.post(new Runnable() {
//                    @Override
//                    public void run() {
////                        mQRCodeView.stopSpot(); /// 关闭扫描
////                        showDialog(null);
//                    }
//                });
//            }
//        }, 10, TimeUnit.SECONDS);
    }

    private  void startScan()
    {
        mQRCodeView.startCamera();
        Log.d(TAG, "onStart: startCamera");
        mQRCodeView.startSpotAndShowRect(); ///开启扫描  --要重新开启扫描，否则扫描不出下一个新的二维码

        mDisplayScanResultTv.setText("请对准二维码");
    }

    private void stopScan()
    {
        mQRCodeView.stopSpot();
        mQRCodeView.stopCamera();

        mDisplayScanResultTv.setText("扫码已停止");
    }
//    private  void startScanTimer()
//    {
//        mStopScanTimer.schedule(new Runnable() {
//            @Override
//            public void run() {
////                ToastUtils.showShort("扫描失败，切换至手动模式！");
//                mQRCodeView.post(new Runnable() {
//                    @Override
//                    public void run() {
////                        mQRCodeView.stopSpot(); /// 关闭扫描
////                        showDialog(null);
//                    }
//                });
//            }
//        }, 10, TimeUnit.SECONDS);
//    }
//    private  void stopScanTimer()
//    {
//        mStopScanTimer.shutdown();
//    }
    @Override
    protected void onStop() {
        stopScan();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        mQRCodeView.onDestroy();
        super.onDestroy();
//        if (scanQrResultDialog!=null){
//            scanQrResultDialog.dismiss();
//        }
//        if(!mStopScanTimer.isShutdown() ) {
//            mStopScanTimer.shutdownNow();
//        }
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

        // 解析数据，并填入
        Gson gson = new Gson();
        DistanceData distanceData = gson.fromJson(result, DistanceData.class);

        if (mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_LUJING ||
                mRequestCodeFroPrev==Constant.REQUEST_CODE_SCAN_TO_FILTER_LUJING_CACULATE) {
            /**
             * 把二维码 累积起来，用于退出时筛选路径
             */
            boolean bFind = false;
            for (DistanceData data : mScanResultDistanceList) {
                if (data.getQr_id() == distanceData.getQr_id())
                    bFind = true;
            }

            if (!bFind) {
                mScanResultDistanceList.add(distanceData);
            }
            else
            {
                return;
            }

        }else if (mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_BRANCH_ON_PATH
                || mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_SUB_ON_PATH )
        {
            //first , add the qr to result list, then check if qr is in path or not, if not, then remove it from result list.
            boolean bFind = false;
            for (DistanceData data : mScanResultDistanceList) {
                if (data.getQr_id() == distanceData.getQr_id())
                    bFind = true;
            }

            if (!bFind) {
                mScanResultDistanceList.add(distanceData);
            }
            else
            {
                return;
            }

            HashMap<String,String> parameters = new HashMap<>();

            String strQrids = Integer.toString(distanceData.getQr_id());

            parameters.put("qr_ids", strQrids) ;
            String strUrl = Constant.getLujingDistanceExist.replace("{lujingID}", Integer.toString(mLujing.getId()));

            //check if qr in the path
            mNetwork.get(strUrl,parameters, new GetPathContainsDistanceQRHandler(parameters),
                    (Handler, msg)->{
                        Handler.sendMessage(msg);
                    });
        }
        else if (mRequestCodeFroPrev == Constant.REQUEST_CODE_SCAN_TO_ADD_NEW_QR){
            /**
             * 每次扫码成功，都尝试把二维码加入到路径
             */
            LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
            mPostValue.put("qr_id", new Gson().toJson(distanceData.getQr_id()));
            String url = Constant.putLujingDistanceUrl.replace("lujingID", String.valueOf(mLujing.getId()));
            mNetwork.putLujingDistance(url, mPostValue, new PutLujingDistanceHandler());
        }

        String rst = result.replace("distance","长度");
        rst = rst.replace("preset_name","展现名称");
        rst = rst.replace("qr_id","Id号");
        rst = rst.replace("qr_name","名称");
        rst = rst.replace("serial_number","序列号");
        rst = rst.replace("type","类型");

        if(distanceData.getType().equals("0")){
            rst = rst.replace("类型\":0","类型\": 固定码");
        } else if(distanceData.getType().equals("1")){
            rst = rst.replace("类型\":1","类型: 通用码");
        } else {
            //不动
        }
        mDisplayScanResultTv.setText(rst);
    }

    @SuppressLint("HandlerLeak")
    class PutLujingDistanceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Network.OK) {
                ShowMessage.showToast(ZxingScanActivity.this,"添加间距成功！",ShowMessage.MessageDuring.SHORT);

//                startScan();

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
    @SuppressLint("HandlerLeak")
    class FilterPathHandler extends Handler {
        private HashMap<String, String> getParameters = new HashMap<>();
        public FilterPathHandler(HashMap<String, String> mPostValue)
        {
            this.getParameters = mPostValue;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ////////////////

            String errorMsg = "";
            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);

            if (errorMsg != null)
            {
                Log.d("路径获取 NG:", errorMsg);
                Toast.makeText(ZxingScanActivity.this, "筛选路径出错！" + errorMsg, Toast.LENGTH_SHORT).show();
                return;
            }

            Result result= (Result)(msg.obj);

            PathsResponse responseData = CommonUtility.objectToJavaObject(result.getData(), PathsResponse.class);
            mFilterLujingList = new ArrayList<>();

            for (PathGetObject pathObj : responseData.paths) {

                LujingData lujingData = new LujingData();
                lujingData.setId( pathObj.id );
                lujingData.setName(pathObj.name);
                lujingData.setCreator(pathObj.creator);
                //lujingData.setLujingCaozuo(pathObj.);
                lujingData.setCreate_time(pathObj.createTime);

                mFilterLujingList.add( lujingData);
            }

            Log.d(TAG, "获取路径: size: " + mFilterLujingList.size());

            if (mFilterLujingList.size() == 0) {
                Toast.makeText(ZxingScanActivity.this, "筛选得到路径数量为0！", Toast.LENGTH_SHORT).show();

                return;
            }

            ShowMessage.showToast(ZxingScanActivity.this, "筛选 得到路径数量：" + mFilterLujingList.size(), ShowMessage.MessageDuring.SHORT);
            /**
             * 把筛选结果返回给主页面
             */

            Intent intent = new Intent();
            intent.putExtra("getParameters", getParameters);
            intent.putExtra("mFilterLujingList", mFilterLujingList);
            setResult(RESULT_OK, intent);

            finish();
            ////////////////
        }
    }

    @SuppressLint("HandlerLeak")
    class GetPathContainsDistanceQRHandler extends Handler {
        private HashMap<String, String> getParameters = new HashMap<>();
        public GetPathContainsDistanceQRHandler(HashMap<String, String> mPostValue)
        {
            this.getParameters = mPostValue;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ////////////////

            String errorMsg = "";

            errorMsg = RequestUrlUtility.getResponseErrMsg(msg);

            if (errorMsg != null)
            {
                if (!mScanResultDistanceList.isEmpty())
                {
                    mScanResultDistanceList.remove(mScanResultDistanceList.size()-1);
                }

                //startScanTimer();

                startScan();

                Log.d("路径获取 NG:", errorMsg);
                Toast.makeText(ZxingScanActivity.this, "查找路径出错！", Toast.LENGTH_SHORT).show();

                return;
            }

            Result result= (Result)(msg.obj);

            pathContainsQRResponse responseData = CommonUtility.objectToJavaObject(result.getData(), pathContainsQRResponse.class);

            ArrayList<DistanceData> tempList = new ArrayList<>();

            for (Integer value : responseData.not_in_path) {

                for (DistanceData tempValue :mScanResultDistanceList)
                {
                    if (tempValue.getQr_id() == value.intValue())
                    {
                        tempList.add(tempValue);
                    }
                }
            }

            for(DistanceData v: tempList)
            {
                mScanResultDistanceList.remove(v);
            }

            if (!responseData.not_in_path.isEmpty())
            {
                ShowMessage.showToast(ZxingScanActivity.this, "二维码id " +tempList.get(0).getQr_id() + "不在路径中：" , ShowMessage.MessageDuring.SHORT);
            }
            ////////////////
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