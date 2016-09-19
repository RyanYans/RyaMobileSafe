package com.rya.ryamobilesafe.engin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.rya.ryamobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppInfoProvider {
    /**
     * @param context
     *
     * 返回AppInfo集合(包括--> 名称、包名、图标、内存、系统/用户应用)
     */
    public static ArrayList<AppInfo> getAppInfo(Context context) {
        ArrayList<AppInfo> appInfoArrayList = new ArrayList<AppInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> applicationInfos =
                packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        for (PackageInfo packageInfo : applicationInfos) {
            AppInfo appInfo = new AppInfo();
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;

            appInfo.setName(applicationInfo.loadLabel(packageManager).toString());
            appInfo.setPackageName(packageInfo.packageName);
            appInfo.setIcon(applicationInfo.loadIcon(packageManager));

            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                // 系统应用
                appInfo.setIsSystem(true);
            } else {
                // 非系统应用
                appInfo.setIsSystem(false);
            }
            appInfoArrayList.add(appInfo);
        }

        return appInfoArrayList;

    }
}
