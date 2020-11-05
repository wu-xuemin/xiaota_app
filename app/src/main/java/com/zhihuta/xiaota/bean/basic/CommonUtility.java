package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.JSONObject;
import com.zhihuta.xiaota.bean.response.LoginResponseData;

import org.apache.poi.ss.formula.functions.T;

public class CommonUtility {

    public static <T> T objectToJavaObject( Object jsonObject, Class<T> cls)
    {
        if (jsonObject == null)
            return null;

        if (jsonObject instanceof  JSONObject)
        {
            return JSONObject.toJavaObject((JSONObject)jsonObject, cls);
        }
        else
        {
            return ((T) jsonObject);
        }
    }

}
