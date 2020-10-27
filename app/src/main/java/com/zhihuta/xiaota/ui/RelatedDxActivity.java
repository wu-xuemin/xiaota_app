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
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.common.Constant;

import java.util.ArrayList;
import java.util.List;

public class RelatedDxActivity extends AppCompatActivity {

    private ArrayList<DianxianQingCeData> mDianxianList;

    private DianXianQingceAdapter mDianXianQingceAdapter;
    private Button mAddNewDxBt;
    private Button mOKtoConfirmRelateDxBt; //
    private static final int RELATE_NEW_DX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_dx);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        showDxList();

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

    private void initViews() {

        TextView textViewTitle = findViewById(R.id.textView_lujingNameShow);
        textViewTitle.setText("路径ABC");

        mDianxianList = new ArrayList<>();
        for(int k=0;k<15; k++) {
            DianxianQingCeData mDxData1 = new DianxianQingCeData();
            mDxData1.setId(k);
            mDxData1.setSerial_number("DX2211-" + k );
            mDxData1.setStart_point("杭州A点" + k);
            mDxData1.setEnd_point("岳阳A点" + k);
            mDxData1.setParts_code("型号K");
            mDxData1.setLength("500km");
            mDxData1.setWickes_cross_section("3X185");
            mDxData1.setSteel_redundancy("10M");
            mDxData1.setHose_redundancy("5M");
            mDxData1.setFlag(Constant.FLAG_RELATED_DX);

            mDianxianList.add(mDxData1);
        }
        mAddNewDxBt = (Button) findViewById(R.id.button_to_add_new_Dx);
        mAddNewDxBt.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             Intent intent = new Intent(RelatedDxActivity.this, RelateNewDxActivity.class);
                                             startActivityForResult(intent,RELATE_NEW_DX);
                                         }
                                     });

        mOKtoConfirmRelateDxBt= (Button) findViewById(R.id.button_OK_to_relate_dx);
        mOKtoConfirmRelateDxBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelatedDxActivity.this.finish();
            }
        });
    }

    private void showDxList(){
        //电线列表
        RecyclerView mDxRV = (RecyclerView) findViewById(R.id.rv_dx);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        mDianXianQingceAdapter = new DianXianQingceAdapter(mDianxianList,this);
        mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDxRV.setAdapter(mDianXianQingceAdapter);

        // 设置item及item中控件的点击事件
        mDianXianQingceAdapter.setOnItemClickListener(MyItemClickListener);

    }

    /**
     * 已选电线item 里的控件点击监听事件
     */
    private DianXianQingceAdapter.OnItemClickListener MyItemClickListener = new DianXianQingceAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, DianXianQingceAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.buttonDxDelete:
                    Toast.makeText(RelatedDxActivity.this," 已选电线的 删除:" + (position+1),Toast.LENGTH_SHORT).show();
                    mDianxianList.remove(position);
                    mDianXianQingceAdapter.notifyDataSetChanged(); //刷新列表
                    break;

                default:
                    Toast.makeText(RelatedDxActivity.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RELATE_NEW_DX:
                if (resultCode == RESULT_OK)
                {
                    // 取出Intent里的选择电线的结果
                    List<DianxianQingCeData> list = (List<DianxianQingCeData>) data.getSerializableExtra("mCheckedDxList");
                    for(int i =0; i<list.size(); i++ ) {
                        Toast.makeText(this, " 选中了电线：" + list.get(i).getSerial_number(), Toast.LENGTH_LONG).show();
                        //把扫码新加的各个间距加入间距列表
                        mDianxianList.add(list.get(i));
                        showDxList();
                    }
                }
                break;
            default:
                break;
        }
    }

}