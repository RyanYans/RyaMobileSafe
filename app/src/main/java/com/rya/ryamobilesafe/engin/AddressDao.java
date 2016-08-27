package com.rya.ryamobilesafe.engin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AddressDao {
    private static String path = "data/data/com.rya.ryamobilesafe/files/address.db";
    private static String mAddress = null;

    public static String checkAddress(String phone) {
        switch (phone.length()) {
            case 3:
                mAddress = "报警电话";
                break;
            case 5:
                mAddress = "服务电话";
                break;
            case 8:
                mAddress = "本地电话";
                break;
            case 11:
                mAddress = chaek11(phone);
                break;
            case 12:
                mAddress = check12(phone);
                break;
            default:
                mAddress = "格式有误";
                break;
        }
        return mAddress;
    }

    private static String check12(String phone) {
        phone = phone.substring(1, 4);
        if (phone.startsWith("0")) {
            phone = phone.substring(1, 3);
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{phone}, null, null, null);

        if (cursor.moveToNext()) {
            String location = cursor.getString(0);
            return location;
        }
        cursor.close();
        return "未知归属地";
    }

    private static String chaek11(String phone) {
        //正则表达式判断
        String regularExpression = "^1[3-8]\\d{9}";
        if (!phone.matches(regularExpression)) {
            return "格式有误";
        }

        phone = phone.substring(0, 7);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor_data1 = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
        if (cursor_data1.moveToNext()) {
            String outkey = cursor_data1.getString(0);

            Cursor cursor_data2 = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
            if (cursor_data2.moveToNext()) {
                String location = cursor_data2.getString(0);
                return location;
            }
            cursor_data1.close();
            cursor_data2.close();
        }
        return "未知归属地";
    }
}
