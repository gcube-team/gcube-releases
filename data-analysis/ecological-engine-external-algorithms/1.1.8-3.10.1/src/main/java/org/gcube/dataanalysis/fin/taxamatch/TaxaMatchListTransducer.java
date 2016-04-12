package org.gcube.dataanalysis.fin.taxamatch;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.taxamatch.fin.func_Taxamatch;
import org.hibernate.SessionFactory;

public class TaxaMatchListTransducer extends StandardLocalExternalAlgorithm {

	public static enum operators {
		EQUAL, NOT_EQUAL, CONTAINS, BEGINS_WITH, ENDS_WITH
	};

	static String GenusOperator = "ComparisonOperatorforGenus";
	static String SpeciesOperator = "ComparisonOperatorforSpecies";
	static String Genus = "Genus";
	static String Species = "Species";
	static String databaseParameterName = "FishBase";
	static String userParameterName = "user";
	static String passwordParameterName = "password";
	static String urlParameterName = "FishBase";
	
	String outputtablename;
	String outputtable;
	
	
	@Override
	public String getDescription() {
		return "An algorithm for Taxa Matching with respect to the Fishbase database";
	}

	@Override
	public void init() throws Exception {

	}

	
	@Override
	protected void process() throws Exception {
		SessionFactory dbconnection = null;
		try{
			
		System.out.println("taxa->USING THE FOLLOWING PARAMETERS FOR DB:");	
		System.out.println("taxa->driver:"+config.getParam("DatabaseDriver"));
		System.out.println("taxa->url:"+config.getParam("DatabaseURL"));
		System.out.println("taxa->user:"+config.getParam("DatabaseUserName"));
		System.out.println("taxa->password:"+config.getParam("DatabasePassword"));
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		dbconnection = DatabaseUtils.initDBSession(config);
		String tablename = getInputParameter("TaxaTable");
		String columnnames = getInputParameter("TaxaColumns");
		
		outputtablename = getInputParameter("OutputTableName");
		outputtable = getInputParameter("OutputTable");
		
		String genusOperator = getInputParameter(GenusOperator);
		String speciesOperator = getInputParameter(SpeciesOperator);
		
		System.out.println("taxa->got input parameters");
		
		String databaseJdbc = getInputParameter(urlParameterName).replace("//", "");

		int separator = databaseJdbc.lastIndexOf("/");
		if (separator<0){
			log("Bad database URL: "+databaseJdbc);
			return;
		}
		
		System.out.println("taxa->got DB parameters "+databaseJdbc);
		
		String databaseIP = databaseJdbc.substring(0,separator);
		String databaseName = databaseJdbc.substring(separator+1);
		String databaseUser = getInputParameter(userParameterName);
		String databasePwd = getInputParameter(passwordParameterName);
		
//		databaseIP = "biodiversity.db.i-marine.research-infrastructures.eu";
//		databaseUser = "postgres";
//		databasePwd = "0b1s@d4sc13nc3";
//		databaseName = "fishbase";
				
		System.out.println("taxa->Fishbase Database Parameters to use: "+databaseIP+" "+databaseName+" "+databaseUser+" "+databasePwd);
		
		//end inputs recover
		
		String[] columnlist = columnnames.split(AlgorithmConfiguration.getListSeparator());
		System.out.println("taxa->got columns: "+columnlist[0]+" and "+columnlist[1]);
		
		System.out.println("taxa->Selecting genus ");
		List<Object> genusList = DatabaseFactory.executeSQLQuery("select "+columnlist[0]+" from "+tablename, dbconnection);
		System.out.println("taxa->Selecting species");
		List<Object> speciesList = DatabaseFactory.executeSQLQuery("select "+columnlist[1]+" from "+tablename, dbconnection);
		
		System.out.println("taxa->creating table "+"create table "+outputtable+" (scientific_name character varying, value real)");
		DatabaseFactory.executeSQLUpdate("create table "+outputtable+" (scientific_name character varying, value real)", dbconnection);
		
		//loop
		
		
		System.out.println("taxa->inserting into table "+"insert into "+outputtable+" (scientific_name,value) values ('Gadus morhua', 3)");
		DatabaseFactory.executeSQLUpdate("insert into "+outputtable+" (scientific_name,value) values ('Gadus morhua', 3)", dbconnection);
		
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			DatabaseUtils.closeDBConnection(dbconnection);
		}

	}
	
	private String doTaxaMatch(){
		/*
		log("Computing matching for " + genus + " " + species);
		log("With operators: " + genusOperator + " " + speciesOperator);
		if ((genus == null) || (species == null)) {
			log("Void input");
			addOutputString("Number of Matches", "0");
		} else {
			func_Taxamatch func = new func_Taxamatch();
			AnalysisLogger.getLogger().trace("TaxaMatcher Initialized");
			String[] matches = func.func_Taxamatch(genus, species, genusOperator, speciesOperator, databaseIP, databaseUser, databasePwd, databaseName);

			if ((matches == null) || (matches.length == 0)) {
				log("No match");
				addOutputString("Number of Matches", "0");
			} else {
				log("Found " + matches[0] + " matches");
				addOutputString("Number of Matches", matches[0]);
				String[] speciesn = matches[1].split("\n");
				if (Integer.parseInt(matches[0]) > 0) {
					for (int i = 0; i < speciesn.length; i++) {
						addOutputString("Match " + (i + 1), speciesn[i].trim());
					}
				}
			}
		}
		log(outputParameters);
		 */
		return null;
	}
		
		
	@Override
	public void shutdown() {

	}

	@Override
	protected void setInputParameters() {
		
		addEnumerateInput(operators.values(), GenusOperator, "Comparison Operator for Genus", "" + operators.EQUAL);
		addEnumerateInput(operators.values(), SpeciesOperator, "Comparison Operator for Species", "" + operators.EQUAL);
		
		addRemoteDatabaseInput(databaseParameterName,urlParameterName,userParameterName,passwordParameterName,"driver","dialect");
		
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates,"TaxaTable","The table containing the taxa information");
		ColumnTypesList columns = new ColumnTypesList ("TaxaTable","TaxaColumns", "Select the columns for genus and species", false);
		
		addStringInput("OutputTableName", "The name of the output table", "taxa_");
		
		ServiceType randomstring = new ServiceType(ServiceParameters.RANDOMSTRING, "OutputTable","","taxa");
		
		inputs.add(tinput);
		inputs.add(columns);
		inputs.add(randomstring);
		
		DatabaseType.addDefaultDBPars(inputs);
		
	}

	@Override
	public StatisticalType getOutput() {
	
		List<TableTemplates> outtemplate = new ArrayList<TableTemplates>();
		outtemplate.add(TableTemplates.GENERIC);

		OutputTable out = new OutputTable(outtemplate, outputtablename, outputtable, "The output table containing all the matches");

		return out;
		
	 
	}
	
}
