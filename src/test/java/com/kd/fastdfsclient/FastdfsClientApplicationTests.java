package com.kd.fastdfsclient;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        /*
         * 实体带查询使用方法  输出看结果
         */
        IPage<FileInfo> fileInfoIPage = fileInfoMapper.selectPage(
                new Page<FileInfo>(1, 10),
                new QueryWrapper<FileInfo>().orderByAsc()
        );
        System.out.println(fileInfoIPage.getRecords());
    }

}
