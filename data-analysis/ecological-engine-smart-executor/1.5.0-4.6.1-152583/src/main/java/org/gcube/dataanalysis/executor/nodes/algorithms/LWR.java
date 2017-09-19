package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.hibernate.SessionFactory;

public class LWR extends ActorNode {

	public String destinationTable;
	public String destinationTableLabel;
	public String originTable;
	public String familyColumn;
	public int count;

	public float status = 0;
	public int prevbroadcastTimePeriod;
	public int prevmaxNumberOfStages;
	public int prevmaxMessages;
	
	private SessionFactory dbconnection;
	private static String createOutputTable = "CREATE TABLE %1$s (Fam character varying(255),   SF character varying(255), BS character varying(255),	  SpC character varying(255),	  LWR real,	  priormeanlog10a real,	  priorsdlog10a real,	  priormeanb real,		  priorsdb real,		  note character varying(255)		)";
	private static String columnNames = "Fam,SF,BS,SpC,LWR,priormeanlog10a,priorsdlog10a,priormeanb,priorsdb,note";
	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
		return p;
	}

	@Override
	public String getName() {
		return "LWR";
	}

	@Override
	public String getDescription() {
		return "An algorithm to estimate Length-Weight relationship parameters for marine species, using Bayesian methods. Runs an R procedure. Based on the Cube-law theory.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templateLWRInput = new ArrayList<TableTemplates>();
		templateLWRInput.add(TableTemplates.GENERIC);
		InputTable p1 = new InputTable(templateLWRInput, "LWR_Input", "Input table containing taxa and species information", "lwr");
		ColumnType p3 = new ColumnType("LWR_Input", "FamilyColumn", "The column containing Family information", "Family", false);
		ServiceType p4 = new ServiceType(ServiceParameters.RANDOMSTRING, "RealOutputTable", "name of the resulting table", "lwr_");
		PrimitiveType p2 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "TableLabel", "Name of the table which will contain the model output", "lwrout");

		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p1);
		parameters.add(p3);
		parameters.add(p2);
		parameters.add(p4);
		
		DatabaseType.addDefaultDBPars(parameters);
		
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(template, destinationTableLabel, destinationTable, "Output lwr table");
		return p;
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {

	}

	@Override
	public float getInternalStatus() {
		return status;
	}

	private static String scriptName = "UpdateLWR_4.R";
//	private static String scriptName = "UpdateLWR_Test2.R";
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,  boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		String insertQuery = null;
		try {
			status = 0;
			AlgorithmConfiguration config = config = Transformations.restoreConfig(new File (sandboxFolder,nodeConfigurationFileObject).getAbsolutePath());
			config.setConfigPath(sandboxFolder);
			System.out.println("Initializing DB");
			dbconnection = DatabaseUtils.initDBSession(config);
			destinationTableLabel = config.getParam("TableLabel");
			destinationTable = config.getParam("RealOutputTable");

			System.out.println("Destination Table: "+destinationTable);
			System.out.println("Destination Table Label: "+destinationTableLabel);
			
			
			originTable = config.getParam("LWR_Input");
			familyColumn = config.getParam("FamilyColumn");
			System.out.println("Origin Table: "+originTable);
			
			// take the families to process
			List<Object> families = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(originTable, familyColumn, ""), dbconnection);
			
			// transform the families into a string
			// Families <- Fam.All[Fam.All== "Acanthuridae" | Fam.All == "Achiridae"]
			StringBuffer familiesFilter = new StringBuffer();
			familiesFilter.append("Families <- Fam.All[");
			
			/*
			rightStartIndex=519;
			numberOfRightElementsToProcess=19;
			*/
			
			int end = rightStartIndex + numberOfRightElementsToProcess;
			
			for (int i = rightStartIndex; i < end; i++) {
				familiesFilter.append("Fam.All == \"" + families.get(i) + "\"");
				if (i < end - 1)
					familiesFilter.append(" | ");
			}
			familiesFilter.append("]");

			OSCommand.ExecuteGetLine("ls", null);
			OSCommand.ExecuteGetLine("pwd", null);
			OSCommand.ExecuteGetLine("chmod +x * | whoami", null);
						
			String substitutioncommand = "sed -i 's/Families <- Fam.All[Fam.All== \"Acanthuridae\" | Fam.All == \"Achiridae\"]/" + familiesFilter + "/g' " + "UpdateLWR_Test2.R";
