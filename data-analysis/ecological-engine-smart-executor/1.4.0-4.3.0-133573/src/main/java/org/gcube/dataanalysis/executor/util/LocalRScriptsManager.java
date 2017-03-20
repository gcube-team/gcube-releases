package org.gcube.dataanalysis.executor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.junit.runner.notification.RunListener;

public class LocalRScriptsManager {

	
	public float status = 0;
	public String currentOutputURL;
	Process process;

	public String getCurrentOutputURL() {
		return currentOutputURL;
	}

	public void setCurrentOutputURL(String currentOutputURL) {
		this.currentOutputURL = currentOutputURL;
	}

	public String currentOutputFileName;

	public String getCurrentOutputFileName() {
		return currentOutputFileName;
	}

	public void setCurrentOutputFileName(String currentOutputFileName) {
		this.currentOutputFileName = currentOutputFileName;
	}

	public float getStatus() {
		return status;
	}

	public void setStatus(float status) {
		this.status = status;
	}


	public static void substituteStringInFile(String file, String newFile, String s, String sub, boolean regex) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFile)));
		String line = br.readLine();

		while (line != null) {
			if (!regex) {
				int idx = line.indexOf(s);
				if (idx >= 0) {
					line = line.replace(s, sub);
				}
			}else{
//				AnalysisLogger.getLogger().debug("Matching "+line+" vs "+s);
				if (line.matches(s)){
					AnalysisLogger.getLogger().debug("Matched Input "+s+" line becomes "+sub);
					line = sub;
				}
					
			}
				
			bw.write(line + "\n");
			line = br.readLine();
		}

		br.close();
		bw.close();
	}

	public String printRConsole(Process process) {
		StringBuffer uberbuffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			AnalysisLogger.getLogger().debug(sb);
			uberbuffer.append(sb.toString());
			AnalysisLogger.getLogger().debug("---ERRORS---");
			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			line = br.readLine();
			sb = new StringBuffer();

			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			AnalysisLogger.getLogger().debug(sb);
			uberbuffer.append(sb.toString());
			AnalysisLogger.getLogger().debug("---END OF ERRORS---");
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("---END BY PROCESS INTERRUPTION---");
		}
		
		return uberbuffer.toString();
	}

	public void executeRScript(AlgorithmConfiguration config, String scriptName, String inputFileURL, HashMap<String, String> inputParameters, String defaultInputFile, String defaultOutputFile, HashMap<String, String> codeInjections, boolean mustReturnAFile, boolean uploadOutputOnStorage, String sandboxFolder) throws Exception {
		executeRScript(config, scriptName, inputFileURL, inputParameters, null, defaultInputFile, defaultOutputFile, codeInjections, mustReturnAFile, uploadOutputOnStorage, true, sandboxFolder);
	}

	public void executeRScript(AlgorithmConfiguration config, String scriptName, String inputFileURL, HashMap<String, String> inputParameters, HashMap<String, String> outputParameters, String defaultInputFile, String defaultOutputFile, HashMap<String, String> codeInjections, boolean mustReturnAFile, boolean uploadOutputOnStorage, String sandboxFolder) throws Exception {
		executeRScript(config, scriptName, inputFileURL, inputParameters, outputParameters, defaultInputFile, defaultOutputFile, codeInjections, mustReturnAFile, uploadOutputOnStorage, true, sandboxFolder);
	}

	public void executeRScript(AlgorithmConfiguration config, String scriptName, String inputFileURL, HashMap<String, String> inputParameters, String defaultInputFile, String defaultOutputFile, HashMap<String, String> codeInjections, boolean mustReturnAFile, boolean uploadOutputOnStorage, boolean deletefiles, String sandboxFolder) throws Exception {
		executeRScript(config, scriptName, inputFileURL, inputParameters, null, defaultInputFile, defaultOutputFile, codeInjections, mustReturnAFile, uploadOutputOnStorage, deletefiles, sandboxFolder);
	}

	public String executeRScript(AlgorithmConfiguration config, String scriptName, String inputFileURL, HashMap<String, String> inputParameters, HashMap<String, String> outputParameters, String defaultInputFile, String defaultOutputFile, HashMap<String, String> codeInjections, boolean mustReturnAFile, boolean uploadOutputOnStorage, boolean deletefiles, String sandboxFolder) throws Exception {
		List<String> tempfiles = new ArrayList<String>();
		String RLog = "";
		try {
			status = 0;
			String scriptPath = new File(sandboxFolder, scriptName).getAbsolutePath();
			// String originalScriptPath = scriptPath;
			String preparedScriptPath = null;

			String owner = config.getParam("ServiceUserName");
			if (owner == null)
				owner = "ecological.engine";

			String scope = config.getGcubeScope();
			if (scope == null)
				scope = ScopeProvider.instance.get();

			AnalysisLogger.getLogger().debug("Current User: " + owner);
			AnalysisLogger.getLogger().debug("Current Scope: " + scope);
			AnalysisLogger.getLogger().debug("Sandbox Folder: " + sandboxFolder);
			AnalysisLogger.getLogger().debug("Script : " + scriptPath);
			AnalysisLogger.getLogger().debug("Prepared Script : " + preparedScriptPath);

			try {
				OSCommand.ExecuteGetLine("ls", null);
				OSCommand.ExecuteGetLine("pwd", null);
				OSCommand.ExecuteGetLine("chmod +x * | whoami", null);
			} catch (Throwable e) {
				AnalysisLogger.getLogger().debug("Cannot execute SO commands");
			}

			if (codeInjections != null) {
				for (String toSubstitute : codeInjections.keySet()) {
					String substitution = codeInjections.get(toSubstitute);
					AnalysisLogger.getLogger().debug("Substituting : " + toSubstitute + " with " + substitution);
					preparedScriptPath = new File(sandboxFolder, UUID.randomUUID() + scriptName).getAbsolutePath();
					substituteStringInFile(scriptPath, preparedScriptPath, toSubstitute, substitution,true);
					AnalysisLogger.getLogger().debug("New Script name: " + preparedScriptPath + " exists: " + new File(preparedScriptPath).exists());
					tempfiles.add(preparedScriptPath);
					scriptPath = preparedScriptPath;
				}
			}

			if (defaultInputFile != null) {
				String newInputFile = new File(sandboxFolder, UUID.randomUUID() + defaultInputFile).getAbsolutePath().replace("\\", "/");
				tempfiles.add(newInputFile);
				AnalysisLogger.getLogger().debug("New Input File is " + newInputFile);
				if (inputFileURL.trim().length() > 0) {
					AnalysisLogger.getLogger().debug("Substituting standard Input");
					preparedScriptPath = new File(sandboxFolder, UUID.randomUUID() + scriptName).getAbsolutePath();
					substituteStringInFile(scriptPath, preparedScriptPath, defaultInputFile, newInputFile,false);
				} else {
					if ((preparedScriptPath == null) || (preparedScriptPath.trim().length() == 0))
						preparedScriptPath = new File(sandboxFolder, scriptName).getAbsolutePath();
				}

				AnalysisLogger.getLogger().debug("Creating local file from remote file");
				if (inputFileURL.startsWith("http:") || inputFileURL.startsWith("smp:")) {
					StorageUtils.downloadInputFile(inputFileURL, newInputFile);
				} else if (inputFileURL.trim().length() > 0) {
					OSCommand.FileCopy(inputFileURL, newInputFile);
				}

				tempfiles.add(preparedScriptPath);
				scriptPath = preparedScriptPath;
			}
			String newOutputFile = UUID.randomUUID().toString() + defaultOutputFile;
			String newOutputFilePath = new File(sandboxFolder, newOutputFile).getAbsolutePath().replace("\\", "/");

			if (defaultOutputFile != null) {
				int extension = defaultOutputFile.lastIndexOf(".");
				if (extension > 0)
					newOutputFile = defaultOutputFile.substring(0, extension) + "_" + UUID.randomUUID().toString() + "." + defaultOutputFile.substring(extension + 1);
				newOutputFilePath = new File(sandboxFolder, newOutputFile).getAbsolutePath().replace("\\", "/");
				AnalysisLogger.getLogger().debug("New Output File is " + newOutputFilePath);
				AnalysisLogger.getLogger().debug("Substituting standard Output");
				preparedScriptPath = new File(sandboxFolder, UUID.randomUUID() + scriptName).getAbsolutePath();
				substituteStringInFile(scriptPath, preparedScriptPath, defaultOutputFile, newOutputFilePath,false);
				tempfiles.add(preparedScriptPath);
				scriptPath = preparedScriptPath;
			}

			AnalysisLogger.getLogger().debug("Executing R script " + scriptPath);
			status = 10;
			// run the R code
			String scriptdir = new File(scriptPath).getParent().replace("\\", "/");
			process = Runtime.getRuntime().exec("R --no-save\n", null, new File(scriptdir));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			// set the working directory to the one of the script
			bw.write("setwd(\"" + scriptdir + "\")\n");
			for (String inputparam : inputParameters.keySet()) {
				String value = inputParameters.get(inputparam);
				bw.write(inputparam + "=" + value + "\n");
			}
			bw.write("source('" + scriptPath.replace("\\", "/") + "')\n");
			// bw.write("source('" + originalScriptPath.replace("\\", "/") + "')\n");
			if (outputParameters != null) {
				for (String outputparam : outputParameters.keySet()) {
					bw.write("paste(\"out->\"," + outputparam + ")" + "\n");
					bw.write("write.csv(" + outputparam + ",file=\"" + new File(sandboxFolder, outputparam).getAbsolutePath().replace("\\", "/") + "\",row.names=FALSE,append=F,fileEncoding=\"UTF-8\",eol = \"\\n\")" + "\n");
				}
			}

			bw.write("q()\n");
			bw.close();
			RLog = printRConsole(process);
			
			process.destroy();

			if ((new File(newOutputFilePath)).exists()) {

				if (uploadOutputOnStorage) {
					AnalysisLogger.getLogger().debug("Found output file ... saving on the workspace");
					String outputURL = StorageUtils.uploadFilesOnStorage(scope, owner, sandboxFolder, newOutputFile);
					currentOutputURL = outputURL;
					AnalysisLogger.getLogger().debug("Output URL is " + outputURL);
				}
				currentOutputFileName = newOutputFilePath;
				AnalysisLogger.getLogger().debug("Output File is " + currentOutputFileName);

			} else if (!mustReturnAFile) {
				AnalysisLogger.getLogger().debug("No file detected as output");
				currentOutputFileName = null;
				currentOutputURL = null;
			} else
				throw new Exception("The script did not return an output - an error occurred");

			AnalysisLogger.getLogger().debug("The procedure was successful");
			status = 100f;
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("The R Script generated an error! " + e.getLocalizedMessage());
			throw e;
		} finally {
			if (deletefiles) {
				AnalysisLogger.getLogger().debug("Deleting temp files");

				for (String file : tempfiles) {
					boolean deleted = new File(file).delete();
					AnalysisLogger.getLogger().debug("Deleting " + file + " " + deleted);

				}

				AnalysisLogger.getLogger().debug("Done: Deleted temp files");
			}
		}
		
		return RLog;
	}

	public void stop() {
		if (process != null) {
			try {
				process.destroy();
			} catch (Exception e) {

			}
		}
	}

	public static void main1(String[] args) throws Exception {
		String url = StorageUtils.uploadFilesOnStorage("/gcube/devsec/devVRE", "ecological.engine", "C:\\Users\\coro\\Documents\\", "tacsatmini.csv");
		System.out.println(url);
	}

	public static void main(String[] args) throws Exception {

		LocalRScriptsManager scriptmanager = new LocalRScriptsManager();
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/gcube/devsec/devVRE");
		config.setPersistencePath("./PARALLEL_PROCESSING");

		String scriptName = "interpolateTacsat.r";
		String inputFileURL = "smp://tacsatmini.csv?5ezvFfBOLqa2CUPC0velBUnqe0zlyBOiHB8b4B4yi0tsfi7ohCndPEZuRB/t7SXwAms8bk8KwvyHVLeQ2bBnUSnXP87yjTYTEFaZxxzNE/tmBefs5CNO3iRTeJGE8NDJ1bwVGRS//+4QELLbyLZ/GesZiduaN+bqrNOs/9/AGgA3Pq24H+aXe5suWD31Sxwu";
		String defaultInputFile = "tacsat.csv";
		String defaultOutputFile = "tacsat_interpolated.csv";

		LinkedHashMap<String, String> inputParameters = new LinkedHashMap<String, String>();
		inputParameters.put("interval", "120");
		inputParameters.put("margin", "10");
		inputParameters.put("res", "100");
		inputParameters.put("method", "\"cHs\"");
		inputParameters.put("fm", "0.5");
		inputParameters.put("distscale", "20");
		inputParameters.put("sigline", "0.2");
		inputParameters.put("minspeedThr", "2");
		inputParameters.put("maxspeedThr", "6");
		inputParameters.put("st", "c(minspeedThr,maxspeedThr)");
		inputParameters.put("headingAdjustment", "0");
		inputParameters.put("fast", "TRUE");
		inputParameters.put("npoints", "10");
		inputParameters.put("equalDist", "TRUE");

		scriptmanager.executeRScript(config, scriptName, inputFileURL, inputParameters, defaultInputFile, defaultOutputFile, null, true, true, "./");
	}

}
