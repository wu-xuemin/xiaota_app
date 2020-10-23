package com.zhihuta.xiaota.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.SettingFragment;
import com.zhihuta.xiaota.WeixinFragment;
import com.zhihuta.xiaota.adapter.DistanceAdapter;
import com.zhihuta.xiaota.adapter.LujingAdapter;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.DistanceData;
import com.zhihuta.xiaota.bean.basic.LujingData;

import java.io.Serializable;
import java.util.ArrayList;

//public class DianxianQingCe extends AppCompatActivity {
public class Main extends FragmentActivity implements View.OnClickListener {
    //声明3个Tab的布局文件
    private LinearLayout mTabDxQingce;
//    private LinearLayout mTabFrd;
    private LinearLayout mTabLujingMoxing;
    private LinearLayout mTabJisuan;

    //声明3个Tab的ImageButton
    private ImageButton mQingceImg;
    private ImageButton mLujingMoxingImg;
    private ImageButton mJisuanImg;

    //声明3个Tab分别对应的Fragment
    private Fragment mFragDxQingce;
    private Fragment mFragLujingMoxing;
    private Fragment mFragJisuan;

    private LinearLayout mLayoutQingCe;
    private LinearLayout mLayoutLujing;
    private LinearLayout mLayoutCompute; //计算页面， 包含 计算路径的电线长度、计算两点间距 两部分。

    // 计算路径的电线长度
    private LinearLayout mLayoutComputeDx;
    // 计算两点的间距长度
    private LinearLayout mLayoutComputeDistance;
    // "计算路径的电线长度" 的按钮
    private Button mComputeDxBt;
    // "计算两点间距" 的按钮
    private Button mComputeDistanceBt;

    // 电线 "手动添加" 按钮
    private Button addDxByHandBt;
    // 电线 "从文件导入" 按钮
    private Button addDxFromFileBt;
    // 建全新路径 按钮
    private Button addTotalNewLujingBt;


    private Button mComputeScanBt;

    private DianXianQingceAdapter mQingceAdapter;
    private ArrayList<DianxianQingCeData> mDianxianQingCeList = new ArrayList<>();

//    private OrderAdapter mOrderAdapter;
//    private ArrayList<OrderData> mOrderList = new ArrayList<>();

    private LujingAdapter mLujingAdapter;
    private ArrayList<LujingData> mLujingList = new ArrayList<>();


