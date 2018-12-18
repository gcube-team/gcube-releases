package org.gcube.dataanalysis.ewe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.ewe.notification.NotificationHelper;
import org.gcube.dataanalysis.ewe.util.Workspace;
import org.gcube.dataanalysis.ewe.util.ZipUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A simple implementation of a wrapper around EwE
 * 
 * @author Paolo Fabriani (Engineering Ingegneria Informatica S.p.A.)
 * 
 */
public class SimpleEwE extends StandardLocalInfraAlgorithm {

  // location of the ewe model package
//  private static final String EWE_MODEL_DOWNLOAD_LOCATION = "/home/paolo/Downloads/EwECmdV3_64bit.zip";
  private static final String EWE_MODEL_DOWNLOAD_LOCATION = "http://goo.gl/hBc8UN";
  
  // name of the local model package
  private static final String EWE_MODEL_DEST_PKGNAME = "EwECmd.zip";
  
  // the executable file within the binary package
  private static final String EWE_EXE_FILE = "EwECmd.exe";
  
  // the local position of input files (relative to execution root)
  private static final String INPUT_LOCATION = ".";

  // the local position of output files (relative to execution root)
  private static final String OUTPUT_LOCATION = ".";

  // the local position of executables files (relative to execution root)
  private static final String BINARIES_LOCATION = "bin";

  // input labels
  private static final String IN_MODEL_FILE = "Model File";
  private static final String IN_CONFIG_FILE = "Config File";

  // input descriptions
  private static final String IN_MODEL_FILE_DESCRIPTION = "A file containing the model (e.g. Georgia_Strait.eiixml)";
  private static final String IN_CONFIG_FILE_DESCRIPTION = "A file containing execution parameters (e.g. run_config.xml)";
  
  // input files as expected by the model
  private static String IN_CONFIG_FILE_EXPECTED_NAME = "run_config.xml";
  
  // output files
  private static final String OUT_RUN_LOG_FILENAME = "run_log.txt";
  
  // The name of the tag in 'run_config' referencing the model file
  private static final String MODEL_FILE_TAG_NAME = "model_file";

  // An unique id for this execution
  private String executionId;
  
  // The execution sandbox
  private Workspace workspace;

  private NotificationHelper notificationHelper;

  @Override
  public void init() throws Exception {
    AnalysisLogger.getLogger().info("EwE Initialisation");
    AnalysisLogger.getLogger().info(this.getDescription());
    this.executionId = "ewe_model_" + UUID.randomUUID().toString();
    this.workspace = new Workspace(this.getExecutionId());
    this.workspace.setExecutionsRoot(config.getConfigPath());
//    this.workspace.setExecutionsRoot("/tmp/ewe/workspaces");
    this.workspace.setBinariesLocation(BINARIES_LOCATION);
    this.workspace.setInputLocation(INPUT_LOCATION);
    this.workspace.setOutputLocation(OUTPUT_LOCATION);
    
    this.notificationHelper = new NotificationHelper();
    this.notificationHelper.setScope(config.getGcubeScope());
    this.notificationHelper.setStartTime(Calendar.getInstance());
    this.notificationHelper.setTaskId(config.getTaskID());
    
  }

  @Override
  public String getDescription() {
    String description = "Ecopath with Ecosim (EwE) is a free ecological/ecosystem modeling software suite. ";
    description += " This algorithm implementation expects a model and a configuration file as inputs; the result of the analysis is returned as a zip archive.";
    description += " References: Christensen, V., & Walters, C. J. (2004). Ecopath with Ecosim: methods, capabilities and limitations. Ecological modelling, 172(2), 109-139.";
    return description;
  }

  @Override
  protected void setInputParameters() {
    
    // ask for the model input file
    this.inputs.add(new PrimitiveType(File.class.getName(), null,
        PrimitiveTypes.FILE, IN_MODEL_FILE, IN_MODEL_FILE_DESCRIPTION));

    // ask for the model input file
    this.inputs.add(new PrimitiveType(File.class.getName(), null,
        PrimitiveTypes.FILE, IN_CONFIG_FILE, IN_CONFIG_FILE_DESCRIPTION));

  }

  private void prepareInput() throws Exception {
    // copy input files to working directory
    AnalysisLogger.getLogger().debug("Copying input files...");
    
    // copy and rename config file
    FileUtils.copyFile(new File(config.getParam(IN_CONFIG_FILE)), new File(
        workspace.getInputLocation(), IN_CONFIG_FILE_EXPECTED_NAME));

    // extract the name of the model file
    String modelFileName = this.extractModelFileNameFromConfigFile(new File(workspace
        .getInputLocation(), IN_CONFIG_FILE_EXPECTED_NAME));

    // copy and rename model file
    FileUtils.copyFile(new File(config.getParam(IN_MODEL_FILE)), new File(
        workspace.getRoot(), modelFileName));

  }

  private String extractModelFileNameFromConfigFile(File config_file)
      throws Exception {

    AnalysisLogger.getLogger().debug(
        "Extracting model file name from " + config_file);

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      AnalysisLogger.getLogger().error(e);
      throw new Exception(e);
    }

    Document doc = null;
    try {
      doc = builder.parse(config_file);
    } catch (SAXException e) {
      AnalysisLogger.getLogger().error(e);
      throw new Exception(
          "Unable to parse the configuration file. Is it an xml file?");
    } catch (IOException e) {
      AnalysisLogger.getLogger().error(e);
      throw new Exception("I/O problem in accessing the configuration file");
    }
    doc.getDocumentElement().normalize();

