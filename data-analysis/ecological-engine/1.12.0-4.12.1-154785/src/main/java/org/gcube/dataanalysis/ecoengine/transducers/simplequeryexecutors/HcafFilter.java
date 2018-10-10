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

public class HcafFilter extends QueryExecutor {

	static String bbx1 = "B_Box_Left_Lower_Long";
	static String bbx2 = "B_Box_Right_Upper_Long";
	static String bby1 = "B_Box_Left_Lower_Lat";
	static String bby2 = "B_Box_Right_Upper_Lat";

	String bbx1$;
	String bbx2$;
	String bby1$;
	String bby2$;

	String species;

	@Override
	public void init() throws Exception {

		finalTableName = config.getParam(finalTable);
		finalTableLabel = config.getParam(finalTableLabel$);
		bbx1$ = config.getParam(bbx1);
		bbx2$ = config.getParam(bbx2);
		bby1$ = config.getParam(bby1);
		bby2$ = config.getParam(bby2);

		query = "select * into " + finalTableName + " from hcaf_d where (centerlat-0.25)>" + bby1$ + " and (centerlong-0.25)>" + bbx1$ + " and (centerlat+0.25)<" + bby2$ + " and (centerlong+0.25)<" + bbx2$+"; ALTER TABLE "+finalTableName+" ADD PRIMARY KEY (\"csquarecode\")";
	}

	@Override
	public List<StatisticalType> getInputParameters() {

		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableLabel$,"the name of the Filtered Hcaf", "hcaf_filtered");
		ServiceType p1 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTable, "the name of the Filtered Hcaf", "hcaf_filtered");
		PrimitiveType p4 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, bby1, "the left lower latitude of the bounding box (range [-90,+90])", "-17.098");
		PrimitiveType p2 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, bbx1, "the left lower longitude of the bounding box (range [-180,+180])", "89.245");
		PrimitiveType p5 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, bby2, "the right upper latitude of the bounding box (range [-90,+90])", "25.086");
		PrimitiveType p3 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, bbx2, "the right upper longitude of the bounding box (range [-180,+180])", "147.642");

		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p0);
		parameters.add(p1);
		parameters.add(p4);
		parameters.add(p2);
		parameters.add(p5);
		parameters.add(p3);

		DatabaseType.addDefaultDBPars(parameters);
		
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.HCAF);
		return new OutputTable(template, finalTableLabel, finalTableName, "a HCAF table focusing on the selected Bounding Box");
	}

	@Override
	public String getDescription() {
		return "An algorithm producing a HCAF table on a selected Bounding Box (default identifies Indonesia)";
	}

}
