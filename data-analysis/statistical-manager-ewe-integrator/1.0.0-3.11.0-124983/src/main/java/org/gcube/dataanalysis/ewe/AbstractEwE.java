package org.gcube.dataanalysis.ewe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ewe.util.ExecUtils;
import org.gcube.dataanalysis.ewe.util.FileSystemUtils;
import org.gcube.dataanalysis.ewe.util.NetworkUtils;
import org.gcube.dataanalysis.ewe.util.ZipUtils;

/**
 *
 * @author Paolo Fabriani (Engineering Ingegneria Informatica S.p.A.)
 *
 */
public abstract class AbstractEwE extends StandardLocalExternalAlgorithm {

	protected static String CONFIG_FILE_NAME = "run_config.xml";

	private String executionId;

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().info("EwE Abstract Initialisation");
		this.executionId = UUID.randomUUID().toString();
	}

	@Override
	public String getDescription() {
		String description = "Ecopath with Ecosim (EwE) is a free ecological/ecosystem modeling software suite. ";
		description += " This algorithm implementation expects a model and a configuration file as inputs; the result of the analysis is returned as a zip archive.";
		description += " References: Christensen, V., & Walters, C. J. (2004). Ecopath with Ecosim: methods, capabilities and limitations. Ecological modelling, 172(2), 109-139.";
		return description;
	}

  /**
   * The goal of this method is to make input files available in the working
   * directory.
   */
  protected void setupWorkingDirectory() throws Exception {
    FileSystemUtils fsu = new FileSystemUtils(executionId);
    // create the working directory
    AnalysisLogger.getLogger().debug(
        "setting up working directory '" + fsu.getExecutionRoot() + "'");
    fsu.ensureExecutionStructureExists();
  }

  /**
   * The goal of this method is to make input files available in the working
   * directory.
   */
	protected abstract void prepareInput() throws Exception;

  /**
   * The goal of this method is to make input files available in the working.
   * directory.
   */
	protected void downloadAndSetupEwE() throws Exception {
		AnalysisLogger.getLogger().debug("Downloading EwE package");
		new NetworkUtils(executionId).downloadAndUnzipEwe();
	}

  /**
   * The goal of this method is to make input files available in the working.
   * directory
   */
  protected void prepareOutput() throws Exception {
    FileSystemUtils fsu = new FileSystemUtils(executionId);
    File outputZip = new File(config.getPersistencePath(),
        this.getOutputFileName());
    String binDir = fsu
        .getBinariesLocation()
        .getAbsoluteFile()
        .toString()
        .substring(
            fsu.getExecutionRoot().getAbsoluteFile().toString().length() + 1);
    ZipUtils.zipFolder(fsu.getExecutionRoot(), outputZip,
        new String[] {binDir});
  }

  /**
   * The goal of this method is to make input files available in the working
   * directory.
   */
	protected void removeWorkingDirectory() throws Exception {
		AnalysisLogger.getLogger().debug("Removing working directory");
		FileSystemUtils fsu = new FileSystemUtils(executionId);
		fsu.removeExecutionStructure();
	}

	@Override
	protected void process() throws Exception {
		FileSystemUtils fsu = new FileSystemUtils(executionId);

    // make sure the working directory is ready and contains input files
		this.setupWorkingDirectory();

		// make input files available in the working directory
		this.prepareInput();

		// make sure EwE is available in the working directory
		this.downloadAndSetupEwE();

		// execute the algorithm
		String command = String.format("mono %s/EwECmd.exe %s",
				fsu.getBinariesLocation(), CONFIG_FILE_NAME);
    AnalysisLogger.getLogger().debug("Executing '" + command + "'");
		String log = ExecUtils.exec(command, fsu.getExecutionRoot());
		AnalysisLogger.getLogger().debug(log);

		// check for errors in run_log.txt
		this.checkForErrors(fsu.getExecutionRoot());

		// prepare the output
		this.prepareOutput();

		// cleanup
		this.removeWorkingDirectory();
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().info("Shutdown");
	}

	protected String getOutputFileName() {
		return "ewe-output-" + this.executionId+".zip";
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

	public String getExecutionId() {
		return executionId;
	}

	public void checkForErrors(File executionRoot) throws Exception {
		// read run_log.txt line by line
		File runLog = new File(executionRoot, "run_log.txt");
		FileInputStream fis = new FileInputStream(runLog);
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		Exception e = null;
		while ((line = br.readLine()) != null) {
			if("Ecopath model failed to load".equalsIgnoreCase(line)) {
				e = new Exception("Ecopath model failed to load");
			}
		}
	 
		br.close();

		if(e!=null)
			throw e;
		
	}
	
}