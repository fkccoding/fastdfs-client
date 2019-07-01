package com.kd.fastdfsclient.service;

import javax.servlet.http.HttpServletResponse;

public interface FileDownLoadService {
    /**
     * 下载文件
     * @param groupName
     * @param remoteFileName
     * @param response
     * @return
     */
    String downFile(String groupName,String remoteFileName, HttpServletResponse response);
}
