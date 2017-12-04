package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class BioClimateHCAFTransducer extends BioClimateHSPECTransducer{
	
	private String[] hcafTables;
	private String[] hcafTablesNames;
	
	
	
	@Override
	public void init() throws Exception {
		//init the analyzer
		bioClimate=new BioClimateAnalysis(config.getConfigPath(),config.getPersistencePath(),config.getParam("DatabaseURL"),config.getParam("DatabaseUserName"), config.getParam("DatabasePassword"), false);
		//build the hspec names:
		hcafTables = config.getParam("HCAF_Table_List").split(AlgorithmConfiguration.getListSeparator());
		hcafTablesNames = config.getParam("HCAF_Table_Names").split(AlgorithmConfiguration.getListSeparator());
	}

	@Override
	public String getDescription() {
		return "A transducer algorithm that generates an Half-degree Cells Authority File (HCAF) dataset for a certain time frame, with environmental parameters used by the AquaMaps approach. Evaluates the climatic changes impact on the variation of the ocean features contained in HCAF tables";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HCAF);
		
		TablesList p7 = new TablesList(templateHspec, "HCAF_Table_List", "list of HCAF tables to analyze", false);
		PrimitiveTypesList p8 = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "HCAF_Table_Names", "list of HCAF table names to be used as labels", false);
		
		parameters.add(p7);
		parameters.add(p8);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public void compute() throws Exception {
		
		status = 0.1f;
		try{
		bioClimate.hcafEvolutionAnalysis(hcafTables, hcafTablesNames);
		producedImages=bioClimate.getProducedImages();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			status = 100f;
		}
	}


}
