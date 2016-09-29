package com.rya.ryamobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.rya.ryamobilesafe.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AntiVirusDao {
    private static String path = "data/data/com.rya.ryamobilesafe/files/antivirus.db";

    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null, null);

        ArrayList<String> VirusList = new ArrayList<>();
        while (cursor.moveToNext()) {
            VirusList.add(cursor.getString(0));
        }
        db.close();
        cursor.close();

        return VirusList;
    }

}
