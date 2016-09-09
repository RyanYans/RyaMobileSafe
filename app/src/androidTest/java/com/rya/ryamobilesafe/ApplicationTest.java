package com.rya.ryamobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.rya.ryamobilesafe.db.dao.BlackNumberDao;
import com.rya.ryamobilesafe.db.domain.BlackNumber;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testBlackNumberDao() {
        BlackNumberDao blackNumberDao = BlackNumberDao.create(getContext());

        for(int i = 0; i <50; i++)
            blackNumberDao.insert("12580", "2");
    }
}