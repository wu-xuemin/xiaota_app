package com.zhihuta.xiaota.bean.response;


import com.zhihuta.xiaota.bean.basic.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2018/3/28.
 */

public class UserResponseData {
    private List<UserData> list = new ArrayList<>();

    public List<UserData> getList() {
        return list;
    }
}