    private LujingAdapter mLujingShaixuanAdapter;
    private ArrayList<LujingData> mLujingShaixuanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(1);//默认选中第2个Tab


    }

    private void initEvents() {
        //初始化3个Tab的点击事件
        mTabDxQingce.setOnClickListener(this);
        mTabLujingMoxing.setOnClickListener(this);
        mTabJisuan.setOnClickListener(this);
    }

    private void initViews() {
        //初始化3个Tab的布局文件
        mTabDxQingce = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabLujingMoxing = (LinearLayout) findViewById(R.id.id_tab_lujing_moxing);
        mTabJisuan = (LinearLayout) findViewById(R.id.id_tab_setting);

        //初始化3个ImageButton
        mQingceImg = (ImageButton) findViewById(R.id.id_tab_cx_qingce_img);
        mLujingMoxingImg = (ImageButton) findViewById(R.id.id_tab_lujing_moxing_img);
        mJisuanImg = (ImageButton) findViewById(R.id.id_tab_setting_img);

        addDxByHandBt = (Button) findViewById(R.id.button_add_dx_by_hand);
        addDxByHandBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, AddDxQingCeByHandActivity.class);
                startActivity(intent);
            }
        });

        mComputeDxBt = (Button) findViewById(R.id.button_compute_dx);
        mComputeDxBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDx.setVisibility(View.VISIBLE);
                mLayoutComputeDistance.setVisibility(View.GONE);
            }
        });
        mComputeDistanceBt = (Button) findViewById(R.id.button_compute_liangdian);
        mComputeDistanceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutComputeDistance.setVisibility(View.VISIBLE);
                mLayoutComputeDx.setVisibility(View.GONE);
            }
        });

        addDxFromFileBt = (Button) findViewById(R.id.button_add_dx_from_file);
        addDxFromFileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, AddDxQingCeFromFileActivity.class);

                startActivity(intent);
            }
        });
        /// 全新路径和基于已有路径 来新建路径，两个是否可以用同个acitivity？？
        addTotalNewLujingBt = (Button) findViewById(R.id.button_add_new_lujing);
        addTotalNewLujingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, AddNewLujingActivity.class);

                ArrayList<DistanceData> mDistanceList;
                DistanceData mDistanceData1 = new DistanceData();
                mDistanceData1.setId(1);
                mDistanceData1.setDistanceName("间距杭州上海211");
                mDistanceData1.setDistanceNumber("JJ_0981");
                DistanceData mDistanceData2 = new DistanceData();
                mDistanceData2.setId(2);
                mDistanceData2.setDistanceName("间距杭州上海222");
                mDistanceData2.setDistanceNumber("JJ_0982");
                mDistanceList = new ArrayList<>();
                mDistanceList.add(mDistanceData1);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData2);
                mDistanceList.add(mDistanceData1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDistanceList", (Serializable) mDistanceList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //获取传递过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDianxianQingCeList = (ArrayList<DianxianQingCeData>) bundle.getSerializable("mDianxianQingCeList");

        if(mDianxianQingCeList !=null) {
            Toast.makeText(this, "得到 电线清单 size:" + mDianxianQingCeList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "电线清单 为空！！！" , Toast.LENGTH_SHORT).show();
        }
        //电线列表
        RecyclerView mQingceRV = (RecyclerView) findViewById(R.id.rv_dianxian);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mQingceRV.setLayoutManager(manager);
        mQingceAdapter = new DianXianQingceAdapter(mDianxianQingCeList,this);
        mQingceRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mQingceRV.setAdapter(mQingceAdapter);

        //获取传递过来的路径信息
        mLujingList = (ArrayList<LujingData>) bundle.getSerializable("mLujingList");

        if(mLujingList !=null) {
            Toast.makeText(this, "       得到 路径列表 size:" + mLujingList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "   路径列表为空！！！" , Toast.LENGTH_SHORT).show();
        }
        //路径列表
        RecyclerView mLujingRV = (RecyclerView) findViewById(R.id.rv_lujing);
        LinearLayoutManager manager3 = new LinearLayoutManager(this);
        manager3.setOrientation(LinearLayoutManager.VERTICAL);
        mLujingRV.setLayoutManager(manager3);
//        mLujingAdapter = new LujingAdapter(mLujingList);
        mLujingAdapter = new LujingAdapter(mLujingList,this);
        mLujingRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mLujingRV.setAdapter(mLujingAdapter);
        // 设置item及item中控件的点击事件
        mLujingAdapter.setOnItemClickListener(MyItemClickListener);


        mLujingShaixuanList = (ArrayList<LujingData>) bundle.getSerializable("mLujingShaixuanList");
        if(mLujingShaixuanList !=null) {
            Toast.makeText(this, "    得到    筛选的 路径列表 size:" + mLujingShaixuanList.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "   路径列表为空！！！" , Toast.LENGTH_SHORT).show();
        }
        //计算路径，扫描筛选得到的路径列表
        RecyclerView mLujingShaixuanRV = (RecyclerView) findViewById(R.id.rv_lujing_compute);
//        RecyclerView mDistanceRV = (RecyclerView) findViewById(R.id.rv_distance);  /// ====
        LinearLayoutManager manager4 = new LinearLayoutManager(this);
        manager4.setOrientation(LinearLayoutManager.VERTICAL);
        mLujingShaixuanRV.setLayoutManager(manager4);
//        mLujingShaixuanAdapter = new LujingAdapter(mLujingShaixuanList);
        mLujingShaixuanAdapter = new LujingAdapter(mLujingShaixuanList,this);
        mLujingShaixuanRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mLujingShaixuanRV.setAdapter(mLujingShaixuanAdapter);

        mLayoutQingCe = (LinearLayout)findViewById(R.id.layout_dianxian_qingce_id);
//        mLayoutOrder = (LinearLayout)findViewById(R.id.layout_order_id);
        mLayoutLujing = (LinearLayout)findViewById(R.id.layout_lujing);

        mLayoutCompute = (LinearLayout)findViewById(R.id.layout_compute);
        mLayoutComputeDx = (LinearLayout)findViewById(R.id.layout_compute_dianxian);
        mLayoutComputeDistance = (LinearLayout)findViewById(R.id.layout_compute_dis);

        initViewsCompute();
    }
    private void initViewsCompute() {
        mComputeScanBt = (Button) findViewById(R.id.button_compute_scan);
        mComputeScanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }



    /**
     * item＋item里的控件点击监听事件
     */
    private LujingAdapter.OnItemClickListener MyItemClickListener = new LujingAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, LujingAdapter.ViewName viewName, int position) {
            //viewName可区分item及item内部控件
            switch (v.getId()){
                case R.id.button_modify_lujing:
                    Toast.makeText(Main.this,"你点击了 修改路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_create_lujing_base_exist:
                    Toast.makeText(Main.this,"你点击了 新建路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button_delete_lujing:
                    Toast.makeText(Main.this,"你点击了 删除路径 按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(Main.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onItemLongClick(View v) {

        }
    };




    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将3个ImageButton置为灰色
        resetImgs();
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                selectTab(0);//当点击Tab就选中该的Tab
                break;
//            case R.id.id_tab_frd:
//                selectTab(1);
//                break;
            case R.id.id_tab_lujing_moxing:
                selectTab(1);
                break;
            case R.id.id_tab_setting:
                selectTab(2);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);

//        Button addDianxianQinCeButton = (Button) findViewById(R.id.button5);///
//        Button showDianxianQinCeButton = (Button) findViewById(R.id.button6);///
//        Button addLujinMoxingButton = (Button) findViewById(R.id.button4);///
//        Button showLujingMoxingButton = (Button) findViewById(R.id.button7);///
//        Button calculateDianXianLengthButton = (Button) findViewById(R.id.button8);///
//        Button showDianXianLengthButton = (Button) findViewById(R.id.button9);///
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //设置微信的ImageButton为绿色
                mQingceImg.setImageResource(R.mipmap.tab_dx_qingce_pressed);
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (mFragDxQingce == null) {
                    mFragDxQingce = new WeixinFragment();
//                    transaction.add(R.id.layout_dianxian_qingce_id, mFragWeinxin);
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragDxQingce);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFragDxQingce);
                }
                mLayoutQingCe.setVisibility(View.VISIBLE);