//			System.out.println("Executing command: " + substitutioncommand);
			System.out.println("Preparing for processing the families names: "+familiesFilter.toString());
			
			// substitute the string in the RCode
//			OSCommand.ExecuteGetLine(substitutioncommand, null);
//			substring(sandboxFolder+"UpdateLWR_Test2.R",sandboxFolder+"UpdateLWR_Tester.R","Families <- Fam.All[Fam.All== \"Acanthuridae\" | Fam.All == \"Achiridae\"]",familiesFilter.toString());

			substring(sandboxFolder+scriptName,sandboxFolder+"UpdateLWR_Tester.R","Families <- Fam.All[Fam.All== \"Acanthuridae\" | Fam.All == \"Achiridae\"]",familiesFilter.toString());
			//for test only
//			substring(sandboxFolder+scriptName,sandboxFolder+"UpdateLWR_Tester.R","Families <- Fam.All[Fam.All== \"Acanthuridae\" | Fam.All == \"Achiridae\"]","Families <- Fam.All[Fam.All== \"Abyssocottidae\"]");
			
//			substring(sandboxFolder+scriptName,sandboxFolder+"UpdateLWR_Tester.R","Families <- Fam.All[Fam.All== \"Acanthuridae\" | Fam.All == \"Achiridae\"]","Families <- Fam.All[Fam.All== \"Sparidae\"]");
			
			System.out.println("Creating local file from remote table");
			// download the table in csv
			DatabaseUtils.createLocalFileFromRemoteTable(sandboxFolder+"RF_LWR.csv", originTable, ",", config.getDatabaseUserName(),config.getDatabasePassword(),config.getDatabaseURL());

			String headers = "Subfamily,Family,Genus,Species,FBname,SpecCode,AutoCtr,Type,a,b,CoeffDetermination,Number,LengthMin,Score,BodyShapeI";
			System.out.println("Adding headers to the file");
			
			String headerscommand = "sed -i '1s/^/"+headers+"\\n/g' "+"RF_LWR2.csv";
			// substitute the string in the RCode
//			OSCommand.ExecuteGetLine(headerscommand, null);
			addheader(sandboxFolder+"RF_LWR.csv",sandboxFolder+"RF_LWR2.csv",headers);
			
//			OSCommand.ExecuteGetLine("head RF_LWR2.csv", null);
			System.out.println("Headers added");
			
			System.out.println("Executing R script " + "R --no-save < UpdateLWR_Tester.R");
		
			// run the R code
			Process process = Runtime.getRuntime().exec("R --no-save");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			bw.write("source('UpdateLWR_Tester.R')\n");
			bw.write("q()\n");
			bw.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			System.out.println(line);
			while (line!=null){
				line = br.readLine();
				System.out.println(line);
			}
			
			process.destroy();
			
//			OSCommand.ExecuteGetLine("R --no-save <UpdateLWR_Tester.R", null);
			

			System.out.println("Appending csv to table");
			// transform the output into table
			
