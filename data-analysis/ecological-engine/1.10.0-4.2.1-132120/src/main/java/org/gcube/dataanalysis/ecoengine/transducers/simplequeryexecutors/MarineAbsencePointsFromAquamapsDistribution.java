package org.gcube.dataanalysis.ecoengine.transducers.simplequeryexecutors;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.transducers.QueryExecutor;

public class MarineAbsencePointsFromAquamapsDistribution extends QueryExecutor {

	static String doRandom = "Take_Randomly";
	static String AquamapsHSpecTable = "Aquamaps_HSPEC";
	static String numberOfPoints = "Number_of_Points";
	static String speciesCode = "Species_Code";
	String dorandom;
	
	String aquamapsTable;
	String nPoints;
	String species;
	
	@Override
	public void init() throws Exception {
		 dorandom = config.getParam(doRandom);
		 finalTableName = config.getParam(finalTable);
		 finalTableLabel = config.getParam(finalTableLabel$);
		 aquamapsTable= config.getParam(AquamapsHSpecTable);
		 nPoints= config.getParam(numberOfPoints);
		 species = config.getParam(speciesCode);
		 
		String dorandom$ = "";
		if (Boolean.parseBoolean(dorandom))
			dorandom$="order by random()";
		
		query = "select * into "+finalTableName+" from hcaf_d as a where a.csquarecode in (select csquarecode from "+aquamapsTable+" where probability<=0.2 and speciesid='"+species+"'"+dorandom$+" limit "+nPoints+") and oceanarea>0; ALTER TABLE "+finalTableName+" ADD PRIMARY KEY (\"csquarecode\");";
	}
	
	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HSPEC);
		
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableLabel$,"the name of the Filtered Hcaf", "AbsenceCells_");
		InputTable p1 = new InputTable(templateHspec,AquamapsHSpecTable,"an Aquamaps table from which to produce the absence points","hspec");
		PrimitiveType p2 = new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, doRandom, "a flag for taking points randomly (true) or close together (false)","true");
//		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.RANDOM, finalTable,"Name of the HCAF table to produce containing Absence Cells","absence_hcaf");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTable,"Name of the HCAF table to produce containing Absence Cells","absence_hcaf");
		
		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, numberOfPoints,"number of points to take","20");
		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, speciesCode,"the species code according to the Fish-Base conventions","Fis-30189");
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p0);
		parameters.add(p1);
		parameters.add(p2);
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
		return new OutputTable(template, finalTableLabel, finalTableName, "a HCAF table containing Absence Points cells");
	}
	
	@Override
	public String getDescription() {
		return "An algorithm producing cells and features (HCAF) for a species containing absense points taken by an Aquamaps Distribution";
	}
	
	
}
