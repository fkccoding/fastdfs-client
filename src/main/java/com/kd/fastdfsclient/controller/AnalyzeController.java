package com.kd.fastdfsclient.controller;

import com.kd.fastdfsclient.service.AnalyzeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: www.chuckfang.top
 * @Date: 2019/5/15 16:42
 */
@RestController
public class AnalyzeController {
    private static Logger logger = LoggerFactory.getLogger(AnalyzeController.class);

    @Autowired
    AnalyzeService analyzeService;

    @GetMapping("/countUpload")
    public Map<LocalDateTime,Integer> uploadTime(String dateTime){
        Map<LocalDateTime, Integer> result = new HashMap<>();
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
            int a = analyzeService.countUpload(localDateTime);
            result.put(localDateTime, a);
            System.out.println("result = "+result);
        } catch (DateTimeParseException e) {
            logger.error("日期解析错误");
            result.put(LocalDateTime.now(), 0);
        }
        return result;
    }
}
