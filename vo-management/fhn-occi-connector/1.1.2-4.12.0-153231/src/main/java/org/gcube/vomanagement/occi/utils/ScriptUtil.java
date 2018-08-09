package org.gcube.vomanagement.occi.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ScriptUtil {

  /**
   * Return the content of the resource located at the given file.
   * @param file the file to read.
   * @return the content of the given file.
   * @throws IOException
   */
  public static String getScriptFromFile(File file) throws IOException {
    if (file == null) {
      return null;
    }
    FileInputStream input = new FileInputStream(file);
    String everything = IOUtils.toString(input);
    input.close();
    return everything;
  }

  /**
   * Return the content of the resource at the given URL.
   * @param url the url to read.
   * @return the content at the given URL.
   * @throws IOException
   */
  public static String getScriptFromURL(URL url) throws IOException {
    if (url == null) {
      return null;
    }
    URLConnection yc = url.openConnection();
    BufferedReader input = new BufferedReader(new InputStreamReader(
        yc.getInputStream()));
    String line;
    StringBuffer buffer = new StringBuffer();
    while ((line = input.readLine()) != null) {
      buffer.append(line + "\n");
    }
    String bufferScript = buffer.substring(0, buffer.length());
    input.close();
    return bufferScript;
  }
}
