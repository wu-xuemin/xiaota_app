package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.bean.basic.DistanceData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddNewLujingActivity extends AppCompatActivity {

    private DistanceAdapter mDistanceAdapter;
    private ArrayList<DistanceData> mDistanceList = new ArrayList<>();

    private Button mButtonScanToAddXianduan;
    private static final int SCAN_QRCODE_START = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lujing);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();//初始化控件
    }


    private void initViews() {


        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDistanceList = (ArrayList<DistanceData>) bundle.getSerializable("mDistanceList");

        if(mDistanceList !=null) {
            Toast.makeText(this, "  得到 间距列表 size:" + mDistanceList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, " 间距 列表为空！！！" , Toast.LENGTH_SHORT).show();
        }
        //间距列表
        RecyclerView mDistanceRV = (RecyclerView) findViewById(R.id.rv_distance);
        LinearLayoutManager manager5 = new LinearLayoutManager(this);
        manager5.setOrientation(LinearLayoutManager.VERTICAL);
        mDistanceRV.setLayoutManager(manager5);
        mDistanceAdapter = new DistanceAdapter(mDistanceList);
        mDistanceRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDistanceRV.setAdapter(mDistanceAdapter);

        mButtonScanToAddXianduan = (Button) findViewById(R.id.button_scan_to_add_xianduan);
        mButtonScanToAddXianduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewLujingActivity.this, ZxingScanActivity.class);
//                startActivity(intent);
                startActivityForResult(intent,SCAN_QRCODE_START);
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCAN_QRCODE_START:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<DistanceData> mDistanceList;
                    // 取出Intent里的扫码结果
//                    List<DistanceData> list = (List<DistanceData>) getIntent().getSerializableExtra("mScanResultDistanceList");
                    List<DistanceData> list = (List<DistanceData>) data.getSerializableExtra("mScanResultDistanceList");
                    for(int i =0; i<list.size(); i++ ) {
                        Toast.makeText(this, " 扫码获得的结果信息1：" + list.get(i).getDistanceName(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

}