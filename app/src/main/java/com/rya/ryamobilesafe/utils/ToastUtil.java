package com.rya.ryamobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ToastUtil {
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
