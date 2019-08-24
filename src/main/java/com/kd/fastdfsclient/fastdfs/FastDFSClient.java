package com.kd.fastdfsclient.fastdfs;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FastDFSClient {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

	static {
		try {
//			String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
			String filePath = new ClassPathResource("fdfs_client.conf").getPath();
			ClientGlobal.init(filePath);
		} catch (Exception e) {
			logger.error("FastDFS Client Init Fail!",e);
		}
	}

	/**
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public static String[] saveFile(MultipartFile multipartFile) throws IOException {
		String[] fileAbsolutePath = {};
		String fileName = multipartFile.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		byte[] file_buff = null;
		InputStream inputStream = multipartFile.getInputStream();
		if (inputStream != null) {
			int len1 = inputStream.available();
			file_buff = new byte[len1];
			inputStream.read(file_buff);
		}
		inputStream.close();
		FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
		try {
			fileAbsolutePath = upload(file);  // -> upload to fastdfs
		} catch (Exception e) {
			logger.error("upload file Exception!", e);
		}
		if (fileAbsolutePath == null) {
			logger.error("upload file failed,please upload again!");
		}
		String[] strings = new String[3];
		strings[0] = getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
		strings[1] = fileAbsolutePath[0];
		strings[2] = fileAbsolutePath[1];
		return strings;
	}

	private static String[] upload(FastDFSFile file) {
		logger.info("File Name: " + file.getName() + " File Length:" + file.getContent().length);

		NameValuePair[] meta_list = new NameValuePair[1];
		meta_list[0] = new NameValuePair("author", file.getAuthor());

		long startTime = System.currentTimeMillis();
		String[] uploadResults = null;
		StorageClient storageClient=null;
		try {
			storageClient = getTrackerClient();
			uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
		} catch (IOException e) {
			logger.error("IO Exception when uploadind the file:" + file.getName(), e);
		} catch (Exception e) {
			logger.error("Non IO Exception when uploadind the file:" + file.getName(), e);
		}
		logger.info("Upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

		if (uploadResults == null && storageClient!=null) {
			logger.error("Upload file fail, error code:" + storageClient.getErrorCode());
		}
		String groupName = uploadResults[0];
		String remoteFileName = uploadResults[1];
		logger.info("Upload file successfully!!!" + " group_name:" + groupName + ", remoteFileName:" + " " + remoteFileName);
		return uploadResults;
	}

	public static FileInfo getFile(String groupName, String remoteFileName) {
		try {
			StorageClient storageClient = getTrackerClient();
			return storageClient.get_file_info(groupName, remoteFileName);
		} catch (IOException e) {
			logger.error("IO Exception: Get File from Fast DFS failed", e);
		} catch (Exception e) {
			logger.error("Non IO Exception: Get File from Fast DFS failed", e);
		}
		return null;
	}

	//下载文件
	public static InputStream downFile(String groupName, String remoteFileName) {
		try {
			StorageClient storageClient = getTrackerClient();
			byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
			InputStream ins = new ByteArrayInputStream(fileByte);
			return ins;
		} catch (IOException e) {
			logger.error("IO Exception: Get File from Fast DFS failed", e);
		} catch (Exception e) {
			logger.error("Non IO Exception: Get File from Fast DFS failed", e);
		}
		return null;
	}

	//使用NIO下载文件
	public static FileInputStream downFileNio(String groupName, String remoteFileName) {
		try {
			StorageClient storageClient = getTrackerClient();
			byte[] fileByte = storageClient.download_file(groupName, remoteFileName);

			// 把字节数组转化为File对象，然后再把File对象转化为FileInputStream
			File file = new File("");
			OutputStream output = new FileOutputStream(file);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
			bufferedOutput.write(fileByte);
			FileInputStream fileInputStream = new FileInputStream(file);
			return fileInputStream;
		} catch (IOException e) {
			logger.error("IO Exception: Get File from Fast DFS failed", e);
		} catch (Exception e) {
			logger.error("Non IO Exception: Get File from Fast DFS failed", e);
		}
		return null;
	}

	//删除文件
	public static String deleteFile(String groupName, String remoteFileName)
			throws Exception {
		StorageClient storageClient = getTrackerClient();
		int i = storageClient.delete_file(groupName, remoteFileName);
		logger.info("Delete file successfully！！！" + i);
		return "Delete file successfully！！！";
	}

	public static StorageServer[] getStoreStorages(String groupName)
			throws IOException {
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		return trackerClient.getStoreStorages(trackerServer, groupName);
	}

	public static ServerInfo[] getFetchStorages(String groupName,
												String remoteFileName) throws IOException {
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
	}

	private static String getTrackerUrl() throws IOException {
		return "http://"+getTrackerServer().getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port()+"/";
	}

	private static StorageClient getTrackerClient() throws IOException {
		TrackerServer trackerServer = getTrackerServer();
		StorageClient storageClient = new StorageClient(trackerServer, null);
		return  storageClient;
	}

	private static TrackerServer getTrackerServer() throws IOException {
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		return  trackerServer;
	}
}