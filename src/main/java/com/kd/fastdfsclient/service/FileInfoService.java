package com.kd.fastdfsclient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kd.fastdfsclient.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:12
 */
public interface FileInfoService extends IService<FileInfo> {
    /**
     * 根据文件名寻找文件
     * @param fileName
     * @return
     */
    FileInfo findFileByName(String fileName);

    /**
     * 根据文件名删除文件
     * @param fileName
     */
    void deleteByFileName(String fileName);

    /**
     * 根据文件类型查询总数
     * @param category
     * @return
     */
    int selectCount(String category);

    /**
     * 根据文件类型、页码、排序规则、是否正序查询文件列表
     * @param category
     * @param current
     * @param size
     * @param order
     * @param asc
     * @return
     */
    List<FileInfo> selectList(String category, long current, long size, String order, boolean asc);

    /**
     * fuzzySearch
     * @param fileName
     * @param current
     * @param size
     * @param order
     * @param asc
     * @return
     */
    List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc);

    /**
     * 以适合人类的方式显示文件大小
     * @param realSize
     * @return
     */
    default String getHumanSize(double realSize) {
        String hFileSize = realSize + "B";
        if (realSize > 1024 && realSize / 1024 <= 1024) {
            BigDecimal bd = new BigDecimal(realSize / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB";
        } else if (realSize / 1024 > 1024) {
            BigDecimal bd = new BigDecimal(realSize / 1024 / 1024);
            hFileSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
        }
        return hFileSize;
    }

    /**
     * 文件类型转换为后缀名
     * @param category
     * @return
     */
    default Map<String, Object> categoryToSuffix(String category) {
        Map<String, Object> map = new HashMap<String, Object>();
        String suffix;
        boolean other = false;
        if ("image".equals(category)) {
            suffix = ".jpg|.bmp|.gif|.ico|.pcx|.jpeg|.tif|.png|.raw|.tga|.svg|.webp";
        } else if ("doc".equals(category)) {
            suffix = ".doc|.docx|.dot|.dotx|.dotm|.rtf|.xls|.xlsx|.ppt|.pptx|.txt|.pdf";
        } else if ("video".equals(category)) {
            suffix = ".avi|.mp4|.rmvb|.mpeg|.mov|.mkv|.wmv|.flv|.webm";
        } else if ("music".equals(category)) {
            suffix = ".mp3|.aac|.wav|.flav|.ape|.alac|.flac";
        } else if ("all".equals(category)) {
            suffix = ".";
        } else {
            suffix = ".jpg|.bmp|.gif|.ico|.pcx|.jpeg|.tif|.png|.raw|.tga|.svg|.webp" +
                    ".doc|.docx|.dot|.dotx|.dotm|.rtf|.xls|.xlsx|.ppt|.pptx|.txt|.pdf|" +
                    ".avi|.mp4|.rmvb|.mpeg|.mov|.mkv|.wmv|.flv|.webm|" +
                    ".mp3|.aac|.wav|.flav|.ape|.alac|.flac";
            other = true;
        }
        map.put("suffix", suffix);
        map.put("other", other);
        return map;
    }

    /**
     * 更新文件版本
     * @param fileName
     */
    void updateVersion(String fileName);

    /**
     * 回退版本
     * @param fileName
     * @param remoteFileName
     */
    void revert(String fileName, String remoteFileName);

    /**
     * 保存文件
     * @param file
     * @param remoteAddr
     * @return
     */
    int saveFile(MultipartFile file, String remoteAddr);

    /**
     * 根据组名和FastDFS存储的文件名删除历史版本
     * @param groupName
     * @param remoteFileName
     * @return
     */
    boolean deleteHistory(String groupName, String remoteFileName);
}
