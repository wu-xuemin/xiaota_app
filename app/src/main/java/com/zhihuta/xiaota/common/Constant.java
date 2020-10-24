package com.zhihuta.xiaota.common;

public class Constant {
    /**
     * 用于标记不同的场合
     * 不同的场合 显示不同的图标/按钮等
     * "ToBeSelectDx"   -- 候选电线界面 （在 新增路径-关联电线） -- 要显示  checkBox
     * "RelatedDx"      -- 已选电线界面 （在 新增路径-关联电线） -- 要显示 删除按钮
     */
    public static String FLAG_TOBE_SELECT_DX = "候选电线";
    public static String FLAG_RELATED_DX = "已选电线";
    public static String FLAG_QINGCE_DX = "清册中的电线"; // 这个和 候选电线 是一样的，只是显示在清册中。

    public static final int REQUEST_CODE_ADD_NEW_LUJING = 1;            //路径中心，添加 全新 新路径
    public static final int REQUEST_CODE_SCAN_TO_SHAIXUAN_LUJING = 2;    //路径中心，扫码筛选路径
}
