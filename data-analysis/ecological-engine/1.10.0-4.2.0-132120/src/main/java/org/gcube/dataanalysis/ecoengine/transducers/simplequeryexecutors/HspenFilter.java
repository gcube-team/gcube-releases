package org.gcube.dataanalysis.ecoengine.transducers.simplequeryexecutors;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.transducers.QueryExecutor;

public class HspenFilter extends QueryExecutor {

	static String speciesCodes = "Species_Codes";
	String species;
	
	@Override
	public void init() throws Exception {
		 
		 finalTableName = config.getParam(finalTable);
		 finalTableLabel = config.getParam(finalTableLabel$);
		 species = config.getParam(speciesCodes).replace(AlgorithmConfiguration.getListSeparator(), "','");
		 
		 query = "select * into "+finalTableName+" from hspen where speciesid in ('"+species+"')";
	}
	
	@Override
	public List<StatisticalType> getInputParameters() {

//		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.RANDOM, finalTable,"the name of the Filtered Hspen","hspen_filtered");
		PrimitiveType p1 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableLabel$,"the name of the Filtered Hspen","hspen_filtered");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTable,"the name of the Filtered Hspen","hspen_filtered");
		PrimitiveTypesList pl = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, speciesCodes, "A list of species codes (Fish Base Format) to take. E.g. Fis-30189", false);
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p1);
		parameters.add(p3);
		parameters.add(pl);
		DatabaseType.addDefaultDBPars(parameters);
		
		return parameters;
	}
	
	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.HSPEN);
		return new OutputTable(template, finalTableLabel, finalTableName, "a HSPEN table containing only selected species");
	}
	
	@Override
	public String getDescription() {
		return "An algorithm producing a HSPEN table containing only the selected species";
	}
	
	
}
