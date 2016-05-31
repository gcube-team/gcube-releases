package org.gcube.dataanalysis.ecoengine.transducers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

public class BioClimateHSPECTransducer implements Transducerer{
	
	protected HashMap<String, Image> producedImages;
	protected AlgorithmConfiguration config;
	protected BioClimateAnalysis bioClimate;
	private String[] hspecTables;
	private String[] hspecTablesNames;
	
	protected float status = 0;
	
	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}
	
	
	@Override
	public void init() throws Exception {
		//init the analyzer
		bioClimate=new BioClimateAnalysis(config.getConfigPath(),config.getPersistencePath(),config.getParam("DatabaseURL"),config.getParam("DatabaseUserName"), config.getParam("DatabasePassword"), false);
		//build the hspec names:
		hspecTables = config.getParam("HSPEC_Table_List").split(AlgorithmConfiguration.getListSeparator());
		hspecTablesNames = config.getParam("HSPEC_Table_Names").split(AlgorithmConfiguration.getListSeparator());
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config=config;
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public float getStatus() {
		if ((status>0)&&(status<100)){
			return Math.min(bioClimate.getStatus(),95f);
		}
		else
			return status;
	}

	@Override
	public String getDescription() {
		return "A transducer algorithm that generates a table containing an estimate of species distributions per half-degree cell (HSPEC) in time. Evaluates the climatic changes impact on species presence.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HSPEC);
		TablesList p7 = new TablesList(templateHspec, "HSPEC_Table_List", "List of HSPEC tables to analyze", false);
		PrimitiveTypesList p8 = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "HSPEC_Table_Names", "list of HSPEC table names to be used as labels", false);
		
		PrimitiveType p9 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "Threshold", "a threshold of probability over which the abundancy per species will be calculated","0.5");
		
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		String name = HashMap.class.getName();
		PrimitiveType p = new PrimitiveType(name, producedImages, PrimitiveTypes.IMAGES, "Charts","A map with keys and Images");
		return p;
	
	}

	@Override
	public void compute() throws Exception {
		
		status = 0.1f;
		try{
		float threshold = Float.parseFloat(config.getParam("Threshold"));
		bioClimate.globalEvolutionAnalysis(null, hspecTables, null, hspecTablesNames, "probability", "csquare", threshold);
		producedImages=bioClimate.getProducedImages();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			status = 100f;
		}
	}


	ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}


	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

}
