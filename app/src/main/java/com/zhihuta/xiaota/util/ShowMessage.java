package com.zhihuta.xiaota.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ShowMessage {


    public enum MessageType {
        TOAST,
        NOTIFICATION,
        DIALOG
    };

    public enum MessageDuring {
        SHORT,//0 ==> short
        LONG  //1 ==> long
    };

    public static void showToast(Context ctx, String msg, MessageDuring shortOrLong ) {
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showDialog(Activity activity, String msg) {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.create().show();
    }
}
