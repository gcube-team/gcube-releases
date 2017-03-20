package org.gcube.dataanalysis.executor.rscripts;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.executor.util.RScriptsManager;

public class SGVMS_Interpolation extends StandardLocalInfraAlgorithm {

	private static int maxPoints = 10000;
	public enum methodEnum { cHs, SL};
	RScriptsManager scriptmanager;
	String outputFile;
	
	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initializing SGVMS_Interpolation");
	}

	@Override
	public String getDescription() {
		return "An interpolation method relying on the implementation by the Study Group on VMS (SGVMS). The method uses two interpolation approached to simulate vessels points at a certain temporal resolution. The input is a file in TACSAT format uploaded on the Statistical Manager. The output is another TACSAT file containing interpolated points." +
				"The underlying R code has been extracted from the SGVM VMSTools framework. This algorithm comes after a feasibility study (http://goo.gl/risQre) which clarifies the features an e-Infrastructure adds to the original scripts. " +
				"Limitation: the input will be processed up to "+maxPoints+" vessels trajectory points. " +
				"Credits: Hintzen, N. T., Bastardie, F., Beare, D., Piet, G. J., Ulrich, C., Deporte, N., Egekvist, J., et al. 2012. VMStools: Open-source software for the processing, analysis and visualisation of fisheries logbook and VMS data. Fisheries Research, 115-116: 31-43. " +
				"Hintzen, N. T., Piet, G. J., and Brunel, T. 2010. Improved estimation of trawling tracks using cubic Hermite spline interpolation of position registration data. Fisheries Research, 101: 108-115. " +
				"VMStools, available as an add-on package for R. Documentation available at https://code.google.com/p/vmstools/.  " +
				"Build versions of VMStools for Window, Mac, Linux available at https://docs.google.com/. " +
				"Authors: Niels T. Hintzen, Doug Beare";
	}

	@Override
	protected void process() throws Exception {
		
		status = 0;
		//instantiate the R Script executor
		scriptmanager = new RScriptsManager();
		//this is the script name
		String scriptName = "interpolateTacsat.r";
		//absolute path to the input, provided by the SM  
		String inputFile = config.getParam("InputFile");
		
		AnalysisLogger.getLogger().debug("Starting SGVM Interpolation-> Config path "+config.getConfigPath()+" Persistence path: "+config.getPersistencePath());
		//default input and outputs		
		String defaultInputFileInTheScript = "tacsat.csv";
		String defaultOutputFileInTheScript = "tacsat_interpolated.csv";
		//input parameters: represent the context of the script. Values will be assigned in the R environment.
		LinkedHashMap<String,String> inputParameters = new LinkedHashMap<String, String>();
		inputParameters.put("npoints",config.getParam("npoints"));
		inputParameters.put("interval",config.getParam("interval"));
		inputParameters.put("margin",config.getParam("margin"));
		inputParameters.put("res",config.getParam("res"));
		inputParameters.put("fm",config.getParam("fm"));
		inputParameters.put("distscale",config.getParam("distscale"));
		inputParameters.put("sigline",config.getParam("sigline"));
		inputParameters.put("minspeedThr",config.getParam("minspeedThr"));
		inputParameters.put("maxspeedThr",config.getParam("maxspeedThr"));
		inputParameters.put("headingAdjustment",config.getParam("headingAdjustment"));
		inputParameters.put("equalDist",config.getParam("equalDist").toUpperCase());
		//add static context variables
		inputParameters.put("st", "c(minspeedThr,maxspeedThr)");
		inputParameters.put("fast", "TRUE");
		inputParameters.put("method", "\""+config.getParam("method")+"\"");
		
		AnalysisLogger.getLogger().debug("Starting SGVM Interpolation-> Input Parameters: "+inputParameters);
		//if other code injection is required, put the strings to substitute as keys and the substituting ones as values
		HashMap<String,String> codeInjection = null;
		//force the script to produce an output file, otherwise generate an exception 
		boolean scriptMustReturnAFile = true;
		boolean uploadScriptOnTheInfrastructureWorkspace = false; //the Statistical Manager service will manage the upload
		AnalysisLogger.getLogger().debug("SGVM Interpolation-> Executing the script ");
		status = 10;
		//execute the script in multi-user mode
		scriptmanager.executeRScript(config, scriptName, inputFile, inputParameters, defaultInputFileInTheScript, defaultOutputFileInTheScript, codeInjection, scriptMustReturnAFile,uploadScriptOnTheInfrastructureWorkspace, config.getConfigPath());
		//assign the file path to an output variable for the SM
		outputFile = scriptmanager.currentOutputFileName;
		AnalysisLogger.getLogger().debug("SGVM Interpolation-> Output File is "+outputFile);
		status = 100;
	}

	@Override
	protected void setInputParameters() {
		//declare the input parameters the user will set: they will basically correspond to the R context
		inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, "InputFile", "Input file in TACSAT format. E.g. http://goo.gl/i16kPw"));
		addIntegerInput("npoints", "The number of pings or positions required between each real or actual vessel position or ping", "10");
		addIntegerInput("interval", "Average time in minutes between two adjacent datapoints", "120");
		addIntegerInput("margin", "Maximum deviation from specified interval to find adjacent datapoints (tolerance)", "10");
		addIntegerInput("res", "Number of points to use to create interpolation (including start and end point)", "100");
		addEnumerateInput(methodEnum.values(), "method","Set to cHs for cubic Hermite spline or SL for Straight Line interpolation", "cHs");
		addDoubleInput("fm", "The FM parameter in cubic interpolation", "0.5");
		addIntegerInput("distscale", "The DistScale parameter for cubic interpolation", "20");
		addDoubleInput("sigline", "The Sigline parameter in cubic interpolation", "0.2");
		addDoubleInput("minspeedThr", "A filter on the minimum speed to take into account for interpolation", "2");
		addDoubleInput("maxspeedThr", "A filter on the maximum speed to take into account for interpolation", "6");
		addIntegerInput("headingAdjustment", "Parameter to adjust the choice of heading depending on its own or previous point (0 or 1). Set 1 in case the heading at the endpoint does not represent the heading of the arriving vessel to that point but the departing vessel.", "0");
		inputs.add(new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, "equalDist", "Whether the number of positions returned should be equally spaced or not", "true"));
	}

	@Override
	public StatisticalType getOutput() {
		//return the output file by the procedure to the SM
		PrimitiveType o = new PrimitiveType(File.class.getName(), new File(outputFile), PrimitiveTypes.FILE, "OutputFile", "Output file in TACSAT format.");
		return o;
	}
	
	@Override
	public void shutdown() {
		//in the case of forced shutdown, stop the R process
		if (scriptmanager!=null)
			scriptmanager.stop();
		System.gc();
	}
	
}
