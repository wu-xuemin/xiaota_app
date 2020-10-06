package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.common.CallbackBundle;
import com.zhihuta.xiaota.common.CommonService;
import com.zhihuta.xiaota.common.OpenFileDialog;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddDxQingCeFromFileActivity extends AppCompatActivity {

    static private int openfileDialogId = 0;

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
                 * failed，文件系统的访问是个问题。。。
                 */
                readExcel("/storage/emulated/0/Download/test.xlsx");
            }
        });


//
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
    private void readExcel(String fileName) {
        try {
            InputStream inputStream = new FileInputStream(fileName);
            Workbook workbook;
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                return;
            }
            Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                CellValue v0 = formulaEvaluator.evaluate(row.getCell(0));
                CellValue v1 = formulaEvaluator.evaluate(row.getCell(1));
                Log.i("Excel", "readExcel: " + v0.getStringValue() + "," + v1.getStringValue());
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}