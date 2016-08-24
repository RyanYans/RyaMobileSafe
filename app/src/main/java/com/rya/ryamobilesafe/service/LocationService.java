package com.rya.ryamobilesafe.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import com.rya.ryamobilesafe.utils.ConstantValues;
import com.rya.ryamobilesafe.utils.SPUtil;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class LocationService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Rya Message >>>>>", "LocationService.....");

        //最优方式获取经纬度
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private class MyLocationListener implements LocationListener {

        @SuppressLint("LongLogTag")
        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();

            //发短信
            String phone = SPUtil.getString(getApplicationContext(), ConstantValues.CONTACT_PHONE, "");
            SmsManager smsManager = SmsManager.getDefault();
            String msg = "longitude = " + longitude + ", latitude = " + latitude;
            smsManager.sendTextMessage(phone, null, msg, null, null);
            Log.i("Rya Message >>>>>>>>>>>> ", msg);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
