package com.rya.ryamobilesafe.engin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ProcessProvider {
    public static int getRunningProcess(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    public static long getTotleProcessSpace(Context context) {
        /*ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;
        return totalMem;*/  //需要API16以上。
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;
        long totalSpace = 0;
        try {
            inputStream =  new FileInputStream("proc/meminfo");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            char[] dataLine = bufferedReader.readLine().toCharArray();

            StringBuffer totalSpaceString = new StringBuffer();

            for (char c : dataLine) {
                if (c >= '0' && c <= '9') {
                    totalSpaceString.append(c);
                }
            }
            totalSpace = Integer.parseInt(totalSpaceString.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null && bufferedReader != null) {
                try {
                    inputStream.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return totalSpace * 1024;
    }

    public static long getAvailProcessSpace(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        return availMem;
    }

    /**
     * @param context
     * @return  当前运行程序的信息ProcessInfo List
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        List<ProcessInfo> processInfosList = new ArrayList<>();

        //获取ActivityManager
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取PackageManager
        PackageManager packageManager = context.getPackageManager();

        //获取当前运行的进程

//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ProcessManager.getRunningAppProcessInfo(context);

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();

            //设置程序占用内存
            Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            Debug.MemoryInfo memoryInfo = memoryInfos[0];
            long totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
            processInfo.setMemorySize(totalPrivateDirty * 1024);

            //程序名 == 程序包名
            processInfo.setPackageName(runningAppProcessInfo.processName);

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(processInfo.getPackageName(),
                        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                //设置程序名 -- applicationinfo.loadLabel(pkName);
                processInfo.setName(applicationInfo.loadLabel(packageManager).toString());

                //设置图标
                processInfo.setIcon(applicationInfo.loadIcon(packageManager));

                //判断是否系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.setSystem(true);
                } else {
                    processInfo.setSystem(false);
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //异常处理
                processInfo.setName(processInfo.getPackageName());
                Drawable defIcon = context.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.setIcon(defIcon);
            }
            //把进程信息添加到List中
            processInfosList.add(processInfo);
        }
        return processInfosList;
    }

    public static void killProcessList(Context context, List<ProcessInfo> killProcessList) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ProcessInfo info : killProcessList) {
            activityManager.killBackgroundProcesses(info.getPackageName());
        }
    }


    /**
     * 杀死所有进程
     *
     * @param context 上下文环境
     */
    public static void killProcessAll(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = ProcessManager.getRunningAppProcessInfo(context);
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfo) {
            if (info.processName.equals(context.getPackageName())) {
                continue;
            }
            activityManager.killBackgroundProcesses(info.processName);
        }

    }
}