//                mLayoutOrder.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.GONE);
                mLayoutCompute.setVisibility(View.GONE);
                break;
            case 1:
                mLayoutQingCe.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.VISIBLE);
                mLayoutCompute.setVisibility(View.GONE);
                mLujingMoxingImg.setImageResource(R.mipmap.tab_lujing_moxing_pressed);
                if (mFragLujingMoxing == null) {
                    mFragLujingMoxing = new AddressFragment();
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragLujingMoxing);
                } else {
                    transaction.show(mFragLujingMoxing);
                }
//                Toast.makeText(this, "按下路径模型", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mLayoutQingCe.setVisibility(View.GONE);
                mLayoutLujing.setVisibility(View.GONE);
                mLayoutCompute.setVisibility(View.VISIBLE);

                // 在计算tab 默认看到的是计算路径电线长度，隐藏两点间距的
                mLayoutComputeDistance.setVisibility(View.GONE);
                mJisuanImg.setImageResource(R.mipmap.tab_compute_pressed);
                if (mFragJisuan == null) {
                    mFragJisuan = new SettingFragment();
                    transaction.add(R.id.layout_dianxian_qingce_id, mFragJisuan);
                } else {
                    transaction.show(mFragJisuan);
                }
//                Toast.makeText(this, "按下路径计算", Toast.LENGTH_SHORT).show();
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    //将3个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mFragDxQingce != null) {
            transaction.hide(mFragDxQingce);
        }
//        if (mFragFrd != null) {
//            transaction.hide(mFragFrd);
//        }
        if (mFragLujingMoxing != null) {
            transaction.hide(mFragLujingMoxing);
        }
        if (mFragJisuan != null) {
            transaction.hide(mFragJisuan);
        }
    }

    //将3个ImageButton置为灰色
    private void resetImgs() {
        mQingceImg.setImageResource(R.mipmap.tab_dx_qingce_normal);
//        mFrdImg.setImageResource(R.mipmap.tab_find_frd_normal);
        mLujingMoxingImg.setImageResource(R.mipmap.tab_address_normal);
        mJisuanImg.setImageResource(R.mipmap.tab_compute_normal);
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