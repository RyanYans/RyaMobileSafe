package com.rya.ryamobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rya.ryamobilesafe.db.BlackNumberOpenHelper;
import com.rya.ryamobilesafe.db.domain.BlackNumber;
import com.rya.ryamobilesafe.utils.ConstantValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper mBlackNumberOpenHelper;

    // 设计为单例模式
    // 1.私有化构造方法
    private BlackNumberDao(Context context) {
        mBlackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    // 2.声明当前类成员对象
    private static BlackNumberDao blackNumberDao = null;

    // 3.暴露静态获取对象方法
    public static BlackNumberDao create(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    public void insert(String phone, String state) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

        //创建contentValue对象（数据键值对Map）
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        contentValues.put("state", state);

        db.insert("BlackNumber", null, contentValues);

        db.close();
    }

    public void delete(String phone) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

        db.delete("BlackNumber", "phone = ?", new String[]{phone});

        db.close();
    }

    public void update(String phone, String state) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("state", state);

        db.update("BlackNumber", contentValues, "phone = ?", new String[]{phone});

        db.close();
    }

    public List<BlackNumber> searchAll() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        List<BlackNumber> blackNumberList = new ArrayList<>();

        Cursor cursor = db.query("BlackNumber", new String[]{"phone", "state"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setPhone(cursor.getString(0));
            blackNumber.setState(cursor.getString(1));
            blackNumberList.add(blackNumber);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    public List<BlackNumber> searchPart(int index) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        List<BlackNumber> blackNumberList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select phone,state from BlackNumber order by _id desc limit ?,15;", new String[]{Integer.toString(index)});
        while (cursor.moveToNext()) {
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setPhone(cursor.getString(0));
            blackNumber.setState(cursor.getString(1));
            blackNumberList.add(blackNumber);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    public int countAll() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        List<BlackNumber> blackNumberList = new ArrayList<>();
        int count = 0;

        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        while (cursor.moveToNext()) {
            BlackNumber blackNumber = new BlackNumber();
            blackNumber.setPhone(cursor.getString(0));
            blackNumber.setState(cursor.getString(1));
            blackNumberList.add(blackNumber);
        }
        cursor.close();
        db.close();

        return count;
    }


    public String getState(String phone) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        String state = null;

        Cursor cursor = db.query("BlackNumber", new String[]{"state"}, "phone = ?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            state =  cursor.getString(0);
        }

        cursor.close();
        db.close();

        return state;
    }
}
