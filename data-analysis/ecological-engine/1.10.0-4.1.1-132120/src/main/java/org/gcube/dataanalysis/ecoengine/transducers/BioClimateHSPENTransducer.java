package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class BioClimateHSPENTransducer extends BioClimateHSPECTransducer{
	
	private String[] envelopeTables;
	private String[] envelopeTablesNames;
	
	@Override
	public void init() throws Exception {
		//init the analyzer
		bioClimate=new BioClimateAnalysis(config.getConfigPath(),config.getPersistencePath(),config.getParam("DatabaseURL"),config.getParam("DatabaseUserName"), config.getParam("DatabasePassword"), false);
		//build the hspec names:
		envelopeTables = config.getParam("HSPEN_Table_List").split(AlgorithmConfiguration.getListSeparator());
		envelopeTablesNames = config.getParam("HSPEN_Table_Names").split(AlgorithmConfiguration.getListSeparator());
	}

	@Override
	public String getDescription() {
		return "A transducer algorithm that generates a table containing species envelops (HSPEN) in time, i.e. models capturing species tolerance with respect to environmental parameters, used by the AquaMaps approach. Evaluates the climatic changes impact on the variation of the salinity values in several ranges of a set of species envelopes";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HSPEN);
		TablesList p7 = new TablesList(templateHspec, "HSPEN_Table_List", "list of HSPEN tables containing the species for which the salinity will be analyzed", false);
		PrimitiveTypesList p8 = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "HSPEN_Table_Names", "list of HSPEN table names to be used as labels", false);
		
		parameters.add(p7);
		parameters.add(p8);
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public void compute() throws Exception {
		
		status = 0.1f;
		try{
		bioClimate.speciesEvolutionAnalysis(envelopeTables,envelopeTablesNames, BioClimateAnalysis.salinityMinFeature, BioClimateAnalysis.salinityDefaultRange);
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
