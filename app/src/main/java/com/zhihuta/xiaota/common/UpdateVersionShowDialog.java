package com.zhihuta.xiaota.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.AppVersionInfoBean;

import java.io.File;

public class UpdateVersionShowDialog extends DialogFragment {
    private static final String TAG = "UpdateVersionShowDialog";

    private static final String KEY_APP_VERSION_INFO_BEAN = "app_version_info_bean";

    /**
     * 版本更新信息bean，由show方法传入
     */
    private AppVersionInfoBean appVersionInfoBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            appVersionInfoBean = (AppVersionInfoBean) arguments.getSerializable(KEY_APP_VERSION_INFO_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_app_version, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title_update);
        TextView tvContent = view.findViewById(R.id.tv_content_update);
        final Button btUpdate = view.findViewById(R.id.bt_download_apk);
        final Button btCancel = view.findViewById(R.id.bt_apk_dld_cancel);

        tvTitle.setText(appVersionInfoBean.getTitle());
        tvContent.setText(appVersionInfoBean.getContent());

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);

                //安装包的下载地址,选择getCacheDir路径，可以避免存储权限的处理
                final File targetFile = new File(getActivity().getCacheDir(), "target.apk");
                AppUpdater.getInstance().getINetManager().download(appVersionInfoBean.getUrl(), targetFile, new IDownloadCallback() {
                    @Override
                    public void onSuccess(File apkFile) {
                        v.setEnabled(true);

                        dismiss();

                        //下载成功
                        Log.d(TAG, "下载成功 success = " + apkFile.getAbsolutePath());

                        //TODO check MD5
                        String fileMd5 = AppUtils.getFileMd5(targetFile);
                        Log.d(TAG, "md5 = " + fileMd5);

                        if(Constant.md5CheckForDownloadAPK){
                            if (fileMd5 != null && fileMd5.equals(appVersionInfoBean.getMd5())) {
                                //校验成功，安装
                                Toast.makeText(getActivity(), "开始安装", Toast.LENGTH_SHORT).show();

                                AppUtils.installApk(getActivity(), apkFile);
                            } else {
                                Toast.makeText(getActivity(), "md5检测失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (fileMd5 != null ) {
                                //校验成功，安装
                                Toast.makeText(getActivity(), "开始安装", Toast.LENGTH_SHORT).show();
                                AppUtils.installApk(getActivity(), apkFile);
                            }
                        }
                    }

                    @Override
                    public void progress(int progress) {
                        Log.d(TAG, "progress = " + progress);

//                        tvUpdate.setText(progress + "%");
                        btUpdate.setText("下载中");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        v.setEnabled(true);

                        throwable.printStackTrace();
                        Toast.makeText(getActivity(), "文件下载失败", Toast.LENGTH_SHORT).show();
                    }
                }, UpdateVersionShowDialog.this);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                UpdateVersionShowDialog.this.dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.d("tag", "onDismiss: ");
        AppUpdater.getInstance().getINetManager().cancel(this);
    }

    public static void show(FragmentActivity fragmentActivity, AppVersionInfoBean appVersionInfoBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_APP_VERSION_INFO_BEAN, appVersionInfoBean);

        UpdateVersionShowDialog updateVersionShowDialog = new UpdateVersionShowDialog();
        updateVersionShowDialog.setArguments(bundle);

        updateVersionShowDialog.show(fragmentActivity.getSupportFragmentManager(), "updateVersionShowDialog");
    }
}
