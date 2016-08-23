package com.rya.ryamobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        checkPhoneNumber(context);
    }

    private void checkPhoneNumber(Context context) {
        String sim = SPUtil.getString(context, ConstantValues.SAFE_SIM, "");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber() + "xxx";
        if (!sim.equals(simSerialNumber)) {
            String phone = SPUtil.getString(context, ConstantValues.CONTACT_PHONE, "");
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phone, null, "SIM card has Change!!!~", null, null);
        }
    }
}
