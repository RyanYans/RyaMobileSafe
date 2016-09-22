package com.rya.ryamobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.db.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class CommomNumberDao {
    private static String path = "data/data/com.rya.ryamobilesafe/files/commonnum.db";

    public List<numberGroup> getGroup() {
        List<numberGroup> numberGroups = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            numberGroup numberGroup = new numberGroup();

            numberGroup.name = cursor.getString(0);
            numberGroup.idx = cursor.getString(1);
            numberGroup.childLists = getChildList(numberGroup.idx);

            numberGroups.add(numberGroup);
        }
        return numberGroups;
    }

    private List<child> getChildList(String index) {
        List<child> childList = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + index, null);
        while (cursor.moveToNext()) {
            child child = new child();

            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);

            childList.add(child);
        }
        return childList;
    }

    public class numberGroup {
        String name;
        String idx;
        List<child> childLists;
    }

    public class child {
        String _id;
        String number;
        String name;
    }


}
