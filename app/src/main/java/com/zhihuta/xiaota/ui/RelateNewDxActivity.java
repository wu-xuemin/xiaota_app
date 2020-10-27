package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.common.Constant;

import java.io.Serializable;
import java.util.ArrayList;

public class RelateNewDxActivity extends AppCompatActivity {

    private ArrayList<DianxianQingCeData> mDianxianTobeSelectList;
    private Button mOkTobeSelectBt;

    private DianXianQingceAdapter mDianXianQingceAdapter;
    private ArrayList<Boolean> checkedList = new ArrayList<>(); //用于记录哪些被选中了
    // 选中的电线，传回去
    private ArrayList<DianxianQingCeData> mCheckedDxList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_new_dx);
        //返回前页按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        showTobeSelectedDxList();
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
//        DianxianQingCeData mDxData1 = new DianxianQingCeData();

        mDianxianTobeSelectList = new ArrayList<>();
        for(int i=0; i<10; i++) {
            DianxianQingCeData mDxData1 = new DianxianQingCeData(); //这个放在循环外面，所有mDxData1对象 会被编译优化成最后一个对象的
            mDxData1.setId(i);
            mDxData1.setSerial_number("DX22候选" + i);
            mDxData1.setStart_point("乌鲁木齐" + i);
            mDxData1.setEnd_point("北京B点" + i);
            mDxData1.setParts_code("型号S" + i);
            mDxData1.setLength("3100km" +i);
            mDxData1.setWickes_cross_section("4X180");
            mDxData1.setSteel_redundancy("55M");
            mDxData1.setHose_redundancy("15M");
            mDxData1.setFlag(Constant.FLAG_TOBE_SELECT_DX);
//        mDxData1.setHoseRedundancy("5M");

            mDianxianTobeSelectList.add(mDxData1);
            Log.d("newDx", "mDxData1 getDxNumber:" + mDxData1.getSerial_number());

            /**
             * 备选的电线，初始状态都是未选
             */
            checkedList.add(false);
        }
        mOkTobeSelectBt = (Button) findViewById(R.id.button_OK_to_add_dxTobeSelect22 );
        mOkTobeSelectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int k=0; k< checkedList.size(); k++){
                    if(checkedList.get(k)){
                        //该电线由待选 改为 已选
                        mDianxianTobeSelectList.get(k).setFlag(Constant.FLAG_RELATED_DX);
                        mCheckedDxList.add(mDianxianTobeSelectList.get(k));
                    }

                }
                Intent intent = getIntent();
                intent.setClass(RelateNewDxActivity.this, RelatedDxActivity.class);
                intent.putExtra("mCheckedDxList", (Serializable) mCheckedDxList);

                RelateNewDxActivity.this.setResult(RESULT_OK, intent);
                RelateNewDxActivity.this.finish();

            }
        });
    }
    private void showTobeSelectedDxList(){
        //候选的电线列表
        RecyclerView mDxRV = (RecyclerView) findViewById(R.id.rv_dx_tobeSelect);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        mDianXianQingceAdapter = new DianXianQingceAdapter(mDianxianTobeSelectList,this);
        mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDxRV.setAdapter(mDianXianQingceAdapter);
        // 设置item及item中控件的点击事件
        mDianXianQingceAdapter.setOnItemClickListener(MyItemClickListener);
    }

    /**
     * 备选电线item 里的控件点击监听事件
     */
    private DianXianQingceAdapter.OnItemClickListener MyItemClickListener = new DianXianQingceAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, DianXianQingceAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){

                case R.id.checkBox_dx_to_be_select:
                    Toast.makeText(RelateNewDxActivity.this,"你点击了 备选电线的 checkbox" + (position+1),Toast.LENGTH_SHORT).show();
                    //对原先的值取反，比如原先是未选中则改为选中。
                    checkedList.set(position,  !checkedList.get(position) );
                    break;
                default:
                    Toast.makeText(RelateNewDxActivity.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };
}