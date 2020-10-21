/**
 * zbar 二维码 因为有包冲突，先保留着，也许后面可用。
 */
//package com.zhihuta.xiaota.ui;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Vibrator;
//import android.provider.Settings;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.zhihuta.xiaota.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.bingoogolapple.qrcode.core.QRCodeView;
//import cn.bingoogolapple.qrcode.zbar.ZBarView;
//
//public class QrActivity extends AppCompatActivity {
//
//
//
//    QRCodeView qrCodeView;
//    String[] permissions = new String[]{
//            Manifest.permission.CAMERA,
//            Manifest.permission.VIBRATE
//    };
//    private final int permissionCode = 100;//权限请求码
//
//    //检查权限
//    private void checkPermission() {
//        List<String> permissionList = new ArrayList<>();
//        for (int i = 0; i < permissions.length; i++) {
//            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
//                permissionList.add(permissions[i]);
//            }
//        }
//        if (permissionList.size() <= 0) {
//            //说明权限都已经通过，可以做你想做的事情去
//            bindEvent();
//        } else {
//            //存在未允许的权限
//            ActivityCompat.requestPermissions(this, permissions, permissionCode);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean haspermission = false;
//        if (permissionCode == requestCode) {
//            for (int i = 0; i < grantResults.length; i++) {
//                if (grantResults[i] == -1) {
//                    haspermission = true;
//                }
//            }
//            if (haspermission) {
//                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
//                permissionDialog();
//            } else {
//                //全部权限通过，可以进行下一步操作
//                bindEvent();
//            }
//        }
//    }
//
//    AlertDialog alertDialog;
//
//    //打开手动设置应用权限
//    private void permissionDialog() {
//        if (alertDialog == null) {
//            alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("提示信息")
//                    .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
//                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            cancelPermissionDialog();
//                            Uri packageURI = Uri.parse("package:" + getPackageName());
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            cancelPermissionDialog();
//                        }
//                    })
//                    .create();
//        }
//        alertDialog.show();
//    }
//
//    private void cancelPermissionDialog() {
//        alertDialog.cancel();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qr);
//        //6.0才用动态权限
//        if (Build.VERSION.SDK_INT >= 23) {
//            checkPermission();
//        } else {
//            bindEvent();
//        }
//    }
//
//    private void bindEvent() {
//        qrCodeView = (ZBarView) findViewById(R.id.zbarview);
//        qrCodeView.setDelegate(new QRCodeView.Delegate() {
//            @Override
//            public void onScanQRCodeSuccess(String result) {
//                vibrate();//震动手机
//                //扫描成功后处理事件
//                Toast.makeText(QrActivity.this, result, Toast.LENGTH_SHORT).show();
//                qrCodeView.startSpot();//继续扫描
//                TextView txtText = (TextView) findViewById(R.id.txtText);
//                txtText.setText(result);
//            }
//
////            @Override
//            public void onCameraAmbientBrightnessChanged(boolean isDark) {
//                // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
//                String tipText = qrCodeView.getScanBoxView().getTipText();
//                String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
//                if (isDark) {
//                    if (!tipText.contains(ambientBrightnessTip)) {
//                        qrCodeView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
//                    }
//                } else {
//                    if (tipText.contains(ambientBrightnessTip)) {
//                        tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
//                        qrCodeView.getScanBoxView().setTipText(tipText);
//                    }
//                }
//            }
//
//            @Override
//            public void onScanQRCodeOpenCameraError() {
//                Toast.makeText(QrActivity.this, "错误", Toast.LENGTH_SHORT).show();
//            }
//        });
//        qrCodeView.startCamera();
//
//        findViewById(R.id.start_spot).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onStart();
//                Toast.makeText(QrActivity.this, "开始扫码", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//        findViewById(R.id.stop_spot).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onStop();
//                Toast.makeText(QrActivity.this, "停止扫码", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        findViewById(R.id.open_flashlight).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qrCodeView.openFlashlight();
//                Toast.makeText(QrActivity.this, "打开闪光灯", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        findViewById(R.id.close_flashlight).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qrCodeView.closeFlashlight();
//                Toast.makeText(QrActivity.this, "关闭闪光灯", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        qrCodeView.startCamera();//打开后置摄像头开始预览，但是并未开始识别
//        qrCodeView.startSpotAndShowRect(); // 显示扫描框，并开始识别
//
////        mQRCodeView.showScanRect();//显示扫描框
////        mQRCodeView.startSpot();//开始识别二维码
//        //mQRCodeView.openFlashlight();//开灯
//        //mQRCodeView.closeFlashlight();//关灯
//    }
//
//    @Override
//    protected void onStop() {
//        qrCodeView.stopCamera();// 关闭摄像头预览，并且隐藏扫描框
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        qrCodeView.onDestroy();
//        super.onDestroy();
//    }
//
//    private void vibrate() {
//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        vibrator.vibrate(200);
//    }
//
//
//}