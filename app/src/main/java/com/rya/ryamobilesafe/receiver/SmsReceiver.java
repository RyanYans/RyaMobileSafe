package com.rya.ryamobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.service.LocationService;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;
import com.rya.ryamobilesafe.utils.ToastUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDPM;
    private ComponentName mComponentName;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean safe_isopen = SPUtil.getBoolean(context, ConstantValues.SAFE_ISOPEN, false);
        /**
         * 组件对象、DeviceAdmin字节码
         */
        mComponentName = new ComponentName(context, DeviceAdmin.class);
        /**
         * 获取设备的管理者对象
         */
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (safe_isopen) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.oneperson);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else if (messageBody.contains("#*location*#")) {
                    Intent intentLocation = new Intent(context, LocationService.class);
                    context.startService(intentLocation);
                } else if (messageBody.contains("#*wipedata*#")) {
                    if (mDPM.isAdminActive(mComponentName)) {           //  DevicePolicyManager
                        mDPM.wipeData(0);
                    } else {
                        ToastUtil.show(context, "请到设置 ->安全 ->设备管理器中勾选服务！");
                    }
                } else if (messageBody.contains("#*lockscreen*#")) {
                    if (mDPM.isAdminActive(mComponentName)) {
                        mDPM.lockNow();
                        mDPM.resetPassword("123456", 0);
                    } else {
                        ToastUtil.show(context, "请到 设置 ->安全 ->设备管理器 中勾选服务！");
                    }
                }
            }
        }
    }
}
