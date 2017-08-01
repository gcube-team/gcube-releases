package org.gcube.dataanalysis.oscar.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPDownloader {

  private static final Logger logger = LoggerFactory.getLogger(FTPDownloader.class);

  private FTPClient ftp = null;

  public FTPDownloader(String url) throws Exception {
    this(getFtpHost(url), "anonymous", null);
  }  
  
  public FTPDownloader(String host, String user, String pwd) throws Exception {
    this.ftp = new FTPClient();
//    this.ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    int reply;
    this.ftp.connect(host);
    reply = ftp.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      this.ftp.disconnect();
      throw new Exception("Exception in connecting to FTP Server");
    }
    this.ftp.login(user, pwd);
    this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
    this.ftp.enterLocalPassiveMode();
    this.ftp.setControlKeepAliveTimeout(300);
//  ftpClient.sendNoOp();
  }

  /**
   * 
   * @param remoteFilePath can be either full URL or relative to the root
   * @param localFilePath
   */
  public void downloadFile(String remoteFilePath, String localFilePath) {
    if(remoteFilePath.startsWith("ftp://")) {
      remoteFilePath = getFtpPath(remoteFilePath);
    }
    try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
      this.ftp.retrieveFile(remoteFilePath, fos);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<String> listFiles(String directory) {
    List<String> out = new Vector<>();
    try {
      this.ftp.changeWorkingDirectory(directory);
      for(FTPFile f:this.ftp.listFiles()) {
        out.add(f.getName());
//        System.out.println(f);
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
    return out;
  }
  
  public boolean checkFileExists(String remoteFilePath) throws IOException {
    String directory = getFtpDirectory(remoteFilePath);
    String file = getFtpFile(remoteFilePath);
    return this.listFiles(directory).contains(file);
  }
  
  public void disconnect() {
    if (this.ftp.isConnected()) {
      try {
        this.ftp.logout();
        this.ftp.disconnect();
      } catch (IOException f) {
        // do nothing as file is already downloaded from FTP server
      }
    }
  }
  
  private static String getFtpHost(String ftpSource) {
    if (ftpSource.startsWith("ftp:")) {
      // extracting host and file
      String tmp = ftpSource.substring(6);
      return tmp.split("/")[0];
    } else {
      return null;
    }
  }

  private static String getFtpPath(String ftpSource) {
    if (ftpSource.startsWith("ftp:")) {
      // extracting host and file
      String tmp = ftpSource.substring(6);
      return tmp.split("/", 2)[1];
    } else {
      return null;
    }
  }

  private static String getFtpDirectory(String ftpSource) {
    String file = getFtpPath(ftpSource);
    if(file!=null) {
      String directory = file.substring(0, file.lastIndexOf("/"));
      return directory;
    }
    return null;
  }

  private static String getFtpFile(String ftpSource) {
    String path = getFtpPath(ftpSource);
    if(path!=null) {
      String file = path.substring(path.lastIndexOf("/")+1);
//      System.out.println("**** FILE is " + file);
      return file;
    }
    return null;
  }
  
  public static boolean checkFtpFileExists(String ftpURL) {
    FTPDownloader downloader = null;
    try {
      downloader = new FTPDownloader(ftpURL);
      return downloader.checkFileExists(ftpURL);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (downloader != null) {
        downloader.disconnect();
      }
    }
    return false;
  }

}