package com.zhihuta.xiaota.bean.basic;

import android.content.ContextWrapper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    //distance_factor 0.6, 500
    public static void setDistanceToTriggerSync(SwipeRefreshLayout mSwipeRefreshLayout, ContextWrapper contextWrapper, float distance_factor, int trigger_distance)
    {

        try {

            ViewTreeObserver vto = mSwipeRefreshLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                public void onGlobalLayout() {
                    final DisplayMetrics metrics = contextWrapper.getResources().getDisplayMetrics();
                    Float mDistanceToTriggerSync = Math.min(((View) mSwipeRefreshLayout.getParent()).getHeight() * distance_factor, trigger_distance * metrics.density);
                    try {
//                        Field field = SwipeRefreshLayout.class.getDeclaredField("mTotalDragDistance");
//                        field.setAccessible(true);
//                        field.setFloat(mSwipeRefreshLayout, mDistanceToTriggerSync);

                        mSwipeRefreshLayout.setDistanceToTriggerSync(mDistanceToTriggerSync.intValue());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ViewTreeObserver obs = mSwipeRefreshLayout.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
}
