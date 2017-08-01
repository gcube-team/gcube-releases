package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.gcube.dataanalysis.executor.util.LocalRScriptsManager;
import org.gcube.dataanalysis.executor.util.RScriptsManager;
import org.gcube.dataanalysis.executor.util.StorageUtils;

public class CMSY extends ActorNode {

	public int count;

	public float status = 0;
	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
		return p;
	}

	@Override
	public String getName() {
		return "CMSY";
	}

	@Override
	public String getDescription() {
		return "An algorithm to estimate the Maximum Sustainable Yield from a catch statistic. If also a Biomass trend is provided, MSY estimation is provided also with higher precision. The method has been developed by R. Froese, G. Coro, N. Demirel and K. Kleisner.";
	}

	static String idsFile = "IDsFile";
	static String stocksFile = "StocksFile";
	static String stock= "SelectedStock";
	static String processOutput= "ProcessOutput";
	static String scriptName = "CMSY_22_noplot.R";
	
	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		
		IOHelper.addStringInput(parameters, idsFile, "Http link to a file containing prior information about the stocks, in WKLife IV format. Example: http://goo.gl/9rg3qK", "");
		IOHelper.addStringInput(parameters, stocksFile, "Http link to a file containing catch and biomass (or CPUE) trends , in WKLife IV format. Example: http://goo.gl/Mp2ZLY", "");
		IOHelper.addStringInput(parameters,stock,"The stock on which the procedure has to focus e.g. HLH_M07","" );
				
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		File outfile = new File(config.getPersistencePath(),config.getParam(processOutput));
		PrimitiveType o = new PrimitiveType(File.class.getName(), outfile, PrimitiveTypes.FILE, "OutputFile", "Output file");
		return o;
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
		
	}

	@Override
	public float getInternalStatus() {
		return status;
	}
	
	String outputFileName;
	AlgorithmConfiguration config;
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,  boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		try {
			status = 0;
			config = Transformations.restoreConfig(new File (sandboxFolder,nodeConfigurationFileObject).getAbsolutePath());
			String outputFile = config.getParam(processOutput);
			AnalysisLogger.getLogger().info("CMSY expected output "+outputFile);
			
			String fileid=new File(sandboxFolder,"WKLIFE4ID.csv").getAbsolutePath();
			String filestock=new File(sandboxFolder,"WKLIFE4Stocks.csv").getAbsolutePath();
			StorageUtils.downloadInputFile(config.getParam(idsFile), fileid);
			StorageUtils.downloadInputFile(config.getParam(stocksFile), filestock);
			AnalysisLogger.getLogger().debug("Check fileID: "+fileid+" "+new File(fileid).exists());
			AnalysisLogger.getLogger().debug("Check fileStocks: "+filestock+" "+new File(filestock).exists());
			//RScriptsManager scriptmanager = new RScriptsManager();
			LocalRScriptsManager scriptmanager = new LocalRScriptsManager();
			
			HashMap<String,String> codeinj = new HashMap<String,String>();
			codeinj.put("HLH_M07",config.getParam(stock));
			//config.setConfigPath("./");
			scriptmanager.executeRScript(config, scriptName, "", new HashMap<String,String>(), "", "outputfile.txt", codeinj, true,false,false,sandboxFolder);
			
			outputFileName = scriptmanager.getCurrentOutputFileName();
			String outputFilePath = new File(sandboxFolder,outputFile).getAbsolutePath();
			AnalysisLogger.getLogger().info("CMSY writing output file in path "+outputFilePath);
			OSCommand.FileCopy(outputFileName,outputFilePath);
			AnalysisLogger.getLogger().info("CMSY uploading output file "+outputFile);
			StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), sandboxFolder,outputFile);
			AnalysisLogger.getLogger().info("CMSY Finished");
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}	
			
		return 0;
	}

	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		this.config = config;
		AnalysisLogger.getLogger().info("CMSY process is initialized");
		config.setParam(processOutput, "CMSY_"+"output_"+(UUID.randomUUID()+".txt").replace("-", ""));
	}

	@Override
	public int getNumberOfRightElements() {
		return 1;
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
	
	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		try {
			String filename = config.getParam(processOutput);
			StorageUtils.downloadFilefromStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), config.getPersistencePath(), filename);
			AnalysisLogger.getLogger().debug("CMSY - Got file from Storage: "+filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
