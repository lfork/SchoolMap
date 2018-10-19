package com.lfork.amaptest;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lfork.amaptest.util.SharedPreferenceDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SharedPreferenceDaoTest {

    public static final String TEST_KEY = "instrumentedTest";
    public static final String TEST_STRING = "玉刚说";

    SharedPreferenceDao spDao;

    @Before
    public void setUp() {
        spDao = new SharedPreferenceDao(InstrumentationRegistry.getContext());
    }

    @Test
    public void sharedPreferenceDaoWriteRead() {
        spDao.put(TEST_KEY, TEST_STRING);
        Assert.assertEquals(TEST_STRING, spDao.get(TEST_KEY));
    }
}
