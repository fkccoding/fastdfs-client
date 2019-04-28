package com.kd.fastdfsclient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kd.fastdfsclient.entity.FileInfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/3/26 11:12
 */
public interface FileInfoService extends IService<FileInfo> {
    FileInfo findFileByName(String fileName);

    void deleteByFileName(String fileName);

    int selectCountByREGEXP(String suffix, boolean other);

    List<FileInfo> selectListByREGEXP(String suffix, boolean other, long current, long size, String order, boolean asc);

    List<FileInfo> searchPage(String fileName, long current, long size, String order, boolean asc);

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

    void updateVersion(String fileName);

    void revert(String fileName, String remoteFileName);
}
