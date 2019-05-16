package com.kd.fastdfsclient.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kd.fastdfsclient.entity.FileInfo;
import com.kd.fastdfsclient.fastdfs.FastDFSClient;
import com.kd.fastdfsclient.mapper.FileInfoMapper;
import com.kd.fastdfsclient.service.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:13
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    private static Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    private Map<String, String> operators = new HashMap<>();

    @CreateCache(expire = 3600)
    private Cache<Long,FileInfo> fileInfoCache;

    FileInfoServiceImpl() {
        operators.put("192.100.5.100", "魏健东");
        operators.put("192.100.5.101", "武瑞敏");
        operators.put("192.100.5.102", "向凌吉");
        operators.put("192.100.5.103", "方程");
        operators.put("192.100.5.104", "汪垚");
        operators.put("192.100.5.105", "崔志臣");
        operators.put("192.100.5.106", "卢荟芳");
        operators.put("192.100.5.107", "孟祥婷");
    }

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    public FileInfo findFileByName(String fileName) {
        return fileInfoMapper.findCurrentFileByName(fileName);
    }

    @Override
    public void deleteByFileName(String fileName) {
        fileInfoMapper.deleteByFileName(fileName);
    }

    @Override
    public int selectCount(String category) {
        String suffix = (String) categoryToSuffix(category).get("suffix");
        boolean other = (boolean) categoryToSuffix(category).get("other");
        return fileInfoMapper.selectCountByREGEXP(suffix,other);
    }

    @Override
    public List<FileInfo> selectList(String category, long current, long size, String order, boolean asc) {
        String suffix = (String) categoryToSuffix(category).get("suffix");
        boolean other = (boolean) categoryToSuffix(category).get("other");
        List<FileInfo> fileInfoList;
        // 如果是按中文排序
        if ("file_name".equals(order) || "operator".equals(order)) {
            fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order, other, asc);
        } else {
            fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order, other, asc);
        }
        return fileInfoList;
    }

    @Override
    public List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc) {
        List<FileInfo> fileInfoList;
        // 如果是按中文排序
        if ("file_name".equals(order) || "operator".equals(order)) {
            fileInfoList = fileInfoMapper.searchPageForChinese(fileName, current, size, order, asc);
        } else {
            fileInfoList = fileInfoMapper.searchPage(fileName, current, size, order, asc);
        }
        return fileInfoList;
    }

    /**
     * 如果数据库查到已经存在该文件名的文件，那么把其中is_current=1的文件的is_current更新为0，
     * 不管怎样都插入新纪录，且把新纪录的is_current置1
     */
    @Override
    public void updateVersion(String fileName) {
        if (null != findFileByName(fileName)) {
            fileInfoMapper.updateVersionToOldByFileName(fileName);
            logger.info("The file name is occupied, we are already update the version to new.");
        }
    }

    @Override
    @Transactional
    public void revert(String fileName, String remoteFileName) {
        fileInfoMapper.updateVersionToOldByFileName(fileName);
        fileInfoMapper.updateVersionToCurrentByRemoteFileName(remoteFileName);
        logger.info("revert success ！");
    }

    @Override
    public int saveFile(MultipartFile file, String remoteAddr) {
        // To prepare data
        String fileName = file.getOriginalFilename();
        long realSize = file.getSize();
        String hFileSize = getHumanSize(realSize);
        String operator = operators.get(remoteAddr);
        //If the user's IP is illegal
        if (null == operator) {
            operator = remoteAddr;
        }
        try {
            // Get the file and save it to FastDFS somewhere
            String[] strings = FastDFSClient.saveFile(file);
            logger.info("upload path is " + strings[0]);

            // Save FileInfo to mysql
            this.save(new FileInfo(fileName,strings[1],strings[2],new Date(),hFileSize,realSize,1.0,operator,1));
        } catch (Exception e) {
            logger.error("upload file failed", e);
            return 2;
        }
        return 3;
    }

    @Override
    public boolean deleteHistory(String groupName, String remoteFileName) {
        try {
            String deleteFileMsg = FastDFSClient.deleteFile(groupName, remoteFileName);
            logger.info(deleteFileMsg);
            fileInfoMapper.deleteByRemoteFileName(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
