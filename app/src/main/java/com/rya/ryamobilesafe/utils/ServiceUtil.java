package com.rya.ryamobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ServiceUtil {

    private static ActivityManager mActivityManager;

    /**
     * @param context       上下文环境
     * @param serviceName   服务名称
     * @return      判断服务当前状态
     * 开启 -- True
     * 关闭 -- False
     */
    public static boolean checkService(Context context, String serviceName) {

        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = mActivityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (serviceName.equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}