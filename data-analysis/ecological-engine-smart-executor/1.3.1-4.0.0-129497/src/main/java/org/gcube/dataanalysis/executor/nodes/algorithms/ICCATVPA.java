package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.gcube.dataanalysis.ecoengine.utils.ZipTools;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.gcube.dataanalysis.executor.util.RScriptsManager;
import org.gcube.dataanalysis.executor.util.StorageUtils;

public class ICCATVPA extends ActorNode {


	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
		return p;
	}

	@Override
	public String getName() {
		return "ICCAT_VPA";
	}

	@Override
	public String getDescription() {
		return "An algorithm for stock assessment of catch statistics published by the International Commission for the Conservation of Atlantic Tunas (ICCAT).  " +
				"Produces summary statistics about a stock, involving assessment of fishing mortality, abundance, catch trend,  fecundity and recruitment. Developed by IFREMER and IRD. " +
				"Contact persons: Sylvain Bonhommeau sylvain.bonhommeau@ifremer.fr,  Julien Barde julien.barde@ird.fr.";
	}

	protected static String YearStartInp = "StartYear";
	protected static String YearEndInp = "EndYear";
	protected static String CAAInp = "CAAFile";
	protected static String PCAAInp = "PCAAFile";
	protected static String CPUEInp = "CPUEFile";
	protected static String PwaaInp = "PwaaFile";
	protected static String waaInp = "waaFile";
	
	protected String CAAInpURL ;
	protected String PCAAInpURL;
	protected String CPUEInpURL;
	protected String PwaaInpURL;
	protected String waaInpURL;
	
	
	protected static String effectInp = "shortComment";
	protected static String nCPUEInp = "nCPUE";
	protected static String CPUEcutInp = "CPUE_cut";
	protected static String nRemoveYearInp = "n_remove_year";
	protected static String agePlusGroupInp = "age_plus_group";
	protected static String scriptName = "run_vpa.R";
	protected static String packageURL = "http://goo.gl/EqFjNZ";
	
	protected static String processOutputParam= "ProcessOutputParam";
	protected String processOutput= "ProcessOutput";
	
	protected AlgorithmConfiguration config;
	public float status = 0;
	
	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		IOHelper.addIntegerInput(parameters, YearStartInp, "First year of the dataset temporal extent", "1950");
		IOHelper.addIntegerInput(parameters, YearEndInp, "Last year of the dataset temporal extent", "2013");
		
		IOHelper.addFileInput(parameters, CAAInp, "Catch at Age Matrix (Number of Fish caught by year and for each age)", "CAA_Age1_25.csv");
		IOHelper.addFileInput(parameters, PCAAInp, "Partial Catch at Age Matrix (Number of Fish caught by gear and year and for each age)", "PCAA_Age1_25.csv");
		IOHelper.addFileInput(parameters, CPUEInp, "Table of Catch Per Unit of Effort used in the stock assessment", "CPUE.csv");
		IOHelper.addFileInput(parameters, PwaaInp, "Partial weight at age (Weight of Fish caught by gear and year and for each age)", "waa.csv");
		IOHelper.addFileInput(parameters, waaInp, "Fecundity at age (Fecundity of Fish caught by year and for each age)", "fecaa.csv");
		
		IOHelper.addStringInput(parameters, effectInp, "Free text for users to describe the current simulation", " ");
		IOHelper.addIntegerInput(parameters, nCPUEInp, "Number of Catch Per Unit of Effort Time series to use", "7");
		IOHelper.addIntegerInput(parameters, CPUEcutInp, "Identifier of the Catch Per Unit of Effort Time Serie to be shrunk", "1");
		IOHelper.addIntegerInput(parameters, nRemoveYearInp, "Number of the (last) years to be removed", "1");
		IOHelper.addIntegerInput(parameters, agePlusGroupInp, "Maximal age class of catches to be taken into account", "10");
		
		return parameters;
	}
	
	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		this.config = config;
		
		AnalysisLogger.getLogger().info("ICCAT-VPA process is initialized in scope "+config.getGcubeScope()+" for user "+config.getParam("ServiceUserName"));
		String uuid = (UUID.randomUUID()+"").replace("-", "");
		processOutput = "ICCAT-VPA_"+"output_"+uuid+".zip";
		
		config.setParam(processOutputParam, processOutput);
		AnalysisLogger.getLogger().debug("ICCAT-VPA Uploading input files (http): "+config.getGeneralProperties());
		//upload files on the storage manager
		CAAInpURL = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), new File(config.getParam(CAAInp)).getParent(), "/",new File(config.getParam(CAAInp)).getName(),true);
		AnalysisLogger.getLogger().debug("ICCAT-VPA: CAA DONE! "+CAAInpURL);
		PCAAInpURL = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), new File(config.getParam(PCAAInp)).getParent(),"/", new File(config.getParam(PCAAInp)).getName(),true);
		AnalysisLogger.getLogger().debug("ICCAT-VPA: PCAA DONE! "+PCAAInpURL);
		CPUEInpURL = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), new File(config.getParam(CPUEInp)).getParent(),"/", new File(config.getParam(CPUEInp)).getName(),true);
		AnalysisLogger.getLogger().debug("ICCAT-VPA: CPUE DONE! "+CPUEInpURL);
		PwaaInpURL = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), new File(config.getParam(PwaaInp)).getParent(), "/",new File(config.getParam(PwaaInp)).getName(),true);
		AnalysisLogger.getLogger().debug("ICCAT-VPA: Pwaa DONE! "+PwaaInpURL);
		waaInpURL = StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), new File(config.getParam(waaInp)).getParent(),"/", new File(config.getParam(waaInp)).getName(),true);
		AnalysisLogger.getLogger().debug("ICCAT-VPA: waa DONE! "+waaInpURL);
		AnalysisLogger.getLogger().debug("ICCAT-VPA Input files uploaded!");
		
		AnalysisLogger.getLogger().debug("ICCAT-VPA Setting input URLs: "+config.getGeneralProperties());
		
		config.setParam(CAAInp, CAAInpURL);
		config.setParam(PCAAInp, PCAAInpURL);
		config.setParam(CPUEInp, CPUEInpURL);
		config.setParam(PwaaInp, PwaaInpURL);
		config.setParam(waaInp, waaInpURL);
					
	}
	
	@Override
	public StatisticalType getOutput() {
		
		//File outfile = new File(config.getPersistencePath(),processOutput);
		File outfile = new File(processOutput);
		LinkedHashMap<String, StatisticalType> outputmap = new LinkedHashMap<String, StatisticalType>();
		AnalysisLogger.getLogger().debug("ICCAT-VPA Output: "+outfile.getAbsolutePath()+" : "+outfile.exists());
		
		if (!outfile.exists()){
			AnalysisLogger.getLogger().debug("ICCAT-VPA Output file "+processOutput+" does not exist - returning null ");
			return null;
		}
		
		PrimitiveType o = new PrimitiveType(File.class.getName(), outfile, PrimitiveTypes.FILE, "ProcessSummary", "Output file containing the process summary");
		outputmap.put("Zip file containing the process output", o);
	
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), outputmap, PrimitiveTypes.MAP, "Results File", "Results File");
		AnalysisLogger.getLogger().debug("ICCAT-VPA Output Managed");
		return output;
	}
	
	@Override
	public void initSingleNode(AlgorithmConfiguration config) {}
	
	@Override
	public float getInternalStatus() {return status;}
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,  boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		try {
			status = 0;
			config = Transformations.restoreConfig(nodeConfigurationFileObject);
			
			String outputFile = config.getParam(processOutputParam);
			String localzipFile = "iccat_zip.zip"; 
			
			AnalysisLogger.getLogger().info("ICCAT-VPA ranges: "+" Li:"+leftStartIndex+" NLi:"+leftStartIndex+" Ri:"+rightStartIndex+" NRi:"+numberOfRightElementsToProcess);
			AnalysisLogger.getLogger().info("ICCAT-VPA expected output "+outputFile);
			
			//download the package
			AnalysisLogger.getLogger().info("ICCAT-VPA : downloading package URL: "+packageURL);
			StorageUtils.downloadInputFile(packageURL, localzipFile,true);
			
			//unzip the package
			AnalysisLogger.getLogger().info("ICCAT-VPA : Unzipping file: "+localzipFile+" having size "+new File(localzipFile).length());
			ZipTools.unZip(localzipFile, sandboxFolder);
			
			//download input files
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading remote input files "+config.getGeneralProperties());
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading  CAA");
			StorageUtils.downloadInputFile(config.getParam(CAAInp), "CAA_Age1_25.csv",true);
			StorageUtils.downloadInputFile(config.getParam(PCAAInp), "PCAA_Age1_25_Run3.csv",true);
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading  PCAA");
			StorageUtils.downloadInputFile(config.getParam(CPUEInp), "CPUE_Run3.csv",true);
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading  CPUE");
			StorageUtils.downloadInputFile(config.getParam(PwaaInp), "waa.csv",true);
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading  Pwaa");
			StorageUtils.downloadInputFile(config.getParam(waaInp), "fecaa.csv",true);
			AnalysisLogger.getLogger().info("ICCAT-VPA : Downloading  waa");
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : all files downloaded: ");
			AnalysisLogger.getLogger().info("ICCAT-VPA : CCA size: "+new File("CAA_Age1_25.csv"));
			AnalysisLogger.getLogger().info("ICCAT-VPA : PCAA size: "+new File("PCAA_Age1_25_Run3.csv"));
			AnalysisLogger.getLogger().info("ICCAT-VPA : CPUE size: "+new File("CPUE_Run3.csv"));
			AnalysisLogger.getLogger().info("ICCAT-VPA : Pwaa size: "+new File("waa.csv"));
			AnalysisLogger.getLogger().info("ICCAT-VPA : waa size: "+new File("fecaa.csv"));
			
			String run = "Run_"+rightStartIndex;
			//create the input file: e.g. 
			//Run_7	1950-2013	CAA_Age1_25.csv	PCAA_Age1_25_Run3.csv	CPUE_Run3.csv	waa.csv	fecaa.csv	Run_2	split JP_LL NEAST and wihtout last 1 year in ESPMARTrap	8	1	1	10
			String header  =  "Run,Year,CAA,PCAA,CPUE,Pwaa,waa,compare to,effect,nCPUE,CPUE_cut,n_remove_year,age_plus_group";
			String input = run+","+config.getParam(YearStartInp)+"-"+config.getParam(YearEndInp)+","+"CAA_Age1_25.csv"+","+"PCAA_Age1_25_Run3.csv"+","+"CPUE_Run3.csv"+","+"waa.csv"+","+"fecaa.csv"+",,"+config.getParam(effectInp)+","+config.getParam(nCPUEInp)+","+config.getParam(CPUEcutInp)+","+config.getParam(nRemoveYearInp)+","+config.getParam(agePlusGroupInp);
			FileWriter fw = new FileWriter(new File(sandboxFolder,"run_spec.csv"));
			fw.write(header+"\n");
			fw.write(input+"\n");
			fw.close();
			
			//run the code as-is after substituting the Run string
			HashMap<String,String> codeinj = new HashMap<String,String>();
			codeinj.put("Run_0", run);
			String pathSand = new File(sandboxFolder).getAbsolutePath();
			if (!pathSand.endsWith("/"))
				pathSand=pathSand+"/";
			
			AnalysisLogger.getLogger().debug("ICCAT-VPA : substituting path to sandbox folder "+pathSand);
			codeinj.put("/home/gcube/irdstockassessment/allinone/", pathSand);
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : changing rights to the fortran file");
			OSCommand.ExecuteGetLine("chmod 777 vpa-2box.out", null);
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : running the script: "+scriptName);
			RScriptsManager scriptmanager = new RScriptsManager();
			scriptmanager.executeRScript(config, scriptName, "", new HashMap<String,String>(), "", "output.csv", codeinj, false,false,false, sandboxFolder);
			
			//zip the output
			File outputFolder = new File(sandboxFolder,run);
			AnalysisLogger.getLogger().info("ICCAT-VPA : checking the output folder: "+outputFolder.getAbsolutePath()+" exists: "+outputFolder.exists());
			if (!outputFolder.exists())
				throw new Exception("ICCAT-VPA: output was not produced!");
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : producing zip output: "+outputFile);
			ZipTools.zipFolder(new File(sandboxFolder,run).getAbsolutePath(), outputFile);
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : zip output exists: "+new File(outputFile).exists());
			
			//upload the output
			AnalysisLogger.getLogger().info("ICCAT-VPA : uploading on storage");
			StorageUtils.uploadFilesOnStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), sandboxFolder,outputFile);
			
			AnalysisLogger.getLogger().info("ICCAT-VPA : Finished");
		}catch(Exception e){
			e.printStackTrace();
		}	
			
		return 0;
	}

	

	int nExperiments=1;
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
		AnalysisLogger.getLogger().info("ICCAT-VPA process stopped");
	}

	boolean haspostprocessed = false;
	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		try {
			
			AnalysisLogger.getLogger().debug("ICCAT-VPA - Downloading file "+processOutput);
			StorageUtils.downloadFilefromStorage(config.getGcubeScope(), config.getParam("ServiceUserName"), config.getPersistencePath(), processOutput);
			File of = new File(config.getPersistencePath(),processOutput);
			for (int i=0;i<3;i++){
				if (of.exists())
					break;
				else
					Thread.sleep(1000);
			}
			
			if (of.exists()){
				AnalysisLogger.getLogger().debug("ICCAT-VPA - Postprocess complete: output ready in "+of.getAbsolutePath());
				processOutput=of.getAbsolutePath();
			}
			else
				AnalysisLogger.getLogger().debug("ICCAT-VPA - Warning Postprocess error - output does not exist ! "+of.getAbsolutePath());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}