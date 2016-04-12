package org.gcube.dataanalysis.executor.rscripts.generic;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.ZipTools;
import org.gcube.dataanalysis.executor.util.LocalRScriptsManager;
import org.gcube.dataanalysis.executor.util.StorageUtils;

public abstract class GenericRScript extends StandardLocalExternalAlgorithm {
	
	// FIXED part
	protected HashMap<String, String> outputValues = new HashMap<String, String>();
	protected LinkedHashMap<String, StatisticalType> output = new LinkedHashMap<String, StatisticalType>();
	LocalRScriptsManager scriptmanager;

	@Override
	public void shutdown() {
		// in the case of forced shutdown, stop the R process
		if (scriptmanager != null)
			scriptmanager.stop();
		System.gc();
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initializing " + this.getClass().getCanonicalName());
		initVariables();
	}

	@Override
	protected void process() throws Exception {
		boolean deletefiles = true; // for test only
		String Rlog = null;
		// init status
		status = 0;
		try {
			// instantiate the R Script executor
			scriptmanager = new LocalRScriptsManager();
			// download and unzip the script in a controlled folder
			AnalysisLogger.getLogger().debug("Starting script");

			File localPackage = new File(config.getConfigPath(), "package" + UUID.randomUUID() + ".zip");
			AnalysisLogger.getLogger().debug("Downloading package " + packageURL + "as: " + localPackage.getAbsolutePath());
			StorageUtils.downloadInputFile(packageURL, localPackage.getAbsolutePath(), true);
			AnalysisLogger.getLogger().debug("Generating sandbox folder");
			File folder = new File(config.getConfigPath(), "rscr_" + UUID.randomUUID());
			boolean mkdir = folder.mkdir();
			AnalysisLogger.getLogger().debug("Sandbox " + folder.getAbsolutePath() + " generated: " + mkdir);
			AnalysisLogger.getLogger().debug("Unzipping package into " + folder.getAbsolutePath());
			ZipTools.unZip(localPackage.getAbsolutePath(), folder.getAbsolutePath());
			localPackage.delete();

			// File folder = new File("C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineSmartExecutor/./cfg/rscr_7d329495-b048-4ce0-8bcc-bd74966db56d/");

			AnalysisLogger.getLogger().debug("Analysing inputs");
			// distinguish the environmental variables and the input variables
			HashMap<String, String> inputParameters = new LinkedHashMap<String, String>();
			int i = 0;
			List<StatisticalType> inputs = getInputParameters();
			for (String input : inputvariables) {
				String value = config.getParam(input);
				String defaultValue = inputs.get(i).getDefaultValue();
				defaultValue = defaultValue.replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]").replace("|", "\\|").replace(".", "\\.").replace("?", "\\?").replace("*", "\\*").replace("+", "\\+").replace("{", "\\{").replace("}", "\\}");
				// inputParameters.put(defaultValue, value);
				String punct = "[ 	\";]*";
				String regexp = punct + input + punct + "(<-|=)" + punct + defaultValue + punct + ".*";
				boolean string = true;
				try {
					Double.parseDouble(value);
					string = false;
				} catch (Exception ee) {

				}
				String line = input + "=" + value;
				if (string && !value.equals("T") && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false") && !value.equalsIgnoreCase("F"))
					line = input + "=\"" + value + "\"";

				inputParameters.put(regexp, line);
				i++;
			}
			AnalysisLogger.getLogger().debug("Inputs: " + inputParameters.toString());
			AnalysisLogger.getLogger().debug("Analysing environmental variables");
			HashMap<String, String> environmentalParameters = new LinkedHashMap<String, String>();
			for (String environment : environmentalvariables) {
				String value = config.getParam(environment);
				environmentalParameters.put(environment, value);
			}
			AnalysisLogger.getLogger().debug("Environmental: " + environmentalParameters.toString());
			AnalysisLogger.getLogger().debug("Analysing output variables");
			HashMap<String, String> outputParameters = new LinkedHashMap<String, String>();
			for (String output : outputvariables) {
				outputParameters.put(output, "");
			}
			AnalysisLogger.getLogger().debug("Output: " + outputParameters.toString());

			AnalysisLogger.getLogger().debug("Main script: " + mainScriptName);
			AnalysisLogger.getLogger().debug("Processing main script name");
			// build the script name
			String scriptName = mainScriptName;
			String scriptSubfolder = "";
			int lioSlash = scriptName.lastIndexOf("/");
			if (lioSlash > -1) {
				scriptSubfolder = scriptName.substring(0, lioSlash);
				scriptName = scriptName.substring(lioSlash + 1);
			}
			scriptSubfolder = new File(folder.getAbsoluteFile(), scriptSubfolder).getAbsolutePath();
			AnalysisLogger.getLogger().debug("Main script name: " + scriptName);
			AnalysisLogger.getLogger().debug("Main script folder: " + scriptSubfolder);

			boolean scriptMustReturnAFile = false;
			boolean uploadScriptOnTheInfrastructureWorkspace = false;
			status = 10;
			AnalysisLogger.getLogger().debug("Executing the script...");
			// execute the script in multi-user mode

			Rlog = scriptmanager.executeRScript(config, scriptName, null, environmentalParameters, outputParameters, null, null, inputParameters, scriptMustReturnAFile, uploadScriptOnTheInfrastructureWorkspace, deletefiles, scriptSubfolder);
			AnalysisLogger.getLogger().debug("..execution finished!");
			// get the output: one file should have been produced for each output
			AnalysisLogger.getLogger().debug("Getting output");
			for (String output : outputvariables) {
				File outPath = new File(scriptSubfolder, output);
				AnalysisLogger.getLogger().debug("Output " + output + " - loading respective file from " + outPath.getAbsolutePath());
				AnalysisLogger.getLogger().debug("File exists? " + outPath.exists());
				String fileContent = FileTools.loadString(outPath.getAbsolutePath(), "UTF-8");
				fileContent = analyseContent(fileContent, scriptSubfolder);
				AnalysisLogger.getLogger().debug("Retrieved output content: " + output + ": " + fileContent);
				outputValues.put(output, fileContent);
			}

			// delete the script folder

			try {
				if (deletefiles) {
					AnalysisLogger.getLogger().debug("Deleting sandbox folder");
					FileUtils.cleanDirectory(folder);
					FileUtils.deleteDirectory(folder);
					AnalysisLogger.getLogger().debug("Folder " + folder.getAbsolutePath() + " deleted");
				}

			} catch (Exception e) {
				AnalysisLogger.getLogger().debug(e);
				AnalysisLogger.getLogger().debug("Could not delete sandbox folder " + folder.getAbsolutePath());
			}
		} catch (Exception e) {

			if (Rlog != null) {
				String httpurl = generateRemoteLogFile(Rlog);
				String message = "Logs of the script can be found at "+httpurl;
				e = new Exception(message);
			} 
				throw e;
		} finally {
			AnalysisLogger.getLogger().debug("Computation finished.");
			status = 100;
		}
	}

