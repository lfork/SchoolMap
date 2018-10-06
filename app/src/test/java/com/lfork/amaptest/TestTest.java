package com.lfork.amaptest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 98620 on 2018/10/6.
 */
public class TestTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test(){
        assertArrayEquals("asd", new String[]{"sda","asd"}, new String[]{"sda","asd"});
    }

    @After
    public void tearDown() throws Exception {
    }
}