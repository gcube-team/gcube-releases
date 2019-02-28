package org.gcube.dataanalysis.ecoengine.transducers.simplequeryexecutors;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.transducers.QueryExecutor;

public class MarinePresencePoints extends QueryExecutor {

	static String numberOfPoints = "Number_of_Points";
	static String speciesCode = "Species_Code";
	String nPoints;
	String species;
	
	@Override
	public void init() throws Exception {
		 
		 finalTableName = config.getParam(finalTable);
		 finalTableLabel = config.getParam(finalTableLabel$);
		 nPoints= config.getParam(numberOfPoints);
		 species = config.getParam(speciesCode);
		
		 String points = "";
		 if (Integer.parseInt(nPoints)>0){
			 points = "limit "+nPoints;
		 }
		 
		query = "select * into "+finalTableName+" from hcaf_d where csquarecode in (select csquarecode from occurrencecells where speciesid = '"+species+"' limit 100000) and oceanarea>0 "+points+"; ALTER TABLE "+finalTableName+" ADD PRIMARY KEY (\"csquarecode\")";
	}
	
	@Override
	public List<StatisticalType> getInputParameters() {

		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableLabel$,"the name of the Filtered Hcaf", "PresenceCells_");
//		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.RANDOM, finalTable,"Name of the HCAF table to produce containing Presence Cells","presence_hcaf");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTable,"Name of the HCAF table to produce containing Presence Cells","presence_hcaf");
		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, numberOfPoints,"Maximum number of points to take (-1 to take all)","-1");
		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, speciesCode,"the species code according to the Fish-Base conventions","Fis-30189");
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p0);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}
	
	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.HCAF);
		return new OutputTable(template, finalTableLabel, finalTableName, "a HCAF table containing Presence Points cells");
	}
	
	@Override
	public String getDescription() {
		return "An algorithm producing cells and features (HCAF) for a species containing presence points";
	}
	
	
}
