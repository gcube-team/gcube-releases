package org.gcube.portlets.user.statisticalalgorithmsimporter.server.blackbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GeneralPurposeScriptProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneralPurposeScriptProducer.class);
	private String remoteTemplateFile;
	
	
	// name,type,default
	public class Triple {
		String a;
		String b;
		String c;

		public Triple(String a, String b, String c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	public GeneralPurposeScriptProducer(String remoteTemplateFile){
		this.remoteTemplateFile=remoteTemplateFile;
	}
	
	public String URLReader(String urlToTemplate) throws Exception {
		URL remoteFile = new URL(urlToTemplate);
		BufferedReader in = new BufferedReader(new InputStreamReader(remoteFile.openStream()));
		StringBuffer sb = new StringBuffer();

		String inputLine = null;

		while ((inputLine = in.readLine()) != null)
			sb.append(inputLine + System.lineSeparator());

		in.close();

		return sb.toString();
	}

	public Path generateScript(List<Triple> input, List<Triple> output, String mainSoftware, String softwareType)
			throws Exception {
		return generateScript(input, output, mainSoftware, softwareType, null);
	}

	public Path generateScript(List<Triple> input, List<Triple> output, String mainSoftware, String softwareType,
			String URLtoSoftware) throws Exception {

		String mainSoftwareName = mainSoftware;
		//if (URLtoSoftware!=null) {
		//	logger.debug("Reading main software from remote URL: "+URLtoSoftware);
		//	mainSoftwareName = URLReader(URLtoSoftware);
		//	logger.debug("Main software name: "+mainSoftwareName);
		//	
		//}

		String inputDeclaration = generateInputStrings(input, softwareType, mainSoftwareName);
		String processInvocation = generateExternalProcessInvokation(mainSoftwareName, input);
		String outputDeclaration = generateOutputStrings(output);
		String fileExistenceChecks = generateFileExistenceCheck(output);
		String script = URLReader(remoteTemplateFile);

		script = script.replace("#INPUT_DECLARATION#", inputDeclaration);
		script = script.replace("#PROCESS_COMPOSITION#", processInvocation);
		script = script.replace("#OUTPUT_DECLARATION#", outputDeclaration);
		script = script.replace("#CHECKS#", fileExistenceChecks);

		// File outputFile = new File ("SAIExternalInvocationScript.R");
		// FileWriter fw = new FileWriter(outputFile);
		// fw.write(script);
		// fw.close();

		Path tempFile = Files.createTempFile("SAIExternalInvocationScript", ".R");

		Files.write(tempFile, script.getBytes(), StandardOpenOption.WRITE);

		logger.debug("Created output file: " + tempFile.toFile().getAbsolutePath());
		return tempFile;
	}

	private String generateExternalProcessInvokation(String mainSoftware, List<Triple> input) {
		StringBuffer sb = new StringBuffer();
		sb.append("external_process_to_invoke<-'" + mainSoftware
				+ "' #can be a python, fortran, linux-compiled, octave, java process" + System.lineSeparator());
		sb.append("#prepare the command to invoke" + System.lineSeparator());
		sb.append("command = paste(external_process_to_invoke");

		for (Triple in : input) {
			sb.append(","+"paste('\"',"+in.a+",'\"',sep='')");
		}

		sb.append(",sep=\" \")" + System.lineSeparator());

		sb.append("cat('Command:',command,'\\n')" + System.lineSeparator() + System.lineSeparator());

		return sb.toString();
	}

	private String generateFileExistenceCheck(List<Triple> output) {
		StringBuffer sb = new StringBuffer();
		sb.append("#check if the output exists before exiting" + System.lineSeparator());

		for (Triple o : output) {
			sb.append("fexists = file.exists(paste(" + o.a + ",sep=''))" + System.lineSeparator());
			sb.append("cat(\"file " + o.a + " exists?\",fexists,\"\\n\")" + System.lineSeparator());
			sb.append("if (!fexists){" + System.lineSeparator());
			sb.append("\tcat(fexists,\"Error, the output " + o.a + "(" + o.b + ")" + " does not exist!\\n\")"
					+ System.lineSeparator());
			sb.append("\tstoptheprocess" + System.lineSeparator());
			sb.append("}" + System.lineSeparator() + System.lineSeparator());
		}

		return sb.toString();
	}

	private String generateInputStrings(List<Triple> input, String softwareType, String mainSoftware) {

		StringBuffer sb = new StringBuffer();
		sb.append(System.lineSeparator() + "#parameters of the process to invoke" + System.lineSeparator());
		sb.append("false<-F" + System.lineSeparator());
		sb.append("true<-T" + System.lineSeparator());
		sb.append("softwaretype<-'" + softwareType + "'" + System.lineSeparator());
		sb.append("softwareName<-'" + mainSoftware + "'" + System.lineSeparator());

		String oinput = "overallInput<-'";
		String oinputValues = "overallInputValues<-paste(";
		String oinputTypes = "overallInputTypes<-'";

		for (Triple in : input) {
			String value = in.b;
			if (in.c.equals("enumerated")) {
				value = in.b.substring(0, in.b.indexOf("|"));
				sb.append(in.a + "<-\"" + value + "\"" + System.lineSeparator());
			} else
				sb.append(in.a + "<-\"" + in.b + "\"" + System.lineSeparator());

			oinput += in.a + "\t";
			oinputValues += in.a + ",";
			oinputTypes += in.c + "\t";

			sb.append("cat('Input Parameter:" + in.a + "'," + in.a + ",'\\n'" + ")" + System.lineSeparator());
		}

		sb.append(oinput.trim() + "'" + System.lineSeparator());
		sb.append(oinputValues.substring(0, oinputValues.length() - 1) + ",sep='\t')" + System.lineSeparator());
		sb.append(oinputTypes.trim() + "'" + System.lineSeparator());

		return sb.toString() + System.lineSeparator();
	}

	private String generateOutputStrings(List<Triple> output) {

		StringBuffer sb = new StringBuffer();

		sb.append(System.lineSeparator() + "#prepare the final output file" + System.lineSeparator());
		sb.append(
				"outputfolder <- getwd() #current working folder (a temporary folder is created at each run and set as working directory)"
						+ System.lineSeparator());

		for (Triple o : output) {
			sb.append(o.a + "<-\"" + o.b + "\"" + System.lineSeparator());
//			sb.append("#fixed code to add some randomness to the output file name and save concurrent requests"
//					+ System.lineSeparator());
//			sb.append(o.a + "<-paste("+"Sys.time(),'_'," + o.a + ",sep='')" + System.lineSeparator());
//			sb.append(o.a + "<-gsub(' ', '_', " + o.a + ")" + System.lineSeparator());
//			sb.append(o.a + "<-gsub(':', '_', " + o.a + ")" + System.lineSeparator());
//			sb.append("file.rename('" + o.b + "'," + o.a + ")" + System.lineSeparator());

			sb.append("cat('Output Parameter:" + o.a + "'," + o.a + ",'\\n'" + ")" + System.lineSeparator());

		}

		return sb.toString() + System.lineSeparator();
	}

}