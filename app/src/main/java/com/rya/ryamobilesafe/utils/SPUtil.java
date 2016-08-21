package com.rya.ryamobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SPUtil {

    //获取sp，需要context环境
    private static SharedPreferences sp;

    /**
     * @param context   上下文环境
     * @param key       键--存储节点名称
     * @param value     值--存储节点的值boolean
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if(sp == null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    /**
     * @param context   上下文环境
     * @param key       键--存储节点名称
     * @param defValue  默认返回值
     * @return      返回存储节点的值boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null) {
            sp =  context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * @param context   上下文环境
     * @param key       键--存储节点名称
     * @param value     值--存储节点的值String
     */
    public static void putString(Context context, String key, String value) {
        if(sp == null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    /**
     * @param context   上下文环境
     * @param key       键--存储节点名称
     * @param defValue  默认返回值
     * @return      返回存储节点的值boolean
     */
    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp =  context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public static void remove(Context Context, String key) {
        if (sp == null) {
            sp = Context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.apply();
    }
}
