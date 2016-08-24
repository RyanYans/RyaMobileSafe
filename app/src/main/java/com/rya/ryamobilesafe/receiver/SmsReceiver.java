package com.rya.ryamobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.service.LocationService;
import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean safe_isopen = SPUtil.getBoolean(context, ConstantValues.SAFE_ISOPEN, false);
        if (safe_isopen) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.yiban);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else if (messageBody.contains("#*location*#")) {
                    Intent intentLocation = new Intent(context, LocationService.class);
                    context.startService(intentLocation);
                }
            }
        }
    }
}
