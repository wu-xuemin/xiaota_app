package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.bean.basic.CommonUtility;
import com.zhihuta.xiaota.bean.basic.Result;
import com.zhihuta.xiaota.bean.response.BaseResponse;
import com.zhihuta.xiaota.common.CallbackBundle;
import com.zhihuta.xiaota.common.OpenFileDialog;

//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook; ///
//import jxl.Workbook; ///  jxl.jar包暂未用。

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.zhihuta.xiaota.common.PathUtils;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;


public class AddDxQingCeFromFileActivity extends AppCompatActivity {

    static private int openfileDialogId = 0;

    private static final int PICK_FILE = 1;

    ImportDianxianQinceHandler importDianxianQinceHandler = new ImportDianxianQinceHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dx_qing_ce_from_file);

        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 设置单击按钮时打开文件对话框
        findViewById(R.id.button_openfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                showDialog(openfileDialogId);
                /**
                 * 文件系统的访问是个问题。。。--> 可以调用系统自己的文件过滤器来选择文件。
                 */
//                readExcel("/storage/emulated/0/Download/test.xlsx");
                Intent intent;
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");
//                intent.setType("application/vnd.ms-excel"); /// 这个 xlsx文件 还是灰色
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case  PICK_FILE :
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri fileuri = data.getData();
                    if (fileuri != null) {
//                        InputStream inputStream = ContentResolver.get(uri) //openInputStream
//                        // 执行文件读取操作
//                        Toast.makeText(this, "文件路径："+ fileuri.getPath().toString(), Toast.LENGTH_SHORT).show();
//uri.getEncodedPath()
//                        uri:  content://com.android.externalstorage.documents/document/primary%3ADownload%2Fdx.xlsx
                        //readExcel(uri.getPath());
                        String actualfilepath = "";//fileuri.getPath();//getPath(fileuri);
                        try {

                            actualfilepath = PathUtils.getPath(getApplicationContext(), fileuri );
                            LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();
                            mPostValue.put("file",actualfilepath );

                            ///document/home:电线统计平台.xlsx
                            int  pos = actualfilepath.lastIndexOf('/');
                            String fileName = actualfilepath.substring(pos+1);

                            mPostValue.put("filename",fileName );


                            if (!importDianxianQinceHandler.getIsGetting())
                            {
                                importDianxianQinceHandler.setIsGetting(true);

                                String url = RequestUrlUtility.build(URL.PUT_DIANXIAN_QINGCE_IMPORT.replace("{project_id}", Main.project_id));
                                Network.Instance(getApplication()).putMultiForm( url,mPostValue,
                                        importDianxianQinceHandler,
                                        (handler,msg)->{
                                            handler.sendMessage(msg);
                                        });
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.e("import file ", "导入电线失败 " + ex.getMessage());
                            Toast.makeText(AddDxQingCeFromFileActivity.this, "导入电线失败！" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    }


        @Override
    protected Dialog onCreateDialog(int id) {
        if(id==openfileDialogId){
            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.mipmap.filedialog_root);	// 根目录图标
            images.put(OpenFileDialog.sParent, R.mipmap.filedialog_folder_up);	//返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.mipmap.filedialog_folder);	//文件夹图标
            images.put("wav", R.mipmap.filedialog_wavfile);	//wav文件图标
            images.put(OpenFileDialog.sEmpty, R.mipmap.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String filepath = bundle.getString("path");
                            setTitle(filepath); // 把文件路径显示在标题上
                        }
                    },
                    ".wav;",
                    images);
            return dialog;
        }
        return null;
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
//    private void readExcel(String fileName) {
//        try {
//            // todo: 这里是hardcode了。
//            File file3 = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/dx.xlsx");
//
//            if(! file3.exists()){
//                Log.i("Excel", "readExcel: 不存在 " + file3.toString());
//            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                int REQUEST_CODE_CONTACT = 101;
//                String[] permissions = {
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                //验证是否许可权限
//                for (String str : permissions) {
//                    if (AddDxQingCeFromFileActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                        //申请权限
//                        AddDxQingCeFromFileActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
//                        return;
//                    } else {
//                        //这里就是权限打开之后自己要操作的逻辑
//                        Log.i("Excel", " 权限OK " + file3.toString());
//                        InputStream inputStream = new FileInputStream(file3);
//                        Workbook workbook;
//                        if (fileName.endsWith(".xls")) {
//                            workbook = new HSSFWorkbook(inputStream);
//                        } else if (fileName.endsWith(".xlsx")) {
//                            workbook = new XSSFWorkbook(inputStream);
//                        } else {
//                            return;
//                        }
//                        Sheet sheet = workbook.getSheetAt(0);
//                        int rowsCount = sheet.getPhysicalNumberOfRows();
//                        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
////                        for (int r = 0; r < rowsCount; r++) {
////                            Row row = sheet.getRow(r);
////                            CellValue v0 = formulaEvaluator.evaluate(row.getCell(0));
////                            CellValue v1 = formulaEvaluator.evaluate(row.getCell(1));
////                            if(v0 != null){
////                                Log.i("Excel", "readExcel: " + v0.getStringValue() +"坐标：" + v0.get  );
////                            }
////                            if(v1 != null){
////                                Log.i("Excel", "readExcel: "  + v1.getStringValue());
////                            }
////                        }
//                        //解析每行， 第0、1两行是标题，从第2行
//                        for (int r = 2; r < rowsCount; r++) {
//                            Row row = sheet.getRow(r);
//                            //第一列 电缆编号
//                            CellValue v0 = formulaEvaluator.evaluate(row.getCell(0));
//                            if (v0 != null) {
//                                Log.i("Excel", "=====电缆编号: " + v0.getStringValue()  );
//                            }
//                            //第2列 起点
//                            CellValue v1 = formulaEvaluator.evaluate(row.getCell(1));
//                            if (v0 != null) {
//                                Log.i("Excel", "起点: " + v1.getStringValue()  );
//                            }
//                            //第3列 终点
//                            CellValue v2 = formulaEvaluator.evaluate(row.getCell(2));
//                            if (v0 != null) {
//                                Log.i("Excel", "终点: " + v2.getStringValue()  );
//                            }
//                            //第4列 型号
//                            CellValue v3 = formulaEvaluator.evaluate(row.getCell(3));
//                            if (v0 != null) {
//                                Log.i("Excel", "型号: " + v2.getStringValue()  );
//                            }
//                            //第5列 芯数x截面
//                            CellValue v4 = formulaEvaluator.evaluate(row.getCell(4));
//                            if (v0 != null) {
//                                Log.i("Excel", "芯数x截面: " + v4.getStringValue()  );
//                            }
//                            //第6列 长度
//                            CellValue v5 = formulaEvaluator.evaluate(row.getCell(5));
//                            if (v0 != null) {
//                                Log.i("Excel", "长度: " + v5.getStringValue()  );
//                            }
//
//                            //第6列 钢管冗余
//                            CellValue v6 = formulaEvaluator.evaluate(row.getCell(6));
//                            if (v0 != null) {
//                                Log.i("Excel", "钢管冗余: " + v6.getStringValue()  );
//                            }
//                            //第6列 软管冗余
//                            CellValue v7 = formulaEvaluator.evaluate(row.getCell(7));
//                            if (v0 != null) {
//                                Log.i("Excel", "软管冗余: " + v6.getStringValue()  );
//                            }
//                        }
//
//                        workbook.close();
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @SuppressLint("HandlerLeak")
    class ImportDianxianQinceHandler extends Handler {

        private boolean bIsGetting = false;

        public boolean getIsGetting()
        {
            return bIsGetting;
        }

        public void setIsGetting(boolean getting)
        {
            bIsGetting = getting;
        }

        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            //////////////////////
            String errorMsg = "";

            try {

                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);
                if (errorMsg!= null)
                {//failed
                    Log.d("导入电线失败:", errorMsg);
                    Toast.makeText(AddDxQingCeFromFileActivity.this, "导入电线失败！" + errorMsg, Toast.LENGTH_LONG).show();

                    return;
                }

                Result result= (Result)(msg.obj);
                BaseResponse baseResponse = CommonUtility.objectToJavaObject(result.getData(),BaseResponse.class);

                Toast.makeText(AddDxQingCeFromFileActivity.this, "导入电线成功！", Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //do something
                        AddDxQingCeFromFileActivity.this.finish();
                    }
                }, 2000);    //延时2s执行
            }
            catch (Exception ex)
            {
                Log.d("导入电线失败:", ex.getMessage());
            }
            finally {
                setIsGetting(false);
            }
        }//handle message

    }
}