//			DatabaseUtils.createRemoteTableFromFile(sandboxFolder + "LWR_Test1.csv", destinationTable, ",", true,  config.getDatabaseUserName(),config.getDatabasePassword(),config.getDatabaseURL());
			StringBuffer lines = readFromCSV("LWR_Test1.csv");
			insertQuery = DatabaseUtils.insertFromBuffer(destinationTable, columnNames, lines);
			
			DatabaseFactory.executeSQLUpdate(insertQuery, dbconnection);
			
			System.out.println("The procedure was successful");

			status = 1f;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("warning: error in node execution " + e.getLocalizedMessage());
			System.out.println("Insertion Query: "+insertQuery);
			System.err.println("Error in node execution " + e.getLocalizedMessage());
			return -1;
		} finally {
			if (dbconnection != null)
				try {
					dbconnection.close();
				} catch (Exception e) {
				}
		}
		return 0;
	}

	private StringBuffer readFromCSV(String csvfile) throws Exception{
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(csvfile));
		String line = br.readLine();
		while (line!=null){
			lines.add(line);
			line = br.readLine();
		}
		br.close();
		
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		int m = lines.size();
		for (int i=0;i<m;i++){
			sb.append(lines.get(i).replace("\"", "'"));
			if (i<m-1)
				sb.append("),(");
		}
		sb.append(")");
		
		
		
		return sb;
	}
	
	private static void substring(String file, String newFile, String s,String sub) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFile)));
		String line = br.readLine();
		
		while (line!=null){
			int idx = line.indexOf(s);
			if (idx>=0){
				line = line.replace(s, sub);
			}
			bw.write(line+"\n");
			line = br.readLine();
		}
		
		br.close();
		bw.close();
	}
	
	
	private static void addheader(String file, String newFile, String header) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFile)));
		bw.write(header+"\n");
		
		String line = br.readLine();
		
		while (line!=null){
			bw.write(line+"\n");
			line = br.readLine();
		}
		
		br.close();
		bw.close();
	}
	
	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {

		destinationTableLabel = config.getParam("TableLabel");
		AnalysisLogger.getLogger().info("Table Label: "+destinationTableLabel);
		destinationTable = config.getParam("RealOutputTable");
		AnalysisLogger.getLogger().info("Uderlying Table Name: "+destinationTable);
		originTable = config.getParam("LWR_Input");
		AnalysisLogger.getLogger().info("Original Table: "+originTable);
		familyColumn = config.getParam("FamilyColumn");
		AnalysisLogger.getLogger().info("Family Column: "+familyColumn);
		haspostprocessed = false;
		
		AnalysisLogger.getLogger().info("Initializing DB Connection");
		dbconnection = DatabaseUtils.initDBSession(config);
		List<Object> families = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(originTable, familyColumn, ""), dbconnection);
		 count = families.size();
//		count = 4;
		
//		count = 200;
		
		prevmaxMessages=D4ScienceDistributedProcessing.maxMessagesAllowedPerJob;
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=1;
		
		AnalysisLogger.getLogger().info("Creating Destination Table " + destinationTable);
		try{
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(destinationTable), dbconnection);
		}catch (Exception e) {
			AnalysisLogger.getLogger().info("Table "+destinationTable+" did not exist");
		}
		DatabaseFactory.executeSQLUpdate(String.format(createOutputTable, destinationTable), dbconnection);
		AnalysisLogger.getLogger().info("Destination Table Created! Addressing " + count + " species");
		
		
	}

	@Override
	public int getNumberOfRightElements() {
		return count;
	}

	@Override
	public int getNumberOfLeftElements() {
		return 1;
	}

	@Override
	public void stop() {
		
		//if has not postprocessed, then abort the computations by removing the database table
		if (!haspostprocessed){
			try{
				AnalysisLogger.getLogger().info("The procedure did NOT correctly postprocessed ....Removing Table "+destinationTable+" because of computation stop!");
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(destinationTable), dbconnection);
			}catch (Exception e) {
				AnalysisLogger.getLogger().info("Table "+destinationTable+" did not exist");
			}
		}
		else
			AnalysisLogger.getLogger().info("The procedure has correctly postprocessed: shutting down the connection!");
		if (dbconnection != null)
			try {
				dbconnection.close();
			} catch (Exception e) {
			}
	}

	boolean haspostprocessed = false;
	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=prevmaxMessages;
		haspostprocessed=true;
	}

}