	protected String generateRemoteLogFile(String Rlog) throws Exception {
		String uuid = "" + UUID.randomUUID();
		AnalysisLogger.getLogger().debug("Writing the logs of the execution");
		File logfile = new File(config.getPersistencePath(), "RLOG" + uuid + ".txt");

		FileWriter fw = new FileWriter(logfile);
		fw.write(Rlog);
		fw.close();
		AnalysisLogger.getLogger().debug("Written in " + logfile);
		String httpurl = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), logfile.getParent(), "/ScriptLogs/" + uuid + "/", logfile.getName(),true);
		AnalysisLogger.getLogger().debug("Uploaded on storage: " + httpurl);
		
		// String httpurl = url.replace("smp:", "http:");
		/*
		if (config.getGcubeScope().startsWith("/gcube"))
			httpurl = "http://data-d.d4science.org/uri-resolver/smp?smp-uri=" + url + "&fileName=" + logfile.getName() ;
		else
			httpurl = "http://data.d4science.org/uri-resolver/smp?smp-uri=" + url + "&fileName=" + logfile.getName() ;
		 */
		
		AnalysisLogger.getLogger().debug("Deleting log file and returning " + httpurl);
		logfile.delete();
		// httpurl = URLEncoder.encode(httpurl, "UTF-8");
		return httpurl;
	}

	protected String analyseContent(String filecontent, String scriptfolder) throws Exception {
		AnalysisLogger.getLogger().debug("Analysing file content");
		String[] rows = filecontent.split(System.lineSeparator());
		List<String> files = new ArrayList<String>();
		for (String row : rows) {
			AnalysisLogger.getLogger().debug("Analysing -> " + row);
			if (row != null) {
				row = row.replace("\"", "");
				File rowFile = new File(row.trim());
				AnalysisLogger.getLogger().debug("Checking row file-> " + rowFile.getAbsolutePath());
				if (!rowFile.exists()) {
					rowFile = new File(scriptfolder, row.trim());
					AnalysisLogger.getLogger().debug("File does not exist - checking complete row file-> " + rowFile.getAbsolutePath());
				}
				if (rowFile.exists()) {

					String preparedFile = new File(config.getConfigPath(), rowFile.getName()).getAbsolutePath();
					AnalysisLogger.getLogger().debug("Copying " + rowFile.getAbsolutePath() + " to " + preparedFile);
					org.gcube.dataanalysis.executor.rscripts.generic.FileUtils.moveFileToDirectory(rowFile, new File(config.getConfigPath()), false);
					files.add(preparedFile);

					break;
				} else
					AnalysisLogger.getLogger().debug("Checking row file does not exist - treating as a String");
			}
		}

		if (files.size() > 0) {
			AnalysisLogger.getLogger().debug("A File was recognized as output");
			return files.get(0);
		} else {
			AnalysisLogger.getLogger().debug("A String was recognized as output");
			return filecontent;
		}
	}

	// DYNAMIC part
	protected String mainScriptName = "";
	protected String packageURL = "";

	protected List<String> environmentalvariables = new ArrayList<String>();
	protected List<String> inputvariables = new ArrayList<String>();
	protected List<String> outputvariables = new ArrayList<String>();

	protected void initVariables() {

	}

	@Override
	protected void setInputParameters() {
	}

	@Override
	public StatisticalType getOutput() {
		return null;
	}

}
