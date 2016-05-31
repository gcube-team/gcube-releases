package org.gcube.dataanalysis.ewe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class FileSystemUtils {

  private String executionId;

  public FileSystemUtils(String executionId) {
    this.executionId = executionId;
  }

  private static String executionsRoot = "/tmp";
  private static String binariesSubpath = "bin";
  private static String inputSubpath = ".";

  public File getExecutionsRoot() {
    return new File(executionsRoot);
  }

  public File getExecutionRoot() {
    return new File(this.getExecutionsRoot(), this.executionId);
  }

  public File getBinariesLocation() {
    return new File(this.getExecutionRoot(), binariesSubpath);
  }

  public File getInputLocation() {
    return new File(this.getExecutionRoot(), inputSubpath);
  }

  public void removeExecutionStructure() throws IOException {
    System.out.println("Removing execution directory '"
        + this.getExecutionRoot() + "'");
    FileUtils.deleteDirectory(this.getExecutionRoot());
    System.out.println("Removed.");
  }

  public void ensureExecutionStructureExists() {
    this.ensureDirectory(this.getExecutionsRoot());
    this.ensureDirectory(this.getExecutionRoot());
    this.ensureDirectory(this.getBinariesLocation());
    this.ensureDirectory(this.getInputLocation());
  }

  public void copyInputFileAs(String sourcePath, String newName)
      throws IOException {
    File source = new File(sourcePath);
    File destination = new File(this.getInputLocation(), newName);
    this.copyFile(source, destination);
  }

  public void copyFile(File source, File dest) throws IOException {
    AnalysisLogger.getLogger().debug(
        "Copying file '" + source + "' to '" + dest + "'");
    FileUtils.copyFile(source, dest);
    AnalysisLogger.getLogger().debug("Copied.");
  }

  public boolean renameFile(File from, File to) throws IOException {
    AnalysisLogger.getLogger().debug(
        "Renaming " + from.getAbsoluteFile().toString() + " to "
            + to.getAbsoluteFile().toString());
    return from.renameTo(to);
  }

  private void ensureDirectory(File dir) {
    // if the directory does not exist, create it
    AnalysisLogger.getLogger().debug(
        "Creating directory: '" + dir.getAbsolutePath() + "'");
    if (!dir.exists()) {
      boolean result = false;
      try {
        dir.mkdir();
        result = true;
      } catch (SecurityException se) {
        AnalysisLogger.getLogger().error(
            "Unable to create directory '" + dir.getAbsolutePath() + "'");
      }
      if (result) {
        AnalysisLogger.getLogger().debug(
            "Created directory '" + dir.getAbsolutePath() + "'");
      }
    } else {
      AnalysisLogger.getLogger().debug(
          "Directory '" + dir.getAbsolutePath() + "' already exists. Skiping.");
    }
  }

}
