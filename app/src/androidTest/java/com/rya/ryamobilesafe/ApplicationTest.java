package com.rya.ryamobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.rya.ryamobilesafe.db.dao.BlackNumberDao;
import com.rya.ryamobilesafe.db.domain.BlackNumber;
import com.rya.ryamobilesafe.engin.ProcessProvider;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testBlackNumberDao() {
    }
}