    NodeList nList = doc.getElementsByTagName(MODEL_FILE_TAG_NAME);

    String out = null;

    if (nList.getLength() == 0) {
      AnalysisLogger.getLogger().error(
          "Can't find a tag named '" + MODEL_FILE_TAG_NAME
              + "' in the configuration file");
      throw new Exception(
          "Unable to extract model name from the configuration file");
    } else {
      out = nList.item(0).getTextContent();
      if (nList.getLength() > 1) {
        AnalysisLogger.getLogger().warn(
            "More than one model name found. Returning the first: " + out);
      }
    }
    AnalysisLogger.getLogger().debug("Model file name is " + out);
    System.out.println(out);
    return out;
  }

  private String getExecutionId() {
    return executionId;
  }

  @Override
  protected void process() throws Exception {
    try {
      // make sure the working directory is ready and contains input files
      this.setupWorkingDirectory();
  
      // make input files available in the working directory
      this.prepareInput();
  
      // make sure EwE is available in the working directory
      this.downloadAndSetupEwE();
  
      // execute the algorithm
      String command = String.format("mono %s/%s %s/%s",
          this.workspace.getBinariesLocation(), EWE_EXE_FILE, this.workspace.getInputLocation(), IN_CONFIG_FILE_EXPECTED_NAME);
      AnalysisLogger.getLogger().debug("Executing '" + command + "'");
      this.workspace.exec(command,  null, "stdout-stderr.log");
    
      // prepare the output
      this.prepareOutput();

      // check for errors in run_log.txt
      this.checkForErrors(this.workspace.getRoot());
  
    } catch(Exception e) {
      e.printStackTrace();
      AnalysisLogger.getLogger().error(e);
      // notify error
      this.notificationHelper.setExecutionException(e);
      this.notifySubmitter();
      throw e;
    } finally {
      // cleanup
      this.removeWorkingDirectory();
    }
    // notify job complete
    this.notifySubmitter();
  }

  /**
   * The goal of this method is to make input files available in the working
   * directory.
   */
  private void setupWorkingDirectory() throws Exception {
    AnalysisLogger.getLogger().debug(
        "Setting up working directory '" + this.workspace.getRoot() + "'");
    workspace.ensureStructureExists();
  }

  /**
   * The goal of this method is to make input files available in the working.
   * directory.
   */
  private void downloadAndSetupEwE() throws Exception {
    AnalysisLogger.getLogger().debug("Downloading EwE package");
    if (EWE_MODEL_DOWNLOAD_LOCATION.startsWith("http")) {
      FileUtils.copyURLToFile(new URL(EWE_MODEL_DOWNLOAD_LOCATION),
          new File(workspace.getBinariesLocation(), EWE_MODEL_DEST_PKGNAME));
    } else {
      FileUtils.copyFile(new File(EWE_MODEL_DOWNLOAD_LOCATION),
          new File(workspace.getBinariesLocation(), EWE_MODEL_DEST_PKGNAME));
    }
    ZipUtils.unzipFile(new File(workspace.getBinariesLocation(), EWE_MODEL_DEST_PKGNAME), workspace.getBinariesLocation());
  }

  /**
   * Inspect the log file for error messages.
   * @param executionRoot
   * @throws Exception
   */
  private void checkForErrors(File executionRoot) throws Exception {
    // read run_log.txt line by line
    File runLog = new File(executionRoot, OUT_RUN_LOG_FILENAME);
    FileInputStream fis = new FileInputStream(runLog);

    // Construct BufferedReader from InputStreamReader
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

    String line = null;
    Exception e = null;
    while ((line = br.readLine()) != null) {
      if ("Ecopath model failed to load".equalsIgnoreCase(line)) {
        e = new Exception("Ecopath model failed to load");
      }
    }

    br.close();

    if (e != null) {
      throw e;
    }

  }
  
  /**
   * The goal of this method is to make input files available in the working.
   * directory
   */
  protected void prepareOutput() throws Exception {
    File outputZip = new File(config.getPersistencePath(),
        this.getOutputFileName());
    ZipUtils.zipFolder(this.workspace.getRoot(), outputZip,
        new String[] { this.workspace.getBinariesLocation().getName() });
  }

  private String getOutputFileName() {
    return this.getExecutionId() + "-output.zip";
  }

  /**
   * The goal of this method is to make input files available in the working
   * directory.
   */
  private void removeWorkingDirectory() throws Exception {
    AnalysisLogger.getLogger().debug("Removing working directory");
    this.workspace.destroy();
  }

  @Override
  public StatisticalType getOutput() {
    String outputFileName = config.getPersistencePath() + "/"
        + this.getOutputFileName();
    
    PrimitiveType file = new PrimitiveType(File.class.getName(), new File(
        outputFileName), PrimitiveTypes.FILE, "OutputFile ",
        "An archive containing all output files");
    
    return file;
  }

  @Override
  public void shutdown() {
    AnalysisLogger.getLogger().info("Shutdown");
  }

  /**
   * Notify the submitter that the job has completed.
   * @throws Exception
   */
  private void notifySubmitter() throws Exception {
    AnalysisLogger.getLogger().info("notifying submitter ...");
    AnalysisLogger.getLogger().debug(this.notificationHelper.getSubject());
    AnalysisLogger.getLogger().debug(this.notificationHelper.getBody());
    super.sendNotification(this.notificationHelper.getSubject(),
        this.notificationHelper.getBody());
  }

}
