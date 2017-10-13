package org.gcube.dataanalysis.executor.rscripts.generic;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.ZipTools;
import org.gcube.dataanalysis.executor.util.LocalRScriptsManager;
import org.gcube.dataanalysis.executor.util.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericRScript extends StandardLocalInfraAlgorithm {
	
	private static final  Logger LOGGER = LoggerFactory.getLogger(GenericRScript.class);
	
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
		LOGGER.debug("Initializing " + this.getClass().getCanonicalName());
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
			LOGGER.debug("Starting script");

			File localPackage = new File(config.getConfigPath(), "package" + UUID.randomUUID() + ".zip");
			LOGGER.debug("Downloading package " + packageURL + "as: " + localPackage.getAbsolutePath());
			StorageUtils.downloadInputFile(packageURL, localPackage.getAbsolutePath(), true);
			LOGGER.debug("Generating sandbox folder");
			File folder = new File(config.getConfigPath(), "rscr_" + UUID.randomUUID());
			boolean mkdir = folder.mkdir();
			LOGGER.debug("Sandbox " + folder.getAbsolutePath() + " generated: " + mkdir);
			LOGGER.debug("Unzipping package into " + folder.getAbsolutePath());
			ZipTools.unZip(localPackage.getAbsolutePath(), folder.getAbsolutePath());
			localPackage.delete();

			// File folder = new File("C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineSmartExecutor/./cfg/rscr_7d329495-b048-4ce0-8bcc-bd74966db56d/");

			LOGGER.debug("Analysing inputs");
			// distinguish the environmental variables and the input variables
			HashMap<String, String> inputParameters = new LinkedHashMap<String, String>();
			int i = 0;
			List<StatisticalType> inputs = getInputParameters();
			for (String input : inputvariables) {
				String value = config.getParam(input);
				if (value == null)
					value = "";
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
			LOGGER.debug("Inputs: " + inputParameters.toString());
			LOGGER.debug("Analysing environmental variables");
			HashMap<String, String> environmentalParameters = new LinkedHashMap<String, String>();
			for (String environment : environmentalvariables) {
				String value = config.getParam(environment);
				environmentalParameters.put(environment, value);
			}
			LOGGER.debug("Environmental: " + environmentalParameters.toString());
			LOGGER.debug("Analysing output variables");
			HashMap<String, String> outputParameters = new LinkedHashMap<String, String>();
			for (String output : outputvariables) {
				outputParameters.put(output, "");
			}
			LOGGER.debug("Output: " + outputParameters.toString());

			LOGGER.debug("Main script: " + mainScriptName);
			LOGGER.debug("Processing main script name");
			// build the script name
			String scriptName = mainScriptName;
			String scriptSubfolder = "";
			int lioSlash = scriptName.lastIndexOf("/");
			if (lioSlash > -1) {
				scriptSubfolder = scriptName.substring(0, lioSlash);
				scriptName = scriptName.substring(lioSlash + 1);
			}
			scriptSubfolder = new File(folder.getAbsoluteFile(), scriptSubfolder).getAbsolutePath();
			LOGGER.debug("Main script name: " + scriptName);
			LOGGER.debug("Main script folder: " + scriptSubfolder);

			boolean scriptMustReturnAFile = false;
			boolean uploadScriptOnTheInfrastructureWorkspace = false;
			status = 10;
			LOGGER.debug("Executing the script...");
			// execute the script in multi-user mode

			environmentalParameters.put("gcube_token","\""+config.getGcubeToken()+"\"");
			environmentalParameters.put("gcube_username","\""+config.getGcubeUserName()+"\"");
			environmentalParameters.put("gcube_context","\""+config.getGcubeScope()+"\"");
			
			Rlog = scriptmanager.executeRScript(config, scriptName, null, environmentalParameters, outputParameters, null, null, inputParameters, scriptMustReturnAFile, uploadScriptOnTheInfrastructureWorkspace, deletefiles, scriptSubfolder);
			LOGGER.debug("..execution finished!");
			// get the output: one file should have been produced for each output
			LOGGER.debug("Getting output");
			for (String output : outputvariables) {
				File outPath = new File(scriptSubfolder, output);
				LOGGER.debug("Output " + output + " - loading respective file from " + outPath.getAbsolutePath());
				LOGGER.debug("File exists? " + outPath.exists());
				String fileContent = FileTools.loadString(outPath.getAbsolutePath(), "UTF-8");
				fileContent = analyseContent(fileContent, scriptSubfolder);
				LOGGER.debug("Retrieved output content: " + output + ": " + fileContent);
				outputValues.put(output, fileContent);
			}

			// delete the script folder

			try {
				if (deletefiles) {
					LOGGER.debug("Deleting sandbox folder");
					FileUtils.cleanDirectory(folder);
					FileUtils.deleteDirectory(folder);
					LOGGER.debug("Folder " + folder.getAbsolutePath() + " deleted");
				}

			} catch (Exception e) {
				LOGGER.warn("Could not delete sandbox folder "+folder.getAbsolutePath(),e);
			}
			
			if (Rlog != null) {
				File logFile = saveLogFile(Rlog);
				output.put("Log", new PrimitiveType(File.class.getName(), logFile, PrimitiveTypes.FILE, "LogFile", "Log of the computation"));
			} 

		} catch (Exception e) {

			if (Rlog != null) {
				String httpurl = generateRemoteLogFile(Rlog);
				String message = "Logs of the script can be found at "+httpurl;
				e = new Exception(message);
			} 
				throw e;
		} finally {
			LOGGER.debug("Computation finished.");
			status = 100;
		}
	}

	protected File saveLogFile(String Rlog) throws Exception {
		String uuid = UUID.randomUUID().toString();
		LOGGER.debug("Writing the logs of the execution");
		File logfile = new File(config.getPersistencePath(), "RLOG" + uuid + ".txt");

		FileWriter fw = new FileWriter(logfile);
		fw.write(Rlog);
		fw.close();
		LOGGER.debug("Written in " + logfile);
		return logfile;
	}
	
	protected String generateRemoteLogFile(String Rlog) throws Exception {
		String uuid = "" + UUID.randomUUID();
		LOGGER.debug("Writing the logs of the execution");
		File logfile = new File(config.getPersistencePath(), "RLOG" + uuid + ".txt");

		FileWriter fw = new FileWriter(logfile);
		fw.write(Rlog);
		fw.close();
		LOGGER.debug("Written in " + logfile);
		String httpurl = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), logfile.getParent(), "/ScriptLogs/" + uuid + "/", logfile.getName(),true);
		LOGGER.debug("Uploaded on storage: " + httpurl);
		
		// String httpurl = url.replace("smp:", "http:");
		/*
		if (config.getGcubeScope().startsWith("/gcube"))
			httpurl = "http://data-d.d4science.org/uri-resolver/smp?smp-uri=" + url + "&fileName=" + logfile.getName() ;
		else
			httpurl = "http://data.d4science.org/uri-resolver/smp?smp-uri=" + url + "&fileName=" + logfile.getName() ;
		 */
		
		LOGGER.debug("Deleting log file and returning " + httpurl);
		logfile.delete();
		// httpurl = URLEncoder.encode(httpurl, "UTF-8");
		return httpurl;
	}

	protected String analyseContent(String filecontent, String scriptfolder) throws Exception {
		LOGGER.debug("Analysing file content");
		String[] rows = filecontent.split(System.lineSeparator());
		List<String> files = new ArrayList<String>();
		for (String row : rows) {
			LOGGER.debug("Analysing -> " + row);
			if (row != null) {
				row = row.replace("\"", "");
				File rowFile = new File(row.trim());
				LOGGER.debug("Checking row file-> " + rowFile.getAbsolutePath());
				if (!rowFile.exists()) {
					rowFile = new File(scriptfolder, row.trim());
					LOGGER.debug("File does not exist - checking complete row file-> " + rowFile.getAbsolutePath());
				}
				if (rowFile.exists()) {

					String preparedFile = new File(config.getConfigPath(), rowFile.getName()).getAbsolutePath();
					LOGGER.debug("Copying " + rowFile.getAbsolutePath() + " to " + preparedFile);
					try{
					org.gcube.dataanalysis.executor.rscripts.generic.FileUtils.moveFileToDirectory(rowFile, new File(config.getConfigPath()), false);
					files.add(preparedFile);
					}catch(Exception e){
						LOGGER.error("error in moving file "+rowFile.getAbsolutePath()+" to "+preparedFile,e);
						throw e;
					}
					break;
				} else
					LOGGER.debug("Checking row file does not exist - treating as a String");
			}
		}

		if (files.size() > 0) {
			LOGGER.debug("A File was recognized as output");
			return files.get(0);
		} else {
			LOGGER.debug("A String was recognized as output");
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
