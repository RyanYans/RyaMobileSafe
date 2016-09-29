package com.rya.ryamobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.rya.ryamobilesafe.db.AppLockOpenHelper;
import com.rya.ryamobilesafe.db.BlackNumberOpenHelper;
import com.rya.ryamobilesafe.db.domain.BlackNumber;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppLockDao {

    private final Context context;
    private AppLockOpenHelper mAppLockOpenHelper;

    // 设计为单例模式
    // 1.私有化构造方法
    private AppLockDao(Context context) {
        this.context = context;
        mAppLockOpenHelper = new AppLockOpenHelper(context);
    }

    // 2.声明当前类成员对象
    private static AppLockDao appLockDao = null;

    // 3.暴露静态获取对象方法
    public static AppLockDao create(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    //增（插入）, 每次插入都需要内容观察者去Notify数据改变。
    public void insert(String pkgName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgname", pkgName);
        db.insert("applock", null, contentValues);
        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content:// applock/datachange"), null);
    }

    // 每次插入都需要内容观察者去Notify数据改变。
    public void delete(String pkgName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgname", pkgName);
        db.delete("applock", "pkgname = ?", new String[]{pkgName});
        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content:// applock/datachange"), null);
    }

    /**
     * @return 返回数据库中已锁屏应用的包名
     */
    public List<String> searchAll() {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"pkgname"}, null, null, null, null, null);
        List<String> lockAppList = new ArrayList<>();
        while (cursor.moveToNext()) {
            lockAppList.add(cursor.getString(0));
        }
        db.close();
        cursor.close();
        return lockAppList;
    }

}
