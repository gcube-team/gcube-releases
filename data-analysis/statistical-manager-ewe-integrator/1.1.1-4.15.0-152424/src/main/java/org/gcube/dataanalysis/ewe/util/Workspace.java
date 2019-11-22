package org.gcube.dataanalysis.ewe.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class Workspace {

  private String executionId;

  private String executionsRoot;
  private String inputSubpath;
  private String binariesSubpath;
  private String outputSubpath;

  public Workspace(String executionId) {
    this.executionId = executionId;
  }

  private File getExecutionsRoot() {
    return new File(this.executionsRoot);
  }
  
  public void setExecutionsRoot(String executionsRoot) {
    this.executionsRoot = executionsRoot;
  }

  public File getRoot() {
    return new File(this.executionsRoot, this.executionId);
  }

  public File getInputLocation() {
    return new File(this.getRoot(), inputSubpath);
  }

  public File getBinariesLocation() {
    return new File(this.getRoot(), binariesSubpath);
  }

  public File getOutputLocation() {
    return new File(this.getRoot(), outputSubpath);
  }

  public void destroy() throws IOException {
    AnalysisLogger.getLogger().info(
        "Removing execution directory '" + this.getRoot() + "'");
    FileUtils.deleteDirectory(this.getRoot());
    AnalysisLogger.getLogger().info("Removed.");
  }

  public void ensureStructureExists() {
    this.ensureDirectory(this.getExecutionsRoot());
    this.ensureDirectory(this.getRoot());
    this.ensureDirectory(this.getInputLocation());
    this.ensureDirectory(this.getBinariesLocation());
    this.ensureDirectory(this.getOutputLocation());
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
          "Directory '" + dir.getAbsolutePath() + "' already exists. Skipping.");
    }
  }

  public void setBinariesLocation(String binariesLocation) {
    this.binariesSubpath = binariesLocation;
  }

  public void setOutputLocation(String outputLocation) {
    this.outputSubpath = outputLocation;
  }

  public void setInputLocation(String inputLocation) {
    this.inputSubpath = inputLocation;
  }

  /**
   * Executes the given 'command' in the given 'dir' as current directory.
   * 
   * @param command
   *          the command to execute
   * @param subpath
   *          the subdirectory where to execute the command
   * @param logfilename
   *          the file where stdout and stderr are redirected
   * @return
   */
  public void exec(final String command, String subpath, String logfilename) {
    AnalysisLogger.getLogger().info(
        String.format("Executing command '%s'", command));
    ProcessBuilder builder = new ProcessBuilder(command.split(" "));
    if (subpath == null) {
      builder.directory(this.getRoot());
    } else {
      builder.directory(new File(this.getRoot(), subpath));
    }
    if (logfilename != null) {
      File logFile = new File(this.getRoot(), logfilename);
      builder.redirectErrorStream(true);
      builder.redirectOutput(logFile);
      AnalysisLogger.getLogger().info(
          String.format("  writing stdout and stderr to '%s'",
              logFile.getPath()));
    }
    try {
      Process process = builder.start();
      process.waitFor();
    } catch (InterruptedException | IOException e) {
      AnalysisLogger.getLogger().error(e);
    }
    AnalysisLogger.getLogger().info("Execution complete.");
  }
}
