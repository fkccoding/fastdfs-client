package com.kd.fastdfsclient;

import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.PrintStream;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FastdfsClientApplicationTests {
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Test
    public void contextLoads() {

    }

    @Test
    public void testTSQL11() {

    }

}
