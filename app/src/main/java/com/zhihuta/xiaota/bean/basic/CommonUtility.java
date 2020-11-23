package com.zhihuta.xiaota.bean.basic;

import com.alibaba.fastjson.JSONObject;
import com.zhihuta.xiaota.bean.response.LoginResponseData;

import org.apache.poi.ss.formula.functions.T;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    public static boolean isPhone(String string) {
        if (string == null)
            return false;
        String regEx1 = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }
}
