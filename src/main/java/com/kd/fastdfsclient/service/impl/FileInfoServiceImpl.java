package com.kd.fastdfsclient.service.impl;

import com.alicp.jetcache.anno.Cached;
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
@Service()
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    private static Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    private Map<String, String> operators = new HashMap<>();

    FileInfoServiceImpl() {
        operators.put("192.100.1.210", "曲广昊");
        operators.put("192.100.1.211", "方程");
        operators.put("192.100.1.213", "崔志臣");
        operators.put("192.100.1.230", "武瑞敏");
        operators.put("192.100.1.231", "汪垚");
        operators.put("192.100.1.232", "向凌吉");
        operators.put("192.100.1.233", "江天");
        operators.put("192.100.1.237", "卢荟芳");
        operators.put("192.100.1.123", "魏健东");
    }

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Override
    @Cached(name = "FileInfoService.getUserById", expire = 3600)
    public FileInfo findFileByName(String fileName) {
        return fileInfoMapper.findCurrentFileByName(fileName);
    }

    @Override
    public void deleteByFileName(String fileName) {
        fileInfoMapper.deleteByFileName(fileName);
    }

    @Override
    @Cached(name = "FileInfoService.selectCountByREGEXP", expire = 3600)
    public int selectCountByREGEXP(String suffix, boolean other) {
        int count;
        if (!other) {
            count = fileInfoMapper.selectCountByREGEXP(suffix,"");
        } else {
            count = fileInfoMapper.selectCountByREGEXP(suffix,"NOT");
        }
        return count;
    }

    @Override
    @Cached(name = "FileInfoService.selectListByREGEXP", expire = 3600)
    public List<FileInfo> selectListByREGEXP(String suffix, boolean other, long current, long size, String order, boolean asc) {
        List<FileInfo> fileInfoList;
        if ("file_name".equals(order) || "operator".equals(order)) {
            if (!other) {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "", "DESC");
                }
            } else {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                             "NOT","ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListForChinese(suffix, (current - 1) * size, size, order,
                            "NOT","DESC");
                }
            }
        } else {
            if (!other) {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "", "ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "", "DESC");
                }
            } else {
                if (asc) {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "NOT","ASC");
                } else {
                    fileInfoList = fileInfoMapper.selectListByREGEXP(suffix, (current - 1) * size, size, order,
                            "NOT","DESC");
                }
            }
        }

        return fileInfoList;
    }

    @Override
    @Cached(name = "FileInfoService.searchPage", expire = 3600)
    public List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc) {
        List<FileInfo> fileInfoList;
        // 如果是按中文排序
        if ("file_name".equals(order) || "operator".equals(order)) {
            if (asc) {
                fileInfoList = fileInfoMapper.searchPageForChinese(fileName, current, size, order, "ASC");
            } else {
                fileInfoList = fileInfoMapper.searchPageForChinese(fileName, current, size, order, "DESC");
            }
        } else {
            if (asc) {
                fileInfoList = fileInfoMapper.searchPage(fileName, current, size, order, "ASC");
            } else {
                fileInfoList = fileInfoMapper.searchPage(fileName, current, size, order, "DESC");
            }
        }
        return fileInfoList;
    }

    // 如果数据库查到已经存在该文件名的文件，那么把其中is_current=1的文件的is_current更新为0，
    // 不管怎样都插入新纪录，且把新纪录的is_current置1
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
        if (null == operator) {     //If the user's IP is illegal
            operator = "Hacker";
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

}
