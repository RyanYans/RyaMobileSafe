package com.rya.ryamobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;

import com.rya.ryamobilesafe.R;
import com.rya.ryamobilesafe.db.dao.CommomNumberDao;

import java.util.List;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class CommomNumberSearchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools_commomnumber_search);

        CommomNumberDao commomNumberDao = new CommomNumberDao();
        List<CommomNumberDao.numberGroup> group = commomNumberDao.getGroup();

        System.out.println(group);
    }

}
