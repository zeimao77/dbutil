package com.zeimao77.test;

import org.junit.Test;
import top.zeimao77.dbutil.controller.DbSourceConf;

import java.io.IOException;
import java.util.UUID;

public class Test1 {

    @Test
    public void test() throws IOException {
        DbSourceConf d = new DbSourceConf();
        d.init();
    }
}
