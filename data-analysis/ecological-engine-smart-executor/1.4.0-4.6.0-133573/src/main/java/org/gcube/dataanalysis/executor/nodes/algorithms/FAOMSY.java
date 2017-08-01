package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.gcube.dataanalysis.executor.util.RScriptsManager;
import org.gcube.dataanalysis.executor.util.StorageUtils;

public class FAOMSY extends ActorNode {

	public int count;

	public float status = 0;
	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
		return p;
	}

	@Override
	public String getName() {
		return "FAOMSY";
	}

	@Override
	public String getDescription() {
		return "An algorithm to be used by Fisheries managers for stock assessment. " +
				"Estimates the Maximum Sustainable Yield (MSY) of a stock, based on a catch trend. " +
				"The algorithm has been developed by the Resource Use and Conservation Division of the FAO Fisheries and Aquaculture Department (contact: Yimin Ye, yimin.ye@fao.org). " +
				"It is applicable to a CSV file containing metadata and catch statistics for a set of marine species and produces MSY estimates for each species. " +
				"The CSV must follow a FAO-defined format (e.g. http://goo.gl/g6YtVx). " +
				"The output is made up of two (optional) files: one for sucessfully processed species and another one for species that could not be processed because data were not sufficient to estimate MSY.";
	}

	static String stocksFile = "StocksFile";
	static String processOutput= "ProcessOutput";
	static String nonProcessedOutput= "NonProcessedOutput";
	static String scriptName = "CatchMSY_Dec2014.R";
	String processedSpOutputFile="";
	String nonProcessedSpOutputFile=""; 
	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		IOHelper.addStringInput(parameters, stocksFile, "Http link to a file containing catch statistics for a group of species. Example: http://goo.gl/g6YtVx", "");
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		File outfile = new File(processedSpOutputFile);
		File outfile2 = new File(nonProcessedSpOutputFile);
		AnalysisLogger.getLogger().debug("FAOMSY Output 1: "+outfile.getAbsolutePath()+" : "+outfile.exists());
		AnalysisLogger.getLogger().debug("FAOMSY Output 2: "+outfile2.getAbsolutePath()+" : "+outfile2.exists());
		
		LinkedHashMap<String, StatisticalType> outputmap = new LinkedHashMap<String, StatisticalType>();
		
		if (outfile.exists()){
			PrimitiveType o = new PrimitiveType(File.class.getName(), outfile, PrimitiveTypes.FILE, "ProcessedSpecies", "Output file with processed species");
			outputmap.put("File containing Processed Species", o);
		}
		if (outfile2.exists()){
			PrimitiveType o2 = new PrimitiveType(File.class.getName(), outfile2, PrimitiveTypes.FILE, "NonProcessedSpecies", "Output file with non processed species");
			outputmap.put("File containing Non Processed Species", o2);
		}
		
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), outputmap, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		AnalysisLogger.getLogger().debug("FAOMSY Output Managed");
		
		return output;
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
		
	}

	@Override
	public float getInternalStatus() {
		return status;
	}
	
	AlgorithmConfiguration config;
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,  boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		try {
			status = 0;
			config = config = Transformations.restoreConfig(new File (sandboxFolder,nodeConfigurationFileObject).getAbsolutePath());
			String outputFile = config.getParam(processOutput)+"_part"+rightStartIndex;
			String nonprocessedoutputFile = config.getParam(nonProcessedOutput)+"_part"+rightStartIndex;
			AnalysisLogger.getLogger().info("FAOMSY ranges: "+" Li:"+leftStartIndex+" NLi:"+leftStartIndex+" Ri:"+rightStartIndex+" NRi:"+numberOfRightElementsToProcess);
			
			AnalysisLogger.getLogger().info("FAOMSY expected output "+outputFile);
			
			File filestock=new File(sandboxFolder,"D20_1.csv");
			StorageUtils.downloadInputFile(config.getParam(stocksFile), filestock.getAbsolutePath());
			
			AnalysisLogger.getLogger().debug("Check fileStocks: "+filestock.getAbsolutePath()+" "+filestock.exists());
			File filestocksub=new File(sandboxFolder,"D20.csv");
			
			StorageUtils.FileSubset(filestock, filestocksub, rightStartIndex, numberOfRightElementsToProcess, true);
			
			RScriptsManager scriptmanager = new RScriptsManager();
			
			HashMap<String,String> codeinj = new HashMap<String,String>();
			config.setConfigPath("./");
			scriptmanager.executeRScript(config, scriptName, "", new HashMap<String,String>(), "", "CatchMSY_Output.csv", codeinj, false,false,false,sandboxFolder);
			AnalysisLogger.getLogger().info("FAOMSY The script has finished");
			String outputFileName = "";
			//manage the fact that the outputfile could even not exist
			try{outputFileName = scriptmanager.getCurrentOutputFileName();}catch(Exception e){
				AnalysisLogger.getLogger().info("FAOMSY Could not get curent output file");
			}
			String optionalFileOutputName =  "NonProcessedSpecies.csv";
			
			if (outputFileName!=null && outputFileName.length()>0 && new File(outputFileName).exists()){
				AnalysisLogger.getLogger().info("FAOMSY Main file exists!");
				outputFileName = scriptmanager.getCurrentOutputFileName();
				String outputFilePath = new File(sandboxFolder,outputFile).getAbsolutePath();
				AnalysisLogger.getLogger().info("FAOMSY writing output file in path "+outputFilePath);
				OSCommand.FileCopy(outputFileName,outputFilePath);
				AnalysisLogger.getLogger().info("FAOMSY uploading output file "+outputFile);
				StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), sandboxFolder,outputFile);
			}
			if (new File(optionalFileOutputName).exists()){
				AnalysisLogger.getLogger().info("FAOMSY Optional file exists!");
				OSCommand.FileCopy(optionalFileOutputName,nonprocessedoutputFile);
				AnalysisLogger.getLogger().info("FAOMSY uploading output file "+nonprocessedoutputFile);
				//check only
//				String file = FileTools.loadString(nonprocessedoutputFile, "UTF-8");
//				AnalysisLogger.getLogger().info("FAOMSY File check"+file);
				StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), sandboxFolder,nonprocessedoutputFile);
			}

			AnalysisLogger.getLogger().info("FAOMSY Finished");
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}	
			
		return 0;
	}

	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		this.config = config;
		AnalysisLogger.getLogger().info("FAOMSY process is initialized");
		String uuid = (UUID.randomUUID()+".txt").replace("-", "");
		config.setParam(processOutput, "FAOMSY_"+"output_"+uuid);
		config.setParam(nonProcessedOutput, "FAOMSY_nonprocessed_"+"output_"+uuid);
		File tempfile = new File(config.getPersistencePath(),"FAOMSY_input_"+(UUID.randomUUID()+".csv").replace("-", ""));
		try{
			if (config.getParam(stocksFile).toLowerCase().startsWith("https:"))
				throw new Exception("Error in FAOMSY:  https link not supported!");
			StorageUtils.downloadInputFile(config.getParam(stocksFile), tempfile.getAbsolutePath());
		}catch(Exception e){
			throw new Exception("Error in FAOMSY: error in accessing file (alert: https not supported)");
		}
		nstocks = StorageUtils.calcFileRows(tempfile, true);
		AnalysisLogger.getLogger().info("FAOMSY Found "+nstocks+" stocks!");
		if (nstocks==0)
			throw new Exception("Error in FAOMSY: No stocks to process found in the file "+config.getParam(stocksFile));
		
	}

	int nstocks = 0;
	@Override
	public int getNumberOfRightElements() {
		return nstocks;
	}

	@Override
	public int getNumberOfLeftElements() {
		return 1;
	}

	@Override
	public void stop() {
		AnalysisLogger.getLogger().info("CMSY process stopped");
	}

	boolean haspostprocessed = false;
	
	public void assembleFiles(String outputFileName) throws Exception{
		
		//try downloading all the files
		List<String> fileslist = new ArrayList<String>();
		for (int i=0;i<=nstocks;i++){
			String filename = outputFileName+"_part"+i;
			try{
				StorageUtils.downloadFilefromStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), config.getPersistencePath(), filename);
				AnalysisLogger.getLogger().debug("FAOMSY - Saved from Storage: "+filename);
				fileslist.add(filename);
			}catch(Exception e){
				AnalysisLogger.getLogger().debug("FAOMSY - Did not save file from Storage: "+filename);
				}
		}
		
		
		AnalysisLogger.getLogger().debug("FAOMSY - Merging files in "+outputFileName);
		if (fileslist.size()>0)
			StorageUtils.mergeFiles(config.getPersistencePath(), fileslist, outputFileName, true);
		
		AnalysisLogger.getLogger().debug("FAOMSY - Deleting parts");
		for (String file:fileslist){
			new File(config.getPersistencePath(),file).delete();
		}
		
		AnalysisLogger.getLogger().debug("FAOMSY - File assembling complete");
		
	}
	
	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		try {
			String mainOutputfilename = config.getParam(processOutput);
			String optionalOutputfilename = config.getParam(nonProcessedOutput);
			processedSpOutputFile = new File(config.getPersistencePath(),mainOutputfilename).getAbsolutePath();
			nonProcessedSpOutputFile = new File(config.getPersistencePath(),optionalOutputfilename).getAbsolutePath();
			assembleFiles(mainOutputfilename);
			assembleFiles(optionalOutputfilename);
			AnalysisLogger.getLogger().debug("FAOMSY - Postprocess complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
