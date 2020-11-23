package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuta.xiaota.R;
import com.zhihuta.xiaota.adapter.DianXianQingceAdapter;
import com.zhihuta.xiaota.bean.basic.DianxianQingCeData;
import com.zhihuta.xiaota.bean.basic.LujingData;
import com.zhihuta.xiaota.common.Constant;
import com.zhihuta.xiaota.common.RequestUrlUtility;
import com.zhihuta.xiaota.common.URL;
import com.zhihuta.xiaota.net.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RelatedDxActivity extends AppCompatActivity {

    private static String TAG = "RelatedDxActivity";
    private ArrayList<DianxianQingCeData> mDianxianList;

    private DianXianQingceAdapter mDianXianAdapter;
    private RecyclerView mDxRV;
    private Button mAddNewDxBt;
    private Button mOKtoConfirmRelateDxBt; //
    private static final int RELATE_NEW_DX = 1;

    private Network mNetwork;
    //从路径界面传给电线界面的路径信息，在电线界面查看已绑定的电线，也在该路径里添加电线
    private LujingData mLujing;

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
        mNetwork = Network.Instance(getApplication());
        initViews();
        showDxList();
    }
    @Override
    protected void onResume() {
        super.onResume();

        getDxList();///
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

        Intent intent = getIntent();
        mLujing = (LujingData) intent.getExtras().getSerializable("mLujing");

        TextView textViewTitle = findViewById(R.id.textView_lujingNameShow);
        textViewTitle.setText(mLujing.getName());

        mDianxianList = new ArrayList<>();
        getDxList();
        mAddNewDxBt = (Button) findViewById(R.id.button_to_add_new_Dx);
        mAddNewDxBt.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             Intent intent = new Intent(RelatedDxActivity.this, RelateNewDxActivity.class);
                                             intent.putExtra("mLujing", (Serializable) mLujing);
                                             intent.putExtra("DianxianHasbeenRelated",mDianxianList);

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

    private void getDxList(){
        /**
         * 获取该路径的电线列表
         */
            LinkedHashMap<String, String> quereyValue = new LinkedHashMap<>();

            String theUrl = RequestUrlUtility.build(URL.GET_DX_OF_LUJING
                    .replace("{lujingId}", String.valueOf(mLujing.getId()))
                    .replace("{dxSN}", "").replace("{dxPartsCode}", "")
            );

            Log.i(TAG, "获取该路径的电线列表 " + theUrl);
            mNetwork.fetchDxListOfLujing(theUrl, quereyValue, new GetRelatedDxListOfLujingHandler());///

    }

    private void showDxList(){
        //电线列表
        mDxRV = (RecyclerView) findViewById(R.id.rv_dx);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mDxRV.setLayoutManager(manager);
        mDianXianAdapter = new DianXianQingceAdapter(mDianxianList,this,Constant.REQUEST_CODE_LUJING_RELATED_WIRES);
        if (mDxRV.getItemDecorationCount() == 0)
        {
            mDxRV.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }
        mDxRV.setAdapter(mDianXianAdapter);

        // 设置item及item中控件的点击事件
        mDianXianAdapter.setOnItemClickListener(MyItemClickListener);

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
//                    Toast.makeText(RelatedDxActivity.this," 已选电线的 删除:" + (position+1),Toast.LENGTH_SHORT).show();

                    android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RelatedDxActivity.this);
                    alertDialogBuilder.setTitle("确认删除电线" + mDianxianList.get(position).getSerial_number() + "吗？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LinkedHashMap<String, String> mPostValue = new LinkedHashMap<>();

                                    String theUrl = RequestUrlUtility.build(URL.DEL_DX_OF_LUJING.replace("{id}",String.valueOf(mLujing.getId()))
                                    .replace("{wires_id}",String.valueOf( mDianxianList.get(position).getId())));

                                    mNetwork.delete(theUrl,null, new Handler(){
                                        @Override
                                        public void handleMessage(final Message msg) {
                                            String errorMsg = "";

                                            try {

                                                errorMsg = RequestUrlUtility.getResponseErrMsg(msg);

                                                if (errorMsg!= null)
                                                {
                                                    Log.d("移除电线失败:", errorMsg);
                                                    Toast.makeText(RelatedDxActivity.this, "移除电线失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Toast.makeText(RelatedDxActivity.this, "移除电线成功", Toast.LENGTH_SHORT).show();

                                                //删除成功，再刷新一次
                                                getDxList();

                                            } catch (Exception ex) {
                                                Log.d("移除电线失败:", ex.getMessage());
                                                Toast.makeText(RelatedDxActivity.this, "移除电线失败！" + errorMsg, Toast.LENGTH_SHORT).show();
                                            }
                                            finally {
                                             }
                                        }

                                    }, (handler, msg)->{

                                        handler.sendMessage(msg);
                                    });
                                }
                            })
                            .show();
                     //mDianxianList.remove(position);
                    //mDianXianAdapter.notifyDataSetChanged(); //刷新列表
                    //直接删除可能会导致后面的下标对应的poistion，id出错，最好是重新刷新。

                    break;

                default:
                    View view = getLayoutInflater().inflate(R.layout.dialog_dx, null);
                    final TextView tvDxSName = (TextView) view.findViewById(R.id.textView_dilag_bianhao);
                    final TextView tvDxQidian = (TextView) view.findViewById(R.id.textView_dialog_qidian);
                    final TextView tvDxZhongdian = (TextView) view.findViewById(R.id.textView15);
                    final TextView tvDxModel = (TextView) view.findViewById(R.id.textView16);
                    final TextView tvDxXinshuJiemian = (TextView) view.findViewById(R.id.textView17);
                    final TextView tvDxLength = (TextView) view.findViewById(R.id.textView18);
                    final TextView tvDxSteel = (TextView) view.findViewById(R.id.textView19);
                    final TextView tvDxHose = (TextView) view.findViewById(R.id.textView20);
                    tvDxSName.setText( mDianxianList.get(position).getSerial_number());
                    tvDxQidian.setText( mDianxianList.get(position).getStart_point());
                    tvDxZhongdian.setText( mDianxianList.get(position).getEnd_point());
                    tvDxModel.setText( mDianxianList.get(position).getParts_code());
                    tvDxXinshuJiemian.setText( mDianxianList.get(position).getWickes_cross_section());
                    tvDxLength.setText( mDianxianList.get(position).getLength());
                    tvDxSteel.setText( mDianxianList.get(position).getSteel_redundancy());
                    tvDxHose.setText( mDianxianList.get(position).getHose_redundancy());
                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(RelatedDxActivity.this);
                    alertDialogBuilder2.setTitle("电线详情")
                            .setView(view)
                            .setPositiveButton("关闭",null)
//                            .setNegativeButton("OK", null)
                            .show();
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

    @SuppressLint("HandlerLeak")
    class GetRelatedDxListOfLujingHandler extends Handler {


        @Override
        public void handleMessage(final Message msg) {
//            if(mLoadingProcessDialog != null && mLoadingProcessDialog.isShowing()) {
//                mLoadingProcessDialog.dismiss();
//            }

            if (msg.what == Network.OK) {
                Log.d("GetDxListOfLujingHand", "OKKK");
                mDianxianList = (ArrayList<DianxianQingCeData>)msg.obj;

                if (mDianxianList == null) {
                    Log.d(TAG, "handleMessage: " + "路径的电线数量为0或异常"  );
                    mDianxianList = new ArrayList<>();
                } else {
                    if (mDianxianList.size() == 0) {
                        Toast.makeText(RelatedDxActivity.this, "已关联的电线数量为0！", Toast.LENGTH_SHORT).show();
                    } else {

                        mDianXianAdapter = new DianXianQingceAdapter(mDianxianList, RelatedDxActivity.this,Constant.REQUEST_CODE_LUJING_RELATED_WIRES);
                        if (mDxRV.getItemDecorationCount() == 0)
                        {
                            mDxRV.addItemDecoration(new DividerItemDecoration(RelatedDxActivity.this, DividerItemDecoration.VERTICAL));
                        }
                        mDxRV.setAdapter(mDianXianAdapter);
                        mDianXianAdapter.notifyDataSetChanged();
                        mDianXianAdapter.setOnItemClickListener(MyItemClickListener);
                    }
                }
            } else {
                String errorMsg = (String)msg.obj;
                Log.d("GetDxListOfLujingHd NG:", errorMsg);
                Toast.makeText(RelatedDxActivity.this, "电线获取失败！" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}