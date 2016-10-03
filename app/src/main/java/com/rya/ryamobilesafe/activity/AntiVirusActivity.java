package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.dao.AntiVirusDao;
import com.rya.ryamobilesafe.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AntiVirusActivity extends Activity {

    private static final int SCAN = 100;
    private static final int SCAN_FISH = 101;
    private RotateAnimation mRotateAnimation;
    private ImageView iv_antivirus_scan;
    private LinearLayout ll_antivirus_text;
    private ProgressBar pb_antivirus;
    private TextView tv_antivirus;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_antivirus.setText(scanInfo.name);
                    TextView textView = new TextView(getApplicationContext());
                    if (scanInfo.isVirus) {
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒 " + scanInfo.pkgName);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描成功 " + scanInfo.pkgName);
                    }
                    ll_antivirus_text.addView(textView, 0);
                    break;
                case SCAN_FISH:
                    tv_antivirus.setText("扫描完成");
                    iv_antivirus_scan.clearAnimation();
                    // 通知用户卸载应用
                    unInstallVirusApp();
                    break;
            }
        }
    };
    private List<ScanInfo> virusInfoList;

    private void unInstallVirusApp() {
        for (ScanInfo info : virusInfoList) {
            String pkgName = info.pkgName;
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + pkgName));
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

        initUI();

        initAnimation();

        new Thread(new Runnable() {
            @Override
            public void run() {
                initScanVirus();
            }
        }).start();
    }


    private void initScanVirus() {
        //获取数据库中病毒的签名md5

        List<String> virusList = AntiVirusDao.getVirusList();

        // 获取已安装应用的 签名+ 未删除的文件
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> pkgSignature = packageManager
                .getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);

        pb_antivirus.setMax(pkgSignature.size());

        virusInfoList = new ArrayList<>();
        List<ScanInfo> AllInfoList = new ArrayList<>();
        int index = 0;
        for (PackageInfo info : pkgSignature) {
            Signature[] signatures = info.signatures;
            Signature signature = signatures[0];
            String encoder = signature.toCharsString();
            String md5Code = Md5Util.encoder(encoder);

            ScanInfo scanInfo = new ScanInfo();
            if (virusList.contains(md5Code)) {
                scanInfo.isVirus = true;
                virusInfoList.add(scanInfo);
            } else {
                scanInfo.isVirus = false;
            }
            // 有何用？？
            AllInfoList.add(scanInfo);
            scanInfo.pkgName = info.packageName;
            scanInfo.name = info.applicationInfo.loadLabel(packageManager).toString();
            index ++ ;
            pb_antivirus.setProgress(index);

            //扫描时间随机
            try {
                Thread.sleep(50 + new Random().nextInt(150));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //告知主线程更新UI
            Message msg = Message.obtain();
            msg.what = SCAN;
            msg.obj = scanInfo;
            mHandler.sendMessage(msg);

            System.out.println(info.packageName);
            System.out.println(md5Code);

        }
        // For循环后修改文本
        //告知主线程更新UI
        Message msg = Message.obtain();
        msg.what = SCAN_FISH;
        mHandler.sendMessage(msg);
    }

    class ScanInfo {
        boolean isVirus;
        String pkgName;
        String name;
    }

    private void initAnimation() {
        mRotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setFillAfter(true);

        iv_antivirus_scan.startAnimation(mRotateAnimation);
    }

    private void initUI() {
        iv_antivirus_scan = (ImageView) findViewById(R.id.iv_antivirus_scan);
        ll_antivirus_text = (LinearLayout) findViewById(R.id.ll_antivirus_text);
        pb_antivirus = (ProgressBar) findViewById(R.id.pb_antivirus);
        tv_antivirus = (TextView) findViewById(R.id.tv_antivirus);

    }
}
